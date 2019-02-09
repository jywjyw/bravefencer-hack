package brm.script;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;

import brm.Script;
import brm.dump.Charset1;
import brm.dump.Charset2;
import brm.dump.Ctrl;
import brm.dump.EnglishConf;
import brm.hack.Encoding;
import brm.hack.Sentence;

/**
 * 0.4脚本的第1种格式:字库指针要手动查找
 *
 */
public class ScriptHandlerEN implements Exporter{
	
	String splitDir;
	Script script;
	
	protected TextAddrGetter textAddrGetter;
	protected ScriptAddrGetter scriptAddrGetter = new Script2Addr();
	
	public ScriptHandlerEN(String splitDir, Script script) {
		this.splitDir = splitDir;
		this.script = script;
		textAddrGetter = new MyTextAddrGetter(script.addr);
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String,String> englishTexts) throws IOException {
//		new CommonExporter(script, splitDir, textAddrGetter, scriptAddrGetter).export(cb, ctrl, enc);
		
		int[] textAddr1=textAddrGetter.getText1Addr();
		RandomAccessFile file = null;
		Charset2 charset2 = new Charset2();//every script has it's own encoding
		EnglishConf english = new EnglishConf(null, script.englishFile);	//not use english, because this script is already english
		try {
			file = new RandomAccessFile(this.splitDir+script.file, "r");
			int nextSenIndex = new ScriptReader(charset2,english).readTextArea(file, script.file, ctrl, charset1,cb, 
								textAddr1[0], textAddr1[1], textAddrGetter.getFontPointerOffset(), 0);
			int[] textAddr2=textAddrGetter.getText2Addr();
			if(textAddr2!=null){
				new ScriptReader(charset2,english).readTextArea(file, script.file, ctrl, charset1, cb,
								textAddr2[0], textAddr2[1], textAddrGetter.getFontPointerOffset(), nextSenIndex);
			}
		} finally{
			file.close();
		}
	}

	class MyTextAddrGetter implements TextAddrGetter{
		
		String[] addr;
		public MyTextAddrGetter(String[] addr) {
			this.addr = addr;
		}
		
		@Override
		public int[] getText1Addr() {
			return new int[]{
					Integer.parseInt(addr[0],16),
					Integer.parseInt(addr[1],16)};
		}
		
		@Override
		public int[] getText2Addr() {
			if(addr.length>2){
				return new int[]{
						Integer.parseInt(addr[addr.length-2],16),
						Integer.parseInt(addr[addr.length-1],16)};
			}
			return null;
		}
		
		@Override
		public int getFontPointerOffset() {
			return 0;
		}
	};
}
