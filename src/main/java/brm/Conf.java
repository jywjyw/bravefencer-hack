package brm;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Conf {
	
	public static final String 
		EXE = "SLPS_014.90",
		SCRIPT1 = "MAIN/010/1.1",
		BIOS = "SCPH1001.BIN";
	
	public static final int 
		LOGIC_BLOCK = 0x800,
		SCRIPT1_ADDR = 0x800CDF58,		//MAIN/010/1.1's entrance, be resident in memory 
		SCRIPT2_ADDR = 0x80128508,		//0.4 script start addr in memory
//		SCRIPTS_LIMIT_ADDR = 0x801FF800,	//script2+3 cannot exceed this memory address, I'm not sure...need testing 
		SCRIPTS_LIMIT_ADDR = 0x801Fe000,
		CHAR_BYTES = 22;	//every character img take up 22 bytes
	
	public static final byte[]
		SQV_MAGIC = new byte[]{0x2E,0x73,0x71,0x76};
	
	public static final String[] CDS = new String[]{"MAIN","SC01","SC02","SC03","SC04","SC05","SC06","SC07"};
	public static final int[] CD_JAPAN_SIZE = new int[]{6205440, 20615168, 6580224, 78168064, 20764672, 16502784, 23908352, 12730368};
	
	public static String jpdir,endir,outdir, desktop;
	
	public static final byte END = 0;
	
	public static Map<String,Integer> getCdSizes(){
		Map<String,Integer> ret = new HashMap<>();
		for(int i=0;i<CDS.length;i++){
			ret.put(CDS[i], CD_JAPAN_SIZE[i]);
		}
		return ret;
	}
	
	static {
		InputStream is=null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties");
			Properties conf = new Properties();
			conf.load(is);
			jpdir = conf.getProperty("jpdir");
			endir = conf.getProperty("endir");
			outdir = conf.getProperty("outdir");
			desktop = conf.getProperty("desktop");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static String getRawFile(String rawFile){
		return System.getProperty("user.dir")+File.separator+"raw"+File.separator+rawFile;
	}
	
	public static String getTranslateFile(String file){
		return System.getProperty("user.dir")+File.separator+"translation"+File.separator+file;
	}
	
	
	public static int getExeOffset(int memAddr){
		return memAddr-0x8000f800;
	}
	
	public static int getExeAddr(int offset){
		return 0x8000f800+offset;
	}
	
	public static void printOffsetXY(int screenX, int screenY){
		System.out.printf("(%d,%d)\n",screenX-160,screenY-120);
	}
	
	public static void printSmallFontXY(int uv) {
		int x=(uv>>>8)/8, y=(uv&0xff)/8;
		System.out.printf("(%d,%d)\n",x,y);
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.toHexString(0x8018a538-Conf.SCRIPT1_ADDR));
//		printOffsetXY(282,155);
//		System.out.println(Integer.toHexString(getExeOffset(0x80062974)));
//		SystSem.out.println(Integer.toHexString(getExeOffset(0x800629ac)));
//		printSmallFontXY(0x5038);
//		printSmallFontXY(0x7038);
//		printSmallFontXY(0x0040);
//		ByteBuffer buf=ByteBuffer.wrap(Util.decodeHex("70387138723870386058604000 40 30 48 70 40 68 40  78 00 60 40 58 38 78 4878 20 68 58 38 48 40 48  78 50 70 58 48 48 50 4858 48 60 48 68 48 70 48  60 38 60 18 50 38 60 3868 38 78 08"));
//		int i=0x8140;
//		while(true){
//			byte b1=buf.get(),b2=buf.get();
//			System.out.printf("%04X=(%d,%d)\n",i++,(b1&0xff)/8,(b2&0xff)/8);
//		}
	}
}
