package brm.script;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import brm.Script;
import brm.dump.Charset1;
import brm.dump.Charset2;
import brm.dump.Ctrl;
import brm.dump.EnglishConf;
import common.Util;

/*
 * 

SC03/061/0.1的E0/E8指针定义方式如下, 在父脚本SC03/001/0.4中:
80186bd0: 07 00 40 10 00 00 00 00  1F 80 04 3C F4 0D 84 24
80186be0: 5E E7 04 0C 00 00 00 00  C4 BC 07 0C 21 20 00 02
80186bf0: 14 00 BF 8F 10 00 B0 8F  18 00 BD 27 08 00 E0 03
ASMLOG:
80186bd0 : BEQ     00000001 (v0), 00000000 (r0), 80186bf0,
80186bd4 : NOP    
80186bd8 : LUI     00000000 (a0), 801f (32799),
80186bdc : ADDIU   801f0000 (a0), 801f0000 (a0), 0df4 (3572),//确定E0字库指针801f0df4,虽然SC03/001/0.4~C2F08处也有801f0df4,但没读到,不知道作用
80186be0 : JAL     80139d78, 80186bd0 (ra),
80186be4 : NOP    
80139d78 : LUI     800c0000 (at), 8012 (32786),
80139d7c : SW      801f0df4 (a0), 7f90 (80120000 (at)) [80127f90]

 * E0字库在脚本中的偏移量为1BD4.E8字符使用父脚本的字库
 * 脚本内容:找到狗后与枷台约翰的对话
 */
public class ScriptHandlerSC03_061_01 implements Exporter{
	String splitDir;
	Script script;
	ScriptAddrGetter scriptAddrGetter;
	public ScriptHandlerSC03_061_01(String splitDir,Script script) {
		this.splitDir=splitDir;
		this.script=script;
		scriptAddrGetter = new Script3Addr(script);
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String,String> englishTexts) throws IOException {
		int start1 = Integer.parseInt(script.addr[0],16);
		int end1 = Integer.parseInt(script.addr[1],16);
		
		RandomAccessFile parentScript = new RandomAccessFile(splitDir+script.parent.file,"r");
		parentScript.seek(0xC2F08);
		int fontPointer = Util.hilo(parentScript.readInt());
		int fontOffset = fontPointer-scriptAddrGetter.getStartAddr();
		parentScript.close();
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
