package brm.tool;

import static brm.picture.PicHandler.TILE;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import common.HexSearcher;
import common.Img4bitUtil;
import common.Palette;
import common.Util;

public class FaceTool {
	
	public static void main(String[] args) throws Exception {
//		dumpVramLfaces();
//		dumpVramRfaces();
//		searchRfaceTile5(); //replace Lface first, then the result is unique
//		printFacePos();
		readAllRface(Conf.desktop+"brmjp/");
	}
	
	public static void erase(String[] args) throws IOException {
		for(String f:new String[]{"MAIN/010/0.0","MAIN/011/0.0"}){
			String filepath=Conf.desktop+"brmjp/"+f;
			RandomAccessFile file=new RandomAccessFile(filepath, "rw");
			byte[] zero=new byte[TILE];
			file.seek(TILE*16);//right
			file.write(zero);
			file.seek(TILE*22);//left
			file.write(zero);
			file.seek(TILE*23);//right
			file.write(zero);
			file.seek(TILE*29);//left
			file.write(zero);
			file.seek(TILE*30);//right
			file.write(zero);
			file.seek(TILE*36);//left
			file.write(zero);
			file.seek(TILE*37);//right
			file.write(zero);
			file.seek(TILE*43);//left
			file.write(zero);
			file.seek(TILE*44);//right
			file.write(zero);
			file.close();
			
			PictureTilePreviewer.previewAs4bit(filepath, 0, 32, 1984, new Palette(16, Conf.getRawFile("0-480")));
		}
	}
	
	public static void dumpVramLfaces() throws IOException {
		RandomAccessFile file=new RandomAccessFile(Conf.desktop+"brmjp/"+"MAIN/010/0.0", "r");
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		byte[] buf=new byte[TILE];
		
		file.skipBytes(29*TILE);
		file.read(buf);
		os.write(buf);
		
		file.skipBytes(6*TILE);
		file.read(buf);
		os.write(buf);
		
		file.skipBytes(6*TILE);
		file.read(buf);
		os.write(buf);
		file.close();
		
		Palette pal=Palette.init16Grey();
		BufferedImage img=Img4bitUtil.readRomToBmp(new ByteArrayInputStream(os.toByteArray()), 32, 3*32, pal);
		ImageIO.write(img, "bmp", new File(Conf.desktop+"lface.bmp"));
		
		int faceW=64,faceH=40;
		List<BufferedImage> faces=new ArrayList<>();
		faces.add(img.getSubimage(0, 16, faceW, faceH));
		faces.add(img.getSubimage(64, 16, faceW, faceH));
		faces.add(img.getSubimage(0, 16+faceH, faceW, faceH));
		faces.add(img.getSubimage(64, 16+faceH, faceW, faceH));
		for(int i=0;i<faces.size();i++){
			ImageIO.write(faces.get(i), "bmp", new File(Conf.desktop+"lface"+(i+1)+".bmp"));
		}
		
	}
	
	public static void dumpVramRfaces() throws IOException {
		RandomAccessFile file=new RandomAccessFile(Conf.desktop+"brmjp/"+"MAIN/010/0.0", "r");
		ByteArrayOutputStream os=new ByteArrayOutputStream();
		byte[] buf=new byte[TILE];
		
		file.skipBytes(16*TILE);
		file.read(buf);
		os.write(buf);
		
		file.skipBytes(6*TILE);
		file.read(buf);
		os.write(buf);
		
		file.skipBytes(6*TILE);
		file.read(buf);
		os.write(buf);
		
		file.skipBytes(6*TILE);
		file.read(buf);
		os.write(buf);
		
		file.skipBytes(6*TILE);
		file.read(buf);
		os.write(buf);
		
		Palette pal=Palette.init16Grey();
		BufferedImage img=Img4bitUtil.readRomToBmp(new ByteArrayInputStream(os.toByteArray()), 32, 5*32, pal);
		BufferedImage rightFaces=img.getSubimage(64, 0, 64, 160);
		ImageIO.write(rightFaces, "bmp", new File(Conf.desktop+"rface.bmp"));
		byte[] data=Img4bitUtil.toVramImg(rightFaces, pal).data;
		int lineBytes=32, targetLine=103;
		System.out.println(Util.hexEncode(Arrays.copyOfRange(data, targetLine*lineBytes, (targetLine+1)*lineBytes)));
		
		file.close();
		
		int faceW=64,faceH=40;
		for(int i=0,y=0;i<4;i++){
			BufferedImage f=img.getSubimage(64, y, faceW, faceH);
			ImageIO.write(f, "bmp", new File(Conf.desktop+"rface"+(i+1)+".bmp"));
			y+=faceH;
		}
	}
	
	public static void searchRfaceTile1() throws IOException{
		BufferedImage rface1=ImageIO.read(new File(Conf.getRawFile("face/rface1.bmp")));
		byte[] data=Img4bitUtil.toVramImg(rface1, Palette.init16Grey()).data;
		int lineBytes=32, targetLine=22;
		byte[] target=Arrays.copyOfRange(data, targetLine*lineBytes, (targetLine+1)*lineBytes);
		System.out.println(Util.hexEncode(target));
		HexSearcher.searchDir(Conf.desktop+"brmjp/", Util.hexEncode(target), new HexSearcher.Callback() {
			@Override
			public void onFound(File f, long addr) {
				int backward=(targetLine*2-1) * lineBytes;
				int tile=(int) ((addr-backward)/0x800);
//				System.out.printf("<file name=\"%s\">\n\t<rface pos=\"%s\">\n</file>\n", f,tile);
				System.out.println(f.getAbsolutePath().replace("C:\\Users\\administrator\\Desktop\\brmjp\\", "")+"\t"+tile);
				
			}
		});
		
	}
	
	public static void searchRfaceTile2() throws IOException{
		BufferedImage rface1=ImageIO.read(new File(Conf.getRawFile("face/rface2.bmp")));
		byte[] data=Img4bitUtil.toVramImg(rface1, Palette.init16Grey()).data;
		int lineBytes=32, targetLine=19;
		byte[] target=Arrays.copyOfRange(data, targetLine*lineBytes, (targetLine+1)*lineBytes);
		System.out.println(Util.hexEncode(target));
		HexSearcher.searchDir(Conf.desktop+"brmjp/", Util.hexEncode(target), new HexSearcher.Callback() {
			@Override
			public void onFound(File f, long addr) {
				int backward=(targetLine*2-1) * lineBytes;
				int tile=(int) ((addr-backward)/0x800);
//				System.out.printf("<file name=\"%s\">\n\t<rface pos=\"%s\">\n</file>\n", f,tile);
				System.out.println(f.getAbsolutePath().replace("C:\\Users\\administrator\\Desktop\\brmjp\\", "")+"\t"+tile);
				
			}
		});
		
	}
	
	public static void searchRfaceTile3() throws IOException{
		BufferedImage rface1=ImageIO.read(new File(Conf.getRawFile("face/rface2.bmp")));
		byte[] data=Img4bitUtil.toVramImg(rface1, Palette.init16Grey()).data;
		int lineBytes=32, targetLine=34;
		byte[] target=Arrays.copyOfRange(data, targetLine*lineBytes, (targetLine+1)*lineBytes);
		System.out.println(Util.hexEncode(target));
		HexSearcher.searchDir(Conf.desktop+"brmjp/", Util.hexEncode(target), new HexSearcher.Callback() {
			@Override
			public void onFound(File f, long addr) {
				int backward=(targetLine*2-1) * lineBytes;
				int tile=(int) ((addr-backward)/0x800)+1;
				System.out.println(f.getAbsolutePath().replace("C:\\Users\\administrator\\Desktop\\brmjp\\", "")+"\t"+tile);
				
			}
		});
		
	}
	
	public static void searchRfaceTile4() throws IOException{
		BufferedImage rface1=ImageIO.read(new File(Conf.getRawFile("face/rface3.bmp")));
		byte[] data=Img4bitUtil.toVramImg(rface1, Palette.init16Grey()).data;
		int lineBytes=32, targetLine=22;
		byte[] target=Arrays.copyOfRange(data, targetLine*lineBytes, (targetLine+1)*lineBytes);
		System.out.println(Util.hexEncode(target));
		HexSearcher.searchDir(Conf.desktop+"brmjp/", Util.hexEncode(target), new HexSearcher.Callback() {
			@Override
			public void onFound(File f, long addr) {
				int backward=(targetLine*2-1) * lineBytes;
				int tile=(int) ((addr-backward)/0x800)+1;
//				System.out.printf("<file name=\"%s\">\n\t<rface pos=\"%s\">\n</file>\n", f,tile);
				System.out.println(f.getAbsolutePath().replace("C:\\Users\\administrator\\Desktop\\brmjp\\", "")+"\t"+tile);
				
			}
		});
		
	}
	
	public static void searchRfaceTile5() throws IOException{
		BufferedImage rface1=ImageIO.read(new File(Conf.getRawFile("face/rface4.bmp")));
		byte[] data=Img4bitUtil.toVramImg(rface1, Palette.init16Grey()).data;
		int lineBytes=32, targetLine=31;
		byte[] target=Arrays.copyOfRange(data, targetLine*lineBytes, (targetLine+1)*lineBytes);
		System.out.println(Util.hexEncode(target));
		HexSearcher.searchDir(Conf.desktop+"brmjp/", Util.hexEncode(target), new HexSearcher.Callback() {
			@Override
			public void onFound(File f, long addr) {
				int backward=(targetLine*2-1) * lineBytes;
				int tile=(int) ((addr-backward)/0x800)+1;
//				System.out.printf("<file name=\"%s\">\n\t<rface pos=\"%s\">\n</file>\n", f,tile);
				System.out.println(f.getAbsolutePath().replace("C:\\Users\\administrator\\Desktop\\brmjp\\", "")+"\t"+tile);
				
			}
		});
		
	}
	
	private static void printFacePos() throws IOException{
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(Conf.desktop+"新建文本文档.txt")));
		String l=null;
		while((l=br.readLine())!=null){
			String[] arr=l.split("\\t");
			System.out.printf("<file name=\"%s\">\n\t<rface pos=\"%s\"/>\n</file>\n", arr[0], arr[1]+","+arr[2]+","+arr[3]+","+arr[4]+","+arr[5]);
		}
		br.close();
	}
	
	private static void readAllRface(String splitDir) throws Exception{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("pic_conf.xml");
		Document doc = new SAXReader().read(is);
		Palette grey=Palette.init16Grey();
		for(Element e : (List<Element>)doc.selectNodes("//rface")){
			String f=e.getParent().attributeValue("name");
			RandomAccessFile file=new RandomAccessFile(splitDir+f, "rw");
			int[] pos = Util.toIntArray(e.attributeValue("pos").split(","));
			byte[] tile=new byte[0x800];
			ByteBuffer tiles=ByteBuffer.allocate(5*tile.length);
			
			for(int i=0;i<pos.length;i++){
				file.seek(pos[i]*tile.length);
				file.read(tile);
				tiles.put(tile);
			}
			
			BufferedImage img=Img4bitUtil.readRomToBmp(new ByteArrayInputStream(tiles.array()), 32, 32*5, grey);
			ImageIO.write(img, "bmp", new File(Conf.desktop+"face/"+f.replace("\\", "_")+".bmp"));
			file.close();
		}
		is.close();
	}
	
}
