package brm.script;

import java.io.IOException;
import java.util.Map;

import brm.Conf;
import brm.Script;
import brm.dump.Charset1;
import brm.dump.Ctrl;

public class ScriptHandlerS3 implements Exporter{
	
	Exporter exporter;
	
	public ScriptHandlerS3(String splitDir, Script script) {
		TextAddrGetter textAddr;
		if(script.addr.length==2 || script.addr.length==4){	
			textAddr = new FontPointerNearbyText(splitDir,script);	//字库指针在文本前面20~24字节
		} else {
			textAddr = new AddrManual(script.addr);	//字库指针要手动查找
		}
		int startAddr = (int) (Conf.SCRIPT2_ADDR+script.parent.length-1);
		this.exporter = new CommonExporter(splitDir, script, textAddr, startAddr);
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String, String> englishTexts) throws IOException {
		exporter.export(cb, ctrl, charset1, englishTexts);
	}

}
