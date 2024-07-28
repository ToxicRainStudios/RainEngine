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


    public void handleCollisions(float sizeOfObject1,CollisionUtils instance, float positionX, float positionY, float sizeX, float sizeY, float posX2, float posY2, char player_LeaveQIfNo) {

        instance.changePosY = 0;
        instance.changePosX = 0;
        instance.isColliding = false;
        float extentTop = posY2 + sizeY;
        float extentBottom = posY2 - sizeY;
        float extentRight = posX2 + sizeX;
        float extentLeft = posX2 - sizeX;





            if (positionY + sizeOfObject1<= extentTop && (positionY + sizeOfObject1>= posY2)) {
                if (positionX + sizeOfObject1>= extentLeft && !(positionX + sizeOfObject1>= posX2)) {
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
                } else if ((positionX + sizeOfObject1<= extentRight) && !(positionX + sizeOfObject1<= posX2)) {
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
            if (positionY + sizeOfObject1>= extentBottom && (positionY + sizeOfObject1<= posY2)) {
                if (positionX + sizeOfObject1>= extentLeft && !(positionX + sizeOfObject1>= posX2)) {
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
                } else if ((positionX + sizeOfObject1<= extentRight) && !(positionX + sizeOfObject1<= posX2)) {
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
            if ((positionX + sizeOfObject1<= extentRight) && (positionX + sizeOfObject1>= posX2)) {
                if ((positionY + sizeOfObject1>= extentBottom && !(positionY + sizeOfObject1> posY2))) {
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
                } else if ((positionY + sizeOfObject1<= extentTop) && !(positionY + sizeOfObject1<= posY2)) {
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
            if ((positionX + sizeOfObject1>= extentLeft) && (positionX + sizeOfObject1<= posX2)) {
                if ((positionY + sizeOfObject1>= extentBottom) && !(positionY + sizeOfObject1>= posY2)) {
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
                } else if ((positionY + sizeOfObject1<= extentTop) && !(positionY + sizeOfObject1<= posY2)) {
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


