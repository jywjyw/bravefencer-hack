package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WordsCount {
	Map<String,Integer> wordCount = new HashMap<>();
	
	public void put(String word){
		Integer count=wordCount.get(word);
		if(count==null) wordCount.put(word, 1);
		else wordCount.put(word, count+1);
	}

	
	public String sortToString(){
		List<WordCount> list = new ArrayList<>();
		for(Entry<String,Integer> kv:wordCount.entrySet()){
			list.add(new WordCount(kv.getKey(),kv.getValue()));
		}
		Collections.sort(list, new Comparator<WordCount>() {
			@Override
			public int compare(WordCount o1, WordCount o2) {
				return o1.count-o2.count;
			}
		});
		
		StringBuilder ret = new StringBuilder();
		for(WordCount w:list){
			ret.append(w.word).append("=").append(w.count).append("\n");
		}
		return ret.toString();
	}
	
	class WordCount{
		String word;
		int count;
		public WordCount(String word, int count) {
			this.word=word;
			this.count=count;
		}
	}
	
}
