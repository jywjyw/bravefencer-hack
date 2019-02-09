package brm.dump;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import brm.Script;
import brm.ScriptConfigLoader;
import brm.ScriptConfigLoader.Callback;
import brm.dump.sentence.AsConsole;
import brm.dump.sentence.AsMap2;
import brm.dump.sentence.AsMultilineExcel;
import brm.dump.sentence.AsOrignalTexts;
import brm.dump.sentence.Map2Xml;
import brm.dump.sentence.Texts2MultilineExcel;
import brm.script.Exporter;
import brm.script.ScriptHandlerMAIN_010_11;

public class AllScriptsDumper {
	
	Ctrl ctrl;
	Charset1 charset1;
	String splitDir;
	ScriptConfigLoader conf;
	
	public AllScriptsDumper(String splitDir, String language){
		this.splitDir=splitDir;
		this.conf = new ScriptConfigLoader(language,splitDir);
		if("jp".equals(language)){
			this.ctrl = new Ctrl();
			this.charset1 = Charset1.loadMainJp();
		} else {
			this.ctrl = new CtrlEn();
			this.charset1 = Charset1.loadMainEn();
		}
	}
	
	public void print() throws IOException{
		new ScriptHandlerMAIN_010_11(splitDir,conf.main).export(new AsConsole(),ctrl,charset1,null);
		conf.loop(new Callback() {
			@Override
			public void do_(Script s) {
				try {
					getExporter(s, splitDir).export(new AsConsole(), ctrl, charset1, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * export to excel. with non-repeat sentence
	 */
	public void exportJpWithEnglish(String excelFile, XSSFWorkbook excel, Map<String,String> englishTexts) throws IOException{
		new ScriptHandlerMAIN_010_11(splitDir,conf.main).export(new AsMultilineExcel(excel,"MAIN"),ctrl,charset1,englishTexts);
		AsOrignalTexts as = new AsOrignalTexts();
		conf.loop(new Callback() {
			@Override
			public void do_(Script s) {
				try {
					getExporter(s, splitDir).export(as, ctrl, charset1, englishTexts);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		new Texts2MultilineExcel(excel).exportXls(as.texts, englishTexts);
		new Map2Xml().export(as.texts, excelFile+".xml");
	}
	
	/**
	 * export to excel. with repeat sentence
	 */
	public void exportByScript(String excelFile) throws IOException{
		XSSFWorkbook excel = new XSSFWorkbook();
		new ScriptHandlerMAIN_010_11(splitDir,conf.main).export(new AsMultilineExcel(excel,"MAIN"),ctrl,charset1,null);
		AsMultilineExcel as = new AsMultilineExcel(excel,"SCRIPTS");
		conf.loop(new Callback() {
			@Override
			public void do_(Script s) {
				try {
					getExporter(s, splitDir).export(as, ctrl, charset1, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		FileOutputStream fos = new FileOutputStream(excelFile);
		excel.write(fos);
		excel.close();
		fos.close();
	}
	
	public Map<String,String> toHashMap() throws IOException{
		Map<String,String> sentences = new HashMap<>();
		new ScriptHandlerMAIN_010_11(splitDir,conf.main).export(new AsMap2(sentences),ctrl,charset1,null);
		conf.loop(new Callback() {
			@Override
			public void do_(Script s) {
				try {
					getExporter(s, splitDir).export(new AsMap2(sentences), ctrl, charset1, null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		return sentences;
	}
	
	private Exporter getExporter(Script s, String splitDir){
		if(s.export.equals("?")){
			s.export = "brm.script.ScriptHandler"+ s.file.replace("/","_").replace(".","");
		} else {
			s.export = "brm.script.ScriptHandler" + s.export;
		}
		
		try {
			return (Exporter)Class.forName(s.export)
					.getConstructor(String.class,Script.class)
					.newInstance(splitDir,s);
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}

}
