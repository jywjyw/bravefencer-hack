package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img4bitUtil;
import common.Palette;

//this picture exists in everywhere, it's hard to import
@Deprecated 
public class Loading implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws IOException{
		Palette pal = new Palette(16, Conf.getRawFile("352-297"));
		FileInputStream file = new FileInputStream(splitDir+"MAIN/010/0.0");
		file.skip(TILE*58);
		BufferedImage[] tiles = new BufferedImage[2];
		for(int i=0;i<tiles.length;i++){
			tiles[i]=Img4bitUtil.readRomToBmp(file, 32, 32, pal);
		}
		ImageIO.write(Img4bitUtil.jointTiles(Arrays.asList(tiles), 1), "bmp", new File(exportDir+Loading.class.getSimpleName()+".bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) throws IOException {
	}
	
}
