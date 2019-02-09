package brm.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import brm.Conf;
import common.DirLooper;
import common.DirLooper.Callback;

public class PrintRamAddr {
	
	public static void main(String[] args) {
		DirLooper.loop(Conf.desktop+"brmjp\\", new Callback() {
			@Override
			public void handleFile(File f) {
				if(f.getName().equals("0.4")) {
					try {
						RandomAccessFile r = new RandomAccessFile(f, "r");
						long i;
						for(i=f.length()-1;i>0;i--){
							r.seek(i);
							if(r.read()!=0) {
								i++;
								break;
							}
						}
						r.close();
						
						int start=Conf.SCRIPT2_ADDR;
						int size = (int)(f.length());
						int extend = 500*22;
						System.out.printf("%s=内存起始(%08X),零区(%X=%08X,size=%X),结束(%X=%08X),扩容(%X=%08X)\n",
								f.getAbsolutePath().replace(Conf.desktop, ""),
								Conf.SCRIPT2_ADDR,
								(int)(i),(int)(start+i),size-i,
								size,start+size,
								size+extend, start+size+extend
								);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
}
