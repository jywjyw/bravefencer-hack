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

public interface Exporter {
	
	public interface Callback{
		void sentenceStart(String file, int sentenceIndex, long sentenceAddr, String english);
		void everyWord(byte[] wordBytes, String word);
		
		/**
		 * @param file
		 * @param sentenceIndex  index in this script
		 * @param sentenceAddr	address in this script
		 * @param sentenceLen
		 */
		void sentenceEnd(String file, int sentenceIndex, long sentenceAddr, int sentenceLen);
	}
	void export(Callback cb, Ctrl ctrl, Charset1 charset1, Map<String,String> englishTexts) throws IOException;

}

