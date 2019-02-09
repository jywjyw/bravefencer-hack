package brm.tool;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import brm.Conf;
import brm.hack.FontGen;
import common.Palette;

public class MAIN01011 {
	
	public static void mainx(String[] args) throws IOException {
		String file = Conf.desktop+"brmjp\\MAIN/010/1.1";
		
		RandomAccessFile f = new RandomAccessFile(new File(file), "rw");
		f.seek(0xa09c);
		byte[] zero=new byte[128*20/2];
		for(int i=0;i<8;i++){
			f.write(zero);
			f.skipBytes(160/2);
		}
		f.close();
		
		int offset=64*32*20+64*2+32-4;//41116
		PictureTilePreviewer.previewAs4bit(file,offset,32,200, new Palette(16, Conf.getRawFile("368-259")));
	}

}
