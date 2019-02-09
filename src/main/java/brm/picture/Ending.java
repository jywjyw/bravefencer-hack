package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img4bitUtil;
import common.Palette;

public class Ending implements PicHandler {
	
	public static void main(String[] args) throws Exception {
		new Ending().import_(Conf.desktop+"brmjp/");
	}
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("clut/ending.16"));
		FileInputStream file=new FileInputStream(splitDir+"SC07/009/1.0");
		int i=0;
		BufferedImage[] tiles = new BufferedImage[8];
		file.skip(151*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(7*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(7*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(5*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		
		BufferedImage joint = Img4bitUtil.jointTiles(Arrays.asList(tiles),2);
		ImageIO.write(joint, "bmp", new File(exportDir+Ending.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		Palette pal = new Palette(16, Conf.getRawFile("clut/ending.16"));
		BufferedImage src= ImageIO.read(new File(Conf.getRawFile("pic/"+Ending.class.getSimpleName()+".bmp")));
		List<BufferedImage> tiles = Img4bitUtil.splitToTiles(src, 32,32);
		RandomAccessFile file=new RandomAccessFile(splitDir+"SC07/009/1.0","rw");
		int i=0;
		file.skipBytes(151*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.skipBytes(7*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.skipBytes(7*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.skipBytes(5*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.write(Img4bitUtil.toVramImg(tiles.get(i++), pal).data);
		file.close();
	}

}
