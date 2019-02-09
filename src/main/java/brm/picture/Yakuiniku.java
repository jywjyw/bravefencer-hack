package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img4bitUtil;
import common.Palette;
import common.VramImg;

public class Yakuiniku implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("368-259")); //sure is 368-259
		FileInputStream file = new FileInputStream(splitDir+"SC01/000/1.0");
		file.skip(185*TILE);
		BufferedImage img = Img4bitUtil.readRomToBmp(file, 32, 16, pal);
		ImageIO.write(img, "bmp", new File(exportDir+Yakuiniku.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		BufferedImage img=ImageIO.read(new File(Conf.getRawFile("pic/"+Yakuiniku.class.getSimpleName()+".bmp")));
		VramImg vram = Img4bitUtil.toVramImg(img, pal);
		RandomAccessFile file = new RandomAccessFile(splitDir+"SC01/000/1.0", "rw");
		file.skipBytes(185*TILE);
		file.write(vram.data);
		file.close();
	}

}
