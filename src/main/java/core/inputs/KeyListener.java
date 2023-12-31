package core.inputs;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;

    //the number of key bindings glfw has
    private boolean[] keyPressed = new boolean[350];

    private KeyListener(){}

    public static KeyListener get(){
        //key listener singleton instance
        if(KeyListener.instance == null){
            KeyListener.instance = new KeyListener();
        }

        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods){
        if(action == GLFW_PRESS){
            get().keyPressed[key] = true;
        }
        else if(action == GLFW_RELEASE){
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int key) {
        return get().keyPressed[key];
    }
}
