
uniform mat4 uMVPMatrix;

attribute vec2 aPos;

void main() {
    gl_Position = uMVPMatrix * vec4(aPos, 0.0, 1.0);
}
