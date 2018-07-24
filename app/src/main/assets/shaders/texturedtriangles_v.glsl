
uniform mat4 uVPMatrix;

attribute vec3 aPos;
attribute vec2 aTexCoord;

varying vec2 vTexCoord;

void main() {
    vTexCoord = aTexCoord;
    gl_Position = uVPMatrix * vec4(aPos, 1.0);
}
