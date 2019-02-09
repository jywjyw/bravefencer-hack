package brm.dump;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import brm.dump.OriginalTexts.OriginalText;
import common.Util;

public class OriginalTexts implements Iterable<OriginalText> {
	
	private LinkedHashMap<String,Integer> hash_index = new LinkedHashMap<>();
	private List<OriginalText> list = new ArrayList<>();
	
	public class OriginalText{
		public String jp,en;
		public int len;
		public List<Location> locs=new ArrayList<>();
	}
	
	public void put(String file, int index, long addr, int len, String jp, String en){
		OriginalText exist = get(jp);
		if(exist==null){
			OriginalText o = new OriginalText();
			o.jp = jp;
			o.en = en;
			o.len = len;
			o.locs.add(new Location(file, index, addr));
			list.add(o);
			hash_index.put(Util.md5(jp.getBytes()), list.size()-1);
		} else {
			exist.locs.add(new Location(file, index, addr));
		}
	}
	
	public OriginalText get(String jp){
		Integer index = hash_index.get(Util.md5(jp.getBytes()));
		if(index!=null){
			return list.get(index);
		}
		return null;
	}

	@Override
	public Iterator<OriginalText> iterator() {
		return list.iterator();
	}

}
