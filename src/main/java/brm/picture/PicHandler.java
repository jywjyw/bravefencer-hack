package brm.picture;

import java.io.IOException;

public interface PicHandler {
	
	public static final int TILE=64*32;//= 0x800Byte = 2KB
	
	void export(String splitDir, String exportDir) throws Exception;
	
	void import_(String splitDir) throws IOException;

}
