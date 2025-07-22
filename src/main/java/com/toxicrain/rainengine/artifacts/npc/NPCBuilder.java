package com.toxicrain.rainengine.artifacts.npc;

import com.toxicrain.rainengine.artifacts.behavior.BehaviorSequence;
import com.toxicrain.rainengine.core.datatypes.Resource;

public class NPCBuilder {

    private float x = 0f;
    private float y = 0f;
    private float rotation = 0f;
    private float size = 1f;
    private float fieldOfViewAngle = 90f;
    private float visionDistance = 300f;
    private String texture = "npcTexture";
    private BehaviorSequence behaviorSequence = new BehaviorSequence(); // Default behavior

    public NPCBuilder position(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public NPCBuilder rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public NPCBuilder size(float size) {
        this.size = size;
        return this;
    }

    public NPCBuilder fieldOfView(float angle) {
        this.fieldOfViewAngle = angle;
        return this;
    }

    public NPCBuilder visionDistance(float distance) {
        this.visionDistance = distance;
        return this;
    }

    public NPCBuilder texture(String resourcePath) {
        this.texture = resourcePath;
        return this;
    }

    public NPCBuilder behavior(BehaviorSequence behaviorSequence) {
        this.behaviorSequence = behaviorSequence;
        return this;
    }

    public NPC build() {
        NPC npc = new NPC(x, y, rotation, size);
        npc.setFieldOfViewAngle(fieldOfViewAngle);
        npc.setVisionDistance(visionDistance);
        npc.setBehaviorSequence(behaviorSequence);
        npc.setTextureResource(new Resource(texture));
        return npc;
    }
}
