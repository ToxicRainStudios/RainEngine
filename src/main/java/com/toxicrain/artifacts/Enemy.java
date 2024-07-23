package com.toxicrain.artifacts;

import com.toxicrain.artifacts.Player;



public class Enemy {

    public float x;
    public float y;
    public float z;
    public float rotX;
    public float rotY;



    public Enemy(float cordX, float cordY, float cordZ){
        x = cordX;
        y = cordY;
        z = cordZ;
        rotX = Player.playerX;
        rotY = Player.playerY;
    }
    public void Ai(){
if(Player.playerX > x){
    x += 0.005f;
}
if(Player.playerX < x){
    x -= 0.005f;
}
if(Player.playerY > y){
    y += 0.005f;
}
if(Player.playerY < y){
    y -= 0.005f;
}


    }



}
