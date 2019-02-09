package brm.hack;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brm.Conf;
import brm.tool.FontViewer;
import common.RscLoader;
import common.Util;
import common.RscLoader.Callback;

public class FontGen {
	
	public static void main(String[] args) throws FontFormatException, IOException {
//		String s = "好＜＞○／□△×%速％？！?!BPＢＰ０１２３４鉴01234（）[]「」…";
		String s = "ab・c";
		List<String> l=new ArrayList<>();
		char[] cs=s.toCharArray();
		for(int i=0;i<cs.length;i++){
			l.add(cs[i]+"");
		}
		System.out.println(String.join(",", l.toArray(new String[]{})));
		byte[] bs = new FontGen().gen(l);
		
		File tmp = new File(Conf.desktop+"xxxx");
		FileOutputStream fos = new FileOutputStream(tmp);
		fos.write(bs);
		fos.close();
		FontViewer.view(tmp.getAbsolutePath(), 0, s.length());
		tmp.delete();
	}
	
	private static Map<String,String> REPLACE = new HashMap<>();
	private static Map<String,byte[]> SPECIAL = new HashMap<>();
	
	static {
		RscLoader.load("font.utf8", "utf-8", new Callback() {
			@Override
			public void doInline(String line) {
				String[] arr=line.split("=");
				if(arr[1].length()==1)
					REPLACE.put(arr[0], arr[1]);
				else 
					SPECIAL.put(arr[0], Util.decodeHex(arr[1]));
			}
		});
	}
	
	int charW=16,charH=11;
	
	public byte[] gen(List<String> chars){
		for(int i=0;i<chars.size();i++){
			String c = chars.get(i);
			if(REPLACE.containsKey(c)) chars.set(i, REPLACE.get(c));
		}
		List<String> commonchars = new ArrayList<>();
		for(String c:chars){
			if(SPECIAL.get(c)==null) commonchars.add(c);
		}
		BufferedImage img=null;
		try {
			if(commonchars.size()>0){
				img = genCharImage(commonchars);
			}
			int[] point = new int[3];
			
			ByteBuffer ret = ByteBuffer.allocate(chars.size()*Conf.CHAR_BYTES);
			for(String c:chars){
				byte[] spec=SPECIAL.get(c);
				if(spec!=null){//insert spec char img to bytes
					ret.put(spec);
				} else if(commonchars.size()>0){
					int commonI = commonchars.indexOf(c);
					for(int y=commonI*charH; y<(commonI+1)*charH; y++){
						int pixelRow=0;//assert charW<=32
						for(int x=0; x<img.getWidth(); x++){
							pixelRow<<=1;
							img.getRaster().getPixel(x, y, point);
							if(point[0]==0) pixelRow = pixelRow|1;//pixel is black or white, if r or g or b equals 0, means this pixel is black
						}
						ret.putShort((short)pixelRow);	//assert　one pixel line occupy 2 bytes 
					}
				}
			}
			return ret.array();
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private BufferedImage genCharImage(List<String> cs) throws FontFormatException, IOException {
		if(cs.size()==0) throw new RuntimeException("chars must not be empty");
		int imgH=(cs.size())*charH;
		BufferedImage large = new BufferedImage(charW, imgH, BufferedImage.TYPE_INT_RGB);
		InputStream is = new FileInputStream(Conf.getRawFile("Zpix.ttf")); 
		Font font = Font.createFont(Font.TRUETYPE_FONT, is);
		is.close();
		font=font.deriveFont(Font.PLAIN, 12);
		for(String c : cs) {
			if(c.length()>1 || !font.canDisplay(c.toCharArray()[0])) 
				throw new RuntimeException("字库无法识别以下字符:" + c);
		}
		
		Graphics2D g = (Graphics2D) large.getGraphics();
		g.setColor(Color.WHITE);	
		g.fillRect(0, 0, charW, imgH);
		
		g.setColor(Color.BLACK);
		g.setFont(font);
		for(int i=0;i<cs.size();i++) {
			int y = (i+1)*(charH);
			g.drawString(cs.get(i), -1, y-2);	//向上,向左修正N个像素,和边缘对齐
		}
		
		g.dispose();
//ImageIO.write(large, "bmp", new File(Conf.desktop+"myfont.bmp"));
	    return large;
	}
	
}
