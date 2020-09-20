#version 330

layout(location=0) in vec3 vpos;
layout(location=1) in vec2 vtex;
layout(location=2) in vec3 vcolor;

out vec2 ftex;
out vec3 fcolor;

uniform struct {
    mat4 viewProj;
} camera;

void main() {
    ftex = vtex;
    fcolor = vcolor;
    gl_Position = camera.viewProj * vec4(vpos, 1.0);
}
