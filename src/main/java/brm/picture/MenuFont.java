package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img4bitUtil;
import common.Palette;
import common.VramImg;

public class MenuFont implements PicHandler{

	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		Palette pal=new Palette(16, Conf.getRawFile("clut/memcard.16"));
		FileInputStream in=new FileInputStream(splitDir+"MAIN/010/0.0");
		List<BufferedImage> tiles=new ArrayList<>();
		tiles.add(Img4bitUtil.readRomToBmp(in, 32, 32, pal));
		in.skip(TILE*7);
		tiles.add(Img4bitUtil.readRomToBmp(in, 32, 32, pal));
		in.skip(TILE*6);
		tiles.add(Img4bitUtil.readRomToBmp(in, 32, 32, pal));
		in.skip(TILE*6);
		tiles.add(Img4bitUtil.readRomToBmp(in, 32, 32, pal));
		in.skip(TILE*6);
		tiles.add(Img4bitUtil.readRomToBmp(in, 32, 32, pal));
		
		in.close();
		
		BufferedImage img=Img4bitUtil.jointTiles(tiles, 1);
		ImageIO.write(img, "bmp", new File(exportDir+"menuFont.bmp"));
	}

	@Override
	public void import_(String splitDir) throws IOException {
		
	}
}
