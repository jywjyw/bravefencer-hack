package brm.script;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;

import brm.Conf;
import brm.dump.Charset1;
import brm.dump.Charset2;
import brm.dump.Ctrl;
import brm.dump.ECR;
import brm.dump.EnglishConf;
import brm.script.Exporter.Callback;
import common.Util;

public class ScriptReader {
	
	private Charset2 charset2;	// charset2 increase while reading
	private byte[] buf = new byte[6];
	private byte[] charImg = new byte[Conf.CHAR_BYTES];
	private Byte last=null;
	private long sentenceAddr;
	private int sentenceIndex;
	private int sentenceLen=0;
	private EnglishConf english;
	
	public ScriptReader(Charset2 charset2, EnglishConf english){
		this.charset2 = charset2;
		this.english = english;
	}
	
	/**
	 * 
	 * @param file
	 * @param script
	 * @param ctrl
	 * @param charset1
	 * @param cb
	 * @param start 	text start addr
	 * @param end 	text end addr
	 * @param subfontOffset
	 * @throws IOException
	 * @returns next sentence index
	 */
	public int readTextArea(RandomAccessFile file, String script, Ctrl ctrl, Charset1 charset1, Callback cb, 
							int start, int end, int subfontOffset, int sentenceStartIndex) throws IOException{
		file.seek(start);
		this.sentenceAddr = file.getFilePointer();
		this.sentenceIndex = sentenceStartIndex;
		cb.sentenceStart(script, this.sentenceIndex, this.sentenceAddr, english.getEnglish(this.sentenceIndex));
		while(file.getFilePointer()<=end){
			file.read(buf, 0, 1);
			if(buf[0]!=Conf.END && last!=null && last==Conf.END){	//if readed some 00 last time, and read new chars this time.
				cb.sentenceEnd(script, this.sentenceIndex, this.sentenceAddr, sentenceLen);
				this.sentenceAddr = file.getFilePointer()-1;
				this.sentenceIndex++;
				cb.sentenceStart(script, this.sentenceIndex, this.sentenceAddr, english.getEnglish(this.sentenceIndex));
				sentenceLen=0;
			}
			sentenceLen+=1;
			
			int restcount = ctrl.getRestCount(buf[0]);
			if(restcount==-1){
				System.out.printf("%08X=%02X\n", file.getFilePointer(), buf[0]);
				throw new UnsupportedOperationException(String.format("%02X", buf[0]));
			}
			if(restcount>0){
				file.read(buf, 1, restcount);
				sentenceLen += restcount;
			}
			
			String word = null;
			if((buf[0]&0xF0)==0xE0){//if(b==E*)
				if((buf[0]&0xff)>=0xE8){
					buf[0]-=8;
				}
				int code = Util.toInt(buf[0], buf[1]);
				word = charset2.getChar(code);
				if(word == null){
					int charIndex = (buf[0]&0xf)<<8|(buf[1]&0xff) ;
					long curFilePos = file.getFilePointer();
					file.seek(subfontOffset+charIndex*Conf.CHAR_BYTES);
					int readLen = file.read(charImg);
					if(readLen!=charImg.length){
						throw new RuntimeException(String.format("char code (%02X%02X) out of file(%s) bound !",buf[0],buf[1],script));
					}
					word = ECR.recognize(charImg);
					charset2.setChar(code, word);
					file.seek(curFilePos);
				}
			} else {
				word = charset1.getChar(buf, restcount+1);
			}
			cb.everyWord(Arrays.copyOfRange(buf, 0, restcount+1), word);
			last = buf[0];
		}
		cb.sentenceEnd(script, this.sentenceIndex++, this.sentenceAddr, sentenceLen);
		return this.sentenceIndex;
	}

}
