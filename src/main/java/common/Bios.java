package common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import brm.Conf;

public class Bios {
	
	public static void main(String[] args) throws Exception {
		new Bios(Conf.outdir+"brm-bios.bin").saveAsBmp( Conf.desktop+"bios.bmp");
	}
	
	private String biosFile;
	private static final int 
			CHAR_W = 16, CHAR_H=15, 
			CHAR_OFFSET=0x66000,	//first char address
			KANJI_OFFSET=0x69d68;	//kanji address
	
	public Bios(String biosFile) {
		this.biosFile = biosFile;
	}
	
	public void eraseFromFirstChar(byte[] data) throws IOException{
		if(data.length>0x198de) throw new UnsupportedOperationException();
		erase(CHAR_OFFSET, data);
	}
	
	//从日文汉字区开始擦除
	public void eraseFromKanji(byte[] data) throws IOException{
		if(data.length>0x15b76) throw new UnsupportedOperationException();
		erase(KANJI_OFFSET, data);
	}
	
	public void erase(int offset, byte[] data) throws IOException{
		RandomAccessFile b = new RandomAccessFile(biosFile, "rw");
		b.seek(offset);
		b.write(data);
		b.close();
	}
	
	//单字符=w16,h15, 从第524个字符开始为汉字区
	public void saveAsBmp(String bmpTarget) throws Exception {
		RandomAccessFile bios = new RandomAccessFile(biosFile, "r");
		bios.seek(0x66000);
		int CHAR_W = 16;
		BufferedImage img = new BufferedImage(CHAR_W, 3489*CHAR_H, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		int buf=0;
		try {
			for(int i=0;i<img.getHeight();i++) {
				buf = bios.readUnsignedShort();
				int x=0;
				for(int j=CHAR_W-1;j>=0;j--) {//每2个字节为字符的一行像素点，将其展开为二进制，1黑0白
					int point = buf>>>j&1;
					int color = point==1?Color.BLACK.getRGB() : Color.WHITE.getRGB();
					img.setRGB(x++, i, color);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		bios.close();
		ImageIO.write(img, "bmp", new File(bmpTarget));
	}
	
	//save as PSP bios font(used by CheatMaster Fusion e.g.)
	public void saveAsPSPFont(String fnt){
		byte[] font=new byte[3489*30]; //3489 chars
		try {
			FileInputStream bios=new FileInputStream(biosFile);
			bios.skip(CHAR_OFFSET);
			bios.read(font);
			bios.close();
			FileOutputStream fos=new FileOutputStream(fnt);
			fos.write(font);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
