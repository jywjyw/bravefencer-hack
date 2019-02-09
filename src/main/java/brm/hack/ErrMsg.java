package brm.hack;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

import brm.Conf;

public class ErrMsg {
	
	private static Set<String> err=new LinkedHashSet<>();
	
	public static void add(String e){
		err.add(e);
	}
	
	public static void checkErr(){
		try {
			if(err.size()>0){
				String errFile=Conf.desktop+"导入错误报告.txt";
				PrintWriter errOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(errFile),"gbk"));
				int i=1;
				for(String s:err){
					errOut.println((i++)+"."+s);
				}
				errOut.flush();
				errOut.close();
				throw new UnsupportedOperationException("has error, please read "+errFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
