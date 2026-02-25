package com.toxicrain.rainengine.core.eventbus.events.render.shader;

import com.github.strubium.smeaglebus.eventbus.CancelableEvent;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.logging.RainLogger;

public class CreateShaderProgramEvent extends CancelableEvent {
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
