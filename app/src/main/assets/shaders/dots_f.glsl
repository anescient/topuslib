
precision mediump float;

uniform sampler2D uTexture;
uniform vec4 uColor;

varying vec2 vTexCoord;
varying float vAlpha;

void main() {
    vec4 shape = texture2D(uTexture, vTexCoord);
    gl_FragColor = uColor * vec4(shape.rgb, vAlpha * shape.a);
}
