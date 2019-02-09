package brm;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ScriptConfigLoader{
	
	public interface Callback{
		void do_(Script s);
	}
	
	private List<Script> roots = new ArrayList<>();
	public Script main;
	
	public ScriptConfigLoader(String language, String splitDir){
		String xml = "jp".equals(language)?"script_jp_conf.xml":"script_en_conf.xml";
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(xml);
			Element root = new SAXReader().read(is).getRootElement();
			
			main = new Script();
			Element mainE = root.element("main");//only first
			main.file = mainE.attributeValue("name");
			main.addr = mainE.element("text").attributeValue("addr").split(",");
			main.englishFile = mainE.element("text").attributeValue("en");
			
			for(Element f2:(List<Element>)root.elements("file")){
				if(f2.element("text")!=null){
					Script s2 = new Script();
					s2.file = f2.attributeValue("name");
					s2.length=getFileLength(splitDir, s2.file);
					s2.pos = 2;
					parseTextE(f2.element("text"), s2);
					
					Element children = f2.element("children");
					if(children != null){
						for(Element f3:(List<Element>)children.elements()){
							Script s3 = new Script();
							s3.file = f3.attributeValue("name");
							s3.length=getFileLength(splitDir, s3.file);
							s3.pos = 3;
							parseTextE(f3.element("text"), s3);
							s3.parent = s2;
							s2.children.add(s3);
						}
					}
					
					roots.add(s2);
				}
			}
			is.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private long getFileLength(String splitDir, String script){
		return new File(splitDir+script).length();
	}
	
	private void parseTextE(Element text, Script s){
		s.export = text.attributeValue("export"); 
		s.import_ = text.attributeValue("import");
		s.addr = text.attributeValue("addr").split(",");
		s.newfont = text.attributeValue("newfont");
		s.englishFile = text.attributeValue("en");
	}
	
	public Script getScript(String file){
		for(Script s2:roots){
			if(s2.file.equalsIgnoreCase(file)){
				return s2;
			}
			for(Script s3:s2.children){
				if(s3.file.equalsIgnoreCase(file)){
					return s3;
				}
			}
		}
		throw new RuntimeException();
	}
	
	public void loop(Callback c){
		for(Script s2:roots){
			c.do_(s2);
			for(Script s3:s2.children){
				c.do_(s3);
			}
		}
	}

}
