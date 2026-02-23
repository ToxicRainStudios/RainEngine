package com.toxicrain.rainengine.core;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.github.strubium.windowmanager.window.WindowManager;
import com.toxicrain.rainengine.artifacts.Camera;
import com.toxicrain.rainengine.core.eventbus.RainBusListener;
import com.toxicrain.rainengine.core.eventbus.events.*;
import com.toxicrain.rainengine.core.eventbus.events.load.LoadEvent;
import com.toxicrain.rainengine.core.eventbus.events.render.AddRenderPassEvent;
import com.toxicrain.rainengine.core.json.*;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.core.render.lowlevel.BatchRenderer;
import com.toxicrain.rainengine.core.render.rendering.Renderer;
import com.toxicrain.rainengine.gui.ImguiSystem;
import com.toxicrain.rainengine.sound.SoundSystem;
import com.toxicrain.rainengine.util.DeltaTimeUtil;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;


@UtilityClass
public class GameEngine {

    // The window handle
    public static WindowManager windowManager;
    private Camera camera;

    public static void run() {
        Thread.setDefaultUncaughtExceptionHandler(CrashReporter.getInstance());
        RainLogger.buildLoggers();

        RainLogger.RAIN_LOGGER.info("Hello LWJGL {}!", Version.getVersion());
        RainLogger.RAIN_LOGGER.info("Hello RainEngine " + Constants.engineVersion + "!");
        RainLogger.RAIN_LOGGER.info("Running: {} by {}", GameInfoParser.getInstance().gameName, GameInfoParser.getInstance().gameMakers);
        RainLogger.RAIN_LOGGER.info("Version: {}", GameInfoParser.getInstance().gameVersion);
        doVersionCheck();

        RainLogger.RAIN_LOGGER.info("Loading Event Bus");
        RainBusListener.addEventListeners();

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.PRE));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.ININT));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.MANAGER));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.POST));

        SmeagleBus.getInstance().post(new LoadEvent(LoadEvent.LoadEventStage.GUI));

        // Create the batch renderer
        BatchRenderer batchRenderer = new BatchRenderer();
        Renderer renderer = new Renderer(batchRenderer);

        SmeagleBus.getInstance().post(new AddRenderPassEvent(renderer));

        loop(renderer);

        // Free the window callbacks and destroy the window
        windowManager.destroy();
    }

    private static void render(Renderer renderer, Camera camera) {
        // Clear color and depth buffers (still done once per frame)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Update the view matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        FloatBuffer cameraBuffer = BufferUtils.createFloatBuffer(16);
        camera.getViewMatrix().get(cameraBuffer);
        glLoadMatrixf(cameraBuffer);

        // Execute all registered render passes
        renderer.render(camera);

        // Swap buffers and poll window events
        windowManager.swapAndPoll();
    }

    public static boolean gamePaused = true;

    private static void loop(Renderer renderer) {
        // Get initial window size
        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowSize(windowManager.window, widthBuffer, heightBuffer);
        int windowWidth = widthBuffer.get(0);
        int windowHeight = heightBuffer.get(0);

        // Create camera with correct aspect ratio
        camera = new Camera(
                70f,
                (float) windowWidth / windowHeight,
                0.1f,
                1000f
        );

        while (!windowManager.shouldClose()) {
            DeltaTimeUtil.update();
            SmeagleBus.getInstance().post(new GameUpdateEvent(gamePaused, camera));


            // update camera aspect if window resized
            widthBuffer.clear();
            heightBuffer.clear();
            GLFW.glfwGetWindowSize(windowManager.window, widthBuffer, heightBuffer);
            int newWidth = widthBuffer.get(0);
            int newHeight = heightBuffer.get(0);
            if (newWidth != windowWidth || newHeight != windowHeight) {
                windowWidth = newWidth;
                windowHeight = newHeight;
                camera.setAspectRatio((float) windowWidth / windowHeight);
            }

            render(renderer, camera);
        }

        ImguiSystem.getInstance().getImguiApp().cleanup();
        SoundSystem.getInstance().cleanup();
    }


    /**
     * Checks the internal engine version with what gameinfo.json is asking for
     */
    private static void doVersionCheck() {
        if (Constants.engineVersion.equals(GameInfoParser.getInstance().engineVersion)) {
            RainLogger.RAIN_LOGGER.info("Engine Version check: Pass");
        } else {
            RainLogger.RAIN_LOGGER.error("Engine Version check: FAIL");
            RainLogger.RAIN_LOGGER.error("Certain features may not work as intended");
        }
    }

    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

    /**
     * Creates a perspective projection matrix.
     *
     * @param fov the field of view angle in degrees
     * @param aspectRatio the aspect ratio of the viewport (width/height)
     * @param near the distance to the near clipping plane
     * @param far the distance to the far clipping plane
     * @return a FloatBuffer containing the perspective projection matrix
     */
    public static FloatBuffer createPerspectiveProjectionMatrix(float fov, float aspectRatio, float near, float far) {
        float f = (float) (1.0f / Math.tan(Math.toRadians(fov) / 2.0));
        float[] projectionMatrix = new float[16];

        projectionMatrix[0] = f / aspectRatio;
        projectionMatrix[1] = 0.0f;
        projectionMatrix[2] = 0.0f;
        projectionMatrix[3] = 0.0f;

        projectionMatrix[4] = 0.0f;
        projectionMatrix[5] = f;
        projectionMatrix[6] = 0.0f;
        projectionMatrix[7] = 0.0f;

        projectionMatrix[8] = 0.0f;
        projectionMatrix[9] = 0.0f;
        projectionMatrix[10] = (far + near) / (near - far);
        projectionMatrix[11] = -1.0f;

        projectionMatrix[12] = 0.0f;
        projectionMatrix[13] = 0.0f;
        projectionMatrix[14] = (2 * far * near) / (near - far);
        projectionMatrix[15] = 0.0f;

        buffer.put(projectionMatrix).flip();
        return buffer;
    }


    /**
     * Gets the perspective projection matrix.
     * @return The FloatBuffer containing the perspective projection matrix
     */
    public static FloatBuffer getPerspectiveProjectionMatrixBuffer() {
        return buffer;
    }

}
