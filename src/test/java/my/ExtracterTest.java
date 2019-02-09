package my;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import brm.Conf;
import brm.dump.Uncompresser;
import common.Util;

public class ExtracterTest {
	
	
	@Test
	public void uncompSingle() throws IOException{
		uncompressAndCompare("D:\\ps3\\hanhua\\musashi-JP\\JAVA\\SC01\\009\\0.4", "D:\\ps3\\hanhua\\musashi-JP\\C\\SC01.CD.dir\\FILE_009.dir\\004");
	}
	@Test
	public void loopcompare() throws IOException{
		String root = "D:\\ps3\\hanhua\\musashi-JP\\";
		getChildrenFiles(new File(root+"JAVA"), new Callback() {
			@Override
			public void x(File f) {
				if(f.getName().equals("0.4")) {
					String[] arr = f.getAbsolutePath().split("\\\\");
					String c = String.format("%sC\\%s.CD.dir\\FILE_%s.dir\\004", 
							root,arr[arr.length-3],arr[arr.length-2]);
					System.out.println(f+"="+c);
					try {
						uncompressAndCompare(f.getAbsolutePath(), c);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void getChildrenFiles(File dir, Callback cb){
		dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory()){
					getChildrenFiles(pathname, cb);
				} else {
					cb.x(pathname);
				}
				return false;
			}
		});
	}
	
	interface Callback{
		void x(File f);
	}
	
	public void uncompressAndCompare(String compressFile, String cUncompressed) throws IOException {
		FileInputStream in = new FileInputStream(compressFile);
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		Uncompresser.uncompress(in,fos);
		fos.close();
		String javamd5 = Util.md5(fos.toByteArray());
		
		File c=new File(cUncompressed);
		String cmd5 = Util.md5(c);
		String msg = "\njava uncompressed: "+javamd5+", "+fos.size()+"\n"+"c    uncompressed: " +cmd5+", "+c.length()+"\n";
		
		FileOutputStream ffff = new FileOutputStream(Conf.desktop+"fffff");
		ffff.write(fos.toByteArray());
		ffff.close();
				
		Assert.assertEquals(msg, javamd5, cmd5);
		
	}

}
