package brm.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class SmallTextTest {
	
	public static void test(String splitDir) throws IOException{
		RandomAccessFile file = new RandomAccessFile(splitDir+"SC01/000/0.4", "rw");
		int baseOffset=0x7b364;
		
		
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("menu_conf.xml");
			Element pause=new SAXReader().read(is).getRootElement().element("pause");
			int start = Integer.parseInt(pause.element("sector").attributeValue("start"),16);
			for(Element sector : (List<Element>)pause.elements("sector")){
				
			}
			is.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		file.close();
	}

}
