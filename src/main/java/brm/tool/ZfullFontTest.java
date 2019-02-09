package brm.tool;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import brm.Conf;

public class ZfullFontTest {
	
	public static void main(String[] args) throws Exception {
		String s="魔猪决定绿色";
		BufferedImage large = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
		InputStream is = new FileInputStream(Conf.desktop+"Zfull-GB.ttf"); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		font=font.deriveFont(Font.PLAIN, 10);
		
		Graphics2D g = (Graphics2D) large.getGraphics();
//		g.setColor(Color.WHITE);	
//		g.fillRect(0, 0, large.getWidth(), large.getHeight());
		
//		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(s, 0, 20);	//向上,向左修正N个像素,和边缘对齐
		
		g.dispose();
		ImageIO.write(large, "bmp", new File(Conf.desktop+"zfull.bmp"));
	}

}
