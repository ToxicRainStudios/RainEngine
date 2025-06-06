# RainEngine

RainEngine is a WIP engine made with [LWJGL3](https://www.lwjgl.org/)


## Features:
* Parsing general game information from gameinfo.json files
* Texture rendering
* Dynamic Texture loading
  * Simply place the image in the images folder, then use it
* Map Loading
  * Now with the ability to "layer" textures in maps for more detail
  * "Palette" system for controlling what's what in a map file
* Keybinding system
* Lighting
* Gui with Imgui
* [Lua](https://www.lua.org/) scripting
  * Check if a key is pressed
  * Load maps
  * Make Imgui windows
  * Work with files
  * Merge scripts
  * Run more scripts
  * Script prefixes to de-hardcore scripting 
  * Dynamically changing the window title
* NPCs with BehaviorSequence's
* Localization
* Event Bus 
  * Listen to an event from anywhere

## What's not done:
* UI with configurability
  * Main Menu
  * Settings Menu (Edit Keybindings etc)
  * Pause Menu
* Some sort of dialog system with JSON or Lua
* Dynamic and directional lighting 
