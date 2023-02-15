package brm.dump;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import brm.Conf;
import brm.picture.AllPicture;

public class Dump {
	
	public static void main(String[] args) throws Exception {
		System.out.println("dumping....");
		
		//make sure call CdSplitter.java to split *.CD first
//		CdSplitter sp=new CdSplitter(Conf.desktop+"brmen\\");
//		sp.split(Conf.endir);
//		String splitdir= sp.getSplitDir();
		
		String excelFile = Conf.desktop+"brm-jp.xlsx";
		XSSFWorkbook excel = new XSSFWorkbook();
		
		String splitdir= Conf.desktop+"brmen/";
		
		Map<String,String> english = new HashMap<>();
		if(new File(splitdir).exists()) {
			new AllScriptsDumper(splitdir,"en").exportByScript(Conf.desktop+"brm-en.xlsx");
			english=new AllScriptsDumper(splitdir,"en").toHashMap();
		}
		splitdir= Conf.desktop+"brmjp/";
		new AllScriptsDumper(splitdir,"jp")
//		.print();
		.exportJpWithEnglish(excelFile, excel, english);
//		.exportByScript(Conf.desktop+"brm-jp.xlsx");//do not remove duplicate sentence, shown by per script, used to compare jp script with english script
		
		new MenuDumper().dumpAllMenu(excel, splitdir);
		
		new AllPicture().export(splitdir, Conf.desktop+"brmpic/");
		
		saveExcel(excel,excelFile);
		
//		s.dispose();
		System.out.println("dump complete");
	}
	
	private static void saveExcel(XSSFWorkbook excel, String file) throws IOException{
		FileOutputStream fos = new FileOutputStream(file);
		excel.write(fos);
		excel.close();
		fos.close();
	}
	
}
