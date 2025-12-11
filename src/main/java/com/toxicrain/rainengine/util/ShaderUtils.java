package com.toxicrain.rainengine.util;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.core.eventbus.events.render.CreateShaderProgramEvent;
import com.toxicrain.rainengine.core.logging.RainLogger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ShaderUtils extends BaseInstanceable<ShaderUtils> {

    private ShaderUtils(){
        SmeagleBus.getInstance().listen(CreateShaderProgramEvent.class)
                .subscribe(event -> {
                    createShaderProgram(event.name, event.vertexShaderPath, event.fragmentShaderPath);
                });
    }

    private final Map<String, Integer> shaderPrograms = new HashMap<>();


    public static ShaderUtils getInstance(){
        return BaseInstanceable.getInstance(ShaderUtils.class);
    }

    private int loadShader(int type, String filePath) {
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

    private int createShaderProgram(String name, String vertexShaderPath, String fragmentShaderPath) {
        RainLogger.RAIN_LOGGER.info("Loading Vertex Shader: " + vertexShaderPath);
        RainLogger.RAIN_LOGGER.info("Loading Fragment Shader: " + fragmentShaderPath);

        int vertexShader = loadShader(GL20.GL_VERTEX_SHADER, vertexShaderPath);
        int fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderPath);

        int shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);

        if (GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Failed to link shader program: " + GL20.glGetProgramInfoLog(shaderProgram));
        }

        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);

        shaderPrograms.put(name, shaderProgram);
        return shaderProgram;
    }

    public int getShader(String name) {
        Integer program = shaderPrograms.get(name);
        if (program == null)
            throw new IllegalArgumentException("Shader program '" + name + "' not found!");
        return program;
    }

    public int getUniformLocation(String name, String uniformName){
        return GL20.glGetUniformLocation(getShader(name), uniformName);
    }

    public void useProgram(String name){
        GL20.glUseProgram(getShader(name));
    }


}