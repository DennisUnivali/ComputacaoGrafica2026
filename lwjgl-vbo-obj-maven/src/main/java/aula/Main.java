package aula;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import java.io.*;
import java.nio.FloatBuffer;
import java.nio.file.*;
import java.util.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Main {
	
	record Vertex(float[] p, float[] uv, float[] n) {
	}

	record MeshData(float[] positions, float[] uvs, float[] normals) {
	}

	static class Mesh implements AutoCloseable {
		int posVbo, uvVbo, normalVbo, count;

		Mesh(MeshData data) {
			count = data.positions.length / 3;
			posVbo = upload(data.positions);
			uvVbo = upload(data.uvs);
			normalVbo = upload(data.normals);
		}

		static int upload(float[] values) {
			FloatBuffer b = BufferUtils.createFloatBuffer(values.length);
			b.put(values).flip();
			int id = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, id);
			glBufferData(GL_ARRAY_BUFFER, b, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			return id;
		}

		void draw() {
			glEnableClientState(GL_VERTEX_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, posVbo);
			glVertexPointer(3, GL_FLOAT, 0, 0L);
			glEnableClientState(GL_NORMAL_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, normalVbo);
			glNormalPointer(GL_FLOAT, 0, 0L);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, uvVbo);
			glTexCoordPointer(2, GL_FLOAT, 0, 0L);
			glDrawArrays(GL_TRIANGLES, 0, count);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_NORMAL_ARRAY);
			glDisableClientState(GL_VERTEX_ARRAY);
		}

		public void close() {
			glDeleteBuffers(posVbo);
			glDeleteBuffers(uvVbo);
			glDeleteBuffers(normalVbo);
		}
	}

	static MeshData loadObj(Path file) throws IOException {
		List<float[]> p = new ArrayList<>(), uv = new ArrayList<>(), n = new ArrayList<>();
		List<Float> outP = new ArrayList<>(), outUv = new ArrayList<>(), outN = new ArrayList<>();
		for (String raw : Files.readAllLines(file)) {
			String line = raw.trim();
			if (line.isEmpty() || line.startsWith("#"))
				continue;
			String[] s = line.split("\\s+");
			switch (s[0]) {
			case "v" -> p.add(new float[] { Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]) });
			case "vt" -> uv.add(new float[] { Float.parseFloat(s[1]), Float.parseFloat(s[2]) });
			case "vn" -> n.add(new float[] { Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]) });
			case "f" -> {
				List<Vertex> face = new ArrayList<>();
				for (int i = 1; i < s.length && !s[i].startsWith("#"); i++) {
					String[] idx = s[i].split("/", -1);
					float[] pp = p.get(index(idx[0], p.size()));
					float[] tt = idx.length > 1 && !idx[1].isEmpty() ? uv.get(index(idx[1], uv.size()))
							: new float[] { 0, 0 };
					float[] nn = idx.length > 2 && !idx[2].isEmpty() ? n.get(index(idx[2], n.size()))
							: new float[] { 0, 0, 1 };
					face.add(new Vertex(pp, tt, nn));
				}
				for (int i = 1; i + 1 < face.size(); i++) {
					for (Vertex v : new Vertex[] { face.get(0), face.get(i), face.get(i + 1) }) {
						for (float x : v.p)
							outP.add(x);
						for (float x : v.uv)
							outUv.add(x);
						for (float x : v.n)
							outN.add(x);
					}
				}
			}
			}
		}
		if (outP.isEmpty())
			throw new IOException("OBJ sem faces: " + file);
		// Centraliza e normaliza o modelo para caber na tela.
		float[] a = toArray(outP);
		float[] min = { Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY };
		float[] max = { Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY };
		for (int i = 0; i < a.length; i += 3)
			for (int j = 0; j < 3; j++) {
				min[j] = Math.min(min[j], a[i + j]);
				max[j] = Math.max(max[j], a[i + j]);
			}
		float scale = Math.max(max[0] - min[0], Math.max(max[1] - min[1], max[2] - min[2]));
		if (scale == 0)
			scale = 1;
		for (int i = 0; i < a.length; i += 3)
			for (int j = 0; j < 3; j++)
				a[i + j] = (a[i + j] - (min[j] + max[j]) / 2f) * 2f / scale;
		return new MeshData(a, toArray(outUv), toArray(outN));
	}

	static int index(String s, int size) {
		int i = Integer.parseInt(s);
		if (i == 0)
			throw new IllegalArgumentException("Indice OBJ zero");
		return i > 0 ? i - 1 : size + i;
	}

	static float[] toArray(List<Float> a) {
		float[] r = new float[a.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = a.get(i);
		return r;
	}

	public static void main(String[] args) throws Exception {
		Path path = Paths.get(args.length > 0 ? args[0] : "src/main/resources/models/Mig_29_obj.obj");
		if (!glfwInit())
			throw new IllegalStateException("GLFW nao iniciou");
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		long window = glfwCreateWindow(900, 650, "Aula VBO - OBJ (sem shaders)", 0, 0);
		if (window == 0)
			throw new IllegalStateException("Falha ao criar janela");
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glClearColor(.09f, .12f, .17f, 1);
		try (Mesh mesh = new Mesh(loadObj(path))) {
			System.out.println("Vertices renderizados: " + mesh.count + " | OBJ: " + path);
			double start = glfwGetTime();
			while (!glfwWindowShouldClose(window)) {
				if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
					glfwSetWindowShouldClose(window, true);
				int[] w = new int[1], h = new int[1];
				glfwGetFramebufferSize(window, w, h);
				if (h[0] == 0) {
					glfwPollEvents();
					continue;
				}
				glViewport(0, 0, w[0], h[0]);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				double aspect = (double) w[0] / h[0];
				glFrustum(-aspect * .7, aspect * .7, -.7, .7, 1, 100);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
				glTranslatef(0, 0, -4);
				glRotatef((float) ((glfwGetTime() - start) * 25), 0, 1, 0);
				glRotatef(20, 1, 0, 0);
				glPolygonMode(GL_FRONT_AND_BACK, glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS ? GL_LINE : GL_FILL);
				glColor3f(.35f, .8f, .95f);
				mesh.draw();
				glfwSwapBuffers(window);
				glfwPollEvents();
			}
		} finally {
			glfwDestroyWindow(window);
			glfwTerminate();
		}
	}
}
