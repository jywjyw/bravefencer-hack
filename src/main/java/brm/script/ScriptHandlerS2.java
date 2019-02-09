package brm.script;

import java.io.IOException;
import java.util.Map;

import brm.Conf;
import brm.Script;
import brm.dump.Charset1;
import brm.dump.Ctrl;

public class ScriptHandlerS2 implements Exporter{
	
	Exporter exporter;
	
	public ScriptHandlerS2(String splitDir, Script script) {
		TextAddrGetter textAddrGetter;
		if(script.addr.length==2 || script.addr.length==4){	
			textAddrGetter = new FontPointerNearbyText(splitDir,script);	//字库指针在文本前面20~24字节
		} else {
			textAddrGetter = new AddrManual(script.addr);	//字库指针要手动查找
		}
		this.exporter = new CommonExporter(splitDir, script, textAddrGetter, Conf.SCRIPT2_ADDR);
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String, String> englishTexts) throws IOException {
		exporter.export(cb, ctrl, charset1, englishTexts);
	}

}
