package org.tuiasi.engine.ui.uiWindows;

//import imgui.ImGui;

import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.internal.flag.ImGuiDockNodeFlags;
import imgui.type.ImInt;
import lombok.Getter;
import lombok.Setter;
import org.tuiasi.engine.global.WindowVariables;
import org.tuiasi.engine.ui.components.IComponent;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class UIWindow extends IUIWindow{

    Integer dockspace_id;
    private Map<IUIWindow, DockData> dockedWindows = new HashMap<>();
    boolean isRoot = false;

    public UIWindow(String windowTitle) {
        super(windowTitle);
    }

    public UIWindow(String windowTitle, ImVec2 relativePosition, ImVec2 size){
        super(windowTitle, relativePosition, size);
    }

    public UIWindow(String windowTitle, ImVec2 relativePosition, ImVec2 size, boolean isRootWindow){
        super(windowTitle, relativePosition, size);
        if(isRootWindow){
            addFlag(ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoBackground  | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoDocking);
        }
        this.isRoot = isRootWindow;
    }

    public void addDockedWindow(IUIWindow window, int direction, float splitRatio){
        dockedWindows.put(window, new DockData(direction, splitRatio));
    }

    @Override
    public void render() {
        super.render();

        // if the window is the root, autoresize to the window size
        if(isRoot) {
            WindowVariables windowVariables = WindowVariables.getInstance();

            ImVec2 windowSize = new ImVec2(windowVariables.getWidth(), windowVariables.getHeight() -windowVariables.getMainMenuHeight());
            ImGui.setNextWindowSize(windowSize.x, windowSize.y);
            ImGui.setNextWindowPos(windowVariables.getWindowPosX(), windowVariables.getWindowPosY() + windowVariables.getMainMenuHeight());
        }

        ImGui.begin(getWindowTitle(), getFlags());

        for (IComponent component : getComponents()) {
            component.render();
        }

        dockspace_id = ImGui.getID(getWindowTitle() + "_dockspace");
        ImGui.dockSpace(dockspace_id, 0, 0, ImGuiDockNodeFlags.PassthruCentralNode | ImGuiDockNodeFlags.NoDockingInCentralNode);

        if(isFirstTime) {
            dockedSetup();
            configurePrefabComponents();
        }

        ImGui.end();

    }

    public void dockedSetup(){
        WindowVariables windowVariables = WindowVariables.getInstance();

        ImGui.dockBuilderRemoveNode(dockspace_id);
        ImGui.dockBuilderAddNode(dockspace_id, ImGuiDockNodeFlags.PassthruCentralNode | ImGuiDockNodeFlags.NoDockingInCentralNode | ImGuiDockNodeFlags.DockSpace);
        ImGui.dockBuilderSetNodeSize(dockspace_id, windowVariables.getWidth(), windowVariables.getHeight() - WindowVariables.getInstance().getMainMenuHeight());

        ImInt newNode = new ImInt(dockspace_id);
        for(Map.Entry<IUIWindow, DockData> entry : dockedWindows.entrySet()){
            int dock_id = ImGui.dockBuilderSplitNode(newNode.get(), entry.getValue().getDirection(), entry.getValue().getSplitRatio(), null, newNode);

            ImGui.dockBuilderDockWindow(entry.getKey().getWindowTitle(), dock_id);

            ImVec2 nodeSize = ImGui.dockBuilderGetNode(dock_id).getSize();
            entry.getKey().setSize(nodeSize);
        }

        ImGui.dockBuilderFinish(dockspace_id);

        isFirstTime = false;
    }

    protected void addPrefabComponents(){}

    protected void configurePrefabComponents(){}
}
