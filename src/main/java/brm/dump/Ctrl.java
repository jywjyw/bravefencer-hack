package brm.dump;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import brm.Conf;
import common.Util;

public class Ctrl {
	
	protected List<Byte> b1,b2,b3,b4,b5,b6;
	
	public Ctrl(){
		b1 = new ArrayList<>();
		b1.addAll(Arrays.asList((byte)0x0,(byte)0x5,(byte)7,(byte)8,(byte)0xa,(byte)0xc,(byte)0xd,(byte)0xe,(byte)0xf,
				(byte)0x15,(byte)0x16,(byte)0x17,(byte)0x18,(byte)0x19,(byte)0x1e));
		for(int i=0x20;i<=0xDF;i++){
			b1.add((byte)(i&0xFF));
		}
		
		b2 = new ArrayList<>();
		b2.addAll(Arrays.asList((byte)0x1,(byte)3,(byte)0x4,(byte)0x9));
		for(int i=0xE0;i<=0xFF;i++){
			b2.add((byte)(i&0xFF));
		}
		
		b3 = Arrays.asList((byte)0x2,(byte)0x10,(byte)0x14,(byte)0x11);
		b4 = Arrays.asList((byte)0xB,(byte)0x12);
		b5 = Arrays.asList();
		b6 = Arrays.asList();
	}
	
	public int getRestCount(byte b){
		if(b1.contains(b))		return 0;
		else if(b2.contains(b))	return 1;
		else if(b3.contains(b)) return 2;
		else if(b4.contains(b)) return 3;
		else if(b5.contains(b)) return 4;
		else if(b6.contains(b)) return 5;
		else return -1;
	}
	
	
	public String decode(byte[] word, int len){
		if(word[0]==Conf.END) {
			return "";
		} else if(word[0]==0x1){
			return String.format("[c%X]", word[1]);	//注意,其它控制符不要以c开头
		} else if(word[0]==0x4){
			return "";
		} else if(word[0]==0x7){
			return "[wt]";
		} else if(word[0]==0x8){
			return "[new]";
		} else if(word[0]==0x9){
			return "[box"+toHex(word, 1, len)+"]";
		} else if(word[0]==0xA){
			return "[br]";
		} else if(word[0]==0x17){
			return "[sel]";
		} else {
			return "["+toHex(word, 0, len)+"]";	//assert < 0x20
//			return "";
		}
	}
	
	public static byte[] encode(String word){
		if("[ed]".equals(word)){
			return new byte[]{0x0};
		} else if(word.startsWith("[c")){
			int val = Integer.parseInt(word.replace("[c", "").replace("]", ""),16);
			return new byte[]{0x1,(byte) val};
		} else if("[wt]".equals(word)){
			return new byte[]{0x7};
		} else if("[new]".equals(word)){
			return new byte[]{0x8};
		} else if(word.startsWith("[box")){
			int val = Integer.parseInt(word.replace("[box", "").replace("]", ""),16);
			return new byte[]{0x9,(byte) val};
		} else if("[br]".equals(word)){
			return new byte[]{0xA};
		} else if("[sel]".equals(word)){
			return new byte[]{0x17};
		} else {
			word=word.replace("[", "").replace("]", "");
			return Util.decodeHex(word);
		}
	}
	
	protected String toHex(byte[] bytes, int offset, int endIndex){
		StringBuilder ret = new StringBuilder(); 
		for(int i=offset;i<endIndex;i++){
			ret.append(String.format("%02X", bytes[i]));
		}
		return ret.toString();
	}
}
