package brm.hack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Script;
import brm.ScriptConfigLoader;
import brm.script.CommonImporter;
import brm.script.Importer;
import common.ExcelParser;
import common.ExcelParser.RowCallback;

/**
 * modify all scripts, except main script
 */
public class AllScriptsImporter {
	
	private String lastId;
	private StringBuilder sentence = new StringBuilder();
	Map<String,String> allSentences = new LinkedHashMap<>();
	
	public void importFrom(File excel, String splitDir, ScriptConfigLoader scriptConfig, Encoding enc1) {
		new ExcelParser(excel).parse("SCRIPTS",2, new RowCallback() {
			@Override
			public void doInRow(List<String> strs, int rowNum) {
				String thisId = strs.get(0);
				if(thisId!=null) {
					if(lastId!=null){
						flushSentence();
					}
					sentence = new StringBuilder();
					lastId = thisId;
				}
				
				String ctrls=getCell(strs, 1), chinese=getCell(strs, 3);
				sentence.append(ctrls).append(chinese);
			}
		});
		flushSentence();
		
		Map<String,List<Sentence>> script_sentences = collect(new File(excel.getAbsolutePath()+".xml"));
		Map<String,List<String>> enc2CharsLib = new HashMap<>();//第2脚本所使用的新字库
		
		//先处理第2脚本
		for(Entry<String,List<Sentence>> e : script_sentences.entrySet()){
			String scriptName = e.getKey();
			Script script = scriptConfig.getScript(scriptName);
			if(script.parent==null){
				try {
					List<String> subchars = getImporter(splitDir, script).import_(enc1, e.getValue());
					enc2CharsLib.put(scriptName, subchars);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		//再处理第3脚本
		for(Entry<String,List<Sentence>> e : script_sentences.entrySet()){
			String scriptName = e.getKey();
			Script script = scriptConfig.getScript(scriptName);
			if(script.parent!=null){
				try {
					getImporter(splitDir, script).import_(enc1, e.getValue());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private Importer getImporter(String splitDir, Script s){
		if(s.import_.equals("?")){
			s.import_= "brm.script.ScriptHandler"+ s.file.replace("/","_").replace(".","");
		} else {
			s.import_= CommonImporter.class.getName();
		}
		try {
			return (Importer)Class.forName(s.import_)
					.getConstructor(String.class,Script.class)
					.newInstance(splitDir,s);
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}
	
	private void flushSentence(){
		allSentences.put(lastId, sentence.toString());
	}
	
	private String getCell(List<String> strs, int cell){
		String ret="";
		if(strs.size()>cell && strs.get(cell)!=null) {
			ret = strs.get(cell);
		}
		return ret;
	}
	
	public Map<String,List<Sentence>> collect(File xml)  {
		Element root=null;
		try {
			root = new SAXReader().read(xml).getRootElement();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		Map<String,List<Sentence>> script_sentences = new LinkedHashMap<>();
		for(Entry<String,String> e:allSentences.entrySet()){
			Element s = root.element("s"+e.getKey());
			int len = Integer.parseInt(s.attributeValue("len"));
			for(Object o:s.elements("loc")){
				Element loc = (Element)o;
				String script = loc.attributeValue("script");
				int startAddr = Integer.parseInt(loc.attributeValue("start"),16);
				List<Sentence> exist = script_sentences.get(script);
				Sentence sent = new Sentence(e.getValue(), script, len, startAddr);
				if(exist==null){
					exist = new ArrayList<>();
					exist.add(sent);
					script_sentences.put(script, exist);
				} else {
					exist.add(sent);
				}
			}
		}
		return script_sentences;
	}

}
