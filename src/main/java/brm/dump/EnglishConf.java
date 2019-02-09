package brm.dump;

import java.util.Map;

public class EnglishConf {
	private Map<String,String> texts;
	private String file;
	
	public EnglishConf(Map<String, String> texts, String file) {
		this.texts = texts;
		this.file = file;
	}

	public String getEnglish(int index){
		if(texts != null)
			return texts.get(file+"/"+index);
		return null;
	}

}
