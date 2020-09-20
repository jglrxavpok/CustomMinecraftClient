#version 330

in vec2 ftex;
in vec3 fcolor;
out vec4 outColor;

uniform sampler2D albedo;

void main() {
    vec4 color = texture(albedo, ftex) * vec4(fcolor, 1.0);
    if(color.a < 0.01)
        discard;
    outColor = color;
}
