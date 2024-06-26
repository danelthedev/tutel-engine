package org.tuiasi.engine.ui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDir;
import lombok.Getter;
import lombok.Setter;
import org.tuiasi.engine.logic.logger.Log;
import org.tuiasi.engine.ui.components.IComponent;
import org.tuiasi.engine.ui.components.basicComponents.TopMenuBar;
import org.tuiasi.engine.ui.uiWindows.IUIWindow;
import org.tuiasi.engine.ui.uiWindows.UIWindow;
import org.tuiasi.engine.ui.uiWindows.prefabs.UIFilesWindow;
import org.tuiasi.engine.ui.uiWindows.prefabs.UILogsWindow;
import org.tuiasi.engine.ui.uiWindows.prefabs.UINodeInspectorWindow;
import org.tuiasi.engine.ui.uiWindows.prefabs.UINodeTreeWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class DefaultEngineEditorUI {

        private TopMenuBar topMenuBar;
        @Getter
        private static List<IUIWindow> uiWindows;
        @Getter
        private static List<IComponent> popups;
        boolean isSetup = false;

        @Getter @Setter
        private static boolean displayingUI = true;
        @Getter @Setter
        private static boolean displayingTopBar = true;

        public DefaultEngineEditorUI() {
            uiWindows = new ArrayList<>();
            popups = new ArrayList<>();

            topMenuBar = new TopMenuBar();
            UIWindow mainWindow = new UIWindow("Main Window", new ImVec2(0, 0), null, true);
            mainWindow.setDocked(true);
            uiWindows.add(mainWindow);

            UILogsWindow debugLogsWindow = new UILogsWindow("Debug logs", new ImVec2(0, 0), new ImVec2(500, 200));
            debugLogsWindow.setDocked(true);
            mainWindow.addDockedWindow(debugLogsWindow, ImGuiDir.Down, 0.2f);
            uiWindows.add(debugLogsWindow);

            UINodeInspectorWindow nodeInspectorWindow = new UINodeInspectorWindow("Node Inspector", new ImVec2(0, 0), new ImVec2(100, 100));
            nodeInspectorWindow.setDocked(true);
            mainWindow.addDockedWindow(nodeInspectorWindow, ImGuiDir.Right, 0.2f);
            uiWindows.add(nodeInspectorWindow);

            UINodeTreeWindow nodeTreeWindow = new UINodeTreeWindow("Node Tree", new ImVec2(0, 0), new ImVec2(100, 100));
            nodeTreeWindow.setDocked(true);
            mainWindow.addDockedWindow(nodeTreeWindow, ImGuiDir.Left, 0.2f);
            uiWindows.add(nodeTreeWindow);

            UIFilesWindow filesWindow = new UIFilesWindow("Files", new ImVec2(0, 0), new ImVec2(100, 100));
            filesWindow.setDocked(true);
            nodeTreeWindow.addDockedWindow(filesWindow, ImGuiDir.Down, 0.2f);
            uiWindows.add(filesWindow);

        }

        public void renderUI()  {
            if (displayingUI) {
                for (int i = 0; i < uiWindows.size(); i++) {
                    uiWindows.get(i).render();
                }
                for (int i = 0; i < popups.size(); i++) {
                    popups.get(i).render();
                }
            }
            if(displayingTopBar)
                topMenuBar.render();
        }

        public static void addPopup(IComponent popup) {
            popups.add(popup);
        }

        public static void removePopup(IComponent popup) {
            popups.remove(popup);
        }

        public static void addWindow(IUIWindow window) {
            uiWindows.add(window);
        }

        public static void removeWindow(IUIWindow window) {
            uiWindows.remove(window);
        }

        public static IUIWindow getWindow(String windowTitle) {
            for (IUIWindow uiWindow : uiWindows) {
                if (uiWindow.getWindowTitle().equals(windowTitle)) {
                    return uiWindow;
                }
            }
            return null;
        }

}
