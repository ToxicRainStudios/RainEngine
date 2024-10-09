# RainEngine

RainEngine is a WIP engine made with [LWJGL3](https://www.lwjgl.org/)


## What's done:
* Parsing general game information from gameinfo.json files
* Basic texture rendering
* Basic Map Loading
  * Now with the ability to "layer" textures in maps for more detail
* A basic "player"
* Keybinding system
* Lighting
* "Palette" system for controlling what's what in a map file
* A File Editor
* Dynamic Texture loading
  * Simply place the image in the images folder, then use it 
* [Lua](https://www.lua.org/) scripting
  * Check if a key is pressed
  * Load maps
  * Make Imgui windows
  * Work with files
  * Merge scripts
  * Run more scripts
  * Script prefixes to de-hardcore scripting 
* NPCs with BehaviorSequence's


## What's not done:
* More control over the window title
  * Dynamically changing the title
* UI with configurability
  * Main Menu
  * Settings Menu (Edit Keybindings etc)
  * Pause Menu
* Some sort of dialog system with JSON
* Dynamic and directional lighting 