package brm.hack;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import common.Bios;
import common.Util;
import common.instruction.MipsCompiler;

public class LargeFontHack {
	
	public static void hack(String splitDir, Encoding enc) throws Exception {
		modifyFontAddr(splitDir);
		writeBios(enc);
	}
	
	public static void modifyFontAddr(String splitDir) throws Exception{
		MipsCompiler mips = new MipsCompiler();
		ByteArrayOutputStream instruction=new ByteArrayOutputStream();
		instruction.write(mips.compileLine("lui a2,BFC7"));
		instruction.write(mips.compileLine("addiu a2,a2,9d68"));	//base offset=Bios.KANJI_OFFSET
		instruction.close();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("script_jp_conf.xml");
		try {
			Document doc = new SAXReader().read(is);
			List<Element> func=doc.selectNodes("//func[@name='locateFont']");
			for(Element e:func){
				RandomAccessFile file=new RandomAccessFile(splitDir+e.getParent().attributeValue("name"), "rw");
				file.seek(0x10D04);
				file.write(instruction.toByteArray());
				file.close();
			}
			is.close();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void writeBios(Encoding enc) throws Exception{
		String outBios=Conf.outdir+"brm-bios.bin";
		Util.copyFile(Conf.getRawFile(Conf.BIOS), outBios);
		Bios bios = new Bios(outBios);
		bios.eraseFromKanji(new FontGen().gen(enc.getDoubleByteChars()));
		bios.saveAsPSPFont(Conf.outdir+"brm.fnt");
		bios.saveAsBmp(Conf.desktop+"bios.bmp");
	}
	

}
