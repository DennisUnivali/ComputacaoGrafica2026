package aula;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import aula.obj.ObjModel;
import aula.util.TextureLoader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Main {

	ObjModel mesh;
	long window;
	
	int textureId;
	int textureId2;
	
	int width = 0;
	int height = 0;
	
	public Main() {
	}
	
	public void init() {
		Path path = Paths.get("../objs/x-35_obj.obj");
		if (!glfwInit())
			throw new IllegalStateException("GLFW nao iniciou");
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		window = glfwCreateWindow(1024, 768, "Aula VBO - OBJ (sem shaders)", 0, 0);
		if (window == 0)
			throw new IllegalStateException("Falha ao criar janela");
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE0);
		
		glClearColor(.5f, .5f, .5f, 1);
		
		try {
			mesh = new ObjModel(path.toString());
			//ObjModel mesh2 = new ObjModel("src/main/resources/models/x-35_obj.obj");
			
			System.out.println("mesh "+mesh.drawboundingbox());
			//System.out.println("mesh "+mesh2.drawboundingbox());
					
			BufferedImage img = ImageIO.read(new File("src/main/resources/saida_textura_03___Default.png"));
			textureId = TextureLoader.loadTexture(img);
			BufferedImage img_2 = ImageIO.read(new File("src/main/resources/saida_textura_glass.png"));
			textureId2 = TextureLoader.loadTexture(img_2);
			
			mesh.setMaterialTexture("03___Default",textureId);
			mesh.setMaterialTexture("glass",textureId2);
			
			System.out.println("Vertices renderizados: " + mesh.f.size() + " | OBJ: " + path);
		}catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	double timer = 0;
	public void loop() {
		try {
			
			double start = glfwGetTime();
			int ssec = (int)start;
			int countframe = 0;
			
			while (!glfwWindowShouldClose(window)) {
				double ftimw = glfwGetTime() - start;
				start = glfwGetTime();
				if(ssec!=(int)start) {
					ssec=(int)start;
					System.out.println("FPS "+countframe);
					//System.out.println("ftimw "+ftimw);
					countframe=1;
				}else {
					countframe++;
				}
				
				if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
					glfwSetWindowShouldClose(window, true);
				int[] w = new int[1], h = new int[1];
				glfwGetFramebufferSize(window, w, h);
				if (h[0] == 0) {
					glfwPollEvents();
					continue;
				}
				width = w[0];
				height = h[0];
				
				gameLogic(ftimw);
				gameRender();
				

				glfwSwapBuffers(window);
				glfwPollEvents();
			}
		} finally {
			glfwDestroyWindow(window);
			glfwTerminate();
		}
	}
	
	public void gameLogic(double ftime) {
		timer+=ftime;
		

	}
	
	public void gameRender() {
		glViewport(0, 0, width, height);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		double aspect = (double) width / height;
		//System.out.println("w0 "+w[0]+" h0 "+h[0]);
		glFrustum(-aspect * .7, aspect * .7, -.7, .7, 1, 100);
		glMatrixMode(GL_MODELVIEW);
		
		glLoadIdentity();
		glTranslatef(0, 0, -4);
		glRotatef((float) (timer * 25), 0, 1, 0);
		
		if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
			glRotatef(0, 1, 0, 0);
		}else {
			glRotatef(20, 1, 0, 0);
		}
		
		glPolygonMode(GL_FRONT_AND_BACK, glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS ? GL_LINE : GL_FILL);
		
		glColor3f(1, 1, 1);
		//System.out.println("textureId "+textureId);
		glEnable(GL_COLOR_MATERIAL);
		glEnable (GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureId);
		
		glTranslatef(0, -1, 0);
		glScaled(0.05f,0.05f,0.05f);
		mesh.draw();
		
		//glTranslatef(0, 0, -6);
		
		//glBindTexture(GL_TEXTURE_2D, textureId2);
		//glScaled(0.01f,0.01f,0.01f);
		
		//mesh2.draw();
	}
	
	public static void main(String[] args) throws Exception {
		Main m = new Main();
		m.init();
		m.loop();
	}
}
