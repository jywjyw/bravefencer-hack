package my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import brm.Conf;
import brm.dump.Uncompresser;
import brm.hack.Compresser;
import common.Util;

public class CompresserTest {
	
	/**
	 * CustomLZSS压缩,Uncompressor解压后是否与原文件一致
	 */
	@Test
	public void test1() throws IOException{
		byte[] src = genBytes();
		FileOutputStream fos = new FileOutputStream(Conf.desktop+"src");
		fos.write(src);
		fos.close();
//		for(byte b:src){
//			System.out.print(b+" ");
//		}
//		System.out.println();
		byte[] comp = new Compresser().compress(new ByteArrayInputStream(src));
//		for(byte b:comp){
//			System.out.print(b+" ");
//		}
//		System.out.println();
		
		ByteArrayOutputStream reExtract = new ByteArrayOutputStream();
		Uncompresser.uncompress(new ByteArrayInputStream(comp), reExtract);
//		for(byte b:reExtract.toByteArray()){
//			System.out.print(b+" ");
//		}
//		System.out.println();
		
		
		Assert.assertArrayEquals(src, reExtract.toByteArray());
	}
	
	@Test
	public void test2() throws IOException{
		File zipped=new File(Thread.currentThread().getContextClassLoader().getResource("SC01_009_0.4").getFile());
		File extracted = new File(zipped.getAbsolutePath()+".extracted");
		
		System.out.println("原始文本 "+extracted.length()+"="+Util.md5(extracted));
		FileInputStream extractedIs = new FileInputStream(extracted);
		byte[] comp = new Compresser().compress(extractedIs);
		extractedIs.close();
		
		ByteArrayOutputStream reextract = new ByteArrayOutputStream();
		Uncompresser.uncompress(new ByteArrayInputStream(comp), reextract);
		System.out.println("重解文本 "+reextract.size()+"="+Util.md5(reextract.toByteArray()));
		Assert.assertEquals("", Util.md5(extracted), Util.md5(reextract.toByteArray()));
		System.out.println("java压缩后大小="+comp.length);
		System.out.println("原生压缩后大小="+zipped.length());
		
//		FileOutputStream os = new FileOutputStream(Conf.desktop+"java重解压.bin");
//		reextract.writeTo(os);
//		os.close();
//		FileOutputStream fff = new FileOutputStream(Conf.desktop+"java压缩.bin");
//		fff.write(comp);
//		fff.close();
		
	}
	
	private byte[] genBytes(){
		return new byte[]{'a','b','c',0,'a','c','d',0,0,0,0,0};
				
//		byte[] bs = new byte[1024633];//>65时结果不一致
//		new Random().nextBytes(bs);
//		return bs;
		
//		try {
//			File f = new File(Conf.desktop+"src");
//			FileInputStream fis = new FileInputStream(f);
//			byte[] ret = new byte[(int) f.length()];
//			fis.read(ret);
//			fis.close();
//			return ret;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
	}
	
	@Test
	public void test3() throws IOException{
		File extracted = new File(Conf.desktop+"\\brmjp\\sc01\\001\\0.4");
		FileInputStream extractedIs = new FileInputStream(extracted);
		byte[] comp = new Compresser().compress(extractedIs);
		extractedIs.close();
		FileOutputStream fos = new FileOutputStream(Conf.desktop+"\\SC01_001_04.lzss");
		fos.write(comp);
		fos.close();
	}

	@Test
	public void test4() throws IOException{
		File src = new File(Conf.desktop+"brmjp\\SC01\\001\\0.4");
		FileInputStream f = new FileInputStream(src);
		byte[] compressed = new Compresser().compress(f);
		f.close();
		System.out.println(Util.md5(src));
		
		FileOutputStream os=new FileOutputStream(Conf.desktop+"xxx.bin");
		os.write(compressed);
		os.close();
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Uncompresser.uncompress(new ByteArrayInputStream(compressed), bos);
		byte[] fff=bos.toByteArray();
		System.out.println(Util.md5(fff));
	}
}
