package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img8bitUtil;
import common.Palette;
import common.VramImg;

public class Title implements PicHandler {
	
	String f="MAIN/003/0.0";
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		RandomAccessFile file = new RandomAccessFile(splitDir+f, "r");
		
		Palette palP = new Palette(256, Conf.getRawFile("clut/pushstart.256"));
		BufferedImage[] tiles = new BufferedImage[6];
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, palP);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,6), "bmp", new File(exportDir+"pushstart.bmp"));
		
		file.skipBytes(TILE*4);
		tiles = new BufferedImage[2];
		Palette palN = new Palette(256, Conf.getRawFile("clut/newgame.256"));
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, palN);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,2), "bmp", new File(exportDir+"newgame.bmp"));
		
		file.skipBytes(TILE*10);
		tiles = new BufferedImage[2];
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, palN);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,2), "bmp", new File(exportDir+"continue.bmp"));
		
		tiles = new BufferedImage[10*13];
		Palette pal = new Palette(256, Conf.getRawFile("clut/title.256"));
		for(int j=0;j<tiles.length;j++){
			tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, pal);
		}
		ImageIO.write(Img8bitUtil.jointTiles(tiles,10), "bmp", new File(exportDir+"titlebg.bmp"));
		
		file.close();
	}
	

	@Override
	public void import_(String splitDir) throws IOException {
		Palette palP = new Palette(256, Conf.getRawFile("clut/pushstart.256"));
		RandomAccessFile file = new RandomAccessFile(splitDir+f, "rw");
		for(VramImg i:Img8bitUtil.splitToTiles(ImageIO.read(new File(Conf.getRawFile("pic/pushstart.bmp"))), 32,32,palP)){
			file.write(i.data);
		}
		file.skipBytes(4*TILE);
		Palette palN = new Palette(256, Conf.getRawFile("clut/newgame.256"));
		for(VramImg i:Img8bitUtil.splitToTiles(ImageIO.read(new File(Conf.getRawFile("pic/newgame.bmp"))), 32,32,palN)){
			file.write(i.data);
		}
		file.skipBytes(10*TILE);
		for(VramImg i:Img8bitUtil.splitToTiles(ImageIO.read(new File(Conf.getRawFile("pic/continue.bmp"))), 32,32,palN)){
			file.write(i.data);
		}
		Palette pal = new Palette(256, Conf.getRawFile("clut/title.256"));
		for(VramImg i:Img8bitUtil.splitToTiles(ImageIO.read(new File(Conf.getRawFile("pic/titlebg.bmp"))), 32,32,pal)){
			file.write(i.data);
		}
		file.close();
	}

}
