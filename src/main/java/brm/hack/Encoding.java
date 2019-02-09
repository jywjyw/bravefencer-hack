package brm.hack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import common.RscLoader;
import common.Util;

/**
 * main script's encoding, contains 00~ff, f000~feff, ff00~ff06
 */
public class Encoding {
	
	public Encoding(){
		String file="init_encoding1.gbk";
		RscLoader.load(file, "gbk", new RscLoader.Callback() {
			@Override
			public void doInline(String line) {
				String[] arr=line.split("=");
				if(char_code.containsKey(arr[1]))
					throw new RuntimeException(file+" has duplicate character:" + arr[1]);
				char_code.put(arr[1], Util.decodeHex(arr[0]));
			}
		});
	}
	
	private LinkedHashMap<String,byte[]> char_code = new LinkedHashMap<>();
	int next = 0xF000;
	
	public byte[] getCode(String char_) {
		byte[] code = char_code.get(char_);
		if(code==null){
			code = new byte[]{(byte)(next>>>8&0xff), (byte) (next&0xff)};
			char_code.put(char_, code);
			next++;
			if(next==0xfeff) throw new UnsupportedOperationException("too many chars");
			return code;
		}
		return code;
	}
	
	public int size(){
		return char_code.size()-7;	//exclude FF0X character
	}
	
	public List<String> getSingleByteChars(){
		List<String> ret = new ArrayList<>();
		for(Entry<String,byte[]> e:char_code.entrySet()){
			if(e.getValue().length==1){
				ret.add(e.getKey());
			}
		}
		return ret;
	}
	
	public List<String> getDoubleByteChars(){
		List<String> ret = new ArrayList<>();
		for(Entry<String,byte[]> e:char_code.entrySet()){
			if(e.getValue().length==2 && e.getValue()[0]!=(byte)0xff){
				ret.add(e.getKey());
			}
		}
		return ret;
	}
	
	
	public void saveAsTbl(String outFile){
		try {
			OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(outFile),"gbk");
			for(Entry<String,byte[]> e:char_code.entrySet()){
				if(e.getValue().length==1){
					fos.write(String.format("%X=%s\n", e.getValue()[0],e.getKey()));
				} else {
					fos.write(Util.hexEncode(e.getValue()).toUpperCase()+"="+e.getKey()+"\n");
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
