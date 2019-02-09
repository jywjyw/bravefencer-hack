package my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import brm.dump.Uncompresser;
import common.Lzss;

public class LzssTest {
	
	@Test
	public void test1() throws IOException{
		byte[] src = genBytes();
//		for(byte b:src){
//			System.out.print(b+" ");
//		}
//		System.out.println();
		
		byte[] comp = new Lzss(new ByteArrayInputStream(src)).compress().toByteArray();
//		for(byte b:comp){
//			System.out.print(b+" ");
//		}
//		System.out.println();
		
		ByteArrayOutputStream reExtract = new Lzss(new ByteArrayInputStream(comp)).uncompress();
//		for(byte b:reExtract.toByteArray()){
//			System.out.print(b+" ");
//		}
//		System.out.println();
		Assert.assertArrayEquals(src, reExtract.toByteArray());
	}
	
	private byte[] genBytes(){
//		byte[] bs = new byte[80000];
//		new Random().nextBytes(bs);
//		return bs;
		return new byte[]{'a','b','c','d','a','b','c','a','b','c','d','e','a','b','c','d','e','f'};
	}

}
