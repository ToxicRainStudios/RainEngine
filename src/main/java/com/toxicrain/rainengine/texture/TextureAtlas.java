package com.toxicrain.rainengine.texture;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureAtlas {

    private final Map<Resource, TextureRegion> regionMap;
    @Getter private final int atlasTextureId;
    @Getter private final int atlasSize;

    private ByteBuffer atlasBuffer; // Store the atlas buffer for saving

    public TextureAtlas(int atlasSize) {
        this.atlasSize = atlasSize;
        this.atlasTextureId = glGenTextures();
        this.regionMap = new HashMap<>(256);
    }

    public void buildAtlas(String directory) {
        try {
            List<Path> imagePaths = Files.walk(Paths.get(directory))
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                    })
                    .toList();

            if (imagePaths.isEmpty()) {
                RainLogger.RAIN_LOGGER.warn("No textures found for atlas: {}", directory);
                return;
            }

            // Allocate atlas buffer
            atlasBuffer = BufferUtils.createByteBuffer(atlasSize * atlasSize * 4);

            // Load all images first (required for MaxRects packing)
            List<LoadedImage> images = new ArrayList<>(imagePaths.size());

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer c = stack.mallocInt(1);

                for (Path path : imagePaths) {
                    String filePath = path.toString();

                    w.clear();
                    h.clear();
                    c.clear();

                    ByteBuffer image = stbi_load(filePath, w, h, c, 4);
                    if (image == null) {
                        throw new RuntimeException("Failed to load texture: "
                                + filePath + " - " + stbi_failure_reason());
                    }

                    int width = w.get(0);
                    int height = h.get(0);

                    if (width > atlasSize || height > atlasSize) {
                        stbi_image_free(image);
                        throw new RuntimeException("Image larger than atlas: " + filePath);
                    }

                    images.add(new LoadedImage(
                            path,
                            Resource.fromFile(directory, path),
                            image,
                            width,
                            height,
                            checkTransparencyFast(image)
                    ));
                }
            }

            // Sort by height (better packing heuristic)
            images.sort((a, b) -> Integer.compare(b.height, a.height));

            MaxRectsPacker packer = new MaxRectsPacker(atlasSize, atlasSize);

            for (LoadedImage img : images) {
                Rect rect = packer.insert(img.width, img.height);
                if (rect == null) {
                    stbi_image_free(img.buffer);
                    throw new RuntimeException(
                            "Texture atlas overflow. Increase atlas size or use multiple atlases."
                    );
                }

                // FAST NATIVE COPY (memcpy per row)
                copyImageToAtlasNative(
                        atlasBuffer,
                        atlasSize,
                        img.buffer,
                        img.width,
                        img.height,
                        rect.x,
                        rect.y
                );

                float u0 = (float) rect.x / atlasSize;
                float v0 = (float) rect.y / atlasSize;
                float u1 = (float) (rect.x + img.width) / atlasSize;
                float v1 = (float) (rect.y + img.height) / atlasSize;

                TextureInfo textureInfo = new TextureInfo(
                        atlasTextureId,
                        atlasSize,
                        atlasSize,
                        img.hasTransparency
                );

                TextureRegion region = new TextureRegion(textureInfo, u0, v0, u1, v1);
                regionMap.put(img.resource, region);

                stbi_image_free(img.buffer);
            }

            uploadAtlasToGPU();

        } catch (IOException e) {
            throw new RuntimeException("Failed to build texture atlas.", e);
        }
    }
    /**
     * Ultra-fast native blit using LWJGL memCopy (row by row).
     * Avoids lots of ByteBuffer get/put calls.
     */
    private void copyImageToAtlasNative(
            ByteBuffer atlas,
            int atlasSize,
            ByteBuffer image,
            int imageWidth,
            int imageHeight,
            int xOffset,
            int yOffset
    ) {
        long atlasAddress = MemoryUtil.memAddress(atlas);
        long imageAddress = MemoryUtil.memAddress(image);

        int srcStride = imageWidth * 4;   // RGBA
        int dstStride = atlasSize * 4;    // RGBA atlas row

        for (int y = 0; y < imageHeight; y++) {
            long src = imageAddress + (long) y * srcStride;
            long dst = atlasAddress + (long) ((yOffset + y) * dstStride + xOffset * 4);

            MemoryUtil.memCopy(src, dst, srcStride);
        }
    }

    private void uploadAtlasToGPU() {
        glBindTexture(GL_TEXTURE_2D, atlasTextureId);

        // Ensure tight packing (important for performance & correctness)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                atlasSize,
                atlasSize,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                atlasBuffer
        );

        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        RainLogger.RAIN_LOGGER.info("Texture atlas uploaded to GPU.");
    }

    /**
     * Fast transparency check using stride iteration
     */
    private boolean checkTransparencyFast(ByteBuffer image) {
        int capacity = image.capacity();
        for (int i = 3; i < capacity; i += 4) { // Check alpha channel only
            if ((image.get(i) & 0xFF) < 255) {
                return true;
            }
        }
        return false;
    }

    public TextureRegion getRegion(Resource resource) {
        return regionMap.get(resource);
    }

    /**
     * Saves the current texture atlas to an image file.
     *
     * @param filePath the path to save the image.
     */
    public void saveAtlasAsImage(String filePath) {
        if (atlasBuffer == null) {
            RainLogger.RAIN_LOGGER.error("No atlas buffer to save.");
            return;
        }

        boolean result = STBImageWrite.stbi_write_png(
                filePath,
                atlasSize,
                atlasSize,
                4,
                atlasBuffer,
                atlasSize * 4
        );

        if (result) {
            RainLogger.RAIN_LOGGER.info("Atlas successfully saved to: {}", filePath);
        } else {
            RainLogger.RAIN_LOGGER.error("Failed to save atlas image to {}", filePath);
        }
    }

    @AllArgsConstructor
    private static class LoadedImage {
        final Path path;
        final Resource resource;
        final ByteBuffer buffer;
        final int width;
        final int height;
        final boolean hasTransparency;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class Rect {
        int x, y, w, h;
    }

    private static class MaxRectsPacker {

        private final List<Rect> freeRects = new ArrayList<>();
        private final List<Rect> usedRects = new ArrayList<>();

        MaxRectsPacker(int width, int height) {
            freeRects.add(new Rect(0, 0, width, height));
        }

        public Rect insert(int width, int height) {
            Rect bestNode = null;
            int bestShortSideFit = Integer.MAX_VALUE;
            int bestLongSideFit = Integer.MAX_VALUE;

            for (Rect free : freeRects) {
                if (free.w >= width && free.h >= height) {
                    int leftoverHoriz = Math.abs(free.w - width);
                    int leftoverVert = Math.abs(free.h - height);
                    int shortSideFit = Math.min(leftoverHoriz, leftoverVert);
                    int longSideFit = Math.max(leftoverHoriz, leftoverVert);

                    if (shortSideFit < bestShortSideFit ||
                            (shortSideFit == bestShortSideFit && longSideFit < bestLongSideFit)) {
                        bestNode = new Rect(free.x, free.y, width, height);
                        bestShortSideFit = shortSideFit;
                        bestLongSideFit = longSideFit;
                    }
                }
            }

            if (bestNode == null) {
                return null;
            }

            splitFreeRects(bestNode);
            usedRects.add(bestNode);
            return bestNode;
        }

        private void splitFreeRects(Rect used) {
            for (int i = 0; i < freeRects.size(); i++) {
                Rect free = freeRects.get(i);

                if (!intersects(used, free)) {
                    continue;
                }

                if (used.x < free.x + free.w && used.x + used.w > free.x) {
                    if (used.y > free.y && used.y < free.y + free.h) {
                        freeRects.add(new Rect(
                                free.x,
                                free.y,
                                free.w,
                                used.y - free.y
                        ));
                    }

                    if (used.y + used.h < free.y + free.h) {
                        freeRects.add(new Rect(
                                free.x,
                                used.y + used.h,
                                free.w,
                                (free.y + free.h) - (used.y + used.h)
                        ));
                    }
                }

                if (used.y < free.y + free.h && used.y + used.h > free.y) {
                    if (used.x > free.x && used.x < free.x + free.w) {
                        freeRects.add(new Rect(
                                free.x,
                                free.y,
                                used.x - free.x,
                                free.h
                        ));
                    }

                    if (used.x + used.w < free.x + free.w) {
                        freeRects.add(new Rect(
                                used.x + used.w,
                                free.y,
                                (free.x + free.w) - (used.x + used.w),
                                free.h
                        ));
                    }
                }

                freeRects.remove(i);
                i--;
            }

            pruneFreeList();
        }

        private boolean intersects(Rect a, Rect b) {
            return a.x < b.x + b.w &&
                    a.x + a.w > b.x &&
                    a.y < b.y + b.h &&
                    a.y + a.h > b.y;
        }

        private void pruneFreeList() {
            for (int i = 0; i < freeRects.size(); i++) {
                Rect a = freeRects.get(i);
                for (int j = i + 1; j < freeRects.size(); j++) {
                    Rect b = freeRects.get(j);
                    if (isContainedIn(a, b)) {
                        freeRects.remove(i);
                        i--;
                        break;
                    }
                    if (isContainedIn(b, a)) {
                        freeRects.remove(j);
                        j--;
                    }
                }
            }
        }

        private boolean isContainedIn(Rect a, Rect b) {
            return a.x >= b.x &&
                    a.y >= b.y &&
                    a.x + a.w <= b.x + b.w &&
                    a.y + a.h <= b.y + b.h;
        }
    }
}