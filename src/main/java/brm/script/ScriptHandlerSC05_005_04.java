package brm.script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import brm.Conf;
import brm.Script;
import brm.dump.Charset1;
import brm.dump.Charset2;
import brm.dump.Ctrl;
import brm.dump.EnglishConf;

/**
 * 1.此程序块中有一处字库,但不对应 
 * 2.exxx字库指针指向了fxxx所在的位置 ,也不对应
 */
public class ScriptHandlerSC05_005_04 implements Exporter{
	String splitDir;
	Script script;
	ScriptAddrGetter scriptAddrGetter;
	public ScriptHandlerSC05_005_04(String splitDir,Script script) {
		scriptAddrGetter = new Script3Addr(script);
		this.script=script;
		this.splitDir=splitDir;
	}

	@Override
	public void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String,String> englishTexts) throws IOException {
		int start1 = Integer.parseInt(script.addr[0],16);
		int end1 = Integer.parseInt(script.addr[1],16);
		
		RandomAccessFile src= new RandomAccessFile(splitDir+script.file, "r");
		File copy=new File(Conf.desktop+script.file.replace("/", "_"));
		FileOutputStream fos = new FileOutputStream(copy);
		int i=0;
		int pos=0;
		while((i=src.read())!=-1){
			if(pos>start1 && pos<end1 && (i&0xf0)==0xe0){
				i+=0x10;	//Ex -> Fx	
			}
			fos.write(i);
			pos++;
		}
		src.close();
		fos.close();
		
		RandomAccessFile file=null;
		try {
			file = new RandomAccessFile(copy, "r");
			new ScriptReader(new Charset2(),new EnglishConf(englishTexts, script.file)).readTextArea(file, script.file, ctrl, charset1, cb, start1, end1, 0, 0);
		} finally{
			file.close();
		}
		copy.delete();
	}

}
