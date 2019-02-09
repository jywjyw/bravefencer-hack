package brm.hack;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import common.Util;
import common.instruction.MipsCompiler;

public class MenuFontAsmHack {
	
	public static void hack(String splitDir) throws Exception{
		hackMenuFont(splitDir);
		hackHUDGetIn(splitDir);
		hackRescueFire(splitDir);
	}
	
	private static int 
			writeMenuSpriteFuncAddr = 0x800d3c24, //warning: asm/uvRouter.asm倒数第2行 j xxxx应当改成 writeMenuSpriteFuncAddr
			writeGETINSpriteFuncAddr;
	
	private static void hackMenuFont(String splitDir) throws IOException{
		MipsCompiler mips = new MipsCompiler();
		RandomAccessFile exe=new RandomAccessFile(Conf.outdir+Conf.EXE, "rw");
		exe.seek(Conf.getExeOffset(0x800232fc));
		exe.write(mips.compileLine("j 8002336c")); //make chars greater than 83xx jump to get83xxUV.asm   
		exe.seek(Conf.getExeOffset(0x8002336c));
		exe.write(mips.compileResource("asm/get83xxUV.asm")); //this function will handle UV of 83xx  
		//Here are UV of 829f~83xx, they will be no longer used, the space will be used to store writeMenuSprite.asm
		exe.close();
		
		RandomAccessFile main=new RandomAccessFile(splitDir+Conf.SCRIPT1, "rw");
		main.seek(writeMenuSpriteFuncAddr-Conf.SCRIPT1_ADDR);
		//
		byte[] writeMenuSprite=mips.compileResource("asm/writeMenuSprite.asm");
		main.write(writeMenuSprite);
		
		writeGETINSpriteFuncAddr = writeMenuSpriteFuncAddr+writeMenuSprite.length;
		byte[] writeGETINSprite=mips.compileResource("asm/writeGETINSprite.asm");
		main.write(writeGETINSprite);
		
		main.seek(0x800D174c-Conf.SCRIPT1_ADDR); //原小字库的浊音界限为80H,修改为FF,即忽略浊音
		main.write(0xff);
		main.seek(0x800D18b8-Conf.SCRIPT1_ADDR); //同上
		main.write(0xff);
		
		main.seek(0x800d1784-Conf.SCRIPT1_ADDR);
		main.write(mips.compileResource("asm/uvRouter.asm")); 
		main.seek(0x800D17e4-Conf.SCRIPT1_ADDR);
		main.write(mips.compileLine("addiu a2,a2,a"));  //全角空格:screenX+=10
		main.seek(0x800D17f0-Conf.SCRIPT1_ADDR);
		main.write(mips.compileLine("addiu a2,a2,5"));  //半角空格:screenX+=5
		
		main.close();
	}
	
	//GETIN in bottom right of HUD 
	private static void hackHUDGetIn(String splitDir) throws IOException, DocumentException{
		//801784c8 : li t2,8 -> addiu t2,r0,a			//char wh += 10
		//80178554 : addiu a2,a2,8 -> addiu a2,a2,a 	//screenX += 10
		//80178584 : -> j 80xxxxx; nop; 			
		MipsCompiler mips = new MipsCompiler();
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("script_jp_conf.xml");
		Document doc = new SAXReader().read(is);
		List<Element> showGetins=doc.selectNodes("//func[@name='showGetin']");
		for(Element e:showGetins){
			RandomAccessFile file=new RandomAccessFile(splitDir+e.getParent().attributeValue("name"), "rw");
			
			//modify sleepness uv, see MenuFontLibBuilder.rebuildLittleFontPic
			file.seek(0x801782a0-Conf.SCRIPT2_ADDR);
			file.write(mips.compileLine("ori a3,a3,2840"));//ne=64,40
			file.seek(0x801782f0-Conf.SCRIPT2_ADDR);
			file.write(mips.compileLine("ori a2,a2,2848"));//mu=72,40
			file.seek(0x80178324-Conf.SCRIPT2_ADDR);
			file.write(mips.compileLine("ori a2,a2,2850"));//ke=80,40
			file.seek(0x801784c0-Conf.SCRIPT2_ADDR);
			
//			testRead1Instruction(file,"8000422c");
//			file.write(mips.compileLine("sltiu v0,v0,ff"));	//skip handling japanese " and .
			file.write(mips.compileLine("nop"));	//skip handling japanese " and .
			file.write(mips.compileLine("addu t1,a0,r0"));	//stay the same
			file.write(mips.compileLine("addiu t2,r0,a"));	//char wh += 10
			
			file.seek(0x80178554-Conf.SCRIPT2_ADDR);
			file.write(mips.compileLine("addiu a2,a2,a"));	//screenX += 10
			
			file.seek(0x80178584-Conf.SCRIPT2_ADDR);
			file.write(mips.compileLine("j "+Integer.toHexString(writeGETINSpriteFuncAddr)));		
			file.write(mips.compileLine("nop"));
			
			file.seek(0x801785bc-Conf.SCRIPT2_ADDR);
			file.write(mips.compileLine("sltiu v0,v0,ff")); //skip handling japanese " and .
			
			file.close();
		}
		is.close();
	}
	
	
	/*
	 * 救火：SC04/017/0.4
	801887e0:52010801,OFFSET=602d8
	801887fc:52010001,OFFSET=602f4
	80188818:52011001,OFFSET=60310
	u=0152&3f<<2=0x48
	都改成40015001,抹掉三个文字
	 */
	private static void hackRescueFire(String splitDir) throws IOException{
		RandomAccessFile file=new RandomAccessFile(splitDir+"SC04/017/0.4", "rw");
		byte[] blank=Util.decodeHex("40015001");
		file.seek(0x801887e0-Conf.SCRIPT2_ADDR);
		file.write(blank);
		file.seek(0x801887fc-Conf.SCRIPT2_ADDR);
		file.write(blank);
		file.seek(0x80188818-Conf.SCRIPT2_ADDR);
		file.write(blank);
		file.seek(0x8017eab8-Conf.SCRIPT2_ADDR);
		file.write(0x10); //big num's uv TODO 不对
		file.close();
	}
	
	
	
	private static void testRead1Instruction(RandomAccessFile file, String instruction) throws IOException{
		long pos=file.getFilePointer();
		byte[] buf=new byte[4];
		file.read(buf);
		if(!instruction.equals(Util.hexEncode(buf)))
			throw new RuntimeException("write to unexpected position");
		file.seek(pos);
	}

}
