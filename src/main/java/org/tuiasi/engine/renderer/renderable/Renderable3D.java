package org.tuiasi.engine.renderer.renderable;

import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.tuiasi.engine.renderer.camera.MainCamera;
import org.tuiasi.engine.renderer.shader.Shader;
import org.tuiasi.engine.renderer.shader.ShaderProgram;
import org.tuiasi.engine.renderer.shader.Uniform;
import org.tuiasi.engine.renderer.texture.Texture;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

@Data
public class Renderable3D implements IRenderable{
    // mesh data
    int VAO, VBO, EBO;
    FloatBuffer verticesBuffer;
    IntBuffer indicesBuffer;

    // texture data
    Texture texture;

    // shader data
    ShaderProgram shaderProgram;

    // position and rotation
    Vector3f position = new Vector3f(0,0,0), rotation = new Vector3f(0,0,0);

    public Renderable3D(float[] vertices, int[] indices){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertices.length * Float.BYTES);
        verticesBuffer = byteBuffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        ByteBuffer indicesByteBuffer = BufferUtils.createByteBuffer(indices.length * Integer.BYTES);
        indicesBuffer = indicesByteBuffer.asIntBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        shaderProgram = new ShaderProgram(new Shader("src/main/resources/shaders/default_vertex.vert", GL_VERTEX_SHADER),
                            new Shader("src/main/resources/shaders/default_fragment.frag", GL_FRAGMENT_SHADER));

        texture = new Texture();

        initVertBuf();
    }

    public Renderable3D(List<Vector3f> vertices, List<Integer> indices){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertices.size() * 3 * Float.BYTES);
        verticesBuffer = byteBuffer.asFloatBuffer();
        for(Vector3f vertex : vertices){
            verticesBuffer.put(vertex.x);
            verticesBuffer.put(vertex.y);
            verticesBuffer.put(vertex.z);
        }
        verticesBuffer.flip();

        ByteBuffer indicesByteBuffer = BufferUtils.createByteBuffer(indices.size() * Integer.BYTES);
        indicesBuffer = indicesByteBuffer.asIntBuffer();
        for(Integer index : indices){
            indicesBuffer.put(index);
        }
        indicesBuffer.flip();

        shaderProgram = new ShaderProgram(new Shader("src/main/resources/shaders/default_vertex.vert", GL_VERTEX_SHADER),
                new Shader("src/main/resources/shaders/default_fragment.frag", GL_FRAGMENT_SHADER));

        texture = new Texture();

        initVertBuf();
    }

    public Renderable3D(float[] vertices, int[] indices, ShaderProgram shaderProgram){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertices.length * Float.BYTES);
        verticesBuffer = byteBuffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        ByteBuffer indicesByteBuffer = BufferUtils.createByteBuffer(indices.length * Integer.BYTES);
        indicesBuffer = indicesByteBuffer.asIntBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        this.shaderProgram = shaderProgram;

        texture = new Texture();

        initVertBuf();
    }

    public Renderable3D(List<Vector3f> vertices, List<Integer> indices, ShaderProgram shaderProgram){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertices.size() * 3 * Float.BYTES);
        verticesBuffer = byteBuffer.asFloatBuffer();
        for(Vector3f vertex : vertices){
            verticesBuffer.put(vertex.x);
            verticesBuffer.put(vertex.y);
            verticesBuffer.put(vertex.z);
        }
        verticesBuffer.flip();

        ByteBuffer indicesByteBuffer = BufferUtils.createByteBuffer(indices.size() * Integer.BYTES);
        indicesBuffer = indicesByteBuffer.asIntBuffer();
        for(Integer index : indices){
            indicesBuffer.put(index);
        }
        indicesBuffer.flip();

        this.shaderProgram = shaderProgram;

        texture = new Texture();

        initVertBuf();
    }

    public Renderable3D(float[] vertices, int[] indices, ShaderProgram shaderProgram, Texture texture){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertices.length * Float.BYTES);
        verticesBuffer = byteBuffer.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.flip();

        ByteBuffer indicesByteBuffer = BufferUtils.createByteBuffer(indices.length * Integer.BYTES);
        indicesBuffer = indicesByteBuffer.asIntBuffer();
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        this.shaderProgram = shaderProgram;
        this.texture = texture;

        initVertBuf();
    }

    public Renderable3D(List<Vector3f> vertices, List<Integer> indices, ShaderProgram shaderProgram, Texture texture){
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(vertices.size() * 3 * Float.BYTES);
        verticesBuffer = byteBuffer.asFloatBuffer();
        for(Vector3f vertex : vertices){
            verticesBuffer.put(vertex.x);
            verticesBuffer.put(vertex.y);
            verticesBuffer.put(vertex.z);
        }
        verticesBuffer.flip();

        ByteBuffer indicesByteBuffer = BufferUtils.createByteBuffer(indices.size() * Integer.BYTES);
        indicesBuffer = indicesByteBuffer.asIntBuffer();
        for(Integer index : indices){
            indicesBuffer.put(index);
        }
        indicesBuffer.flip();

        this.shaderProgram = shaderProgram;
        this.texture = texture;

        initVertBuf();
    }

    public Renderable3D(Renderable3D renderable3D){
        this.verticesBuffer = renderable3D.verticesBuffer;
        this.indicesBuffer = renderable3D.indicesBuffer;
        this.shaderProgram = renderable3D.shaderProgram;
        this.texture = renderable3D.texture;

        initVertBuf();
    }

    public void initVertBuf(){
        // create a vertex array object to store the vertex buffer object
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        // create a vertex buffer object to store the vertices
        VBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

        // create element buffer object
        EBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        // set the vertex attributes

        // position attribute
        GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        // color attribute
        GL20.glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        // texture attribute
        GL20.glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);
    }

    public void setUniform(Uniform<?> value){
        shaderProgram.setUniform(value);
    }

    public void move(Vector3f direction){
        position.add(direction);
    }

    public void rotate(Vector3f rotation){
        this.rotation.add(rotation);
    }

    @Override
    public void render() {
        MainCamera camera = MainCamera.getInstance();

        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        projectionMatrix.perspective(camera.getFov(), camera.getAspect(), camera.getNear(), camera.getFar());

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity();
        modelMatrix.translate(position);
        modelMatrix.rotateX((float) Math.toRadians(rotation.x));
        modelMatrix.rotateY((float) Math.toRadians(rotation.y));
        modelMatrix.rotateZ((float) Math.toRadians(rotation.z));

        Matrix4f viewMatrix = camera.getViewMatrix();

        setUniform(new Uniform<>("projection", projectionMatrix));
        setUniform(new Uniform<>("model", modelMatrix));
        setUniform(new Uniform<>("view", viewMatrix));

        texture.use();
        shaderProgram.use();

        glBindVertexArray(VAO);
        GL20.glDrawElements(GL11.GL_TRIANGLES, indicesBuffer.capacity(), GL_UNSIGNED_INT, 0);
    }

}
