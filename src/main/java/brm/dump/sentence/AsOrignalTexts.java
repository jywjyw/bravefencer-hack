package brm.dump.sentence;

import brm.dump.OriginalTexts;
import brm.script.Exporter;

/**
 */
public class AsOrignalTexts implements Exporter.Callback{
	public OriginalTexts texts = new OriginalTexts();
	StringBuilder jp = new StringBuilder();
	String english;

	@Override
	public void sentenceStart(String file, int sentenceIndex, long sentenceAddr, String english) {
		jp = new StringBuilder();
		this.english = english;
	}

	@Override
	public void everyWord(byte[] wordBytes, String word) {
		jp.append(word);
	}

	@Override
	public void sentenceEnd(String file, int sentenceIndex, long sentenceAddr, int sentenceLen) {
		texts.put(file, sentenceIndex, sentenceAddr, sentenceLen, jp.toString(), english);
	}
	
}
