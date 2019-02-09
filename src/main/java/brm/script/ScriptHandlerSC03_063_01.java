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
 * 
801cb928 : ADDIU   1f800390 (sp), 1f800390 (sp), ffe8 (65512),
801cb92c : SW      80120f28 (s0), 0010 (1f800378 (sp)) [1f800388]
801cb930 : ADDU    80120f28 (s0), 80120f28 (a0), 00000000 (r0),
801cb934 : ADDIU   00000001 (v0), 00000000 (r0), 000e (14),
801cb938 : LUI     80120f28 (a0), 801d (32797),	//定义段地址801d
801cb93c : ADDIU   801d0000 (a0), 801d0000 (a0), c444 (50244),	//拼接字库指针801d+c444=801cc444
801cb940 : SW      80181af8 (ra), 0014 (1f800378 (sp)) [1f80038c]
801cb944 : JAL     80139d78, 80181af8 (ra),
801cb948 : SH      0000000e (v0), 0002 (80120f28 (s0)) [80120f2a]
80139d78 : LUI     800c0000 (at), 8012 (32786),
80139d7c : SW      801cc444 (a0), 7f90 (80120000 (at)) [80127f90]

汇编指令关键字为1d 80 04 3c 44 c4 84 24,该脚本地点为蒸汽树管理员之家,有两处.
脚本内容为: 1.发生了第一次蒸汽事故,去找管理员拿钥匙时的对话
2.漂流完毕后拿到传说武具,在此地点与乔的对话

该脚本的起始位置801CB880,似乎来自于SC03\002\0.4~a0a18,此片区域似乎定义了许多CTY/ACT相关地址,801CB880=80128508+脚本大小-1
 *
 */
public class ScriptHandlerSC03_063_01 implements Exporter{
	String splitDir;
	Script script;
	ScriptAddrGetter scriptAddrGetter;
	public ScriptHandlerSC03_063_01(String splitDir,Script script) {
		this.splitDir=splitDir;
		this.script=script;
		scriptAddrGetter = new Script3Addr(script);
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String,String> englishTexts) throws IOException {
		int start1 = Integer.parseInt(script.addr[0],16);
		int end1 = Integer.parseInt(script.addr[1],16);
		int fontOffset = 0x801CC444-scriptAddrGetter.getStartAddr();	//0xBC4 = font pointer - script start addr
		
		RandomAccessFile file=null;
		try {
			file = new RandomAccessFile(this.splitDir+script.file, "r");
			new ScriptReader(new Charset2(), new EnglishConf(englishTexts, script.englishFile)).readTextArea(file,script.file,ctrl,charset1,cb,
								start1, end1, fontOffset, 0);
		} finally{
			file.close();
		}
	}
	 
}
