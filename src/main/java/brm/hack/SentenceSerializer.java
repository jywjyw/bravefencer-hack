package brm.hack;

import java.nio.ByteBuffer;
import java.util.Arrays;

import brm.dump.Ctrl;
import brm.dump.SentenceSplitter;
import brm.dump.SentenceSplitter.Callback;

public class SentenceSerializer {
	
	Encoding enc1;
	
	public SentenceSerializer(Encoding enc1) {
		this.enc1 = enc1;
	}

	public byte[] toBytes(String sentence){
		ByteBuffer buf = ByteBuffer.allocate(2048);
		new SentenceSplitter().splitToWords(sentence, new Callback() {
			
			@Override
			public void onReadWord(boolean isCtrl, String word) {
				if(isCtrl){
					buf.put(Ctrl.encode(word));
				} else {
					buf.put(enc1.getCode(word));
				}
			}
		});
		buf.put((byte)0);//末尾加上结束符
		return Arrays.copyOfRange(buf.array(), 0, buf.position());
	}
	
	
	public byte[] toBytes(Sentence sentence){
		ByteBuffer buf = ByteBuffer.allocate(2048);
		new SentenceSplitter().splitToWords(sentence.sentence, new Callback() {
			
			@Override
			public void onReadWord(boolean isCtrl, String word) {
				if(isCtrl){
					buf.put(Ctrl.encode(word));
				} else {
					buf.put(enc1.getCode(word));
				}
			}
		});
		buf.put((byte)0);//末尾加上结束符
	
		int exceed=buf.position()-sentence.len;
		if(exceed>0) {
			throw new UnsupportedOperationException(String.format("SCRIPTS文本超出%d字节 : %s", exceed, sentence.sentence));
		}
		return Arrays.copyOfRange(buf.array(), 0, sentence.len);
	}
}
