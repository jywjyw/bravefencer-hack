package brm.script;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import brm.Script;
import brm.dump.Charset1;
import brm.dump.Charset2;
import brm.dump.Ctrl;
import brm.dump.EnglishConf;

public class ScriptHandlerNosubfont implements Exporter{
	
	String splitDir;
	Script script;
	public ScriptHandlerNosubfont(String splitDir, Script script) {
		this.splitDir=splitDir;
		this.script=script;
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String, String> englishTexts) throws IOException {
		int fontOffset=0;//script has not sub font lib; 
		int start1 = Integer.parseInt(script.addr[0],16);
		int end1 = Integer.parseInt(script.addr[1],16);
		RandomAccessFile file=null;
		try {
			file = new RandomAccessFile(this.splitDir+script.file, "r");
			new ScriptReader(new Charset2(),new EnglishConf(englishTexts, script.englishFile))
				.readTextArea(file,script.file,ctrl,charset1,cb,start1, end1, fontOffset, 0);
		} finally{
			file.close();
		}
	}

}
