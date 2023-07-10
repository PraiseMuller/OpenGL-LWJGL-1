package core.utils;

import core.inputs.KeyListener;
import core.inputs.MouseListener;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.type.ImBoolean;

import java.io.File;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ImGui_Impl_001 {
    private long glfwWindow;


    public ImGui_Impl_001(long glfwWindow) {
        this.glfwWindow = glfwWindow;
    }

    // Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini"); // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard()) {
                KeyListener.keyCallback(w, key, scancode, action, mods);
            }
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

        });

//        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
//            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
//            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
//            if (!io.getWantCaptureMouse()) {
//                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
//            } else {
//                MouseListener.clear();
//            }
//        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });

        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

        if (new File("C:/Windows/Fonts/segoeui.ttf").isFile()) {
            final ImFontAtlas fontAtlas = io.getFonts();
            final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

            // Glyphs could be added per-font as well as per config used globally like here
            fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

            // Fonts merge example
            fontConfig.setPixelSnapH(true);
            fontAtlas.addFontFromFileTTF("C:/Windows/Fonts/segoeui.ttf", 32, fontConfig);
            fontConfig.destroy(); // After all fonts were added we don't need this config more
        } else if (new File("C:/Windows/Fonts/Cour.ttf").isFile()) {
            // Fallback font

            final ImFontAtlas fontAtlas = io.getFonts();
            final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

            // Glyphs could be added per-font as well as per config used globally like here
            fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

            // Fonts merge example
            fontConfig.setPixelSnapH(true);
            fontAtlas.addFontFromFileTTF("C:/Windows/Fonts/Cour.ttf", 32, fontConfig);
            fontConfig.destroy(); // After all fonts were added we don't need this config more
        }

        startFrame(1/60f);
    }


    private void startFrame(final float deltaTime) {
        ImGui.newFrame();
    }

    private void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Constants.WIN_WIDTH, Constants.WIN_HEIGHT);
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);

        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        ImGui.render();

        long backupWindowPtr = glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        glfwMakeContextCurrent(backupWindowPtr);
    }

    // If you want to clean a room after yourself - do it by yourself
    private void destroyImGui() {
        ImGui.destroyContext();
    }

    private void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());
        ImGui.setNextWindowPos(0.0f, 0.0f);
        ImGui.setNextWindowSize(Constants.WIN_WIDTH, Constants.WIN_HEIGHT);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));

        ImGui.end();
    }
}
