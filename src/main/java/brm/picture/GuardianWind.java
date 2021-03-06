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

public class GuardianWind implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		FileInputStream file = new FileInputStream(splitDir+"SC05/010/1.0");
		file.skip(146*TILE);
		BufferedImage[] tiles = new BufferedImage[2];
		int i=0;
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		file.skip(22*TILE);
		tiles[i++] = Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		ImageIO.write(Img4bitUtil.jointTiles(Arrays.asList(tiles),2), "bmp", new File(exportDir+GuardianWind.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		BufferedImage img=ImageIO.read(new File(Conf.getRawFile("pic/"+GuardianWind.class.getSimpleName()+".bmp")));
		List<BufferedImage> tiles=Img4bitUtil.splitToTiles(img, 32, 32);
		RandomAccessFile file = new RandomAccessFile(splitDir+"SC05/010/1.0","rw");
		file.skipBytes(146*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(0), pal).data);
		file.skipBytes(22*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(1), pal).data);
		file.close();
	}

}
