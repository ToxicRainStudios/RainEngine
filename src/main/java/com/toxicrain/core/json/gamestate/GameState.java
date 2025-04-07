package com.toxicrain.core.json.gamestate;

public class GameState {
    public String playerName;
    public int playerX;
    public int playerY;
    public float playerHealth;


    // Constructor
    public GameState(String playerName, int playerX, int playerY, float playerHealth) {
        this.playerName = playerName;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerHealth = playerHealth;
    }

    // Default constructor
    public GameState() {
        this.playerName = "Player1";
        this.playerX = 1;
        this.playerY = 1;
        this.playerHealth = 100;
    }
}
