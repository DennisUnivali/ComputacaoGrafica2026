
public class Ponto2D {
	float X;
	float Y;
	
	public Ponto2D(float x, float y) {
		super();
		X = x;
		Y = y;
	}
	
	public void translate(float x,float y) {
		X = X+x;
		Y = Y+y;
	}
	public void scale(float x,float y) {
		X = X*x;
		Y = Y*y;
	}
	public void rotate(float ang) {
		float nX = (float)(X*Math.cos(ang)+Y*Math.sin(ang));
		float nY = (float)(-X*Math.sin(ang)+Y*Math.cos(ang));
		
		X = nX;
		Y = nY;
		
	}
}
