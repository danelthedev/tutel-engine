package org.tuiasi.engine.ui.components.basicComponents.tree;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.tuiasi.engine.global.nodes.Node;
import org.tuiasi.engine.logic.AppLogic;

import java.util.List;

@Setter @Getter @RequiredArgsConstructor
public class Tree extends ITree{
    private String label;
    private Node<?> root;
    private TreeListener nodeClickListener;

    private Integer lastClickedNodeId;

    public Tree(Node<?> root, TreeListener nodeClickListener) {
        this.root = root;
        this.nodeClickListener = nodeClickListener;
    }

    @Override
    public void render() {
        if(root != null)
            renderTreeNodes(List.of(root), null);
    }

    public void render(List<Node<?>> filteredNodes) {
        if(root != null)
            renderTreeNodes(List.of(root), filteredNodes);
    }

    private void renderTreeNodes(List<Node<?>> nodes, List<Node<?>> filteredNodes) {

        for (Node node : nodes) {
            if(filteredNodes != null && (!filteredNodes.isEmpty() && !filteredNodes.contains(node)))
                continue;

            // Push ID to ensure a unique ID for each node
            ImGui.pushID(node.hashCode());

            // Get node data
            boolean isLeaf = node.getChildren().isEmpty();
            int flags = isLeaf ? ImGuiTreeNodeFlags.Leaf : (ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.OpenOnDoubleClick);
            flags |= ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.SpanAvailWidth;

            if(AppLogic.getSelectedNode() != null && AppLogic.getSelectedNode().hashCode() == node.hashCode()) {
                lastClickedNodeId = node.hashCode();
            }

            boolean isLastClickedNode = lastClickedNodeId != null && lastClickedNodeId.equals(node.hashCode());

            if (isLastClickedNode) {
                ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 0.0f, 1.0f);  // Yellow text color
            }

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 0, 5);

            // Render the tree node
            boolean treeNodeOpen = ImGui.treeNodeEx(node.getName() + "##TreeNode", flags);

            // Check if the item (label) is clicked, regardless of whether the node is open or closed
            if (ImGui.isItemClicked()) {
                lastClickedNodeId = node.hashCode();
                if (nodeClickListener != null) {
                    nodeClickListener.onNodeClick(node);
                }
            }
            ImGui.popStyleVar();

            if (isLastClickedNode) {
                ImGui.popStyleColor();
            }

            if (treeNodeOpen) {
                // Recursively render child nodes
                if(node.getChildren() != null && !node.getChildren().isEmpty())
                    renderTreeNodes(node.getChildren(), filteredNodes);
                // Close the tree node if it's not a leaf
                ImGui.treePop();
            }

            // Pop the ID to balance the stack
            ImGui.popID();
        }
    }


    @Override
    public void setNodeClickListener(TreeListener listener) {
        this.nodeClickListener = listener;
    }

    @Override
    public Node<?> getNodeByName(String name) {
        if(root != null)
            return root.getNodeByName(name);

        return null;
    }

    @Override
    public Node<?> getNodeByPath(String path) {
        if(root != null)
            return root.getNodeByPath(path);

        return null;
    }

}
