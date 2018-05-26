
uniform mat4 uMVPMatrix;
uniform vec3 uColor;
uniform vec3 uLight;

attribute vec3 aPos;
attribute vec3 aNormal;

varying vec3 vColor;

void main() {
    vColor = uColor * (0.2 + 0.8 * dot(aNormal, uLight));
    gl_Position = uMVPMatrix * vec4(aPos, 1.0);
}
