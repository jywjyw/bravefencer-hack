package brm.hack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import brm.Conf;
import common.MultiLayerChars;
import common.RscLoader;

public class EncodingMenu {
	public static void main(String[] args) throws IOException {
		RandomAccessFile file=new RandomAccessFile(Conf.jpdir+Conf.EXE, "r");
		file.seek(Conf.getExeOffset(0x800628cc));
		byte[] uvs=new byte[168];
		file.read(uvs);
		for(int i=0;i<uvs.length;i+=2){
			byte b1=uvs[i],b2=uvs[i+1];
			System.out.println(b1+","+b2);
		}
		file.close();
	}
	public static final int EXT_CHAR_LIMIT_COUNT = 12*12*4;
	
	public Map<String,Integer> 
			char_code=new LinkedHashMap<>(),
			char_count=new LinkedHashMap<>();
	LinkedList<Integer> availableCodes=new LinkedList<>();
	private int defaultCharCount=0;
	
	public MultiLayerChars chars=new MultiLayerChars();
	
	public EncodingMenu(){
		RscLoader.load("init_encoding_menu.gbk", "gbk", new RscLoader.Callback() {
			@Override
			public void doInline(String line) {
				String[] arr=line.split("=");
				if(line.startsWith("=")){
					arr[0]=line.substring(0,1);
					arr[1]=line.substring(2,line.length());
				}
				char_code.put(arr[0], Integer.parseInt(arr[1],16));
				char_count.put(arr[0], 0);
			}
		});
		defaultCharCount=char_code.size();
		
		for(int k=0;k<16;k++){	//y>12时即超出字库限制,该值设置为16只是用来统计超出了多少字
			for(int j=1;j<=12;j++){  //x<=12
				for(int i=0x83;i<=0x86;i++){
					int code=(i<<8)|(k<<4)|j;
					availableCodes.add(code);
				}
			}
		}
	}
	
	public Integer get(String key){
		Integer code=char_code.get(key);
		if(code!=null){
			char_count.put(key, char_count.get(key)+1);
		}
		return code;
	}
	
	public Integer put(String key){
		if(char_code.containsKey(key)) throw new UnsupportedOperationException();
		int i = availableCodes.pop();
		char_code.put(key, i);
		char_count.put(key, 1);
		chars.put(key);
		return i;
	}
	
	public void saveAsTbl(String targetFile){
		try {
			FileOutputStream fos = new FileOutputStream(targetFile);
			for(Entry<String,Integer> e:char_code.entrySet()){
				fos.write(String.format("%04X=%s\n", e.getValue(),e.getKey()).getBytes("gbk"));
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int size(){
		return char_code.size();
	}
	
	public String checkErr(){
		int exceed=char_code.size()-defaultCharCount-EXT_CHAR_LIMIT_COUNT;
		if(exceed>0){
			StringBuilder once=new StringBuilder();
			for(Entry<String,Integer> e: char_count.entrySet()){
				if(e.getValue()==1 && !e.getKey().contains("{"))
					once.append(e.getKey());
			}
			return "菜单字库超出"+exceed+"个,以下是仅出现一次的字符:"+once.toString();
		}
		return null;
	}
	
}
