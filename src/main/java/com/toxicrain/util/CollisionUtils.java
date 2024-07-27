package com.toxicrain.util;

import com.toxicrain.core.json.GameInfoParser;
import com.toxicrain.core.json.MapInfoParser;
import com.toxicrain.core.render.Tile;

public class CollisionUtils {

    public boolean isColliding;
    public float changePosY = 0;
    public float changePosX = 0;
    public float collisionType;

    public CollisionUtils() {


    }


    public void handleCollisions(float k,CollisionUtils instance, float positionX, float positionY, float size, float posX2, float posY2, char player_LeaveQIfNo) {

        instance.changePosY = 0;
        instance.changePosX = 0;
        instance.isColliding = false;
        float extentTop = posY2 + size;
        float extentBottom = posY2 - size;
        float extentRight = posX2 + size;
        float extentLeft = posX2 - size;





            if (positionY + k <= extentTop && (positionY + k >= posY2)) {
                if (positionX + k >= extentLeft && !(positionX + k >= posX2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosY = 0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;

                    }
                } else if ((positionX + k <= extentRight) && !(positionX + k <= posX2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosY = 0.02f;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;
                    }
                }
            }
            if (positionY + k >= extentBottom && (positionY + k <= posY2)) {
                if (positionX + k >= extentLeft && !(positionX + k >= posX2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosY = -0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;
                    }
                } else if ((positionX + k <= extentRight) && !(positionX + k <= posX2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosY = -0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;
                    }
                }
            }
            //yay half way done! (ive been doing this for 3 hours :sob:
            if ((positionX + k <= extentRight) && (positionX + k >= posX2)) {
                if ((positionY + k >= extentBottom && !(positionY + k > posY2))) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosX = 0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;
                    }
                } else if ((positionY + k <= extentTop) && !(positionY + k <= posY2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosX = 0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;
                    }
                }

            }
            if ((positionX + k >= extentLeft) && (positionX + k <= posX2)) {
                if ((positionY + k >= extentBottom) && !(positionY + k >= posY2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosX = -0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }
                        }
                    } else {
                        instance.isColliding = true;
                    }
                } else if ((positionY + k <= extentTop) && !(positionY + k <= posY2)) {
                    if (player_LeaveQIfNo != 'Q') {
                        for (int p = MapInfoParser.doCollide.size() - 1; p >= 0; p--) {
                            if (player_LeaveQIfNo == MapInfoParser.doCollide.get(p)) {
                                instance.changePosX -= 0.02f;
                                instance.isColliding = true;
                                break;
                            }
                            if (player_LeaveQIfNo == '1') {
                                instance.collisionType = 1;
                                break;
                            }

                        }
                    } else {
                        instance.isColliding = true;
                    }
                }
            }


        }
    }


