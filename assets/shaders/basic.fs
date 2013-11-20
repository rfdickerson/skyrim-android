precision mediump float;
uniform vec4 vColor;

varying vec4 vColor2;

void main() {
	gl_FragColor = .5*vColor2;
}