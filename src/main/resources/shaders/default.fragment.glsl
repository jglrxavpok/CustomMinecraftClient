#version 330

in vec2 ftex;
out vec4 outColor;

uniform sampler2D albedo;

void main() {
    vec4 color = texture(albedo, ftex);
    outColor = color;
}
