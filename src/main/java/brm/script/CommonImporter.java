package brm.script;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import brm.Script;
import brm.hack.Encoding;
import brm.hack.ErrMsg;
import brm.hack.Sentence;
import brm.hack.SentenceSerializer;

public class CommonImporter implements Importer {
	
	String splitDir;
	Script script;
	
	public CommonImporter(String splitDir, Script script) {
		this.splitDir = splitDir;
		this.script = script;
	}

	@Override
	public List<String> import_(Encoding enc1, List<Sentence> sentences)
			throws IOException {
		File scriptFile = new File(splitDir+script.file);
		if(!scriptFile.exists()) throw new RuntimeException();
		RandomAccessFile file = new RandomAccessFile(scriptFile, "rw");
		SentenceSerializer sparser = new SentenceSerializer(enc1);
		for(Sentence s:sentences){
			try {
				byte[] bs = sparser.toBytes(s);
				file.seek(s.addr);
				file.write(bs);
			} catch (UnsupportedOperationException e) {
				ErrMsg.add(e.getMessage());
			}
		}
		file.close();
		return null;
	}
	
}