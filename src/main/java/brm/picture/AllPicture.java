package brm.picture;

import java.io.File;
import java.io.IOException;

import brm.Conf;

public class AllPicture implements PicHandler {
	
	public static void main(String[] args) throws Exception {
		new AllPicture().export(Conf.desktop+"brmjp\\", Conf.desktop+"brmpic/");
	}
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		new File(exportDir).mkdirs();
		new Chapter().export(splitDir,exportDir);
		new DarkLumina1().export(splitDir,exportDir);
		new DarkLumina2().export(splitDir,exportDir);
		new DarkLumina3().export(splitDir,exportDir);
		new Ending().export(splitDir,exportDir);
		new Empire().export(splitDir,exportDir);
		new GuardianEarth().export(splitDir,exportDir);
		new GuardianFire().export(splitDir,exportDir);
		new GuardianSky().export(splitDir,exportDir);
		new GuardianWater().export(splitDir,exportDir);
		new GuardianWind().export(splitDir,exportDir);
		new Memcard().export(splitDir,exportDir);
		new MenuFont().export(splitDir, exportDir);
		new PlayToy().export(splitDir, exportDir);
		new SkyCastle().export(splitDir,exportDir);
		new SteamKnight().export(splitDir,exportDir);
		new Title().export(splitDir,exportDir);
		new Yakuiniku().export(splitDir,exportDir);
		new Unused().export(splitDir, exportDir);
		new Epilogue().export(splitDir, exportDir);
		new Staff().export(splitDir, exportDir);
		new Squaresoft().export(splitDir, exportDir);
	}

	@Override
	public void import_(String splitDir) throws IOException {
		new Chapter().import_(splitDir);
		new DarkLumina1().import_(splitDir);
		new DarkLumina2().import_(splitDir);
		new DarkLumina3().import_(splitDir);
		new Ending().import_(splitDir);
		new Empire().import_(splitDir);
		new GuardianEarth().import_(splitDir);
		new GuardianFire().import_(splitDir);
		new GuardianSky().import_(splitDir);
		new GuardianWater().import_(splitDir);
		new GuardianWind().import_(splitDir);
		new Memcard().import_(splitDir);
//		new MenuFont().import_(splitDir);	//not used. use MenuFontlibBuilder instead
		new PlayToy().import_(splitDir);
		new SkyCastle().import_(splitDir);
		new SteamKnight().import_(splitDir);
		new Title().import_(splitDir);
		new Yakuiniku().import_(splitDir);
		new Squaresoft().import_(splitDir);
	}

}
