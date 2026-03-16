package com.toxicrain.rainengine.core.eventbus.events.load;

public class LangLoadEvent {

    public String langTag;
    public String correctedLangTag;

    public LangLoadEvent(String langTag){
        this.langTag = langTag;
        this.correctedLangTag = langTag.replace('_', '-');
    }
}
