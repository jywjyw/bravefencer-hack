package brm.hack;

import java.nio.ByteBuffer;
import java.util.Arrays;

import brm.dump.SentenceSplitter;
import brm.dump.SentenceSplitter.Callback;

public class MenuTextSerializer {
	
	public static byte[] toBytes(String sentence, EncodingMenu enc){
		ByteBuffer buf = ByteBuffer.allocate(2048);
		new SentenceSplitter().splitToWords(sentence, new Callback() {
			
			@Override
			public void onReadWord(boolean isCtrl, String word) {
				if(isCtrl){
					int code=Integer.parseInt(word.substring(1,5), 16);
					buf.putShort((short)(code&0xffff));
				} else {
					Integer code = enc.get(word);
					if(code == null){
						if(word.length()>1) {
							throw new UnsupportedOperationException("unrecognize char : "+word);
						}
						code=enc.put(word);
					}
					buf.putShort((short)(code.intValue()&0xffff));
				}
			}
		});
		buf.put(new byte[]{0,0});//末尾加上结束符
		return Arrays.copyOfRange(buf.array(), 0, buf.position());
	}
	

}
