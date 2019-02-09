package brm.tool;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import brm.Conf;

public class FontViewer {
	public static void main(String[] args) throws Exception {
		int offset=0x703a0;
		System.out.printf("%05X\n",Conf.SCRIPT2_ADDR+offset);
		view(Conf.desktop+"brmjp/SC07/006/1.4", 0x800d3c24-Conf.SCRIPT2_ADDR, 100);
//		view(Conf.desktop+"brmjp/SC05/004/0.4", offset, 2000);
//		view(Conf.desktop+"ram.bin", 0x1f3d69, 500);
//		viewMainFontlib();
		
//		System.out.printf("%06X\n",Util.hilo(offset+Conf.SCRIPT2_ADDR));
		
//		viewMainFontlib();
//		viewDayIconlib();
	}
	
	public static void viewMainFontlib(){
		int offset=0x4c4c;
		view(Conf.desktop+"brmjp/MAIN/010/1.1", offset, 618);
	}
	
	public static void viewDayIconlib(){
		int offset=0x80d0-0x16;
		view(Conf.desktop+"brmjp/MAIN/010/1.1", offset, 7);
	}
	
	/**
	 *每个字符占22个字节
	 * @param file  字库所在文件
	 * @param startAddr 字库起始地址
	 * @param charcount 字库中字符个数
	 */
	public static void view(String file, long startAddr, int charcount)  {
		int imgw = 16, imgh = charcount*11;	
		BufferedImage img = new BufferedImage(imgw, imgh, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.WHITE);	
		g.fillRect(0, 0, imgw, imgh);
		
		try {
			RandomAccessFile cd = new RandomAccessFile(new File(file), "r");
			cd.seek(startAddr);//00001e0021002100210021002100	//0x30ec4c
			int buf=0;
			for(int i=0;i<imgh;i++) {
				buf = cd.readUnsignedShort();
				int x=0;
				for(int j=15;j>=0;j--) {
					int point = buf>>>j&1;
					int color = point==1?Color.BLACK.getRGB() : Color.WHITE.getRGB();
					img.setRGB(x++, i, color);
				}
			}
//		System.out.println(cd.getFilePointer());
			cd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ImageIO.write(img, "bmp", new File(Conf.desktop+"font.bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void viewSingleChar(byte[] img, String file) {
		int imgw = 16, imgh = 11;	
		BufferedImage imgFile = new BufferedImage(imgw, imgh, BufferedImage.TYPE_INT_RGB);
		Graphics g = imgFile.getGraphics();
		g.setColor(Color.WHITE);	
		g.fillRect(0, 0, imgw, imgh);
		
		try {
			DataInputStream is = new DataInputStream(new ByteArrayInputStream(img));
			int buf=0;
			for(int i=0;i<imgh;i++) {
				buf = is.readUnsignedShort();
				int x=0;
				for(int j=15;j>=0;j--) {
					int point = buf>>>j&1;
					int color = point==1?Color.BLACK.getRGB() : Color.WHITE.getRGB();
					imgFile.setRGB(x++, i, color);
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ImageIO.write(imgFile, "bmp", new File(file+".bmp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
