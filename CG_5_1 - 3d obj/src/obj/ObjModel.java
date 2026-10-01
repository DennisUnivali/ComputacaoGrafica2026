package obj;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import core3d.Mat4x4;
import core3d.Ponto3D;
import obj.Vector2f;
import obj.Vector3f;
//import org.joml.Vector3f;



public class ObjModel {
	public ArrayList<Vector3f> v;
	public ArrayList<Vector3f> vn;
	public ArrayList<Vector2f> vt;
	public ArrayList<Face3D> f;
	public ArrayList<GrupoFaces> g;
	public ConcurrentHashMap<String, GrupoFaces> gname;

	public ObjModel() {
		// TODO Auto-generated constructor stubn
		v = new ArrayList<Vector3f>();
		vn = new ArrayList<Vector3f>();
		vt = new ArrayList<Vector2f>();
		f = new ArrayList<Face3D>();
		g = new ArrayList<GrupoFaces>();
		gname = new ConcurrentHashMap<String, GrupoFaces>();
	}

	public void loadObj(String file) {
		// System.out.println(" "+file);
		// InputStream in = this.getClass().getResourceAsStream(file);
		InputStream in;
		try {
			in = new FileInputStream(file);

			// System.out.println(""+in);

			BufferedReader dados = new BufferedReader(new InputStreamReader(in));

			String str;

			try {

				while ((str = dados.readLine()) != null) {
					if (str.length() > 0) {
						if (str.contains("#")) {
							continue;
						}
						if (str.contains("v ")) {
							decodeVertice(str);
						}
						if (str.contains("vn ")) {
							decodeVerticeNormal(str);
						}
						if (str.contains("vt ")) {
							decodeTextureMapping(str);
						}
						if (str.contains("f ")) {
							decodeFace(str);
						}
						if (str.contains("g ")) {
							decodeGrupo(str);
						}
					}
				}

				if (g.size() > 0) {
					g.get(g.size() - 1).ffinal = f.size() - 1;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			//System.out.println(" v " + v.size() + " vn " + vn.size() + " vt " + vt.size());

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void decodeVertice(String str) {
		String s[] = str.split(" ");
		// System.out.print("V ");

		Vector3f vec = new Vector3f();

		int index = 0;
		for (int i = 0; i < s.length; i++) {
			// System.out.print(" s["+i+"] = "+s[i]);
			try {
				float numero = Float.parseFloat(s[i]);
				// System.out.print(" OK");

				if (index == 0) {
					vec.x = numero;
				}
				if (index == 1) {
					vec.y = numero;
				}
				if (index == 2) {
					vec.z = numero;
				}
				index++;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		// System.out.println();
		v.add(vec);
	}

	public void decodeVerticeNormal(String str) {
		String s[] = str.split(" ");
		// System.out.print("Vn ");

		Vector3f vec = new Vector3f();

		int index = 0;
		for (int i = 0; i < s.length; i++) {
			// System.out.print(" s["+i+"] = "+s[i]);
			try {
				float numero = Float.parseFloat(s[i]);
				// System.out.print(" OK");

				if (index == 0) {
					vec.x = numero;
				}
				if (index == 1) {
					vec.y = numero;
				}
				if (index == 2) {
					vec.z = numero;
				}
				index++;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		// System.out.println();
		vn.add(vec);
	}

	public void decodeTextureMapping(String str) {
		String s[] = str.split(" ");
		// System.out.print("Vt ");

		Vector2f vec = new Vector2f();

		int index = 0;
		for (int i = 0; i < s.length; i++) {
			// System.out.print(" s["+i+"] = "+s[i]);
			try {
				float numero = Float.parseFloat(s[i]);
				// System.out.print(" OK");

				if (index == 1) {
					vec.x = numero;
				}
				if (index == 0) {
					vec.y = numero;
				}

				index++;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		// System.out.println();
		vt.add(vec);
	}

	int valorestmp[] = new int[3];

	public void decodeFace(String str) {
		String s[] = str.split(" ");
		// System.out.print("f ");

		Face3D face = new Face3D();

		int index = 0;

		for (int i = 0; i < s.length; i++) {
			//System.out.println(" s["+i+"] = "+s[i]+" "+s.length);

			if (s[i].contains("/")) {
				String s2[] = s[i].split("/");

				valorestmp[0] = -1;
				valorestmp[1] = -1;
				valorestmp[2] = -1;

				for (int j = 0; j < s2.length; j++) {
					try {
						int numero = Integer.parseInt(s2[j]);
						valorestmp[j] = numero;
					} catch (Exception e) {
					}
				}
				if(index==4) {
					continue;
				}

				face.v[index] = valorestmp[0];
				face.n[index] = valorestmp[2];
				face.t[index] = valorestmp[1];
				//System.out.println(" v " + valorestmp[0] + " n " + valorestmp[1] + " t " + valorestmp[2]);

				index++;
			}

		}

		face.nvertices = (byte) index;
		// System.out.println();
		f.add(face);
	}

	public void decodeGrupo(String str) {
		//System.out.println("GRUPO    _           __________--" + str);
		String sttmp[] = str.split(" ");
		String nome = "";
		if (sttmp.length >= 2) {
			nome = sttmp[1];
		}

		GrupoFaces gp = new GrupoFaces();
		gp.nome = nome;

		gp.finicial = f.size();

		if (g.size() > 0) {
			g.get(g.size() - 1).ffinal = f.size() - 1;
		}

		g.add(gp);
		gname.put(nome, gp);
		//System.out.println("nome" + nome + "--");

	}

	public float[] getBowndinbox() {
		float bbbox[] = {(float)Float.MAX_VALUE,(float)Float.MAX_VALUE,(float)Float.MAX_VALUE,(float)Float.MIN_VALUE,(float)Float.MIN_VALUE,(float)Float.MIN_VALUE};
		for (int i = 0; i < v.size(); i++) {
			Vector3f v1 = v.get(i);
			if(v1.x < bbbox[0]) {
				bbbox[0] = v1.x;
			}
			if(v1.y < bbbox[1]) {
				bbbox[1] = v1.y;
			}
			if(v1.z < bbbox[2]) {
				bbbox[2] = v1.z;
			}
			if(v1.x > bbbox[3]) {
				bbbox[3] = v1.x;
			}
			if(v1.y > bbbox[4]) {
				bbbox[4] = v1.y;
			}
			if(v1.z > bbbox[5]) {
				bbbox[5] = v1.z;
			}			
		}
		return bbbox;
	}
	
	public void modelScale(float scaleMultiply) {
		float bbox1[] = getBowndinbox();
		System.out.println("bbox "+bbox1[0]+","+bbox1[1]+","+bbox1[2]+","+bbox1[3]+","+bbox1[4]+","+bbox1[5]);
		for (int i = 0; i < v.size(); i++) {
			Vector3f v1 = v.get(i);
			v1.x = v1.x*scaleMultiply;
			v1.y = v1.y*scaleMultiply;
			v1.z = v1.z*scaleMultiply;
		}
		System.out.println("bbox "+bbox1[0]+","+bbox1[1]+","+bbox1[2]+","+bbox1[3]+","+bbox1[4]+","+bbox1[5]);
	}
	
	public void desenhaSe(Graphics2D dbg, Mat4x4 modelview,Mat4x4 projection) {

		//System.out.println("f.size "+f.size());
		
		int excludeclount = 0; 
		
		Vector3f camera = new Vector3f(0,0,-1);

		for (int i = 0; i < f.size(); i++) {
			Face3D face = f.get(i);
			
			if(face.nvertices==3) {
				Vector3f v1 = v.get(face.v[0]-1);
				Vector3f v2 = v.get(face.v[1]-1);
				Vector3f v3 = v.get(face.v[2]-1);
				
				Vector3f vn1 = vn.get(face.n[0]-1);
				Vector3f vn2 = vn.get(face.n[1]-1);
				Vector3f vn3 = vn.get(face.n[2]-1);
				
				Vector2f vt1 = vt.get(0);
				Vector2f vt2 = vt.get(0);
				Vector2f vt3 = vt.get(0);
				
				if(face.t[0]!=-1) {
					vt1 = vt.get(face.t[0]-1);
					vt2 = vt.get(face.t[1]-1);
					vt3 = vt.get(face.t[2]-1);
				}
				
				//System.out.println(v1.x+","+v1.y+","+v1.z);
				
//				glColor3f(0f, 1f, 0f);
//				glBegin(GL_TRIANGLES);
//				
//				  glColor3f(0.5f, 0.5f, 0.5f);
//				  //glColor3f(0f, 1f, 0f);
//				  glTexCoord2f(vt1.x, vt1.y);	
//				  glNormal3f(vn1.x, vn1.y, vn1.z);
//				  glVertex3f(v1.x,v1.y,v1.z);
//				  
//				  glColor3f(0.5f, 0.5f, 0.5f);
//				  //glColor3f(1f, 0f, 0f);
//				  glTexCoord2f(vt2.x, vt2.y);
//				  glNormal3f(vn2.x, vn2.y, vn2.z);
//				  glVertex3f(v2.x,v2.y,v2.z);
//			      
//				  glColor3f(0.5f, 0.5f, 0.5f);
//				  //glColor3f(0f, 0f, 1f);
//				  glTexCoord2f(vt3.x, vt3.y);
//			      glNormal3f(vn3.x, vn3.y, vn3.z);
//			      glVertex3f(v3.x,v3.y,v3.z);
//			   glEnd();

				//System.out.println("1 "+v1.x+" "+v1.y+" "+v1.z);
				//System.out.println("2 "+v2.x+" "+v2.y+" "+v2.z);
				//System.out.println("3 "+v3.x+" "+v3.y+" "+v3.z);
				
				Ponto3D pa = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pb = new Ponto3D(v2.x,v2.y,v2.z);
				Ponto3D pc = new Ponto3D(v3.x,v3.y,v3.z);
				
				Ponto3D pa1 = modelview.multiplicaPonto(pa);
				Ponto3D pb1 = modelview.multiplicaPonto(pb);
				Ponto3D pc1 = modelview.multiplicaPonto(pc);
				
				Ponto3D pa2 = projection.multiplicaPonto(pa1);
				Ponto3D pb2 = projection.multiplicaPonto(pb1);
				Ponto3D pc2 = projection.multiplicaPonto(pc1);
				
				dbg.drawLine((int)pa2.x,(int)pa2.y,(int)pb2.x,(int)pb2.y);
				dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pc2.x,(int)pc2.y);
				dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pa2.x,(int)pa2.y);
				
			}else if(face.nvertices==4) {
				Vector3f v1 = v.get(face.v[0]-1);
				Vector3f v2 = v.get(face.v[1]-1);
				Vector3f v3 = v.get(face.v[2]-1);
				Vector3f v4 = v.get(face.v[3]-1);
				
				Vector3f vn1 = vn.get(face.n[0]-1);
				Vector3f vn2 = vn.get(face.n[1]-1);
				Vector3f vn3 = vn.get(face.n[2]-1);
				Vector3f vn4 = vn.get(face.n[3]-1);
				
				
//				float dot1 = vn1.Dot(camera);
//				float dot2 = vn2.Dot(camera);
//				float dot3 = vn3.Dot(camera);
//				float dot4 = vn4.Dot(camera);
//				
//				if(dot1>0.0) {
//					//System.out.println("exclude "+dot1);
//					excludeclount++;
//					continue;
//				}
				
				
				Vector2f vt1 = vt.get(0);
				Vector2f vt2 = vt.get(0);
				Vector2f vt3 = vt.get(0);
				Vector2f vt4 = vt.get(0);
				
				if(face.t[0]!=-1) {
					vt1 = vt.get(face.t[0]-1);
					vt2 = vt.get(face.t[1]-1);
					vt3 = vt.get(face.t[2]-1);
					vt4 = vt.get(face.t[3]-1);
				}
				
				
				
				
//				glColor3f(0f, 1f, 0f);
//				glBegin(GL_TRIANGLES);
//
//				  glColor3f(0.5f, 0.5f, 0.5f);
//				  glTexCoord2f(vt1.x, vt1.y);
//				  glNormal3f(vn1.x, vn1.y, vn1.z);
//				  glVertex3f(v1.x,v1.y,v1.z);
//				  
//				  glColor3f(0.5f, 0.5f, 0.5f);
//				  glTexCoord2f(vt2.x, vt2.y);
//				  glNormal3f(vn2.x, vn2.y, vn2.z);
//				  glVertex3f(v2.x,v2.y,v2.z);
//			      
//				  glColor3f(0.5f, 0.5f, 0.5f);
//				  glTexCoord2f(vt3.x, vt3.y);
//			      glNormal3f(vn3.x, vn3.y, vn3.z);
//			      glVertex3f(v3.x,v3.y,v3.z);
//			      
//			      glColor3f(0.5f, 0.5f, 0.5f);
//			      glTexCoord2f(vt3.x, vt3.y);
//			      glNormal3f(vn3.x, vn3.y, vn3.z);
//			      glVertex3f(v3.x,v3.y,v3.z);
//			      
//			      glColor3f(0.5f, 0.5f, 0.5f);
//			      glTexCoord2f(vt4.x, vt4.y);
//			      glNormal3f(vn4.x, vn4.y, vn4.z);
//			      glVertex3f(v4.x,v4.y,v4.z);
//			      
//			      glColor3f(0.5f, 0.5f, 0.5f);
//			      glTexCoord2f(vt1.x, vt1.y);
//				  glNormal3f(vn1.x, vn1.y, vn1.z);
//				  glVertex3f(v1.x,v1.y,v1.z);
//			   glEnd();
				
				Ponto3D pa = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pb = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pc = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pd = new Ponto3D(v1.x,v1.y,v1.z);
				
				Ponto3D pa1 = modelview.multiplicaPonto(pa);
				Ponto3D pb1 = modelview.multiplicaPonto(pb);
				Ponto3D pc1 = modelview.multiplicaPonto(pc);
				Ponto3D pd1 = modelview.multiplicaPonto(pd);
				
				Ponto3D pa2 = projection.multiplicaPonto(pa1);
				Ponto3D pb2 = projection.multiplicaPonto(pb1);
				Ponto3D pc2 = projection.multiplicaPonto(pc1);
				Ponto3D pd2 = projection.multiplicaPonto(pd1);
				
				dbg.drawLine((int)pa2.x,(int)pa2.y,(int)pb2.x,(int)pb2.y);
				dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pc2.x,(int)pc2.y);
				dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pa2.x,(int)pa2.y);
				
				dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pd2.x,(int)pd2.y);
				dbg.drawLine((int)pd2.x,(int)pd2.y,(int)pc2.x,(int)pc2.y);
				dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pb2.x,(int)pb2.y);	
				
			}
		}
		
		//System.out.println("f.size "+f.size()+" excludecount "+excludeclount);
	}

	public void desenhaseGrupo(String nome,Graphics2D dbg, Mat4x4 modelview,Mat4x4 projection) {

		GrupoFaces gp = null;
		if (gname.containsKey(nome) == false) {
			System.out.println(" nao rolo");
			return;
		}

		gp = gname.get(nome);
		int inicial = gp.finicial;
		int ffinal = gp.ffinal;

		for (int i = inicial; i < ffinal; i++) {
			Face3D face = f.get(i);

			if (face.nvertices == 3) {
				Vector3f v1 = v.get(face.v[0] - 1);
				Vector3f v2 = v.get(face.v[1] - 1);
				Vector3f v3 = v.get(face.v[2] - 1);

				Vector3f vn1 = vn.get(face.n[0] - 1);
				Vector3f vn2 = vn.get(face.n[1] - 1);
				Vector3f vn3 = vn.get(face.n[2] - 1);
				vn1.Normalize();
				vn2.Normalize();
				vn3.Normalize();

				Vector2f vt1 = vt.get(face.t[0] - 1);
				Vector2f vt2 = vt.get(face.t[1] - 1);
				Vector2f vt3 = vt.get(face.t[2] - 1);

				Ponto3D pa = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pb = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pc = new Ponto3D(v1.x,v1.y,v1.z);
				
				Ponto3D pa1 = modelview.multiplicaPonto(pa);
				Ponto3D pb1 = modelview.multiplicaPonto(pb);
				Ponto3D pc1 = modelview.multiplicaPonto(pc);
				
				Ponto3D pa2 = projection.multiplicaPonto(pa1);
				Ponto3D pb2 = projection.multiplicaPonto(pb1);
				Ponto3D pc2 = projection.multiplicaPonto(pc1);
				
				dbg.drawLine((int)pa2.x,(int)pa2.y,(int)pb2.x,(int)pb2.y);
				dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pc2.x,(int)pc2.y);
				dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pa2.x,(int)pa2.y);

			} else if (face.nvertices == 4) {
				Vector3f v1 = v.get(face.v[0] - 1);
				Vector3f v2 = v.get(face.v[1] - 1);
				Vector3f v3 = v.get(face.v[2] - 1);
				Vector3f v4 = v.get(face.v[3] - 1);

				Vector3f vn1 = vn.get(face.n[0] - 1);
				Vector3f vn2 = vn.get(face.n[1] - 1);
				Vector3f vn3 = vn.get(face.n[2] - 1);
				Vector3f vn4 = vn.get(face.n[3] - 1);

				vn1.Normalize();
				vn2.Normalize();
				vn3.Normalize();
				vn4.Normalize();

				Vector2f vt1 = vt.get(face.t[0] - 1);
				Vector2f vt2 = vt.get(face.t[1] - 1);
				Vector2f vt3 = vt.get(face.t[2] - 1);
				Vector2f vt4 = vt.get(face.t[3] - 1);

				Ponto3D pa = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pb = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pc = new Ponto3D(v1.x,v1.y,v1.z);
				Ponto3D pd = new Ponto3D(v1.x,v1.y,v1.z);
				
				Ponto3D pa1 = modelview.multiplicaPonto(pa);
				Ponto3D pb1 = modelview.multiplicaPonto(pb);
				Ponto3D pc1 = modelview.multiplicaPonto(pc);
				Ponto3D pd1 = modelview.multiplicaPonto(pd);
				
				Ponto3D pa2 = projection.multiplicaPonto(pa1);
				Ponto3D pb2 = projection.multiplicaPonto(pb1);
				Ponto3D pc2 = projection.multiplicaPonto(pc1);
				Ponto3D pd2 = projection.multiplicaPonto(pd1);
				
				dbg.drawLine((int)pa2.x,(int)pa2.y,(int)pb2.x,(int)pb2.y);
				dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pc2.x,(int)pc2.y);
				dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pa2.x,(int)pa2.y);
				
				dbg.drawLine((int)pb2.x,(int)pb2.y,(int)pd2.x,(int)pd2.y);
				dbg.drawLine((int)pd2.x,(int)pd2.y,(int)pc2.x,(int)pc2.y);
				dbg.drawLine((int)pc2.x,(int)pc2.y,(int)pb2.x,(int)pb2.y);

			}
		}
	}

}
