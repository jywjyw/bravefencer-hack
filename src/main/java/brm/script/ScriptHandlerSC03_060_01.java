package brm.script;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import brm.Script;
import brm.dump.Charset1;
import brm.dump.Charset2;
import brm.dump.Ctrl;
import brm.dump.EnglishConf;

/**
 * 脚本中的字库指针是汇编指令形式,例如这段指令
801ef55c : LUI     801c347c (a0), 801f (32799),	//定义段地址:801f
801ef560 : ADDIU   801f0000 (a0), 801f0000 (a0), 02ac (684),	//段地址+偏移量02AC=字库指针801F02AC
801ef564 : JAL     80139d78, 801ef55c (ra),
801ef568 : NOP    
80139d78 : LUI     800c0000 (at), 8012 (32786),
80139d7c : SW      801f02ac (a0), 7f90 (80120000 (at)) [80127f90]//把字库指针存入内存以便后面调用
 * 
 * 在这个脚本中,关键字为1F 80 04 3C AC 02, 一共出现了5次
 * 推测这个脚本是敲村长家门时的对话,随着故事进行,有5种对话,所以hack时如果修改了字库位置,那么这5段汇编指令都要修改
 * 
 * 该段脚本的起始位置0x801EF220来自于SC03/001/0.4~C34F4,此片区域好像定义了许多CTY/ACT相关的地址,801EF220=80128508+脚本大小-1
 * 字库地址=108c. 字符个数=115. 脚本末端地址=801F11D1. 
 *
 */
public class ScriptHandlerSC03_060_01 implements Exporter{
	String splitDir;
	Script script;
	ScriptAddrGetter scriptAddrGetter;
	public ScriptHandlerSC03_060_01(String splitDir,Script script) {
		scriptAddrGetter = new Script3Addr(script);
		this.script=script;
		this.splitDir=splitDir;
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String,String> englishTexts) throws IOException {
		int start1 = Integer.parseInt(script.addr[0],16);
		int end1 = Integer.parseInt(script.addr[1],16);
		int fontOffset = 0x801F02AC-scriptAddrGetter.getStartAddr();	//0x108c = font pointer(hard coded as asm) - script start addr
		RandomAccessFile file=null;
		try {
			file = new RandomAccessFile(this.splitDir+script.file, "r");
			new ScriptReader(new Charset2(),new EnglishConf(englishTexts, script.file)).readTextArea(file, script.file, ctrl, charset1, cb, 
								start1, end1, fontOffset, 0);
		} finally{
			file.close();
		}
	}

}
