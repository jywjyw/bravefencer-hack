package brm.hack;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import common.Img4bitUtil;
import common.Palette;
import common.Util;

public class VramFaceEditor {
	
	public void edit(String splitDir) throws Exception{
		replaceRfacePic(splitDir);
		rebuildXYWH(splitDir);
	}
	
	private void replaceRfacePic(String splitDir) throws Exception{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("pic_conf.xml");
		Document doc = new SAXReader().read(is);
		Palette grey=Palette.init16Grey();
		for(Element e : (List<Element>)doc.selectNodes("//rface")){
			RandomAccessFile file=new RandomAccessFile(splitDir+e.getParent().attributeValue("name"), "rw");
			int[] pos = Util.toIntArray(e.attributeValue("pos").split(","));
			byte[] tile=new byte[0x800];
			ByteBuffer tiles=ByteBuffer.allocate(5*tile.length);
			
			for(int i=0;i<pos.length;i++){
				file.seek(pos[i]*tile.length);
				file.read(tile);
				tiles.put(tile);
			}
			
			BufferedImage img=Img4bitUtil.readRomToBmp(new ByteArrayInputStream(tiles.array()), 32, 32*5, grey);
			Img4bitUtil.patch(rface, img, 64, 0);	//patch only right half of pic, because the left parts are different.
			List<BufferedImage> rebuildTiles=Img4bitUtil.splitToTiles(img, 32, 32);
			
			for(int i=0;i<pos.length;i++){
				file.seek(pos[i]*tile.length);
				file.write(Img4bitUtil.toVramImg(rebuildTiles.get(i), grey).data);
			}
			file.close();
		}
		is.close();
	}
	
	private void rebuildXYWH(String splitDir) throws Exception{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("script_jp_conf.xml");
		Document doc = new SAXReader().read(is);
		List<Element> vfaces=doc.selectNodes("//vface");
		for(Element e:vfaces){
			RandomAccessFile file=new RandomAccessFile(splitDir+e.getParent().attributeValue("name"),"rw");
			int pos=Integer.parseInt(e.attributeValue("xywh"),16);
			file.seek(pos);
//			byte[] buf=new byte[8];
//			file.read(buf);
//			if(!"7001400110002800".equals(Util.hexEncode(buf))){
//				System.err.println(file);
//			} else {
//				System.out.println(file);
//			}
			file.write(NEW_XYWH);
			file.close();
		}
		is.close();
	}
	
	private static BufferedImage rface;
	private static byte[] 
			R1=Util.decodeHex("70 01 40 01 10 00 28 00"),
			R2=Util.decodeHex("70 01 68 01 10 00 28 00"),
			R3=Util.decodeHex("70 01 90 01 10 00 28 00"),
			R4=Util.decodeHex("70 01 B8 01 10 00 28 00"),
			NEW_XYWH;
	static {
		List<BufferedImage> faceTiles=new ArrayList<>();
		try {
			faceTiles.add(ImageIO.read(new File(Conf.getRawFile("face/lface1.bmp"))));	//happy1
			faceTiles.add(ImageIO.read(new File(Conf.getRawFile("face/rface1.bmp"))));	//unhappy1
			faceTiles.add(ImageIO.read(new File(Conf.getRawFile("face/lface2.bmp"))));	//yawn
			faceTiles.add(ImageIO.read(new File(Conf.getRawFile("face/lface3.bmp"))));	//sleep1
			rface=Img4bitUtil.jointTiles(faceTiles, 1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		//rebuildXYWH()
		ByteBuffer ret=ByteBuffer.allocate(64);
		//default XYWH order : R1=unhappy1,R2=happy2,R3=dead,R4=unhappy2, L1=happy1,L2=yawn,L3=sleep1,L4=sleep2
		//right four faces change to : R1=HAPPY1,R2=UNHAPPY1,R3=YAWN,R4=SLEEP1
		ret.put(R2);
		ret.put(R1);
		ret.put(R3);
		ret.put(R2);
		ret.put(R1);
		ret.put(R3);
		ret.put(R4);
		ret.put(R4);
		NEW_XYWH=ret.array();
	}
	
	
}
