package com.toxicrain.rainengine.core.eventbus.events.render.shader;

import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;
import lombok.AllArgsConstructor;

public class CreateShaderProgramEvent {
    public final String name;
    public final String vertexShaderPath;
    public final String fragmentShaderPath;


    public CreateShaderProgramEvent(Resource resource){

        String namespace = resource.getNamespace();
        String path = resource.getPath();

        this.name = path;
        this.vertexShaderPath = "resources/shaders/"+namespace+"/"+path+"/"+path+".vert";
        this.fragmentShaderPath = "resources/shaders/"+namespace+"/"+path+"/"+path+".frag";

        RainLogger.RAIN_LOGGER.info("Loading Shader: {}", resource);

    }
}
