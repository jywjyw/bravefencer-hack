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

public class Memcard implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws IOException{
		FileInputStream file = new FileInputStream(splitDir+"MAIN/012/0.0");
		Palette pal = new Palette(16, Conf.getRawFile("clut/memcard.16"));
		BufferedImage[] tiles = new BufferedImage[16];
		for(int i=0;i<tiles.length;i++){
			tiles[i]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
//			ImageIO.write(tiles[i], "bmp", new File(Conf.desktop+"a.bmp"));
		}
		BufferedImage joint = Img4bitUtil.jointTiles(Arrays.asList(tiles), 2);
		ImageIO.write(joint, "bmp", new File(exportDir+Memcard.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
		BufferedImage img = ImageIO.read(new File(Conf.getRawFile("pic/"+Memcard.class.getSimpleName()+".bmp")));
		Palette pal = new Palette(16, Conf.getRawFile("clut/memcard.16"));
		List<BufferedImage> tiles = Img4bitUtil.splitToTiles(img, 32,32);
		for(String f:new String[]{"MAIN/012/0.0","SC03/089/0.0","SC03/159/0.0","SC04/029/0.0","SC05/026/0.0"}){
			RandomAccessFile file = new RandomAccessFile(splitDir+f, "rw");
			for(BufferedImage i:tiles){
				file.write(Img4bitUtil.toVramImg(i,pal).data);
			}
			file.close();
		}
	}
	
}
