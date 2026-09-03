import java.awt.Graphics;

public class Linha2D {
	Ponto2D A;
	Ponto2D B;

	public Linha2D(float x1,float y1,float x2,float y2) {
		A = new Ponto2D(x1, y1);
		B = new Ponto2D(x2, y2);
	}
	
	public void draw(Graphics g) {
		g.drawLine((int)A.X,(int) A.Y,(int) B.X,(int) B.Y);
	}
	
	public void translate(float x,float y) {
		A.translate(x, y);
		B.translate(x, y);
	}
	public void scale(float x,float y) {
		A.scale(x, y);
		B.scale(x, y);
	}
	
	public void rotate(float ang) {
		A.rotate(ang);
		B.rotate(ang);
	}
	
}

