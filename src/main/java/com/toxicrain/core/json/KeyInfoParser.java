package com.toxicrain.core.json;

import com.toxicrain.core.Logger;
import com.toxicrain.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFW;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

/**
 * KeyInfoParser parsers the keybinds.json file
 * needed for game functionality
 */
public class KeyInfoParser {
    public static String keySprint = null;
    public static String keyWalkForward = null;
    public static String keyWalkBackward = null;
    public static String keyWalkLeft = null;
    public static String keyWalkRight = null;

    /**
     * Loads the keybinds.json and parsers it into variables
     */
    public static void loadKeyInfo() {
        String filePath = FileUtils.getCurrentWorkingDirectory("resources/json/keybinds.json");

        try {
            // Read the file content into a string
            String jsonString = FileUtils.readFile(filePath);

            // Parse the JSON string into a JSONArray
            JSONArray jsonArray = new JSONArray(jsonString);

            // Iterate through the array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Get the values array
                JSONArray valuesArray = jsonObject.getJSONArray("values");
                for (int j = 0; j < valuesArray.length(); j++) {
                    JSONObject valueObject = valuesArray.getJSONObject(j);

                    // Use traditional for-each loop instead of lambda
                    Iterator<String> keys = valueObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = valueObject.getString(key);
                        switch (key) {
                            case "keySprint":
                                keySprint = value;
                                break;
                            case "keyWalkForward":
                                keyWalkForward = value;
                                break;
                            case "keyWalkBackward":
                                keyWalkBackward = value;
                                break;
                            case "keyWalkLeft":
                                keyWalkLeft = value;
                                break;
                            case "keyWalkRight":
                                keyWalkRight = value;
                                break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.printERROR("File not found: " + filePath);
            e.printStackTrace();
        }
        catch (IOException e) {
            Logger.printERROR("Error reading file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            Logger.printERROR("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int convertToGLFWBind(String bind) {
        switch (bind) {
            case "key_space":
                return GLFW.GLFW_KEY_SPACE;
            case "key_apostrophe":
                return GLFW.GLFW_KEY_APOSTROPHE;
            case "key_comma":
                return GLFW.GLFW_KEY_COMMA;
            case "key_minus":
                return GLFW.GLFW_KEY_MINUS;
            case "key_period":
                return GLFW.GLFW_KEY_PERIOD;
            case "key_slash":
                return GLFW.GLFW_KEY_SLASH;
            case "key_0":
                return GLFW.GLFW_KEY_0;
            case "key_1":
                return GLFW.GLFW_KEY_1;
            case "key_2":
                return GLFW.GLFW_KEY_2;
            case "key_3":
                return GLFW.GLFW_KEY_3;
            case "key_4":
                return GLFW.GLFW_KEY_4;
            case "key_5":
                return GLFW.GLFW_KEY_5;
            case "key_6":
                return GLFW.GLFW_KEY_6;
            case "key_7":
                return GLFW.GLFW_KEY_7;
            case "key_8":
                return GLFW.GLFW_KEY_8;
            case "key_9":
                return GLFW.GLFW_KEY_9;
            case "key_semicolon":
                return GLFW.GLFW_KEY_SEMICOLON;
            case "key_equal":
                return GLFW.GLFW_KEY_EQUAL;
            case "key_a":
                return GLFW.GLFW_KEY_A;
            case "key_b":
                return GLFW.GLFW_KEY_B;
            case "key_c":
                return GLFW.GLFW_KEY_C;
            case "key_d":
                return GLFW.GLFW_KEY_D;
            case "key_e":
                return GLFW.GLFW_KEY_E;
            case "key_f":
                return GLFW.GLFW_KEY_F;
            case "key_g":
                return GLFW.GLFW_KEY_G;
            case "key_h":
                return GLFW.GLFW_KEY_H;
            case "key_i":
                return GLFW.GLFW_KEY_I;
            case "key_j":
                return GLFW.GLFW_KEY_J;
            case "key_k":
                return GLFW.GLFW_KEY_K;
            case "key_l":
                return GLFW.GLFW_KEY_L;
            case "key_m":
                return GLFW.GLFW_KEY_M;
            case "key_n":
                return GLFW.GLFW_KEY_N;
            case "key_o":
                return GLFW.GLFW_KEY_O;
            case "key_p":
                return GLFW.GLFW_KEY_P;
            case "key_q":
                return GLFW.GLFW_KEY_Q;
            case "key_r":
                return GLFW.GLFW_KEY_R;
            case "key_s":
                return GLFW.GLFW_KEY_S;
            case "key_t":
                return GLFW.GLFW_KEY_T;
            case "key_u":
                return GLFW.GLFW_KEY_U;
            case "key_v":
                return GLFW.GLFW_KEY_V;
            case "key_w":
                return GLFW.GLFW_KEY_W;
            case "key_x":
                return GLFW.GLFW_KEY_X;
            case "key_y":
                return GLFW.GLFW_KEY_Y;
            case "key_z":
                return GLFW.GLFW_KEY_Z;
            case "key_left_bracket":
                return GLFW.GLFW_KEY_LEFT_BRACKET;
            case "key_backslash":
                return GLFW.GLFW_KEY_BACKSLASH;
            case "key_right_bracket":
                return GLFW.GLFW_KEY_RIGHT_BRACKET;
            case "key_grave_accent":
                return GLFW.GLFW_KEY_GRAVE_ACCENT;
            case "key_world_1":
                return GLFW.GLFW_KEY_WORLD_1;
            case "key_world_2":
                return GLFW.GLFW_KEY_WORLD_2;
            case "key_escape":
                return GLFW.GLFW_KEY_ESCAPE;
            case "key_enter":
                return GLFW.GLFW_KEY_ENTER;
            case "key_tab":
                return GLFW.GLFW_KEY_TAB;
            case "key_backspace":
                return GLFW.GLFW_KEY_BACKSPACE;
            case "key_insert":
                return GLFW.GLFW_KEY_INSERT;
            case "key_delete":
                return GLFW.GLFW_KEY_DELETE;
            case "key_right":
                return GLFW.GLFW_KEY_RIGHT;
            case "key_left":
                return GLFW.GLFW_KEY_LEFT;
            case "key_down":
                return GLFW.GLFW_KEY_DOWN;
            case "key_up":
                return GLFW.GLFW_KEY_UP;
            case "key_page_up":
                return GLFW.GLFW_KEY_PAGE_UP;
            case "key_page_down":
                return GLFW.GLFW_KEY_PAGE_DOWN;
            case "key_home":
                return GLFW.GLFW_KEY_HOME;
            case "key_end":
                return GLFW.GLFW_KEY_END;
            case "key_caps_lock":
                return GLFW.GLFW_KEY_CAPS_LOCK;
            case "key_scroll_lock":
                return GLFW.GLFW_KEY_SCROLL_LOCK;
            case "key_num_lock":
                return GLFW.GLFW_KEY_NUM_LOCK;
            case "key_print_screen":
                return GLFW.GLFW_KEY_PRINT_SCREEN;
            case "key_pause":
                return GLFW.GLFW_KEY_PAUSE;
            case "key_f1":
                return GLFW.GLFW_KEY_F1;
            case "key_f2":
                return GLFW.GLFW_KEY_F2;
            case "key_f3":
                return GLFW.GLFW_KEY_F3;
            case "key_f4":
                return GLFW.GLFW_KEY_F4;
            case "key_f5":
                return GLFW.GLFW_KEY_F5;
            case "key_f6":
                return GLFW.GLFW_KEY_F6;
            case "key_f7":
                return GLFW.GLFW_KEY_F7;
            case "key_f8":
                return GLFW.GLFW_KEY_F8;
            case "key_f9":
                return GLFW.GLFW_KEY_F9;
            case "key_f10":
                return GLFW.GLFW_KEY_F10;
            case "key_f11":
                return GLFW.GLFW_KEY_F11;
            case "key_f12":
                return GLFW.GLFW_KEY_F12;
            case "key_f13":
                return GLFW.GLFW_KEY_F13;
            case "key_f14":
                return GLFW.GLFW_KEY_F14;
            case "key_f15":
                return GLFW.GLFW_KEY_F15;
            case "key_f16":
                return GLFW.GLFW_KEY_F16;
            case "key_f17":
                return GLFW.GLFW_KEY_F17;
            case "key_f18":
                return GLFW.GLFW_KEY_F18;
            case "key_f19":
                return GLFW.GLFW_KEY_F19;
            case "key_f20":
                return GLFW.GLFW_KEY_F20;
            case "key_f21":
                return GLFW.GLFW_KEY_F21;
            case "key_f22":
                return GLFW.GLFW_KEY_F22;
            case "key_f23":
                return GLFW.GLFW_KEY_F23;
            case "key_f24":
                return GLFW.GLFW_KEY_F24;
            case "key_f25":
                return GLFW.GLFW_KEY_F25;
            case "key_kp_0":
                return GLFW.GLFW_KEY_KP_0;
            case "key_kp_1":
                return GLFW.GLFW_KEY_KP_1;
            case "key_kp_2":
                return GLFW.GLFW_KEY_KP_2;
            case "key_kp_3":
                return GLFW.GLFW_KEY_KP_3;
            case "key_kp_4":
                return GLFW.GLFW_KEY_KP_4;
            case "key_kp_5":
                return GLFW.GLFW_KEY_KP_5;
            case "key_kp_6":
                return GLFW.GLFW_KEY_KP_6;
            case "key_kp_7":
                return GLFW.GLFW_KEY_KP_7;
            case "key_kp_8":
                return GLFW.GLFW_KEY_KP_8;
            case "key_kp_9":
                return GLFW.GLFW_KEY_KP_9;
            case "key_kp_decimal":
                return GLFW.GLFW_KEY_KP_DECIMAL;
            case "key_kp_divide":
                return GLFW.GLFW_KEY_KP_DIVIDE;
            case "key_kp_multiply":
                return GLFW.GLFW_KEY_KP_MULTIPLY;
            case "key_kp_subtract":
                return GLFW.GLFW_KEY_KP_SUBTRACT;
            case "key_kp_add":
                return GLFW.GLFW_KEY_KP_ADD;
            case "key_kp_enter":
                return GLFW.GLFW_KEY_KP_ENTER;
            case "key_kp_equal":
                return GLFW.GLFW_KEY_KP_EQUAL;
            case "key_left_shift":
                return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "key_left_control":
                return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "key_left_alt":
                return GLFW.GLFW_KEY_LEFT_ALT;
            case "key_left_super":
                return GLFW.GLFW_KEY_LEFT_SUPER;
            case "key_right_shift":
                return GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "key_right_control":
                return GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "key_right_alt":
                return GLFW.GLFW_KEY_RIGHT_ALT;
            case "key_right_super":
                return GLFW.GLFW_KEY_RIGHT_SUPER;
            case "key_menu":
                return GLFW.GLFW_KEY_MENU;
            case "gamepad_a":
                return GLFW.GLFW_GAMEPAD_BUTTON_A;
            case "gamepad_b":
                return GLFW.GLFW_GAMEPAD_BUTTON_B;
            case "gamepad_x":
                return GLFW.GLFW_GAMEPAD_BUTTON_X;
            case "gamepad_y":
                return GLFW.GLFW_GAMEPAD_BUTTON_Y;
            case "gamepad_left_bumper":
                return GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
            case "gamepad_right_bumper":
                return GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;
            case "gamepad_back":
                return GLFW.GLFW_GAMEPAD_BUTTON_BACK;
            case "gamepad_start":
                return GLFW.GLFW_GAMEPAD_BUTTON_START;
            case "gamepad_left_thumb":
                return GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB;
            case "gamepad_right_thumb":
                return GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB;
            case "gamepad_dpad_up":
                return GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;
            case "gamepad_dpad_right":
                return GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT;
            case "gamepad_dpad_down":
                return GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN;
            case "gamepad_dpad_left":
                return GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
            default:
                return GLFW.GLFW_KEY_UNKNOWN;
        }
    }


}
