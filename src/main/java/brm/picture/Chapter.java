package brm.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import javax.imageio.ImageIO;

import brm.Conf;
import common.Img8bitUtil;
import common.Palette;
import common.VramImg;

public class Chapter implements PicHandler {
	
	String[] files = new String[]{"SC02/006/0.0","SC01/003/0.0","SC03/147/0.0","SC04/019/0.0","SC05/016/0.0","SC03/006/0.0"};
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		for(int i=0;i<6;i++){
			RandomAccessFile file = new RandomAccessFile(splitDir+files[i], "r");
			Palette pal = new Palette(256, Conf.getRawFile("clut/chapter"+(i+1)+".256"));
			BufferedImage[] tiles = new BufferedImage[16];
			for(int j=0;j<tiles.length;j++){
				tiles[j]=Img8bitUtil.readRomToBmp(file, 32, 32, pal);
			}
			BufferedImage joint = Img8bitUtil.jointTiles(tiles,4);
			ImageIO.write(joint, "bmp", new File(exportDir+Chapter.class.getSimpleName()+(i+1)+".bmp"));
			file.close();
		}
	}

	@Override
	public void import_(String splitDir) {
		try {
			for(int i=0;i<files.length;i++){
				Palette pal = new Palette(256, Conf.getRawFile("clut/chapter"+(i+1)+".256"));
				BufferedImage src= ImageIO.read(new File(Conf.getRawFile("pic/"+Chapter.class.getSimpleName()+(i+1)+".bmp")));
				List<VramImg> tiles = Img8bitUtil.splitToTiles(src, 32,32,pal);
				RandomAccessFile file = new RandomAccessFile(splitDir+files[i], "rw");
				for(VramImg v:tiles){
					file.write(v.data);
				}
				file.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportClut(String splitDir) throws IOException{
		FileOutputStream[] cluts = new FileOutputStream[files.length];
		for(int i=0;i<6;i++){
			cluts[i] = new FileOutputStream(Conf.getRawFile("clut/chapter"+(i+1)+".256"));
			RandomAccessFile file = new RandomAccessFile(splitDir+files[i], "r");
			file.seek(0xa000);
			byte[] buf=new byte[64];
			for(int j=0;j<8;j++){
				file.read(buf);
				cluts[i].write(buf);
				file.skipBytes(PicHandler.TILE-buf.length);
			}
			file.close();
			cluts[i].close();
		}
	}

}
