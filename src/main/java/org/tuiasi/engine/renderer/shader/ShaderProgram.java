package org.tuiasi.engine.renderer.shader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tuiasi.engine.renderer.shader.uniform.FUniform;
import org.tuiasi.engine.renderer.shader.uniform.Uniform;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

@NoArgsConstructor @Getter @Setter
public class ShaderProgram {
    private int programID;
    Shader vertexShader, fragmentShader;

    List<Uniform<?>> uniforms;

    public ShaderProgram(Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;

        this.programID = glCreateProgram();
        glAttachShader(programID, vertexShader.getShaderID());
        glAttachShader(programID, fragmentShader.getShaderID());

        link();

        uniforms = new ArrayList<>();
    }

    public void link() {
        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == 0) {
            System.err.println("Error linking shader program: " + glGetProgramInfoLog(programID, 1024));
            System.exit(1);
        }
        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Error validating shader program: " + glGetProgramInfoLog(programID, 1024));
            System.exit(1);
        }

        glDeleteShader(vertexShader.getShaderID());
        glDeleteShader(fragmentShader.getShaderID());
    }

    public void use() {
        glUseProgram(programID);
    }

    public void setUniform(String name, Uniform<?> value){
        glUseProgram(programID);
        // iterate over the uniforms and set the value of the uniform with the given name
        boolean found = false;
        for(Uniform<?> uniform : uniforms){
            if(uniform.getName().equals(name)){
                // check the type of the uniform and set the value accordingly
                if(value instanceof FUniform) {
                    ((FUniform) uniform).setUniform((Float) value.getValue());
                }

                found = true;
                uniform.use();
                break;
            }
        }
        if(!found){
            // create a new uniform and set its value based on the given value
            if(value instanceof FUniform) {
                FUniform uniform = new FUniform(name, (Float) value.getValue(), programID);
                uniforms.add(uniform);
                uniform.use();
            }

        }
    }

}
