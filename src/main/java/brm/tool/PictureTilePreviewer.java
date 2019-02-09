package brm.tool;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.imageio.ImageIO;

import brm.Conf;
import common.DirLooper;
import common.DirLooper.Callback;
import common.Img4bitUtil;
import common.Img8bitUtil;
import common.Palette;

public class PictureTilePreviewer {
	
	public static void main(String[] args) throws IOException {
		previewAll4bit();
	}
	
	public static void previewAs4bit(String file, int offset, int imgW, int imgH, Palette pal) throws IOException{
		FileInputStream f = new FileInputStream(new File(file));
		f.skip(offset);
		BufferedImage img = Img4bitUtil.readRomToPng32(f, imgW, imgH, pal);
		ImageIO.write(img, "png", new File(Conf.desktop+"block.png"));
		f.close();
	}
	public static void previewAs8bit(String file, int offset, int imgW, int imgH, Palette pal) throws IOException{
		RandomAccessFile f = new RandomAccessFile(new File(file), "r");
		f.seek(offset);
		BufferedImage img = Img8bitUtil.readRomToPng(f, imgW, imgH, pal);
		ImageIO.write(img, "png", new File(Conf.desktop+"block.png"));
		f.close();
	}
	
	public static void previewAll4bit() throws FileNotFoundException{
		String dir = Conf.desktop+"brmjp\\";
		Palette pal16 = new Palette(16, Conf.getRawFile("352-297"));
		DirLooper.loop(dir, new Callback() {
			@Override
			public void handleFile(File f) {
				try {
					if(f.getName().endsWith(".0") || f.getName().endsWith(".1")){//never found image in other format file
						FileInputStream file=new FileInputStream(f);
						int h = (int) (f.length()*2/(32*4));//4bit
						BufferedImage img = Img4bitUtil.readRomToPng32(file, 32, h, pal16);
						String targetName = Conf.desktop+"dump\\"+f.getAbsolutePath().replace(dir, "").replace("\\", "_")+".png";
						ImageIO.write(img, "png", new File(targetName));
						file.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void previewAll8bit() throws FileNotFoundException{
		String dir = Conf.desktop+"brmjp\\";
		Palette pal256 = new Palette(256, Conf.getRawFile("0-482"));
		DirLooper.loop(dir, new Callback() {
			@Override
			public void handleFile(File f) {
				try {
					if(f.getName().endsWith(".0") || f.getName().endsWith(".1")){//never found image in other format file
						RandomAccessFile file = new RandomAccessFile(f, "r");
						int h = (int) (f.length()/(32*2));//8bit
						BufferedImage img = Img8bitUtil.readRomToPng(file, 32, h, pal256);
						String targetName = Conf.desktop+"dump\\"+f.getAbsolutePath().replace(dir, "").replace("\\", "_")+".png";
						ImageIO.write(img, "png", new File(targetName));
						file.close();
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
}
