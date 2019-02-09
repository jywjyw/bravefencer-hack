package brm.hack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import brm.CdHeader;
import brm.Conf;

public class ListCdWriter {
	
	ByteBuffer buf = ByteBuffer.allocate(Conf.LOGIC_BLOCK*2);
	
	public void append(CdHeader header){
		buf.put(header.trimBuild());
	}
	
	public void write(String targetFile){
		try {
			FileOutputStream os = new FileOutputStream(targetFile);
			os.write(buf.array());
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
