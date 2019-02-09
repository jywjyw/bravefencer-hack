package brm.hack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import common.ExcelParser;
import common.ExcelParser.RowCallback;
import common.Util;

public class MenuImporter {
	
	String lastType=null;
	List<NoPointerTextWrapper> lastTypeMenu=new ArrayList<NoPointerTextWrapper>();

	public void import_(String splitDir, File excel, EncodingMenu enc) throws Exception{
		Map<String,List<NoPointerTextWrapper>> type_texts=new HashMap<>();
		new ExcelParser(excel).parse("MENU",2, new RowCallback() {
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				String type=strs.get(0),chinese=strs.get(4);
				if(lastType==null) lastType=type;
				if(!type.equals(lastType)){
					type_texts.put(lastType, new ArrayList<NoPointerTextWrapper>(lastTypeMenu));
					lastTypeMenu.clear();
				}
				if(Util.isNotEmpty(chinese)){
					byte[] bs=MenuTextSerializer.toBytes(chinese, enc);
					int exceed=bs.length-Integer.parseInt(strs.get(2));
					if(exceed>0){
						ErrMsg.add(String.format("MENU文本超出%d个字节 : %s", exceed,chinese));
					}
					NoPointerTextWrapper wrapper=new NoPointerTextWrapper();
					wrapper.addr=Integer.parseInt(strs.get(1));
					wrapper.text=bs;
					lastTypeMenu.add(wrapper);
				}
				lastType=type;
			}
		});
		type_texts.put(lastType, lastTypeMenu);
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("script_jp_conf.xml");
		Document doc=new SAXReader().read(is);
		is.close();
		for(Element e:(List<Element>)doc.selectNodes("//vram")){
			int baseAddr=Integer.parseInt(e.attributeValue("text"),16);
			modifyFile(splitDir+ e.getParent().attributeValue("name"), baseAddr, type_texts.get(e.attributeValue("group")));
		}
		modifyFile(Conf.outdir+Conf.EXE, 0x626cc, type_texts.get("career"));
	}
	
	private void modifyFile(String file, int baseAddr, List<NoPointerTextWrapper> texts) throws IOException{
		RandomAccessFile f=new RandomAccessFile(file, "rw");
		for(NoPointerTextWrapper n:texts){
			f.seek(baseAddr+n.addr);
			f.write(n.text);
		}
		f.close();
	}
	
	class NoPointerTextWrapper{
		public int addr;
		public byte[] text;
	}

}
