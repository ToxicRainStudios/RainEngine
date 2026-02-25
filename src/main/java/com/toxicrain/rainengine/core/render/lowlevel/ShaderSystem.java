package com.toxicrain.rainengine.core.render.lowlevel;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.instanceable.BaseInstanceable;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.eventbus.events.render.shader.CreateShaderProgramEvent;
import com.toxicrain.rainengine.core.eventbus.events.render.shader.ShaderProgramCreatedEvent;
import com.toxicrain.rainengine.core.logging.RainLogger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ShaderSystem extends BaseInstanceable<ShaderSystem> {

    private ShaderSystem(){
        SmeagleBus.getInstance().listen(CreateShaderProgramEvent.class)
                .subscribe(event -> {
                    int program = createShaderProgram(event.name, event.vertexShaderPath, event.fragmentShaderPath);

                    SmeagleBus.getInstance().post(new ShaderProgramCreatedEvent(event.name, program));
                });
    }

    private final Map<String, Integer> SHADER_PROGRAMS = new HashMap<>();
    private final Map<String, Map<String, Integer>> SHADER_UNIFORM_CACHE = new HashMap<>();



    public static ShaderSystem getInstance(){
        return BaseInstanceable.getInstance(ShaderSystem.class);
    }

    public void loadShader(Resource shaderResource){
        SmeagleBus.getInstance().post(new CreateShaderProgramEvent(shaderResource));
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
        RainLogger.RAIN_LOGGER.debug("Loading Vertex Shader: {}", vertexShaderPath);
        RainLogger.RAIN_LOGGER.debug("Loading Fragment Shader: {}", fragmentShaderPath);

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

        SHADER_PROGRAMS.put(name, shaderProgram);
        return shaderProgram;
    }

    public int getShader(String name) {
        Integer program = SHADER_PROGRAMS.get(name);
        if (program == null)
            throw new IllegalArgumentException("Shader program '" + name + "' not found!");
        return program;
    }

    public int getUniformLocation(String shaderName, String uniformName) {
        SHADER_UNIFORM_CACHE.putIfAbsent(shaderName, new HashMap<>());
        Map<String, Integer> shaderUniforms = SHADER_UNIFORM_CACHE.get(shaderName);

        return shaderUniforms.computeIfAbsent(uniformName,
                u -> GL20.glGetUniformLocation(getShader(shaderName), u));
    }


    public void useProgram(String name){
        GL20.glUseProgram(getShader(name));
    }

    public void deleteShaderProgram(String name) {
        Integer program = SHADER_PROGRAMS.remove(name);
        if (program != null) GL20.glDeleteProgram(program);
    }



}