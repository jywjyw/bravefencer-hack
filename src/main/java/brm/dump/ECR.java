package brm.dump;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import brm.Conf;
import brm.tool.FontViewer;
import common.Bytes;
import common.RscLoader;
import common.RscLoader.Callback;
import common.Util;

public class ECR {
	
	private static Map<Bytes,String> img_char = new HashMap<>();
	
	public static String recognize(byte[] img){
		String char_ = img_char.get(new Bytes(img));
		if(char_!=null){
			return char_;
		} else {
			String hex = Util.hexEncode(img);
			FontViewer.viewSingleChar(img, Conf.desktop+"ecr\\"+hex);
			System.err.println("there is char img to recognize...");
			img_char.put(new Bytes(img), "*");
			return "*";
		}
	}
	
	private ECR(){}
	
	static { 
		RscLoader.load("ecr", "gbk", new Callback() {
			@Override
			public void doInline(String line) {
				String[] arr = line.split("=",2);
				Bytes k = new Bytes(Util.decodeHex(arr[0]));
				if(img_char.containsKey(k)){
					throw new RuntimeException("ECR config file has duplicate key:"+line);
				}
				img_char.put(k, arr[1]);
			}
		});
	}
	
	public static void main(String[] args) {
		new File(Conf.desktop+"ecr").listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().contains("="))
					System.out.println(pathname.getName().replace(".bmp", ""));
				return true;
			}
		});
	}
}
