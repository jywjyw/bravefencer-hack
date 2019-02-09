package brm.hack;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import brm.Conf;
import brm.hack.Font8x8.XY;
import common.Img4bitUtil;
import common.MultiLayerChars;
import common.MultiLayerFontGen;
import common.Palette;
import common.Util;
import common.VramImg;

public class MenuFontLibBuilder {
	
	Font8x8 font8x8=new Font8x8();
	Palette font8x8Pal;
	
	public MenuFontLibBuilder(){
		try {
			font8x8Pal=new Palette(16, Conf.getRawFile("clut/memcard.16"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void rebuild(String splitdir, MultiLayerChars charLayers) throws Exception{
		ByteBuffer font10WithClut=ByteBuffer.allocate(Conf.LOGIC_BLOCK*4);
		VramImg font10=MultiLayerFontGen.build4LayerFont(charLayers, 32*4, Conf.desktop+"Zfull-GB.ttf", 10);
		font10WithClut.put(patchFont8ToFont10(font10));
		font10WithClut.position(120*32*2); //write clut from the 13th char line
		List<Palette> pals=MultiLayerFontGen.build4Palette((short)0x3F02);	//red.  available color : 1310 9F39 9F5A  3F02 DF02 BF03 9F73
		pals.addAll(MultiLayerFontGen.build4Palette((short)0x777f));		//blue. available color : 604c,6c7e,777f,3f02,df02,bf03,ff7f
		for(Palette p:pals){
			font10WithClut.put(p.getRaw());
		}
		
		List<byte[]> allFontTiles=new ArrayList<>();
		allFontTiles.addAll(rebuildFont8Pic(splitdir));
		for(int i=0;i<4;i++){	//eraseTiles=4
			allFontTiles.add(Arrays.copyOfRange(font10WithClut.array(), i*0x800, (i+1)*0x800));
		}
		replaceFontToRom(splitdir, allFontTiles);
		modifyFontUV();
	}
	
	
	/**
	 * font8的字库容量仍然不够，需要把一部分toY>=96的字符写到font10的区域内
	 */
	private byte[] patchFont8ToFont10(VramImg font10) throws IOException{
		BufferedImage font10Img=Img4bitUtil.readRomToBmp(new ByteArrayInputStream(font10.data), font10.w, font10.h, font8x8Pal);
		for(Entry<String,XY> e: font8x8.coordinate.entrySet()) {
			if(!e.getKey().startsWith("big.") && e.getValue().toY>=96) {
				BufferedImage tile=font8x8.font.getSubimage(e.getValue().fromX, e.getValue().fromY, 8,8);
				Img4bitUtil.patch(tile, font10Img, e.getValue().toX, e.getValue().toY-96);
			}
		}
		return Img4bitUtil.toVramImg(font10Img, font8x8Pal).data;
	}
	
	private List<byte[]> rebuildFont8Pic(String splitdir) throws IOException{
		BufferedImage canvas=ImageIO.read(new File(Conf.getRawFile("pic/menuFont.bmp")));
		for(Entry<String,XY> e: font8x8.coordinate.entrySet()) {
			if(e.getKey().startsWith("big.")) {
				BufferedImage tile=canvas.getSubimage(e.getValue().fromX, e.getValue().fromY, 16,24);
				Img4bitUtil.patch(tile, canvas, e.getValue().toX, e.getValue().toY);
			} else if(e.getValue().toY<96){
				BufferedImage tile=font8x8.font.getSubimage(e.getValue().fromX, e.getValue().fromY, 8,8);
				Img4bitUtil.patch(tile, canvas, e.getValue().toX, e.getValue().toY);
			}
		}
		
		VramImg vram = Img4bitUtil.toVramImg(canvas.getSubimage(0, 0, 128, 128), font8x8Pal);
		List<byte[]> tiles=new ArrayList<>();
		for(int i=0;i<3;i++){
			tiles.add(Arrays.copyOfRange(vram.data, i*0x800, (i+1)*0x800));
		}
		return tiles;
	}
	
	private void replaceFontToRom(String splitdir, List<byte[]> tiles) throws DocumentException, IOException{
		InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("pic_conf.xml");
		Document doc=new SAXReader().read(is);
		for(Element e: (List<Element>)doc.selectNodes("//font")){
			RandomAccessFile file=new RandomAccessFile(splitdir+e.getParent().attributeValue("name"), "rw");
			int[] positions = Util.toIntArray(e.attributeValue("pos").split(","));
			for(int i=0;i<tiles.size();i++){
				file.skipBytes(0x800*positions[i]);
				file.write(tiles.get(i));
			}
			file.close();
		}
		is.close();
	}
	
	private void modifyFontUV() throws IOException{
		RandomAccessFile exe=new RandomAccessFile(Conf.outdir+Conf.EXE, "rw");
		
		//build mapping between init_encoding_menu.gbk and font8x8.fnt 
		exe.seek(Conf.getExeOffset(0x800628cc));
		exe.writeShort(0x0050); //829f={_}
		for(Entry<String,XY> e: font8x8.coordinate.entrySet()) {
			if(e.getKey().startsWith("82.")) {
				exe.write(e.getValue().toX);
				exe.write(e.getValue().toY);
			}
		}
		
		//modify the UV of 815B(-), because new big num has overwritten it 
		exe.seek(Conf.getExeOffset(0X800629aa));
		exe.write(120);
		exe.write(64);
		
		//modify big num's UV
		exe.seek(Conf.getExeOffset(0x800629b4));
		for(Entry<String,XY> e: font8x8.coordinate.entrySet()) {
			if(e.getKey().startsWith("big.")) {
				exe.write(e.getValue().toX);
				exe.write(e.getValue().toY);
			}
		}
		exe.close();
	}
}
