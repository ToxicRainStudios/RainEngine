package com.toxicrain.rainengine.artifacts;

import com.github.strubium.smeaglebus.eventbus.SmeagleBus;
import com.toxicrain.rainengine.core.datatypes.AABB;
import com.toxicrain.rainengine.core.datatypes.Resource;
import com.toxicrain.rainengine.core.eventbus.events.ArtifactUpdateEvent;
import com.toxicrain.rainengine.core.logging.RainLogger;
import com.toxicrain.rainengine.util.DeltaTimeUtil;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

public class CollideableRenderableArtifact extends RenderableArtifact {

    @Getter
    private final AABB aabb;
    private int gridX, gridY;
    private final float cellSize = 10f;

    private static List<CollideableRenderableArtifact>[][] grid = new ArrayList[10][10];
    private static boolean initialized;

    public CollideableRenderableArtifact(Resource textureResource, float x, float y, float rotation, AABB aabb) {
        super(textureResource, x, y, rotation);
        this.aabb = aabb;

        SmeagleBus.getInstance().listen(ArtifactUpdateEvent.class)
                .subscribe(event -> {
                    if (event.artifact instanceof CollideableRenderableArtifact){ //TODO ofc none of this works but heres how it would go
                        if(handleCollisions((float) DeltaTimeUtil.getDeltaTime())){
                            RainLogger.RAIN_LOGGER.info("Name: " + event.artifact.getClass());
                            event.setCanceled(true); //Artifact going to collide, cancel event to prevent movement
                        }

                    }
                });

        gridX = Math.max(0, Math.min((int) Math.floor(x / cellSize), grid.length - 1));
        gridY = Math.max(0, Math.min((int) Math.floor(y / cellSize), grid[0].length - 1));


        grid[gridX][gridY].add(this);

    }

    private boolean handleCollisions(float deltaTime)
    {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = gridX + i;
                int newY = gridY + j;

                if (newX < 0 || newY < 0 || newX >= grid.length || newY >= grid[0].length)
                    continue;

                for (CollideableRenderableArtifact other : grid[newX][newY]) {
                    if (other != this && this.aabb.intersects(other.aabb)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean initializeCollisions(){
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                grid[x][y] = new ArrayList<>();
            }
        }
        return true;
    }
    private void updateGridPosition(float x, float y) {
        int newGridX = (int)(x / cellSize);
        int newGridY = (int)(y / cellSize);

        newGridX = Math.max(0, Math.min(newGridX, grid.length - 1));
        newGridY = Math.max(0, Math.min(newGridY, grid[0].length - 1));

        if (newGridX != gridX || newGridY != gridY) {
            grid[gridX][gridY].remove(this);
            grid[newGridX][newGridY].add(this);
            gridX = newGridX;
            gridY = newGridY;
        }
    }

}
