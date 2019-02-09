package brm.hack;

import java.io.FileOutputStream;
import java.io.IOException;

import brm.picture.PlayToy;

public class CdReducer {
	
	public void reduce(String splitDir) throws IOException{
		String[] files=new String[]{
				"MAIN/001/0.0",//remove Trial Pic In Main
				"MAIN/005/0.0"//remove Trial Pic In Main
				};	
		for(String f:files){
			try {
				FileOutputStream fos=new FileOutputStream(splitDir+f);
				fos.write(0);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
