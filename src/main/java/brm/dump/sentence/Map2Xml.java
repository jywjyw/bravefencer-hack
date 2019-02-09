package brm.dump.sentence;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import brm.dump.Location;
import brm.dump.OriginalTexts;
import brm.dump.OriginalTexts.OriginalText;

public class Map2Xml {
	
	public void export(OriginalTexts texts, String file){
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("xml");
		int i=0;
		for(OriginalText t : texts) {
			Element s = root.addElement("s"+i++);
			s.addAttribute("len", t.len+"");
			for(Location loc:t.locs){
				s.addElement("loc")
				.addAttribute("script", loc.script)
				.addAttribute("index", loc.index+"")
				.addAttribute("start", String.format("%05X", loc.address));
			}
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			OutputFormat fmt = OutputFormat.createPrettyPrint();
//		fmt.setEncoding("gbk");
			XMLWriter writer = new XMLWriter(bos, fmt);
			writer.write(doc);
			writer.close();
			
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bos.toByteArray());
			fos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	

}
