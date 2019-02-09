package brm.dump;

import java.util.HashMap;
import java.util.Map;

import common.RscLoader;
import common.RscLoader.Callback;

/**
 * main script's charset
 */
public class Charset1 extends Charset2{
	
	public static Charset1 loadMainJp(){
		Charset1 cs = new Charset1();
		cs.code_char = load("charset1_jp");
		cs.ctrl = new Ctrl();
		return cs;	
	}
	
	public static Charset1 loadMainEn(){
		Charset1 cs = new Charset1();
		cs.code_char = load("charset1_en");
		cs.ctrl = new CtrlEn();
		return cs;	
	}
	
	private static Map<Integer,String> load(String tbl){
		Map<Integer,String> ret = new HashMap<Integer,String>();
		RscLoader.load(tbl, "gbk", new Callback() {
			@Override
			public void doInline(String line) {
				String[] arr = line.split("=",2);
				ret.put(Integer.parseInt(arr[0], 16), arr[1]);
			}
		});
		return ret;
	}
	
	public Ctrl ctrl;
	
	public String getChar(byte[] buf, int len){
		String s=null;
		if((buf[0]&0xff) >= (0x20&0xff)){
			if(len==1){
				s = getChar(buf[0]&0xFF);
			} else if(len==2){
				s = getChar((buf[0]&0xff)<<8|(buf[1]&0xff));
			} 
		} else {
			s = ctrl.decode(buf, len);
		}
		
		if(s==null){
			StringBuilder sb = new StringBuilder("["); 
			for(int i=0;i<len;i++){
				sb.append(String.format("%02X", buf[i]));
			}
			sb.append("]");
			s = sb.toString();
		}
		return s;
	}
	
}
