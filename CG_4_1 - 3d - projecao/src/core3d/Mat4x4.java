package core3d;

public class Mat4x4 {
	float mat[][] = new float[4][4];
	
	public Mat4x4() {
		setIdentity();
	}
	public void setIdentity() {
		zera();
		mat[0][0] = 1;
		mat[1][1] = 1;
		mat[2][2] = 1;
		mat[3][3] = 1;
	}
	
	public void zera() {
		for(int y = 0; y < 4;y++) {
			for(int x = 0; x < 4;x++) {
				mat[y][x] = 0;
			}
		}
	}
	
	public void setTranslate(float a,float b,float c) {
		zera();
		mat[0][0] = 1;
		mat[1][1] = 1;
		mat[2][2] = 1;
		
		mat[0][3] = a;
		mat[1][3] = b;
		mat[2][3] = c;
		mat[3][3] = 1;
	}
	
	public void setSacale(float a,float b,float c) {
		zera();
		mat[0][0] = a;
		mat[1][1] = b;
		mat[2][2] = c;
		mat[3][3] = 1;
	}
	
	public void setRotateY(float ang) {
		zera();
		float rad = ang*0.017453f;
		float sin = (float)Math.sin(rad);
		float cos = (float)Math.cos(rad);
		mat[0][0] = cos;
		mat[0][1] = 0;
		mat[0][2] = -sin;
		mat[0][3] = 0;
		
		mat[1][0] = 0;
		mat[1][1] = 1;
		mat[1][2] = 0;
		mat[1][3] = 0;
		
		mat[2][0] = sin;
		mat[2][1] = 0;
		mat[2][2] = cos;
		mat[2][3] = 0;
		
		mat[3][0] = 0;
		mat[3][1] = 0;
		mat[3][2] = 0;
		mat[3][3] = 1;
	}
	public void setRotateAxis(float ang,float x0,float y0) {
		zera();
		float rad = ang*0.017453f;
		float sin = (float)Math.sin(rad);
		float cos = (float)Math.cos(rad);
		mat[0][0] = cos;
		mat[0][1] = -sin;
		mat[0][2] = x0*(1-cos)+y0*sin;
		
		mat[1][0] = sin;
		mat[1][1] = cos;
		mat[1][2] = y0*(1-cos)-x0*sin;
		
		mat[2][0] = 0;
		mat[2][1] = 0;
		mat[2][2] = 1;
		
		mat[3][3] = 1;
	}
	
	public void setObliqueProjection(float alpha,float theta) {
		zera();
		float rad = theta*0.017453f;
		float sin = (float)Math.sin(rad);
		float cos = (float)Math.cos(rad);
		mat[0][0] = 1;
		mat[0][1] = 0;
		mat[0][2] = alpha*cos;
		
		mat[1][0] = 0;
		mat[1][1] = 1;
		mat[1][2] = alpha*sin;
		
		mat[3][3] = 1;
	}
	
	public void setParalelProjection() {
		zera();

		mat[0][0] = 1;
		mat[0][1] = 0;
		mat[0][2] = 0;
		
		mat[1][0] = 0;
		mat[1][1] = 1;
		mat[1][2] = 0;
		
		mat[3][3] = 1;
	}
	
	public Ponto3D multiplicaPonto(Ponto3D p) {
		float x1 = mat[0][0]*p.x +mat[0][1]*p.y +mat[0][2]*p.z +mat[0][3]*p.w;
		float y1 = mat[1][0]*p.x +mat[1][1]*p.y +mat[1][2]*p.z +mat[1][3]*p.w;
		float z1 = mat[2][0]*p.x +mat[2][1]*p.y +mat[2][2]*p.z +mat[2][3]*p.w;
		float w1 = mat[3][0]*p.x +mat[3][1]*p.y +mat[3][2]*p.z +mat[3][3]*p.w;
		
		return new Ponto3D(x1/w1,y1/w1,z1/w1,w1/w1);
	}
	
	public Mat4x4 multiplicaMatrizes(Mat4x4 mat1, Mat4x4 mat2) {

	    if (mat1.mat.length != 4 || mat2.mat.length != 4) {
	        throw new IllegalArgumentException("As matrizes devem ser 4x4.");
	    }

	    Mat4x4 resultado = new Mat4x4();
	    resultado.zera();
	    
	    //float[][] resultado = new float[4][4];

	    for (int i = 0; i < 4; i++) {
	        for (int j = 0; j < 4; j++) {
	            for (int k = 0; k < 4; k++) {
	                resultado.mat[i][j] += mat1.mat[i][k] * mat2.mat[k][j];
	            }
	        }
	    }

	    return resultado;
	}
	
	@Override
	public String toString() {
		String s = "";
		
	    for (int i = 0; i < 4; i++) {
	        for (int j = 0; j < 4; j++) {
	        	s += mat[i][j]+" ";
	        }
	        s += "\n";
	    }
		return s;
	}
	
}
