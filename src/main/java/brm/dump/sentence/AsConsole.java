package brm.dump.sentence;
import brm.script.Exporter;

public class AsConsole implements Exporter.Callback{
	
	StringBuilder sentence = new StringBuilder();
	
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
		System.out.printf("%s,%d,%x,%d,%s\n",file,sentenceIndex,sentenceAddr,sentenceLen,sentence);
	}
	
}
