package brm.dump;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import brm.Conf;
import common.NoPointerText;
public class NoPointerTextReader {
	
	public static void main(String[] args) throws IOException {
		Charset2 cs=Charset2.loadVramCharset();
		List<NoPointerText> l=new NoPointerTextReader().loopRead(Conf.desktop+"brmjp/MAIN/012/1.1", 0x5afd4, 0x5b448, cs);
		for(NoPointerText n:l){
			System.out.printf("%X, %d, %s\n",n.addr-0x5afd4,n.size,n.text);
		}
		List<NoPointerText> l1=new NoPointerTextReader().loopRead(Conf.desktop+"brmjp/MAIN/012/1.1", 0x5b7f4, 0x5b838, cs); //gap 3ac
		for(NoPointerText n:l1){
			System.out.printf("%X, %d, %s\n",n.addr-0x5afd4,n.size,n.text);
		}
		List<NoPointerText> l2=new NoPointerTextReader().loopRead(Conf.desktop+"brmjp/MAIN/012/1.1", 0x5b870, 0x5ba00, cs); //gap 38
		for(NoPointerText n:l2){
			System.out.printf("%X, %d, %s\n",n.addr-0x5afd4,n.size,n.text);
		}
	}
	
	private StringBuilder sentence = new StringBuilder();
	private boolean prevIsEnd;
	private int sentenceSize=0;	//这段文本占多少字节
	
	public List<NoPointerText> loopRead(String file, long startAddr, long endAddr, Charset2 charset) throws IOException{
		List<NoPointerText> ret = new ArrayList<>();
		RandomAccessFile exe = new RandomAccessFile(file, "r");
		exe.seek(startAddr);
		byte[] buf=new byte[2];
		while(exe.getFilePointer()<endAddr){
			exe.read(buf, 0, 1);
			if(!isEndFlag(buf[0])) {
				if(prevIsEnd) {
					ret.add(new NoPointerText(startAddr, sentenceSize, sentence.toString()));  //sentenceSize contains end flag
					startAddr = exe.getFilePointer()-1;
					sentence = new StringBuilder();
					sentenceSize=0;
				}
				exe.read(buf, 1, 1);
				int code=(buf[0]&0xff)<<8|(buf[1]&0xff);
				sentenceSize+=2;
				String char_ = charset.getChar(code);
				if(char_==null) char_ = String.format("[%04X]", code);
				sentence.append(char_);
				prevIsEnd=false;
			} else {
				prevIsEnd=true;
				sentenceSize++;
			}
		}
		ret.add(new NoPointerText(startAddr, sentenceSize, sentence.toString()));
		exe.close();
		return ret;
	}
	
	private boolean isEndFlag(byte i) {
		return i==0;
	}
	
}
