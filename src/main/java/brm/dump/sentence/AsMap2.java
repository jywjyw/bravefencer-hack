package brm.dump.sentence;

import java.util.Map;

import brm.script.Exporter;

/**
 * key=file&sentenceIndex, value=sentence
 */
public class AsMap2 implements Exporter.Callback{
	public Map<String,String> sentences;
	StringBuilder sentence = new StringBuilder();

	public AsMap2(Map<String, String> sentences) {
		this.sentences = sentences;
	}

	@Override
	public void sentenceStart(String file, int sentenceIndex, long sentenceAddr, String english) {
		sentence = new StringBuilder();
	}

	@Override
	public void everyWord(byte[] wordBytes, String word) {
		sentence.append(word);
	}

	@Override
	public void sentenceEnd(String file, int sentenceIndex, long sentenceAddr, int sentenceLen) {
		sentences.put(file+"/"+sentenceIndex, sentence.toString());
	}
	
}
