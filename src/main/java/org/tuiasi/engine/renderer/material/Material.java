package org.tuiasi.engine.renderer.material;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.tuiasi.engine.global.nodes.EditorVisible;
import org.tuiasi.engine.renderer.texture.Texture;

@Data @AllArgsConstructor
public class Material implements IMaterial{
    @EditorVisible
    @JsonProperty
    private Texture diffuse;
    @JsonProperty
    @EditorVisible
    private Texture specular;
    @JsonProperty
    @EditorVisible
    private float shininess;

    public Material() {
        diffuse = new Texture();
        specular = new Texture();
        shininess = 32.0f;
    }
}
