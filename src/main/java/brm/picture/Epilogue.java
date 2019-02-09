package brm.picture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import common.Img8bitUtil;
import common.Palette;

public class Epilogue implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		for(int i=12;i<=24;i+=2){
			RandomAccessFile file = new RandomAccessFile(String.format("%sSC07/%03d/0.0", splitDir,i), "r");
			file.skipBytes(84*TILE);
			ByteArrayOutputStream clut=new ByteArrayOutputStream();
			byte[] buf=new byte[64];
			for(int j=0;j<8;j++){
				file.read(buf);
				clut.write(buf);
				file.skipBytes(PicHandler.TILE-buf.length);
			}
			Palette pal = new Palette(256, clut.toByteArray());
			
			file.seek(0);
			List<BufferedImage> tiles = new ArrayList<>();
			for(int j=0;j<6;j++){
				file.skipBytes(4*TILE);
				for(int k=0;k<4;k++){
					tiles.add(Img8bitUtil.readRomToBmp(file, 32, 32, pal));
				}
			}
			BufferedImage joint = Img8bitUtil.jointTiles(tiles.toArray(new BufferedImage[]{}),4);
			ImageIO.write(joint, "bmp", new File(exportDir+Epilogue.class.getSimpleName()+i+".bmp"));
			file.close();
		}
		
		for(int i=13;i<=23;i+=2){
			RandomAccessFile file = new RandomAccessFile(String.format("%sSC07/%03d/0.0", splitDir,i), "r");
			file.skipBytes(84*TILE);
			ByteArrayOutputStream clut=new ByteArrayOutputStream();
			byte[] buf=new byte[64];
			for(int j=0;j<8;j++){
				file.read(buf);
				clut.write(buf);
				file.skipBytes(PicHandler.TILE-buf.length);
			}
			Palette pal = new Palette(256, clut.toByteArray());
			
			file.seek(32*TILE);
			List<BufferedImage> tiles = new ArrayList<>();
			for(int j=0;j<6;j++){
				file.skipBytes(4*TILE);
				for(int k=0;k<4;k++){
					tiles.add(Img8bitUtil.readRomToBmp(file, 32, 32, pal));
				}
			}
			BufferedImage joint = Img8bitUtil.jointTiles(tiles.toArray(new BufferedImage[]{}),4);
			ImageIO.write(joint, "bmp", new File(exportDir+Epilogue.class.getSimpleName()+i+".bmp"));
			file.close();
		}
		
		
		RandomAccessFile file = new RandomAccessFile(splitDir+"SC07/025/0.0", "r");
		file.skipBytes(150*TILE);
		ByteArrayOutputStream clut=new ByteArrayOutputStream();
		byte[] buf=new byte[64];
		for(int j=0;j<8;j++){
			file.read(buf);
			clut.write(buf);
			file.skipBytes(PicHandler.TILE-buf.length);
		}
		Palette pal = new Palette(256, clut.toByteArray());
		
		file.seek(0);
		List<BufferedImage> tiles = new ArrayList<>();
		for(int j=0;j<150;j++){
			tiles.add(Img8bitUtil.readRomToBmp(file, 32, 32, pal));
		}
		BufferedImage joint = Img8bitUtil.jointTiles(tiles.toArray(new BufferedImage[]{}),10);
		ImageIO.write(joint, "bmp", new File(exportDir+Epilogue.class.getSimpleName()+"25.bmp"));
		file.close();
	}

	@Override
	public void import_(String splitDir) {
	}
}
