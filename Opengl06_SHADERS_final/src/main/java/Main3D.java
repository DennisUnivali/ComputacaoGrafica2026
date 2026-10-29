
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Model.VboCube;
import obj.Cubo3D;
import obj.ObjModel;
import obj.Object3D;
import shaders.StaticShader;
import util.TextureLoader;

import java.awt.image.BufferedImage;

//import com.sun.org.apache.xerces.internal.dom.DeepNodeListImpl;

import java.nio.*;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main3D {

	// The window handle
	private long window;
	
	float viewAngX = 0;
	float viewAngY = 0;
	float scale = 1.0f;
	
	public Random rnd = new Random();
	
	VboCube vboc;
	StaticShader shader;
	ArrayList<Object3D> listaObjetos = new ArrayList<>();
	

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(1500, 1000, "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated
		// or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		
			if ( key == GLFW_KEY_W) {
				viewAngX+=5;
			}
			if ( key == GLFW_KEY_S) {
				viewAngX-=5;
			}
			if ( key == GLFW_KEY_A) {
				viewAngY+=5;
			}
			if ( key == GLFW_KEY_D) {
				viewAngY-=5;
			}
			if ( key == GLFW_KEY_Z) {
				scale = scale*1.1f;
			}
			if ( key == GLFW_KEY_X) {
				scale = scale*0.9f;
			}
		
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.

		GL.createCapabilities();

		
		
		BufferedImage imggato = TextureLoader.loadImage("texturaGato.jpeg");
		
		vboc = new VboCube();
		vboc.load();
		shader = new StaticShader();
		
		//Cubo3D cubo = new Cubo3D(0.0f, 0.0f, -1.0f, 0.2f);
		//cubo.vbocube = vboc;
		
		//ObjModel x35 = new ObjModel();
		//x35.loadObj("x-35_obj.obj");
		//x35.load();
		
		System.out.println("f104starfighter.obj");
		ObjModel f101 = new ObjModel();
		f101.loadObj("..\\objs\\x-35_obj.obj");
		f101.load();
		System.out.println("Faces: "+f101.f.size());
		
		System.out.println("AIM120D");
		ObjModel sr71 = new ObjModel();
		//sr71.loadObj("..\\OBJ\\SR71.obj");
		sr71.loadObj("..\\objs\\SR71.obj");
		sr71.load();
		System.out.println("Faces: "+sr71.f.size());
		
		for(int i = 0; i < 100; i++) {
			//Cubo3D cubo = new Cubo3D(rnd.nextFloat()*2-1,rnd.nextFloat()*2-1, rnd.nextFloat()*2-1, rnd.nextFloat()*0.005f+0.0001f);
			//cubo.model = x35;
			Cubo3D cubo = new Cubo3D(rnd.nextFloat()*2-1,rnd.nextFloat()*2-1, rnd.nextFloat()*2-1, rnd.nextFloat()*0.0005f+0.004f);
			cubo.model = sr71;
			cubo.vx = rnd.nextFloat()*0.4f-0.2f;
			cubo.vy = rnd.nextFloat()*0.4f-0.2f;
			cubo.vz = rnd.nextFloat()*0.4f-0.2f;
			cubo.rotvel = rnd.nextFloat()*9;
			listaObjetos.add(cubo);
		}
		
		BufferedImage gatorgba = new BufferedImage(imggato.getWidth(), imggato.getHeight(), BufferedImage.TYPE_INT_ARGB);
		gatorgba.getGraphics().drawImage(imggato, 0, 0, null);
		int tgato = TextureLoader.loadTexture(imggato);
		System.out.println("tgato "+tgato);
		
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.

		int frame = 0;
		long lasttime = System.currentTimeMillis();

		float angle = 0;
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(45, 600f / 800f, 0.5f, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
		long ultimoTempo = System.currentTimeMillis();
		while (!glfwWindowShouldClose(window)) {
			
			long diftime = System.currentTimeMillis()-ultimoTempo;
			ultimoTempo = System.currentTimeMillis();
			
			for(int i = 0; i < listaObjetos.size();i++) {
				listaObjetos.get(i).SimulaSe(diftime);
			}
			
			angle+=0.1;
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glEnable(GL_LIGHTING);
			glShadeModel(GL_SMOOTH);

			//float senoang = (float) Math.sin(Math.toRadians(angle));
			//float cosang = (float) Math.cos(Math.toRadians(angle));
			glLoadIdentity();

			float[] lightAmbient = { 0.1f, 0.1f, 0.1f, 0.5f };
			float[] lightDiffuse = { 0.5f, 0.5f, 0.5f, 0.5f };
			float[] lightPosition = { 0.0f, 0.0f, 0.0f, 1.0f };
			//float[] lightPosition = { 0.0f, senoang * 600f, cosang * 600f, 1.0f };

			glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
			glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
			glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);

			float[] lightAmbient2 = { 0.0f, 0.0f, 0.0f, 1.0f };
			float[] lightDiffuse2 = { 1.0f, 1.0f, 1.0f, 1.0f };
			float[] lightPosition2 = { -5f, -5f, 0f, 1.0f };

			shader.start();
			int modellocation = glGetUniformLocation(shader.programID, "model");
			
			glEnable(GL_DEPTH_TEST);
			
	        glActiveTexture(GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, tgato);
	        
	        int loctexture = glGetUniformLocation(shader.programID, "tex");
	        glUniform1i(loctexture, 0);
			
			
			Matrix4f view = new Matrix4f();
			view.setIdentity();
			
			view.scale(new Vector3f(scale,scale,scale));
			view.rotate(viewAngX*0.0174532f, new Vector3f(1,0,0));
			view.rotate(viewAngY*0.0174532f, new Vector3f(0,1,0));
			view.translate(new Vector3f(0,0,0));
			int viewlocation = glGetUniformLocation(shader.programID, "view");
			int corgeralloc = glGetUniformLocation(shader.programID, "corgeral");
			int angpos = glGetUniformLocation(shader.programID, "ang");
			
			glUniform4f(corgeralloc, .5f, .5f, .5f, 1.0f);
			glUniform1f(angpos, angle);
			
			Matrix4f modelmS = new Matrix4f();
			modelmS.setIdentity();
			
			for(int i = 0; i < listaObjetos.size();i++) {
				listaObjetos.get(i).DesenhaSe(shader);
			}
			
			Matrix4f modelm = new Matrix4f();
			modelm.setIdentity();
			
			//System.out.println(""+x+" "+y+" "+z);
			modelm.translate(new Vector3f(0,0,0));
			modelm.scale(new Vector3f(0.01f,0.01f,0.01f));
			//modelm.rotate(ang,new Vector3f(0.0f,1.0f,0.0f));
			
			
			modelm.storeTranspose(matrixBuffer);
			matrixBuffer.flip();
			glUniformMatrix4fv(modellocation, false, matrixBuffer);
			
			view.storeTranspose(matrixBuffer);
			matrixBuffer.flip();
			glUniformMatrix4fv(viewlocation, false, matrixBuffer);
			
			
			//System.out.println("corgeralloc "+corgeralloc);
			glUniform4f(corgeralloc, .5f, 0.8f, 0.5f, 1.0f);
			
			
			f101.draw();
			
			
			shader.stop();
			
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			frame++;
			long actualTime = System.currentTimeMillis();
			if ((lasttime / 1000) != (actualTime / 1000)) {
				System.out.println("FPS " + frame);
				frame = 0;
				lasttime = actualTime;
			}

		}
	}


	public static void main(String[] args) {
		new Main3D().run();
	}

	public static void gluPerspective(float fovy, float aspect, float near, float far) {
		float bottom = -near * (float) Math.tan(fovy / 2);
		float top = -bottom;
		float left = aspect * bottom;
		float right = -left;
		glFrustum(left, right, bottom, top, near, far);
	}

}
