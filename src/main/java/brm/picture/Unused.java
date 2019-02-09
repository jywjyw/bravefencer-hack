package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img4bitUtil;
import common.Img8bitUtil;
import common.Palette;

public class Unused implements PicHandler{
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		export0(splitDir,exportDir);
		export1(splitDir,exportDir);
		export2(splitDir,exportDir);
		export3(splitDir,exportDir);
		export4(splitDir,exportDir);
	}

	public void export0(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(256, Conf.getRawFile("clut/title.256"));
		RandomAccessFile file = new RandomAccessFile(splitDir+"MAIN/001/0.0", "r");
		
		BufferedImage[] tiles = new BufferedImage[10*14];
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, pal);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,10), "bmp", new File(exportDir+"unused0.bmp"));
		file.close();
	}

	public void export1(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(256, Conf.getRawFile("clut/title.256"));
		RandomAccessFile file = new RandomAccessFile(splitDir+"MAIN/005/0.0", "r");
		
		BufferedImage[] tiles = new BufferedImage[10*14];
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, pal);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,10), "bmp", new File(exportDir+"unused1.bmp"));
		file.close();
	}
	
	public void export2(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(256, Conf.getRawFile("clut/title.256"));
		RandomAccessFile file = new RandomAccessFile(splitDir+"MAIN/008/0.0", "r");
		
		BufferedImage[] tiles = new BufferedImage[5*7];
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, pal);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,5), "bmp", new File(exportDir+"unused2.bmp"));
		file.close();
	}
	
	public void export3(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("352-297"));
		FileInputStream file = new FileInputStream(splitDir+"MAIN/009/1.0");
		int tileBytes=64*32,i=0;
		BufferedImage[] tiles = new BufferedImage[8];
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(tileBytes*19);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(tileBytes*19);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(tileBytes*19);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		ImageIO.write(Img4bitUtil.jointTiles(Arrays.asList(tiles),2), "bmp", new File(exportDir+"unused3.bmp"));
		file.close();
	}
	
	
	public void export4(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(256, Conf.getRawFile("clut/title.256"));
		RandomAccessFile file = new RandomAccessFile(splitDir+"MAIN/009/1.0", "r");
		int tileBytes=64*32,i=0,col=19;
		file.skipBytes(tileBytes*0);
		BufferedImage[] tiles = new BufferedImage[col*30];
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, pal);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,col), "bmp", new File(exportDir+"unused4.bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		
	}

}
