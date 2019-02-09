package brm.tool;
//package common;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.util.Arrays;
//
//public class BinEraser {
//	
//	public static void main(String[] args) {
////		eraseByAddress(0x1433180,0x14336FC, (byte) 0);
//		//88,52b0
//		//5344,7376
////		Util.copyFile("F:\\JING\\isopatcher\\bak\\harvest-jp.iso", "F:\\JING\\isopatcher\\harvest-jp.iso");
////		Util.copyFile(Conf.bin, Conf.hackbin);
//		byte[] copy = Util.copyPartFile(Conf.bin, 0x88, 0x52b0);
//		try {
//			RandomAccessFile bin = new RandomAccessFile(new File(Conf.hackbin), "rw");
//			bin.seek(0x5344);
//			bin.write(copy);
//			bin.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
////		eraseByLength(0x88, 0x52b0, (byte)0);
////		eraseByLength(0x5348, 0x5000, (byte)0);
//	}
//	
//	public static void eraseByLength(long start, int len, byte b) {
//		try {
//			RandomAccessFile bin = new RandomAccessFile(new File(Conf.hackbin), "rw");
//			bin.seek(start);
//			byte[] bs = new byte[len];
//			Arrays.fill(bs, b);
//			bin.write(bs);
//			bin.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static void eraseByAddress(long start, long end, byte b) {
//		try {
//			RandomAccessFile bin = new RandomAccessFile(new File(Conf.hackbin), "rw");
//			bin.seek(start);
//			byte[] bs = new byte[(int) (end-start)];
//			Arrays.fill(bs, b);
//			bin.write(bs);
//			bin.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//
//}
