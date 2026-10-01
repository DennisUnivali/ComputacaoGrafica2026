package core3d;

import java.awt.Graphics2D;

public class Triangulo3D {
	Ponto3D pa;
	Ponto3D pb;
	Ponto3D pc;
	public Triangulo3D(Ponto3D a, Ponto3D b, Ponto3D c) {
		super();
		this.pa = new Ponto3D(a);
		this.pb = new Ponto3D(b);
		this.pc = new Ponto3D(c);
	}
	
	public void desenhase(Graphics2D dbg) {
		
		dbg.drawLine((int)pa.x,(int)pa.y,(int)pb.x,(int)pb.y);
		dbg.drawLine((int)pb.x,(int)pb.y,(int)pc.x,(int)pc.y);
		dbg.drawLine((int)pc.x,(int)pc.y,(int)pa.x,(int)pa.y);
	}
	
	public void desenhase(Graphics2D dbg, Mat4x4 modelview,Mat4x4 projection) {
		Ponto3D pa1 = modelview.multiplicaPonto(pa);
		Ponto3D pb1 = modelview.multiplicaPonto(pb);
		Ponto3D pc1 = modelview.multiplicaPonto(pc);
		
		Ponto3D pa2 = projection.multiplicaPonto(pa1);
		Ponto3D pb2 = projection.multiplicaPonto(pb1);
		Ponto3D pc2 = projection.multiplicaPonto(pc1);
		
		dbg.drawLine((int)pa2.x,(int)pa2.y,(int)pb2.x,(int)pb2.y);
		dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pc2.x,(int)pc2.y);
		dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pa2.x,(int)pa2.y);
	}
	
	public void translacao(float a,float b, float c) {
		Mat4x4 m = new Mat4x4();
		m.setTranslate(a, b,c);
		pa.multiplicaMat(m);
		pb.multiplicaMat(m);
		pc.multiplicaMat(m);
	}
	public void escala(float a,float b,float c) {
		Mat4x4 m = new Mat4x4();
		m.setSacale(a, b, c);
		pa.multiplicaMat(m);
		pb.multiplicaMat(m);
		pc.multiplicaMat(m);
	}	
	
	public void rotacao(float ang) {
		Mat4x4 m = new Mat4x4();
		m.setRotateY(ang);
		//System.out.println("rot Y");
		pa.multiplicaMat(m);
		pb.multiplicaMat(m);
		pc.multiplicaMat(m);
	}
	public void rotacao(Mat4x4 m) {
		//System.out.println("rot Y");
		pa.multiplicaMat(m);
		pb.multiplicaMat(m);
		pc.multiplicaMat(m);
	}
}
