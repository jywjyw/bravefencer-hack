package brm.picture;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import com.ibm.icu.text.SimpleDateFormat;

import brm.Conf;
import common.Img8bitUtil;
import common.Palette;
import common.PixelConverter;
import common.Util;
import common.VramImg;
import common.gpu.primitive.Sprite;

public class Squaresoft implements PicHandler {
	
	@Override
	public void export(String splitDir, String exportDir) throws Exception {
		RandomAccessFile file = new RandomAccessFile(splitDir+"MAIN/000/0.1", "r");
		file.seek(0x2324);
		byte[] squaresoft=new byte[0x2c00];
		file.read(squaresoft);
		file.close();
		
		Palette pal=new Palette(256, Conf.getRawFile("clut/squaresoft.256"));
//		BufferedImage img=Img8bitUtil.readRomToBmp(file, 176, 32, pal);
		BufferedImage img=Img8bitUtil.readRomToBmp(new ByteArrayInputStream(squaresoft), 176, 32, pal);
		ImageIO.write(img, "bmp", new File(exportDir+Squaresoft.class.getSimpleName()+".bmp"));
	}

	
	/**
	 * old data structure:
			0~8f4: logo图像的DMA显示指令
			8f4~20f4:UI小字库3块,有用吗,3块共x1800大小
			20f4~2124:??似乎没读取
			2124~2324:squaresoft的256bit色板,x200大小
			2324-4f24:squaresoft的图像,x2c00大小
			4f24:两组gpu upload信息
				squaresoft色板:09000000未知,0000 e001:xy(0,480), 0001 0100:wh(256*1), 7c000d80:图像体的内存起始地址
				squaresoft图像:00000000未知,8002 0000:xy(640,0), b000 2000:wh(176,32),7c020d80:图像体的内存起始地址
			4f44~end: runtime space(size=0x137)
	 * @throws IOException 
	 */
	@Override
	public void import_(String splitDir) throws IOException {
		RandomAccessFile file = new RandomAccessFile(splitDir+"MAIN/000/0.1", "rw");
		byte[] asm=new byte[0x8f4];
		file.read(asm);
		file.seek(0x2324);
		byte[] oldLogo=new byte[0x2c00];
		file.read(oldLogo);
		int logoDMAOffset=0x4f34;
		file.seek(logoDMAOffset);
		GpuDmaPacket logoDMA=loadDmaPacket(file);
		
		Image newLogo = buildNewLogo(oldLogo);
		logoDMA.x=newLogo.vramX;
		logoDMA.y=newLogo.vramY;
		logoDMA.w=newLogo.w;
		logoDMA.h=newLogo.h;
		
		file.seek(0x507d);
		logoDMA.addr = Conf.SCRIPT1_ADDR+(int)file.getFilePointer();
		file.write(newLogo.data);
		file.seek(logoDMAOffset);
		file.write(logoDMA.toBytes());
		
		modifyPrimitiveAsm(file, newLogo.screenY, newLogo.h);
		file.close();
	}
	

	private GpuDmaPacket loadDmaPacket(RandomAccessFile file) throws IOException{
		GpuDmaPacket p = new GpuDmaPacket();
		file.read(p.unknown);
		byte[] buf=new byte[2];
		file.read(buf);
		p.x=Util.toInt(buf[1], buf[0]);
		file.read(buf);
		p.y=Util.toInt(buf[1], buf[0]);
		file.read(buf);
		p.w=Util.toInt(buf[1], buf[0]);
		file.read(buf);
		p.h=Util.toInt(buf[1], buf[0]);
		buf=new byte[4];
		file.read(buf);
		p.addr=Util.toInt(buf[3], buf[2], buf[1], buf[0]);
		return p;
	}

	class GpuDmaPacket{
		byte[] unknown=new byte[4];
		int x,y,w,h;
		int addr;	//img addr
		public byte[] toBytes(){
			ByteBuffer buf = ByteBuffer.allocate(16);
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.put(unknown);
			buf.putShort((short)x);
			buf.putShort((short)y);
			buf.putShort((short)w);
			buf.putShort((short)h);
			buf.putInt(addr);
			return buf.array();
		}
	}
	
	private Image buildNewLogo(byte[] oldLogo) throws IOException{
		BufferedImage img = ImageIO.read(new File(Conf.getRawFile("pic/"+Squaresoft.class.getSimpleName()+".bmp")));
		VramImg newLogo = Img8bitUtil.toVramImg(img, new Palette(256, Conf.getRawFile("clut/squaresoft.256")));
		return new Image(640,0, newLogo.w, newLogo.h, 240, newLogo.data);
	}
	
	private Image buildNewLogo2(byte[] oldLogo) throws IOException{
		int w=176;	//old logo's width
		BufferedImage img = new BufferedImage(w*2, 220, BufferedImage.TYPE_INT_RGB);	//width must equals old logo's width
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(Color.BLACK);	
		g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		Font font = new JLabel().getFont();	//use system default font
		int fontsize=30;
		font=font.deriveFont(Font.BOLD, fontsize);
        g2d.setFont(font);
        g2d.setColor(Color.white);
        int lineY=fontsize+10,lineH=fontsize+5;
        g2d.drawString("《武藏传》内测版v8", 0, lineY);
        lineY+=lineH;
        g2d.drawString("破解：草之头", 0, lineY);
        lineY+=lineH;
        g2d.drawString("翻译：路西華、人海沉沦", 0, lineY);
        lineY+=lineH;
        g2d.drawString("改图：Feya", 0, lineY);
        lineY+=lineH;
        g2d.drawString("测试：XXXX、", 0, lineY);
        lineY+=lineH;
        g2d.drawString("正式版请关注A9VG", 0, lineY);
        g2d.setFont(font.deriveFont(Font.BOLD, 25));
        lineY+=(lineH-5);
        g2d.drawString(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), img.getWidth()-150, lineY);
        
		g2d.dispose();
//ImageIO.write(img, "bmp", new File(Conf.desktop+"mylogo.bmp"));
		VramImg vramImg = Img8bitUtil.toVramImg(img, new PixelConverter() {
			@Override
			public int toPalIndex(int[] pixel) {
				if(pixel[0]==0){//black
					return 0;
				} else {
					return 0xff;
				}
			}
		});
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(oldLogo);
		bos.write(vramImg.data);
		int screenY=(480-32-lineY)/2;
		return new Image(640,0,w,32+vramImg.h,screenY,bos.toByteArray());
	}
	
	/**
	 * 从内存上传到显存的原始指令: 800cdff4 : 0d80043c7c2e8424785e000c, 在MAIN\000\0.1和MAIN\001\0.1的0x0000009C处
		ASM日志:
		800cdff4 : LUI     0108e450 (a0), 800d (32781), 
		800cdff8 : ADDIU   800d0000 (a0), 800d0000 (a0), 2e7c (11900),	#800d2e7c即为SQUARESOFT色板指针的起始地址,往后16字节即SQUARESOFT图像指针的起始地址
		800cdffc : JAL     800179e0, 800cdff4 (ra),
		800ce000 : NOP   
		
		作用: 查找SQUARESOFT色板指针的起始地址
	 */
	private void modifyAddressingAsm(RandomAccessFile file, int clutPPos)throws IOException{
		int entrance=0x9c;
		byte[] bs=Util.toBytes(clutPPos);
		file.seek(entrance);
		file.writeByte(bs[1]);
		file.writeByte(bs[0]);
		file.skipBytes(2);
		file.writeByte(bs[3]);
		file.writeByte(bs[2]);
	}
	
	
	/**
	 * 写屏原语
	====DMA==== @ C51BC
	0C51C0:E3 - drawing area top left 002800, (0, 10) 
	0C51C4:E4 - drawing area bottom right 07567F, (127, 469) 
	0C51C8:E5 - drawing offset 078140, (320, 240) #定义写屏基准点
	0C51CC:E1 - draw mode 00000A TP(640, 0)(bit:0)
	0C51D0:E2 - texture window 000000, (0, 0)*(0, 0)
	0C51D4:E6 - mask setting 000000, (0, 0) 
	0C51D8:02 - clear rect  (0, 10)*(640, 460) RGB( 0,  0,  0) 
	====DMA==== @ AD76C
	0CE7FC:E1 - draw mode 00008C TP(768, 0)(bit:1)
	0CE800:64 - sprite (80, -16)*(96, 32) clut(0, 480), UV(0, 0) RGB(80, 80, 80)	#80 80 80 64 50 00 f0 ff 00 00 00 78 60 00 20 00 , logo右半部分,该指令在MAIN/000/0.1和MAIN/001/1.1中,
	0CE7CC:E1 - draw mode 00008A TP(640, 0)(bit:1)
	0CE7D0:64 - sprite (-176, -16)*(256, 32) clut(0, 480), UV(0, 0) RGB(80, 80, 80)	#80 80 80 64 50 ff f0 ff 00 00 00 78 00 01 20 00 , logo左半部分. 
	====DMA==== @ A976C
	0CE7E4:E1 - draw mode 00008C TP(768, 0)(bit:1)
	0CE7E8:64 - sprite (80, -16)*(96, 32) clut(0, 480), UV(0, 0) RGB(80, 80, 80)
	0CE7B4:E1 - draw mode 00008A TP(640, 0)(bit:1)
	0CE7B8:64 - sprite (-176, -16)*(256, 32) clut(0, 480), UV(0, 0) RGB(80, 80, 80)
	 */
	private void modifyPrimitiveAsm(RandomAccessFile file, int screenY, int h)throws IOException{
		int l1=0x800CE7D0-Conf.SCRIPT1_ADDR,
			l2=0x800CE7B8-Conf.SCRIPT1_ADDR,
			r1=0x800CE800-Conf.SCRIPT1_ADDR,
			r2=0x800CE7E8-Conf.SCRIPT1_ADDR;
		file.seek(l1);
		Sprite left = Sprite.read(file);
		file.seek(r1);
		Sprite right = Sprite.read(file);
		int baseX=320,baseY=240;
		left.y=(short) (screenY-baseY);
		left.h=(short) h;
		right.y=left.y;
		right.h=left.h;
		
		file.seek(l1);
		file.write(left.toBytes());
		file.seek(l2);
		file.write(left.toBytes());
		file.seek(r1);
		file.write(right.toBytes());
		file.seek(r2);
		file.write(right.toBytes());
	}
	
	class Image{
		int vramX,vramY; //coordinate in vram
		int w,h;
		int screenY;
		byte[] data;
		public Image(int vramX, int vramY, int w, int h, int screenY, byte[] data) {
			this.vramX = vramX;
			this.vramY = vramY;
			this.w = w;
			this.h = h;
			this.screenY = screenY;
			this.data = data;
		}
	}
	
}
