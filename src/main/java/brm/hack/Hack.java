package brm.hack;

import java.io.File;

import brm.Conf;
import brm.ScriptConfigLoader;
import brm.picture.AllPicture;
import brm.script.ScriptHandlerMAIN_010_11;
import common.IsoPatcher;
import common.Util;

public class Hack {
	
	public static void main(String[] args) throws Exception {
		String exe=Conf.outdir+Conf.EXE;
		Util.copyFile(Conf.jpdir+Conf.EXE, exe);
		String splitdir = Conf.desktop+"\\brmjp\\";
//		new CdSplitter(splitdir).split(Conf.jpdir);
		
		ScriptConfigLoader scriptConfig = new ScriptConfigLoader("jp",splitdir);
		File excel = new File(Conf.getTranslateFile("brm-jp-v9.xlsx"));
		
		Encoding enc = new Encoding();
		new ScriptHandlerMAIN_010_11(splitdir, scriptConfig.main).import_(excel, enc);		//import texts and rewrite main font
		new AllScriptsImporter().importFrom(excel, splitdir,scriptConfig, enc);
		enc.saveAsTbl(Conf.outdir+"新主码表.tbl");
		
		EncodingMenu encMenu=new EncodingMenu();
		new MenuImporter().import_(splitdir, excel, encMenu);
		encMenu.saveAsTbl(Conf.outdir+"新菜单码表.tbl");
		
		if(encMenu.checkErr()!=null) ErrMsg.add(encMenu.checkErr());
		ErrMsg.checkErr();
		
		LargeFontHack.hack(splitdir, enc);
		
		new MenuFontLibBuilder().rebuild(splitdir, encMenu.chars);
		MenuFontAsmHack.hack(splitdir);
		new VramFaceEditor().edit(splitdir);
		new AllPicture().import_(splitdir);
		new CdReducer().reduce(splitdir);
//		CheatCode.cheat(exe);
		
		CdRebuilder.rebuild(splitdir, Conf.outdir);
		IsoPatcher.patch(Conf.outdir, Conf.outdir+"brave-hack.iso");
		System.out.println("all complete, use ePSXe to run cd : "+Conf.outdir+"brave-hack.iso");
	}
}
