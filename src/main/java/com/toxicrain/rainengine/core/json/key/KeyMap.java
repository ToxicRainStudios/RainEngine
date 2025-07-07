package com.toxicrain.rainengine.core.json.key;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyMap {
    private static final Map<String, Integer> nameToCode = new HashMap<>();
    private static final Map<Integer, String> codeToName = new HashMap<>();
    public static final Map<Integer, Runnable> keyBinds = new HashMap<>();

    static {
        register("key_space", GLFW.GLFW_KEY_SPACE);
        register("key_apostrophe", GLFW.GLFW_KEY_APOSTROPHE);
        register("key_comma", GLFW.GLFW_KEY_COMMA);
        register("key_minus", GLFW.GLFW_KEY_MINUS);
        register("key_period", GLFW.GLFW_KEY_PERIOD);
        register("key_slash", GLFW.GLFW_KEY_SLASH);
        register("key_0", GLFW.GLFW_KEY_0);
        register("key_1", GLFW.GLFW_KEY_1);
        register("key_2", GLFW.GLFW_KEY_2);
        register("key_3", GLFW.GLFW_KEY_3);
        register("key_4", GLFW.GLFW_KEY_4);
        register("key_5", GLFW.GLFW_KEY_5);
        register("key_6", GLFW.GLFW_KEY_6);
        register("key_7", GLFW.GLFW_KEY_7);
        register("key_8", GLFW.GLFW_KEY_8);
        register("key_9", GLFW.GLFW_KEY_9);
        register("key_semicolon", GLFW.GLFW_KEY_SEMICOLON);
        register("key_equal", GLFW.GLFW_KEY_EQUAL);
        register("key_a", GLFW.GLFW_KEY_A);
        register("key_b", GLFW.GLFW_KEY_B);
        register("key_c", GLFW.GLFW_KEY_C);
        register("key_d", GLFW.GLFW_KEY_D);
        register("key_e", GLFW.GLFW_KEY_E);
        register("key_f", GLFW.GLFW_KEY_F);
        register("key_g", GLFW.GLFW_KEY_G);
        register("key_h", GLFW.GLFW_KEY_H);
        register("key_i", GLFW.GLFW_KEY_I);
        register("key_j", GLFW.GLFW_KEY_J);
        register("key_k", GLFW.GLFW_KEY_K);
        register("key_l", GLFW.GLFW_KEY_L);
        register("key_m", GLFW.GLFW_KEY_M);
        register("key_n", GLFW.GLFW_KEY_N);
        register("key_o", GLFW.GLFW_KEY_O);
        register("key_p", GLFW.GLFW_KEY_P);
        register("key_q", GLFW.GLFW_KEY_Q);
        register("key_r", GLFW.GLFW_KEY_R);
        register("key_s", GLFW.GLFW_KEY_S);
        register("key_t", GLFW.GLFW_KEY_T);
        register("key_u", GLFW.GLFW_KEY_U);
        register("key_v", GLFW.GLFW_KEY_V);
        register("key_w", GLFW.GLFW_KEY_W);
        register("key_x", GLFW.GLFW_KEY_X);
        register("key_y", GLFW.GLFW_KEY_Y);
        register("key_z", GLFW.GLFW_KEY_Z);
        register("key_left_bracket", GLFW.GLFW_KEY_LEFT_BRACKET);
        register("key_backslash", GLFW.GLFW_KEY_BACKSLASH);
        register("key_right_bracket", GLFW.GLFW_KEY_RIGHT_BRACKET);
        register("key_grave_accent", GLFW.GLFW_KEY_GRAVE_ACCENT);
        register("key_world_1", GLFW.GLFW_KEY_WORLD_1);
        register("key_world_2", GLFW.GLFW_KEY_WORLD_2);
        register("key_escape", GLFW.GLFW_KEY_ESCAPE);
        register("key_enter", GLFW.GLFW_KEY_ENTER);
        register("key_tab", GLFW.GLFW_KEY_TAB);
        register("key_backspace", GLFW.GLFW_KEY_BACKSPACE);
        register("key_insert", GLFW.GLFW_KEY_INSERT);
        register("key_delete", GLFW.GLFW_KEY_DELETE);
        register("key_right", GLFW.GLFW_KEY_RIGHT);
        register("key_left", GLFW.GLFW_KEY_LEFT);
        register("key_down", GLFW.GLFW_KEY_DOWN);
        register("key_up", GLFW.GLFW_KEY_UP);
        register("key_page_up", GLFW.GLFW_KEY_PAGE_UP);
        register("key_page_down", GLFW.GLFW_KEY_PAGE_DOWN);
        register("key_home", GLFW.GLFW_KEY_HOME);
        register("key_end", GLFW.GLFW_KEY_END);
        register("key_caps_lock", GLFW.GLFW_KEY_CAPS_LOCK);
        register("key_scroll_lock", GLFW.GLFW_KEY_SCROLL_LOCK);
        register("key_num_lock", GLFW.GLFW_KEY_NUM_LOCK);
        register("key_print_screen", GLFW.GLFW_KEY_PRINT_SCREEN);
        register("key_pause", GLFW.GLFW_KEY_PAUSE);
        register("key_f1", GLFW.GLFW_KEY_F1);
        register("key_f2", GLFW.GLFW_KEY_F2);
        register("key_f3", GLFW.GLFW_KEY_F3);
        register("key_f4", GLFW.GLFW_KEY_F4);
        register("key_f5", GLFW.GLFW_KEY_F5);
        register("key_f6", GLFW.GLFW_KEY_F6);
        register("key_f7", GLFW.GLFW_KEY_F7);
        register("key_f8", GLFW.GLFW_KEY_F8);
        register("key_f9", GLFW.GLFW_KEY_F9);
        register("key_f10", GLFW.GLFW_KEY_F10);
        register("key_f11", GLFW.GLFW_KEY_F11);
        register("key_f12", GLFW.GLFW_KEY_F12);
        register("key_f13", GLFW.GLFW_KEY_F13);
        register("key_f14", GLFW.GLFW_KEY_F14);
        register("key_f15", GLFW.GLFW_KEY_F15);
        register("key_f16", GLFW.GLFW_KEY_F16);
        register("key_f17", GLFW.GLFW_KEY_F17);
        register("key_f18", GLFW.GLFW_KEY_F18);
        register("key_f19", GLFW.GLFW_KEY_F19);
        register("key_f20", GLFW.GLFW_KEY_F20);
        register("key_f21", GLFW.GLFW_KEY_F21);
        register("key_f22", GLFW.GLFW_KEY_F22);
        register("key_f23", GLFW.GLFW_KEY_F23);
        register("key_f24", GLFW.GLFW_KEY_F24);
        register("key_f25", GLFW.GLFW_KEY_F25);
        register("key_kp_0", GLFW.GLFW_KEY_KP_0);
        register("key_kp_1", GLFW.GLFW_KEY_KP_1);
        register("key_kp_2", GLFW.GLFW_KEY_KP_2);
        register("key_kp_3", GLFW.GLFW_KEY_KP_3);
        register("key_kp_4", GLFW.GLFW_KEY_KP_4);
        register("key_kp_5", GLFW.GLFW_KEY_KP_5);
        register("key_kp_6", GLFW.GLFW_KEY_KP_6);
        register("key_kp_7", GLFW.GLFW_KEY_KP_7);
        register("key_kp_8", GLFW.GLFW_KEY_KP_8);
        register("key_kp_9", GLFW.GLFW_KEY_KP_9);
        register("key_kp_decimal", GLFW.GLFW_KEY_KP_DECIMAL);
        register("key_kp_divide", GLFW.GLFW_KEY_KP_DIVIDE);
        register("key_kp_multiply", GLFW.GLFW_KEY_KP_MULTIPLY);
        register("key_kp_subtract", GLFW.GLFW_KEY_KP_SUBTRACT);
        register("key_kp_add", GLFW.GLFW_KEY_KP_ADD);
        register("key_kp_enter", GLFW.GLFW_KEY_KP_ENTER);
        register("key_kp_equal", GLFW.GLFW_KEY_KP_EQUAL);
        register("key_left_shift", GLFW.GLFW_KEY_LEFT_SHIFT);
        register("key_left_control", GLFW.GLFW_KEY_LEFT_CONTROL);
        register("key_left_alt", GLFW.GLFW_KEY_LEFT_ALT);
        register("key_left_super", GLFW.GLFW_KEY_LEFT_SUPER);
        register("key_right_shift", GLFW.GLFW_KEY_RIGHT_SHIFT);
        register("key_right_control", GLFW.GLFW_KEY_RIGHT_CONTROL);
        register("key_right_alt", GLFW.GLFW_KEY_RIGHT_ALT);
        register("key_right_super", GLFW.GLFW_KEY_RIGHT_SUPER);
        register("key_menu", GLFW.GLFW_KEY_MENU);
        register("gamepad_a", GLFW.GLFW_GAMEPAD_BUTTON_A);
        register("gamepad_b", GLFW.GLFW_GAMEPAD_BUTTON_B);
        register("gamepad_x", GLFW.GLFW_GAMEPAD_BUTTON_X);
        register("gamepad_y", GLFW.GLFW_GAMEPAD_BUTTON_Y);
        register("gamepad_left_bumper", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER);
        register("gamepad_right_bumper", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
        register("gamepad_back", GLFW.GLFW_GAMEPAD_BUTTON_BACK);
        register("gamepad_start", GLFW.GLFW_GAMEPAD_BUTTON_START);
        register("gamepad_left_thumb", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
        register("gamepad_right_thumb", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
        register("gamepad_dpad_up", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
        register("gamepad_dpad_right", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
        register("gamepad_dpad_down", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
        register("gamepad_dpad_left", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
    }

    private static void register(String name, int keyCode) {
        nameToCode.put(name, keyCode);
        codeToName.put(keyCode, name);
    }

    public static void registerKeyBind(int keyCode, Runnable runnable) {
        keyBinds.put(keyCode, runnable);
    }

    public static int getKeyNumber(String keyBindname) {
        String name = KeyInfoParser.getInstance().getKeyBind(keyBindname);

        return nameToCode.getOrDefault(name, GLFW.GLFW_KEY_UNKNOWN);
    }

    public static String getKeyString(int keyCode) {
        return codeToName.getOrDefault(keyCode, "key_unknown");
    }
}

