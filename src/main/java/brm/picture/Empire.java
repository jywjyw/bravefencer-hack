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

public class Empire implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		FileInputStream file=new FileInputStream(splitDir+"SC07/011/0.0");
		List<BufferedImage> tiles = new ArrayList<>();
		file.skip(173*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		file.skip(1*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		file.skip(1*TILE);
		tiles.add(Img4bitUtil.readRomToBmp(file, 32, 32, pal));
		
		BufferedImage joint = Img4bitUtil.jointTiles(tiles,1);
		ImageIO.write(joint, "bmp", new File(exportDir+Empire.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		Palette pal = new Palette(16, Conf.getRawFile("368-259"));
		BufferedImage img=ImageIO.read(new File(Conf.getRawFile("pic/"+Empire.class.getSimpleName()+".bmp")));
		List<BufferedImage> tiles=Img4bitUtil.splitToTiles(img, 32, 32);
		
		RandomAccessFile file=new RandomAccessFile(splitDir+"SC07/011/0.0","rw");
		file.skipBytes(173*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(0), pal).data);
		file.skipBytes(1*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(1), pal).data);
		file.skipBytes(1*TILE);
		file.write(Img4bitUtil.toVramImg(tiles.get(2), pal).data);
		file.close();
	}

}
