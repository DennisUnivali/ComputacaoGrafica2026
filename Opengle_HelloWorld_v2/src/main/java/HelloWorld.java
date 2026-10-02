import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.Random;

import org.joml.Matrix4f;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class HelloWorld {

	// The window handle
	private long window;
	
	float zczmera = 0;
	
	Random rnd = new Random();
	
	ArrayList<float[]> listaTriangulos = new ArrayList();
	

	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
		
		for(int i = 0; i < 1000; i++) {
			float f[] = {rnd.nextFloat()-0.5f,rnd.nextFloat()-0.5f,-(rnd.nextFloat()*100),rnd.nextFloat(),rnd.nextFloat(),rnd.nextFloat()};
			listaTriangulos.add(f);
		}

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
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(800, 800, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			
			if ( key == GLFW_KEY_W) {
				zczmera+=1;
			}
			if ( key == GLFW_KEY_S) {
				zczmera-=1;
			}
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);
			
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}
	
	public void configurarPerspectiva(int largura, int altura) {
	    float aspecto = (float) largura / (float) altura;

	    Matrix4f proj = new Matrix4f()
	        .perspective((float)Math.toRadians(45.0f), aspecto, 0.1f, 100.0f);

	    try (MemoryStack stack = MemoryStack.stackPush()) {
	        FloatBuffer fb = stack.mallocFloat(16);
	        proj.get(fb);
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glLoadMatrixf(fb);
	    }

	    GL11.glMatrixMode(GL11.GL_MODELVIEW);
	    GL11.glLoadIdentity();
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		configurarPerspectiva(800,800);
		
		
		// Ativar iluminação
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        // Definir posição da luz (x, y, z, w)
        // w = 1.0 → luz pontual; w = 0.0 → luz direcional
        float[] posicaoLuz = {0.0f, 0.0f, -2.0f, 1.0f};
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, posicaoLuz);

        // Definir cor ambiente da luz
        float[] ambiente = {0.2f, 0.2f, 0.2f, 1.0f};
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambiente);

        // Definir cor difusa da luz
        float[] difusa = {0.8f, 0.8f, 0.8f, 1.0f};
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, difusa);

        // Definir cor especular da luz
        float[] especular = {1.0f, 1.0f, 1.0f, 1.0f};
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPECULAR, especular);

        // Ativar normalização automática das normais
        GL11.glEnable(GL11.GL_NORMALIZE);
     // Permitir que glColor influencie o material
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        // Definir quais propriedades do material serão afetadas por glColor
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        
        
        

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		
		int ang = 0;
		float zobj = -1;
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			//glPushMatrix();
			
			glLoadIdentity();
			
			glTranslated(0, 0, zczmera);
			
			for(int i = 0; i < listaTriangulos.size();i++) {
				float f[] = listaTriangulos.get(i);
				
				glPushMatrix();
				//glRotated(ang, 0, 0, 1);
				glTranslated(f[0], f[1], f[2]);
				glScaled(0.1, 0.1, 0.1);
	
				glColor3f(f[3], f[4], f[5]);
				glBegin(GL_TRIANGLES);
					glNormal3f(0,0,-1);
					//glColor3f(1.0f, 0.0f, 0.0f);
					glVertex3f(0.0f, 0.0f, -3.0f);
					
					glNormal3f(0,0,-1);
					//glColor3f(0.0f, 1.0f, 0.0f);
					glVertex3f(1.0f, 0.0f, -3.0f);
					
					glNormal3f(0,0,-1);
					//glColor3f(0.0f, 0.0f, 1.0f);
					glVertex3f(1.0f, 1.0f, -3.0f);
				glEnd();
				glPopMatrix();
			}
		
			
			//glPopMatrix();
			

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			ang++;
			zobj+=-0.01;
		}
	}

	public static void main(String[] args) {
		new HelloWorld().run();
	}

}