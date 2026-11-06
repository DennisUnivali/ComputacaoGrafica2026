package obj;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.*;

import shaders.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Model.Model;
import Model.VboCube;
import main.Main3D;

public class Personagem extends Object3D {
	//Sphere sphere = new Sphere();
	public Vector3f cor = new Vector3f();
	public Model model = null;
	
	FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
	public float rotvel = 0;
	float ang = 0;
	
	public boolean UP = false;
	public boolean DOWN = false;
	public boolean LEFT = false;
	public boolean RIGHT = false;
	public boolean SHOOT = false;
	
	float velocidade = 5;
	long tirotimer = 0;
	
	public Personagem(float x, float y, float z, float r) {
		super(x, y, z);
		raio = r;
	}
	
	@Override
	public void DesenhaSe(ShaderProgram shader) {
//		glPushMatrix();
//		
//	    glDisable(GL_TEXTURE_2D);
//	    glColor3f(cor.x, cor.y, cor.z);
//	    
//		glTranslatef(x,y,z);
//		
//		sphere.draw(raio, 16, 16);
//		
//		glPopMatrix();
		
		Matrix4f modelm = new Matrix4f();
		modelm.setIdentity();
		
		//System.out.println(""+x+" "+y+" "+z);
		
		modelm.translate(new Vector3f(x,y,z));
		modelm.scale(new Vector3f(raio,raio,raio));
		modelm.rotate((float)Math.PI,new Vector3f(0.0f,1.0f,0.0f));
		
		
		int modellocation = glGetUniformLocation(shader.programID, "model");
		
		modelm.storeTranspose(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(modellocation, false, matrixBuffer);	
		
		model.draw();
	}
	
	@Override
	public void SimulaSe(long diftime) {
		super.SimulaSe(diftime);
		
		if(UP) {
			z -= velocidade*diftime/1000.0f;
		}
		if(DOWN) {
			z += velocidade*diftime/1000.0f;
		}
		
		if(LEFT) {
			x -= velocidade*diftime/1000.0f;
		}
		if(RIGHT) {
			x += velocidade*diftime/1000.0f;
		}
		
		tirotimer+=diftime;
		if(SHOOT && tirotimer > 100) {
			Projetil proj = new Projetil(x-0.5f, y-0.1f, z-0.1f, 0.05f);
			proj.vx = 0.5f;
			proj.vy = 0;
			proj.vz = -10;
			proj.texture = Main3D.ttverde;
			Main3D.listaProjeteis.add(proj);
			proj = new Projetil(x+0.5f, y-0.1f, z-0.1f, 0.05f);
			proj.vx = -0.5f;
			proj.vy = 0;
			proj.vz = -10;
			proj.texture = Main3D.ttverde;
			Main3D.listaProjeteis.add(proj);
			tirotimer = 0;
		}
		
		//y += vy*diftime/1000.0f;
		//z += vz*diftime/1000.0f;
		
		ang+=rotvel*diftime/1000.0f;
	}
}
