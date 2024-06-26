package org.tuiasi.engine.renderer.renderable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joml.Matrix4f;
import org.tuiasi.engine.global.nodes.EditorVisible;
import org.tuiasi.engine.global.nodes.spatial.Spatial3D;
import org.tuiasi.engine.logic.AppLogic;
import org.tuiasi.engine.logic.EngineState;
import org.tuiasi.engine.renderer.Renderer;
import org.tuiasi.engine.renderer.camera.Camera;
import org.tuiasi.engine.renderer.camera.MainCamera;
import org.tuiasi.engine.renderer.material.Material;
import org.tuiasi.engine.renderer.mesh.Mesh;
import org.tuiasi.engine.renderer.shader.DefaultShaders;
import org.tuiasi.engine.renderer.shader.Shader;
import org.tuiasi.engine.renderer.shader.ShaderProgram;
import org.tuiasi.engine.renderer.shader.Uniform;
import org.tuiasi.engine.renderer.texture.Texture;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

@Data
public class Renderable3D extends Spatial3D implements IRenderable {
    @EditorVisible
    @JsonProperty
    Material material;

    ShaderProgram shaderProgram;

    Mesh mesh;

    @EditorVisible
    @JsonProperty
    String meshPath = "";
    String previousMeshPath = "";

    private Boolean playVisible = true;

    public Renderable3D() {
        this(new Mesh(),
                new ShaderProgram(new Shader(DefaultShaders.getVertexShader(), GL_VERTEX_SHADER),
                        new Shader(DefaultShaders.getFragmentShader(), GL_FRAGMENT_SHADER)),
                new Material(new Texture(0), new Texture(1), 32.0f),
                new Spatial3D());
    }

    public Renderable3D(Mesh mesh, ShaderProgram shaderProgram, Material material, Spatial3D transform) {
        this.mesh = mesh;
        previousMeshPath = mesh.getPath();

        this.shaderProgram = shaderProgram;
        this.material = material;

        setRotation(transform.getRotation());
        setPosition(transform.getPosition());
        setScale(transform.getScale());

        if (!material.equals(new Material()))
            setMaterialUniforms();
    }

    public void addToRenderer(){
        Renderer.addRenderable(this);
    }

    public Renderable3D(Spatial3D transform, ShaderProgram shaderProgram, Material material, String meshPath) {
        this(new Mesh(meshPath),
                shaderProgram,
                material,
                transform);
        setPosition(transform.getPosition());
        setRotation(transform.getRotation());
        setScale(transform.getScale());
    }

    public void copy(Renderable3D renderable) {
        if(renderable == null)
            return;

        this.mesh = renderable.getMesh();
        this.material = renderable.getMaterial();
        this.shaderProgram = renderable.getShaderProgram();
        setRotation(renderable.getRotation());
        setPosition(renderable.getPosition());
        setScale(renderable.getScale());
    }

    public void setUniform(Uniform<?> value) {
        shaderProgram.setUniform(value);
    }

    private void setModelViewMatrix() {
        Camera camera = MainCamera.getInstance();

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        projectionMatrix.perspective(camera.getFov(), camera.getAspect(), camera.getNear(), camera.getFar());

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity();
        modelMatrix.translate(getPosition());
        modelMatrix.rotateX((float) Math.toRadians(getRotation().x));
        modelMatrix.rotateY((float) Math.toRadians(getRotation().y));
        modelMatrix.rotateZ((float) Math.toRadians(getRotation().z));

        modelMatrix.scale(getScale());

        Matrix4f viewMatrix = camera.getViewMatrix();

        setUniform(new Uniform<>("model", modelMatrix));
        setUniform(new Uniform<>("view", viewMatrix));
        setUniform(new Uniform<>("projection", projectionMatrix));
    }

    private void setMaterialUniforms() {
        if (material.getDiffuse() != null && !material.getDiffuse().getPath().isEmpty()) {
            material.getDiffuse().use();
            shaderProgram.setUniform(new Uniform<>("diffuseMap", material.getDiffuse().getTextureIndex()));
            shaderProgram.setUniform(new Uniform<>("hasDiffuse", true));
        } else {
            shaderProgram.setUniform(new Uniform<>("hasDiffuse", false));
        }

        if (material.getSpecular() != null && !material.getSpecular().getPath().isEmpty()) {
            material.getSpecular().use();
            shaderProgram.setUniform(new Uniform<>("specularMap", material.getSpecular().getTextureIndex()));
            shaderProgram.setUniform(new Uniform<>("hasSpecular", true));
        } else {
            shaderProgram.setUniform(new Uniform<>("hasSpecular", false));
        }

        setUniform(new Uniform<>("materialShininess", material.getShininess()));
    }

    @Override
    public void render() {
        if(!previousMeshPath.equals(meshPath)) {
            mesh = new Mesh(meshPath);
            previousMeshPath = mesh.getPath();
        }

        if((playVisible && AppLogic.getEngineState() == EngineState.PLAY) || AppLogic.getEngineState() == EngineState.EDITOR) {
            shaderProgram.use();
            setModelViewMatrix();
            setMaterialUniforms();
            mesh.render();
        }
    }

    @Override
    public Object saveState() {
        Renderable3D copyObj = new Renderable3D();
        copyObj.copy(this);
        return copyObj;
    }

    @Override
    public void loadState(Object state) {
        Renderable3D copyObj = (Renderable3D) state;
        copy(copyObj);
    }

}
