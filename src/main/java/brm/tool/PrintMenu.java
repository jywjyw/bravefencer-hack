package brm.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import brm.dump.Charset2;
import common.HexSearcher;
import common.Util;

public class PrintMenu {
	
	public static void main(String[] args) throws IOException {
		String dir=Conf.desktop+"brmjp/";
		print(dir, "career");
	}
	
	//指针长度不固定, 为8B,12B,16B, 指针与文本区不邻接 
	public static void print(String splitDir, String tag) throws IOException {
		Charset2 charset = Charset2.loadVramCharset();
		Element elem=null;
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("menu_conf.xml");
			elem = new SAXReader().read(is).getRootElement().element(tag);
			is.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		String pos=elem.attributeValue("pos");
		int scriptOffset=0;
		if(pos.equals("0")) scriptOffset=0xf800;
		else if(pos.equals("1")) scriptOffset=Conf.SCRIPT1_ADDR;
		else if(pos.equals("2")) scriptOffset=Conf.SCRIPT2_ADDR;
		else throw new UnsupportedOperationException();
		File f = new File(splitDir+elem.attributeValue("from"));
		RandomAccessFile script= new RandomAccessFile(f, "r");
		for(Object o:elem.elements("sector")) {
			Element sector=(Element)o;
			int start=Integer.parseInt(sector.attributeValue("start"),16); 
			int end=Integer.parseInt(sector.attributeValue("end"),16);
			script.seek(start);	
			byte[] buf = new byte[2];
			boolean lastIs0=false;
			int wordOffset=start;
			StringBuilder word = new StringBuilder();
			while(true){
				int len = script.read(buf);
				if(lastIs0){
					if(buf[0]==(byte)0) 
						continue;//ignore repeated zero
					else 
						wordOffset=(int)(script.getFilePointer()-2); 
				}
				if(len!=2 || buf[0]==(byte)0)	{
					int memAddr=0x80000000|(wordOffset+scriptOffset);
					List<Integer> pointerAddr=seekPointer(f,memAddr);
					StringBuilder sb = new StringBuilder();
					for(int i=0; i<pointerAddr.size(); i++)	{
						int poffset=pointerAddr.get(i)-wordOffset;
						if(poffset<0) sb.append("-");
						sb.append(Integer.toHexString(Math.abs(poffset)));
						if(i < pointerAddr.size() - 1)	sb.append(",");
					}
					System.out.printf("<word offset=\"%X\" addr=\"%X\" pOffset=\"%s\" JP=\"%s\" EN=\"\" ZH=\"\"/>\n",wordOffset,memAddr, sb.toString(), word.toString());
					word=new StringBuilder();
					if(script.getFilePointer()>=end) break;
				}else{
					String char_ = charset.getChar(Util.toInt(buf[0], buf[1])); 
					if(char_!=null) word.append(char_);
					else word.append("[").append(Util.hexEncode(buf)).append("]");
				}
				lastIs0 = (buf[0]==(byte)0 && buf[1]==(byte)0);
			}
			System.out.println("=================== sector end ====================");
		}
		script.close();
	}
	
	static List<Integer> seekPointer(File f, int addr){
		byte[] bytes=Util.toBytes(addr);
		Util.reverseArray(bytes);
		return HexSearcher.searchSingleFile(f, bytes);
	}
	
	private static String join(List<Integer> list, String splitter)	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<list.size(); i++)	{
			sb.append(Integer.toHexString(list.get(i)));
			if(i < list.size() - 1)	{
				sb.append(splitter);
			}
		}
		return sb.toString();
	}
}
