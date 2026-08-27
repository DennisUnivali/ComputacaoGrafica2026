import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MainCanvas extends JPanel implements Runnable{
	int W = 640;
	int H = 480;
	
	Thread runner;
	boolean ativo = true;
	int paintcounter = 0;
	
	BufferedImage imageBuffer;
	byte bufferDeVideo[];
	
	Random rand = new Random();
	
	byte memoriaPlacaVideo[];
	short paleta[][];
	
	int framecount = 0;
	int fps = 0;
	
	Font f = new Font("", Font.PLAIN, 30);
	
	int clickX = 0;
	int clickY = 0;
	int mouseX = 0;
	int mouseY = 0;
	
	int pixelSize = 0;
	int Largura = 0;
	int Altura = 0;
	
	BufferedImage imgtmp = null;
	
	float posx = 00;
	float posy = 00;
	
	boolean LEFT = false;
	boolean RIGHT = false;
	boolean UP = false;
	boolean DOWN = false;
	
	float filtroR = 1;
	float filtroG = 1;
	float filtroB = 1;
	
	float q1x = 10,q1y = 100;
	float q2x = 10,q2y = 200;
	
	public MainCanvas() {
		
		File f = new File("imgbmp.bmp");
		try {
			FileInputStream fin = new FileInputStream(f);

			byte todosodbytes[] = new byte[64000];
			int byteslidos = fin.read(todosodbytes);
			System.out.println("Bytes Lidos "+byteslidos);
			for(int i = 0; i < byteslidos;i++) {
				System.out.println(i+": "+todosodbytes[i]);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		setSize(640,480);
		setFocusable(true);
		
		Largura = 640;
		Altura = 480;
		
		pixelSize = 640*480;
		
		
//		try {
//			imgtmp = ImageIO.read(getClass().getResource("fundo.jpg"));
//			System.out.println(""+imgtmp.toString());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		imgtmp = loadImage("fundo.jpg");
		
		imageBuffer = new BufferedImage(640,480, BufferedImage.TYPE_4BYTE_ABGR);
		//imageBuffer.getGraphics().drawImage(imgtmp, 0, 0, null);
		
		
		bufferDeVideo = ((DataBufferByte)imageBuffer.getRaster().getDataBuffer()).getData();
		
		System.out.println("Buffer SIZE "+bufferDeVideo.length );
		
		
//		File f = new File("t1.bmp");
//		try {
//			DataInputStream din = new DataInputStream(new FileInputStream(f));
//			byte b[] = new byte[128];
//			int quant = 0;
//			int cont = 0;
//			while((quant = din.read(b))>=0) {
//				for(int i = 0; i < quant;i++) {
//					System.out.print(""+(b[i]&0xff)+" ");
//				}
//				System.out.println();
//				cont++;
//				if(cont==10) {
//					break;
//				}
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		for(int i = 0; i < H;i++) {
//			for(int j = 0; j < W;j++) {
//				int pos = (i*W*4)+(j*4);
//				
//				int soma = bufferDeVideo[pos+1]&0xff;
//				soma += bufferDeVideo[pos+2]&0xff;
//				soma += bufferDeVideo[pos+3]&0xff;
//				
//				int media = soma/3;
//				//System.out.println(""+media);
//				
//				bufferDeVideo[pos+1] = (byte)(Math.min((media*20)/100,255)&0x00ff);
//				bufferDeVideo[pos+2] = (byte)(Math.min((media*60)/100,255)&0x00ff);
//				bufferDeVideo[pos+3] = (byte)(Math.min((media*20)/100,255)&0x00ff);
//			}
//		}
		
		//memoriaPlacaVideo = new byte[W*H];
		
		
		/*paleta = new short[255][3];
		
		for(int i = 0; i < 255;i++){
			paleta[i][0] = (short)rand.nextInt(255);
			paleta[i][1] = (short)rand.nextInt(255);
			paleta[i][2] = (short)rand.nextInt(255);
			
		}*/
		
		//Seta Bugfeer com noise
		/*for(int i = 0; i < bufferDeVideo.length;i+=4){
			int r = rand.nextInt(255);
			int g = rand.nextInt(255);
			int b = rand.nextInt(255);
			
			bufferDeVideo[i] = (byte)0x00ff;
			bufferDeVideo[i+1] = (byte)(0x00ff&b);
			bufferDeVideo[i+2] = (byte)(0x00ff&g);
			bufferDeVideo[i+3] = (byte)(0x00ff&r);
		}8?
		
//		// 100,20 200,20
//		for(int i = 0; i < 100;i++){
//			int x = 100+i;
//			int y = 20;
//			int bt = x*4+y*640*4;
//			bufferDeVideo[bt] = (byte)0x00ff;
//			bufferDeVideo[bt+1] = (byte)0;
//			bufferDeVideo[bt+2] = (byte)0;
//			bufferDeVideo[bt+3] = (byte)0x00ff;
//		}
		
		/*for(int y = 0; y < H;y++){
			for(int x = 0; x < W;x++){
				memoriaPlacaVideo[x+y*W] = (byte)((y%255)&0x00ff);
			}
		}*/
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_W) {
					UP = false;
				}
				if(key == KeyEvent.VK_S) {
					DOWN = false;
				}
				if(key == KeyEvent.VK_A) {
					LEFT = false;
				}
				if(key == KeyEvent.VK_D) {
					RIGHT = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				//System.out.println("CLICO "+key);
				if(key == KeyEvent.VK_W) {
					UP = true;
				}
				if(key == KeyEvent.VK_S) {
					DOWN = true;
				}
				if(key == KeyEvent.VK_A) {
					LEFT = true;
				}
				if(key == KeyEvent.VK_D) {
					RIGHT = true;
				}
			}
		});		
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				clickX = e.getX();
				clickY = e.getY();
				
				System.out.println("CLICO ");
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseX = arg0.getX();
				mouseY = arg0.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

		
	}
	private void drawImageToBuffer(BufferedImage image,int x,int y, float fr, float fg, float fb) {
		byte[] imgBuffer = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		
		
		int iw = image.getWidth();
		int ih = image.getHeight();
		
		for(int yi = 0; yi < ih; yi++) {
			for(int xi = 0; xi < iw; xi++) {
				int pixi = yi*iw*4 + xi*4;
				int pixb = (yi+y)*W*4 + (xi+x)*4;
				bufferDeVideo[pixb] = imgBuffer[pixi];
				
				//BW
//				int soma = (imgBuffer[pixi+1]&0xff) + (imgBuffer[pixi+2]&0xff) + (imgBuffer[pixi+3]&0xff);
//				int res = (int)(soma/3);
//				
//				bufferDeVideo[pixb+1] = (byte)(res&0xff);
//				bufferDeVideo[pixb+2] = (byte)(res&0xff);
//				bufferDeVideo[pixb+3] = (byte)(res&0xff);
				
				
				int b = (imgBuffer[pixi+1]&0xff);
				int g =	(imgBuffer[pixi+2]&0xff);
				int r = (imgBuffer[pixi+3]&0xff);
				
				b = (int)(b*fb);
				g = (int)(g*fg);
				r = (int)(r*fr);
				
				b = Math.min(255, b);
				g = Math.min(255, g);
				r = Math.min(255, r);
				
				bufferDeVideo[pixb+1] = (byte)(b&0xff);
				bufferDeVideo[pixb+2] = (byte)(g&0xff);
				bufferDeVideo[pixb+3] = (byte)(r&0xff);
			}
		}
	}
	@Override
	public void paint(Graphics g) {
		
		for(int i = 0; i < bufferDeVideo.length; i++) {
			bufferDeVideo[i] = 0;
		}
		
		drawImageToBuffer(imgtmp,(int)posx,(int)posy,filtroR,filtroG,filtroB);
		
		//desenhaLinhaHorizontal((int)posx-100,(int)posy,200);
		
		//desenhaLinhaVertical((int)posx,(int)posy-100,200);
		
		
		
		//desenhaLinhaVertical(300,200,200);
		
		// TODO Auto-generated method stub
		//super.paint(g);
		
//		for(int i = 0; i < bufferDeVideo.length;i+=4){
//			int rr = rand.nextInt(255);
//			int gg = rand.nextInt(255);
//			int bb = rand.nextInt(255);
//			
//			bufferDeVideo[i] = (byte)0x00ff;
//			bufferDeVideo[i+1] = (byte)(0x00ff&bb);
//			bufferDeVideo[i+2] = (byte)(0x00ff&gg);
//			bufferDeVideo[i+3] = (byte)(0x00ff&rr);
//		}
		
		/*for(int i = 0; i < memoriaPlacaVideo.length;i++){
			int bufferindex = i*4;
			bufferDeVideo[bufferindex] = (byte)0x00ff;
			bufferDeVideo[bufferindex+1] = (byte)(paleta[memoriaPlacaVideo[i]&0x00ff][2]&0x00ff);
			bufferDeVideo[bufferindex+2] = (byte)(paleta[memoriaPlacaVideo[i]&0x00ff][1]&0x00ff);
			bufferDeVideo[bufferindex+3] = (byte)(paleta[memoriaPlacaVideo[i]&0x00ff][0]&0x00ff);
		}*/
		
		g.setFont(f);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, 640, 480);
//		g.setColor(Color.black);
//		g.drawLine(0, 0, 640, 480);
		
		//g.drawImage(imageBuffer,0,0,null);
		
		//g.setColor(Color.BLUE);
		//g.drawLine(clickX, clickY, mouseX, mouseY);
		
		g.setColor(Color.blue);
		g.fillRect((int)q1x,(int)q1y, 10, 10);
		
		g.setColor(new Color(0,128,0));
		g.fillRect((int)q2x,(int)q2y, 10, 10);
		
		g.setColor(Color.black);
		g.drawString("FPS "+fps+" mouse: "+mouseX+","+mouseY, 10, 25);
	}
	
	public void desenhaLinhaHorizontal(int x, int y,int w) {
		int pospix = y*(W*4)+x*4;
		
		for(int i = 0; i < w;i++) {
			
			bufferDeVideo[pospix] = (byte)255;
			bufferDeVideo[pospix+1] = (byte)0;
			bufferDeVideo[pospix+2] = (byte)0;
			bufferDeVideo[pospix+3] = (byte)0;
			pospix+=4;
		}
	}
	
	public void desenhaLinhaVertical(int x, int y,int h) {
		int pospix = y*(W*4)+x*4;
		
		for(int i = 0; i < h;i++) {
			
			bufferDeVideo[pospix] = (byte)255;
			bufferDeVideo[pospix+1] = (byte)0;
			bufferDeVideo[pospix+2] = (byte)0;
			bufferDeVideo[pospix+3] = (byte)255;
			pospix+=(W*4);
		}
	}
	
	public void desenhaPixel(int x, int y,int r,int g,int b) {
		int pospix = y*(W*4)+x*4;
			
		bufferDeVideo[pospix] = (byte)255;
		bufferDeVideo[pospix+1] = (byte)(b&0xff);
		bufferDeVideo[pospix+2] = (byte)(g&0xff);
		bufferDeVideo[pospix+3] = (byte)(r&0xff);
	
	}
	
	public void start(){
		runner = new Thread(this);
		runner.start();
	}
	
	int timer = 0;
	public void simulaMundo(long diftime){
		
		float difS = diftime/1000.0f;
		float vel = 50;
		
		timer+=diftime;
		if(timer>=1000) {
			timer = 0;
			filtroR = rand.nextFloat();
			filtroG = rand.nextFloat();
			filtroB = rand.nextFloat();
		}
		
		if(UP) {
			posy -= vel*difS;
		}
		if(DOWN) {
			posy += vel*difS;
		}
		if(LEFT) {
			posx -= vel*difS;
		}
		if(RIGHT) {
			posx += vel*difS;
		}
		
		q1x+=0.2;
		//q2x=q2x+100*diftime/1000.0f;
		float dx = mouseX-q2x;
		float dy = mouseY-q2y;
		
		double ang = Math.atan2(dy, dx);
		
		q2x = (float)(q2x+Math.cos(ang)*100*diftime/1000.0f);
		q2y = (float)(q2y+Math.sin(ang)*100*diftime/1000.0f);
	}
	
	
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		long segundo = time/1000;
		long diftime = 0;
		while(ativo){
			simulaMundo(diftime);
			paintImmediately(0, 0, 640, 480);
			paintcounter+=100;
			
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long newtime = System.currentTimeMillis();
			long novoSegundo = newtime/1000;
			diftime = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			framecount++;
			if(novoSegundo!=segundo) {	
				fps = framecount;
				framecount = 0;
				segundo = novoSegundo;
			}
		}
	}
	
	public BufferedImage loadImage(String filename) {
		try {
			imgtmp = ImageIO.read(new File(filename));
			
			BufferedImage imgout = new BufferedImage(imgtmp.getWidth(), imgtmp.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			
			imgout.getGraphics().drawImage(imgtmp, 0, 0, null);
			
			imgtmp = null;
			
			return imgout;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
