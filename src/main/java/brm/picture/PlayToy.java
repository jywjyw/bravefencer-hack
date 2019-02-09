package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img4bitUtil;
import common.Palette;

public class PlayToy implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("clut/playtoy.16"));
		FileInputStream file=new FileInputStream(splitDir+"SC01/005/1.0");	
		List<BufferedImage> tiles = new ArrayList<>();
		file.skip(172*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		file.skip(2*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		file.skip(2*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		file.skip(1*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		
		BufferedImage joint = Img4bitUtil.jointTiles(tiles,1);
		ImageIO.write(joint, "bmp", new File(exportDir+PlayToy.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		Palette pal = new Palette(16, Conf.getRawFile("clut/playtoy.16"));
		BufferedImage img=ImageIO.read(new File(Conf.getRawFile("pic/"+PlayToy.class.getSimpleName()+".bmp")));
		List<BufferedImage> tiles=Img4bitUtil.splitToTiles(img, 32, 32);
		
		RandomAccessFile file=new RandomAccessFile(splitDir+"SC01/005/1.0","rw");
		file.skipBytes(172*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(0), pal).data);
		file.skipBytes(2*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(1), pal).data);
		file.skipBytes(2*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(2), pal).data);
		file.skipBytes(1*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(3), pal).data);
		file.close();
		
		file=new RandomAccessFile(splitDir+"SC01/006/1.0","rw");
		file.skipBytes(179*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(0), pal).data);
		file.skipBytes(8*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(1), pal).data);
		file.skipBytes(8*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(2), pal).data);
		file.skipBytes(7*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(3), pal).data);
		file.close();
	}
	
	public void erase(String splitDir) throws IOException {
		RandomAccessFile file=new RandomAccessFile(splitDir+"SC01/005/1.0","rw");
		byte[] zero=new byte[TILE];
		file.skipBytes(172*TILE);
		file.write(zero);
		file.skipBytes(2*TILE);
		file.write(zero);
		file.skipBytes(2*TILE);
		file.write(zero);
		file.skipBytes(1*TILE);
		file.write(zero);
		file.close();
	}

}
