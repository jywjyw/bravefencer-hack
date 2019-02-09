package brm.dump;

import java.util.HashMap;
import java.util.Map;

import common.RscLoader;
import common.RscLoader.Callback;

/**
 * script2's charset
 */
public class Charset2 {
	
	public static Charset2 loadVramCharset(){
		Map<Integer,String> map = new HashMap<Integer,String>();
		RscLoader.load("charset_vram_jp.gbk", "gbk", new Callback() {
			@Override
			public void doInline(String line) {
				String[] arr = line.split("=",2);
				map.put(Integer.parseInt(arr[0], 16), arr[1]);
			}
		});
		
		Charset2 ret = new Charset2();
		ret.code_char = map;
		return ret;
	}
	
	protected Map<Integer,String> code_char = new HashMap<>();
	
	public String getChar(int code) {
		return code_char.get(code);
	}
	
	public void setChar(int code, String char_){
		code_char.put(code, char_);
	}

}
