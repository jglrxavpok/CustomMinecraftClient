#version 330

layout(location=0) in vec3 vpos;
layout(location=1) in vec2 vtex;

out vec2 ftex;

uniform struct {
    mat4 viewProj;
} camera;

void main() {
    ftex = vtex;
    gl_Position = camera.viewProj * vec4(vpos, 1.0);
}
