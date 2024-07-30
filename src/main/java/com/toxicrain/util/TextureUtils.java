package com.toxicrain.util;

import com.toxicrain.core.Logger;
import com.toxicrain.core.TextureInfo;
import com.toxicrain.core.json.PackInfoParser;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

public class TextureUtils {
    public static TextureInfo floorTexture;
    public static TextureInfo playerTexture;
    public static TextureInfo splatterTexture;
    public static TextureInfo concreteTexture1;
    public static TextureInfo concreteTexture2;
    public static TextureInfo missingTexture;
    public static TextureInfo dirtTexture1;
    public static TextureInfo dirtTexture2;
    public static TextureInfo grassTexture1;
    public static TextureInfo letterA;
    public static TextureInfo letterB;
    public static TextureInfo letterC;
    public static TextureInfo letterD;
    public static TextureInfo letterE;
    public static TextureInfo letterF;
    public static TextureInfo letterG;
    public static TextureInfo letterH;
    public static TextureInfo letterI;
    public static TextureInfo letterJ;
    public static TextureInfo letterK;
    public static TextureInfo letterL;
    public static TextureInfo letterM;
    public static TextureInfo letterN;
    public static TextureInfo letterO;
    public static TextureInfo letterP;
    public static TextureInfo letterQ;
    public static TextureInfo letterR;
    public static TextureInfo letterS;
    public static TextureInfo letterT;
    public static TextureInfo letterU;
    public static TextureInfo letterV;
    public static TextureInfo letterW;
    public static TextureInfo letterX;
    public static TextureInfo letterY;
    public static TextureInfo letterZ;
    public static TextureInfo letterSPACE;






    /**Init the textures used by the rest of the project*/
    public static void initTextures(){
        concreteTexture1 = loadTexture(PackInfoParser.concreteTexture1);
        missingTexture = loadTexture(PackInfoParser.missingTexture);
        floorTexture = loadTexture(PackInfoParser.floorTexture);
        playerTexture = loadTexture(PackInfoParser.playerTexture);
        splatterTexture = loadTexture(PackInfoParser.splatterTexture);
        concreteTexture2 = loadTexture(PackInfoParser.concreteTexture2);
        dirtTexture1 = loadTexture((PackInfoParser.dirtTexture1));
        dirtTexture2 = loadTexture((PackInfoParser.dirtTexture2));
        grassTexture1 = loadTexture((PackInfoParser.grassTexture1));
        letterA = loadTexture((PackInfoParser.letterA));
        letterB = loadTexture(PackInfoParser.letterB);
        letterC = loadTexture((PackInfoParser.letterC));
        letterD = loadTexture(PackInfoParser.letterD);
        letterE = loadTexture((PackInfoParser.letterE));
        letterF = loadTexture(PackInfoParser.letterF);
        letterG = loadTexture((PackInfoParser.letterG));
        letterH = loadTexture(PackInfoParser.letterH);
        letterI = loadTexture((PackInfoParser.letterI));
        letterJ = loadTexture(PackInfoParser.letterJ);
        letterK = loadTexture((PackInfoParser.letterK));
        letterL = loadTexture(PackInfoParser.letterL);
        letterM = loadTexture((PackInfoParser.letterM));
        letterN = loadTexture(PackInfoParser.letterN);
        letterO = loadTexture((PackInfoParser.letterO));
        letterP = loadTexture(PackInfoParser.letterP);
        letterQ = loadTexture((PackInfoParser.letterQ));
        letterR = loadTexture(PackInfoParser.letterR);
        letterS = loadTexture((PackInfoParser.letterS));
        letterT = loadTexture(PackInfoParser.letterT);
        letterU = loadTexture((PackInfoParser.letterU));
        letterV = loadTexture(PackInfoParser.letterV);
        letterW = loadTexture((PackInfoParser.letterW));
        letterX = loadTexture(PackInfoParser.letterX);
        letterY = loadTexture((PackInfoParser.letterY));
        letterZ = loadTexture(PackInfoParser.letterZ);
        letterSPACE = loadTexture(PackInfoParser.letterSPACE);
    }

    public static TextureInfo loadTexture(String filePath) {
        int width, height;
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            // Load the image with RGBA channels
            image = stbi_load(filePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + filePath + " - " + stbi_failure_reason());
            }

            width = widthBuffer.get();
            height = heightBuffer.get();
            long fileSize = FileUtils.getFileSize(filePath);
            Logger.printLOG(String.format("Loaded texture: %s (Width: %d, Height: %d, File Size: %d bytes)", filePath, width, height, fileSize));
        } catch (Exception e) {
            throw new RuntimeException("Error loading texture: " + filePath, e);
        }

        // Generate and configure the texture
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        try {
            // Upload the texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        } finally {
            // Free the image memory
            stbi_image_free(image);
        }

        return new TextureInfo(textureId, width, height);
    }

    public static TextureInfo getTexture(char textureMapChar) {
        switch (textureMapChar) {
            case ':':
                return floorTexture;
            case '+':
                return concreteTexture1;
            case '-':
                return concreteTexture2;
            case '1':
                return dirtTexture1;
            case '2':
                return dirtTexture2;
            case '3':
                return grassTexture1;
            case '`':
                return playerTexture;
            case '~':
                return splatterTexture;
            default:
                return missingTexture;
        }
    }

}
