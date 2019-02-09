package common;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Img8bitUtil {
	
	public static BufferedImage readRomToBmp(InputStream in, int vramW, int vramH, Palette pal) throws IOException {
		int displayW = vramW*2;	//8bit下显示宽度*2;
		BufferedImage out = new BufferedImage(displayW, vramH, BufferedImage.TYPE_INT_RGB);
		return readRomToImg(in, vramW, vramH, pal, displayW, out);
	}
	
	public static BufferedImage readRomToBmp(RandomAccessFile in, int vramW, int vramH, Palette pal) throws IOException {
		int displayW = vramW*2;	//8bit下显示宽度*2;
		BufferedImage out = new BufferedImage(displayW, vramH, BufferedImage.TYPE_INT_RGB);
		return readRomToImg(in, vramW, vramH, pal, displayW, out);
	}
	
	public static BufferedImage readRomToPng(RandomAccessFile in, int vramW, int vramH, Palette pal) throws IOException {
		int displayW = vramW*2;	//8bit下显示宽度*2;
		BufferedImage out = new BufferedImage(displayW, vramH, BufferedImage.TYPE_INT_ARGB);
		return readRomToImg(in, vramW, vramH, pal, displayW, out);
	}
	
	private static BufferedImage readRomToImg(RandomAccessFile in, int vramW, int vramH, Palette pal, int displayW, BufferedImage out) throws IOException {
		WritableRaster raster = out.getRaster();
		byte[] buf = new byte[vramW*2];
		int x=0,y=0;
		boolean _break=false;
		while(true) {
			in.read(buf);
			for(byte b : buf) {
				int[] color =pal.getRgba8888Matrix()[b&0xff];
				raster.setPixel(x++, y, color);	//rgba
				if(x>=displayW) {
					x=0;
					y++;
					if(y>=vramH) {
						_break=true;
						break;
					}
				}
			}
			if(_break)break;
		}
		return out;
	}
	
	private static BufferedImage readRomToImg(InputStream in, int vramW, int vramH, Palette pal, int displayW, BufferedImage out) throws IOException {
		WritableRaster raster = out.getRaster();
		byte[] buf = new byte[vramW*2];
		int x=0,y=0;
		boolean _break=false;
		while(true) {
			in.read(buf);
			for(byte b : buf) {
				int[] color =pal.getRgba8888Matrix()[b&0xff];
				raster.setPixel(x++, y, color);	//rgba
				if(x>=displayW) {
					x=0;
					y++;
					if(y>=vramH) {
						_break=true;
						break;
					}
				}
			}
			if(_break)break;
		}
		return out;
	}
	
	//从palette中查找精确的颜色
	public static VramImg toVramImg(BufferedImage png, Palette pal){
		return toVramImg(png, new PixelConverter(){
			@Override
			public int toPalIndex(int[] pixel) {
				return pal.getExactColorIndex(pixel[0], pixel[1], pixel[2]);
			}
		});
	}
	
	public static VramImg toVramImg(BufferedImage png, PixelConverter cb){
		ByteBuffer ret = ByteBuffer.allocate(png.getWidth()*png.getHeight());	
		int[] pixel=new int[3];
		for(int y=0;y<png.getHeight();y++){
			for(int x=0;x<png.getWidth();x++){	//8 bit image = 1 BytePerPixel
				png.getRaster().getPixel(x, y, pixel);
				ret.put((byte)cb.toPalIndex(pixel));
			}
		}
		return new VramImg(png.getWidth()/2, png.getHeight(), ret.array());//8bit模式下,图像显示宽度*2. 4bit下,图像显示宽度*4
	}
	
	
	/**
	 * 把32*32的小图块拼合成整张图片
	 * @param tiles wh=(32*bpp)*32
	 * @param column 每一行几张tile
	 * @throws IOException
	 */
	public static BufferedImage jointTiles(BufferedImage[] tiles, int column) throws IOException{
		int tileW=32*2;
		BufferedImage img = new BufferedImage(tileW*column, 32*tiles.length/column, tiles[0].getType());
		WritableRaster raster = img.getRaster();
		int[] buf=new int[tileW*32*4];
		int x=0,y=0,tileI=0;
		
		while(true){
			tiles[tileI].getRaster().getPixels(0, 0, tileW, 32, buf);
			raster.setPixels(x, y, tileW, 32, buf);
			
			tileI++;
			if(tileI>=tiles.length) break;
			x+=tileW;
			if(x>=img.getWidth()){
				x=0;
				y+=32;
			}
		}
		return img;
	}
	
	public static List<BufferedImage> splitToTiles(BufferedImage src, int tileVramW, int tileH){
		List<BufferedImage> tiles = new ArrayList<>();
		for(int y=0;y<src.getHeight();y+=tileH){
			for(int x=0;x<src.getWidth();x+=tileVramW*2){
				tiles.add(src.getSubimage(x, y, tileVramW*2, tileH));
			}
		}
		return tiles;
	}
	
	public static List<VramImg> splitToTiles(BufferedImage src, int tileVramW, int tileH, Palette pal){
		List<VramImg> ret = new ArrayList<>();
		for(int y=0;y<src.getHeight();y+=32){
			for(int x=0;x<src.getWidth();x+=tileVramW*2){
				BufferedImage sub = src.getSubimage(x, y, tileVramW*2, tileH);
				ret.add(toVramImg(sub, pal));
			}
		}
		return ret;
	}
}
