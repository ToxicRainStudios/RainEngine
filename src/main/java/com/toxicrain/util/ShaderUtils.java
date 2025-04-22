package com.toxicrain.util;

import com.toxicrain.core.RainLogger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShaderUtils {

    public int loadShader(int type, String filePath) {
        String shaderSource;
        try {
            shaderSource = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader file!", e);
        }

        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, shaderSource);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Failed to compile shader: " + GL20.glGetShaderInfoLog(shader));
        }

        return shader;
    }

    public int createShaderProgram(String vertexShaderPath, String fragmentShaderPath) {
        RainLogger.RAIN_LOGGER.info("Loading Vertex Shader: " + vertexShaderPath);
        RainLogger.RAIN_LOGGER.info("Loading Fragment Shader: " + fragmentShaderPath);

        int vertexShader = loadShader(GL20.GL_VERTEX_SHADER, vertexShaderPath);
        int fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderPath);

        int shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);

        // Check for linking errors
        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String errorLog = GL20.glGetProgramInfoLog(shaderProgram);
            GL20.glDeleteProgram(shaderProgram);
            throw new RuntimeException("Failed to link shader program: " + errorLog);
        }

        // Optionally detach and delete shaders after linking
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        return shaderProgram;
    }

}