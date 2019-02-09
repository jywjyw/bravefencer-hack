package common;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import brm.Conf;

public class Png8 {
	
	public static void main(String[] args) {
		gen();
		read();
	}
	
	public static void gen()  {
		final int[] colors = {   0xff00ff00, 0xff000000, 0xffffffff, 0xff353535, 0xff888888, 0xff969696, 0xff237fe9, 0xffff0000 };//argb8888
		IndexColorModel colorModel = new IndexColorModel(8, colors.length, colors, 0, true, 0, DataBuffer.TYPE_BYTE );
		BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_BYTE_INDEXED, colorModel);
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
//				image.getRaster().setPixel(x, y, new int[]{0x96,0x96,0x96,0xff});	//TYPE_BYTE_INDEXED时,像素点的值是索引值,并非RGB,不能这样写颜色
				image.getRaster().setPixel(x, y, new int[]{3});	//TYPE_BYTE_INDEXED时,按colorModel的索引值写
//				image.setRGB(x, y, 0xff888888);		//这样写也可以
				
			}
		}
		try {
			ImageIO.write(image, "PNG", new File(Conf.desktop+"my.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read()  {
		try {
			BufferedImage image = ImageIO.read(new File(Conf.desktop+"my.png"));
			int[] buf=new int[4];
			for(int y=0;y<image.getHeight();y++){
				for(int x=0;x<image.getWidth();x++){
					System.out.printf("%x\n",image.getRGB(x, y));
					image.getRaster().getPixel(x, y, buf);
					System.out.printf("(%x,%x,%x,%x)\n",buf[0],buf[1],buf[2],buf[3]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
