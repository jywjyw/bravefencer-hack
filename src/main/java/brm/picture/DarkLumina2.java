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

public class DarkLumina2 implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		FileInputStream file = new FileInputStream(splitDir+"SC07/003/0.0");
		int i=0;
		BufferedImage[] tiles = new BufferedImage[3];
		file.skip(96*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(9*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(9*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		ImageIO.write(Img4bitUtil.jointTiles(Arrays.asList(tiles),3), "bmp", new File(exportDir+DarkLumina2.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		BufferedImage img=ImageIO.read(new File(Conf.getRawFile("pic/"+DarkLumina2.class.getSimpleName()+".bmp")));
		List<BufferedImage> tiles=Img4bitUtil.splitToTiles(img, 32, 32);
		
		RandomAccessFile file=new RandomAccessFile(splitDir+"SC07/003/0.0", "rw");
		file.skipBytes(96*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(0), pal).data);
		file.skipBytes(9*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(1), pal).data);
		file.skipBytes(9*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(2), pal).data);
		file.close();
		
	}

}
