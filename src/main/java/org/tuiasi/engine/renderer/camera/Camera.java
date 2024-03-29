package org.tuiasi.engine.renderer.camera;

import lombok.Data;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.tuiasi.engine.misc.MathMisc;

import static java.lang.Math.*;

@Data
public abstract class Camera {
    private float fov;
    private float aspect;
    private float near;
    private float far;
    private Vector4f position;
    private Vector4f rotation;

    private Matrix4f viewMatrix;

    public Camera(float fov, float aspect, float near, float far){
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        this.position = new Vector4f(0, 0, 0, 0);
        this.rotation = new Vector4f(0, (float) (-PI/2), 0, 0);

        this.viewMatrix = new Matrix4f();
    }

    public Camera(float fov, float aspect, float near, float far, Vector4f position, Vector4f rotation) {
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        this.position = position;
        this.rotation = rotation;

        this.viewMatrix = new Matrix4f();

    }

    public Matrix4f calculateViewMatrix() {

        // use the position vector and front vector in the lookAt function to calculate the view matrix
        viewMatrix = new Matrix4f().lookAt( new Vector3f(position.x, position.y, position.z),
                                            getCameraFront().add(position.x, position.y, position.z),
                                            new Vector3f(0, 1, 0));
        return viewMatrix;
    }

    public void move(float x, float y, float z) {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public void rotate(float x, float y, float z) {
        // Add rotation
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;

        rotation.x = (float) MathMisc.clamp(rotation.x,  Math.toRadians(-89), Math.toRadians(89));
        rotation.y = (float) MathMisc.wrapAngle(rotation.y, -Math.PI, Math.PI);
        rotation.z = (float) MathMisc.wrapAngle(rotation.z, -Math.PI, Math.PI);
    }

    public void lookAtPosition(Vector4f targetPosition) {
        Vector3f direction = new Vector3f(targetPosition.x - position.x,
                targetPosition.y - position.y,
                targetPosition.z - position.z);
        direction.normalize();

        float pitch = (float) asin(-direction.y);
        float yaw = (float) atan2(direction.x, direction.z);

        rotation.x = pitch;
        rotation.y = (float) ((PI/2) + yaw);
    }

    public Vector3f getCameraFront() {
        Vector3f direction = new Vector3f();
        direction.x = (float) (cos(rotation.y) * cos(rotation.x));
        direction.y = (float) sin(rotation.x);
        direction.z = (float) (sin(rotation.y) * cos(rotation.x));

        return direction.normalize();
    }
}
