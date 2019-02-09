package brm.hack;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import common.RscLoader;

public class Font8x8 {
	public BufferedImage font;
	public Map<String,XY> coordinate=new LinkedHashMap<>(); 
	
	public Font8x8() {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("font8x8.bmp");
			font=ImageIO.read(is);
			is.close();
			
			RscLoader.load("font8x8.fnt", "gbk", new RscLoader.Callback() {
				@Override
				public void doInline(String line) {
					String[] arr=line.split("=");
					String[] v=arr[1].split(",");
					XY xy=new XY();
					xy.fromX=Integer.parseInt(v[0]);
					xy.fromY=Integer.parseInt(v[1]);
					xy.toX=Integer.parseInt(v[2]);
					xy.toY=Integer.parseInt(v[3]);
					coordinate.put(arr[0], xy);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class XY{
		public int fromX,fromY,toX,toY;
	}
	
	public interface Callback{
		void do_(Entry<String,XY> kv);
	}
}
