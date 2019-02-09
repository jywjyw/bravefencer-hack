package brm.tool;

import java.io.IOException;
import java.io.RandomAccessFile;

import brm.Conf;
import brm.dump.Charset2;

public class MenuXYSeeker {
	public static void main(String[] args) throws IOException {
//		seek83xx();
//		for(int i=0xa0;i<=0xd0;i+=2) {
//			short s=(short)(0xff61+i);
//			s<<=1;
//			int j=s+0x800628cc;
//			System.out.printf("%X=%X\n",i,j);//AIUEO
//		}
//		printRam();
		
//		printxy(0x8143,"ヲ");
//		printxy(0x8144,".");
//		printxy(0x8145,"·");
//		printxy(0x8146,"$");
		printxy(0x8147,"→");
		printxy(0x8148,"?");
		printxy(0x8149,"！");
		printxy(0x814A,"←");
		printxy(0x814B,"·");
		printxy(0x814C,"/");
		printxy(0x814D,"-");
		printxy(0x814E,"=");
		printxy(0x8152,"人");
		printxy(0x8153,"日");
		printxy(0x8154,"月");
		printxy(0x8155,"火");
		printxy(0x8156,"水");
		printxy(0x8157,"风");
		printxy(0x8158,"空");
		printxy(0x8159,"土");
		printxy(0x815b,"-");
		printxy(0x815C,":");
		printxy(0x815D,"才");
		printxy(0x815E,"(");
		printxy(0x815F,")");
		
		printxy(0x8250,"1");
		printxy(0x8258,"9");
		printxy(0x8260,"A");
		printxy(0x826D,"N");
		printxy(0x826E,"O");
		printxy(0x826F,"P");
		printxy(0x8279,"Z");
		printxy(0x829F,"ぁ");
		printxy(0x82A0,"あ");
		printxy(0x82A1,"ぃ");
		printxy(0x82A2,"い");
		printxy(0x82A3,"ぅ");
		printxy(0x82A4,"う");
		printxy(0x82A5,"ぇ");
		printxy(0x82A6,"え");
		printxy(0x82A7,"ぉ");
		printxy(0x82A8,"お");
		printxy(0x82A9,"か");
		printxy(0x82AA,"がXXX");
		//AA~BB OK
		printxy(0x82BB,"そ");
		printxy(0x82BC,"");
		printxy(0x82BD,"た");
		printxy(0x82BE,"");
		printxy(0x82BF,"ち");
		printxy(0x82C0,"");
		printxy(0x82C1,"っ");
		printxy(0x82C2,"つ");
		printxy(0x82C3,"づXXX");
		printxy(0x82C4,"て");
		printxy(0x82C5,"で");
		printxy(0x82C6,"と");
		printxy(0x82C7,"な");
		printxy(0x82C8,"に");
		printxy(0x82C9,"ぬ");
		printxy(0x82CA,"ね");
		printxy(0x82CB,"の");
		
//		printxy(0x8340,"ァ");
//		printxy(0x8341,"ア");
		printxy(0x837D,"マ");
		printxy(0x837E,"ミ");
		printxy(0x8380,"ム");
		printxy(0x8381,"メ");
		printxy(0x8382,"モ");
		printxy(0x8383,"ャ");
		printxy(0x8384,"ヤ");
		printxy(0x8385,"ュ");
		printxy(0x8386,"ユ");
		printxy(0x8387,"ョ");
		printxy(0x8388,"ヨ");
		printxy(0x8389,"ラ");
		printxy(0x838A,"リ");
		printxy(0x838B,"");
		printxy(0x838C,"");
		printxy(0x838D,"");
		printxy(0x838E,"");
		printxy(0x838F,"");
		
		
	}
	
	public static void printxy(int code,String s){
		short xy=getXY(code);
		System.out.printf("(%d,%d)=%s\n",xy>>>8,xy&0xff,s);
	}
	
	public static short getXY(int code){
		int c1=(code>>>8), c2=code&0xff;
		if(c1==0x81){
			if(c2!=0x40){
				c2+=0xFFC0;
				c2&=0xffff;
				c2<<=1;
				return readShortfromRam(0X00062974+c2);
			} else {
				//8140时不写指令
				return 0;
			}
		} else if(c1==0x82){
			if(c2<0x9f){
				c2+=0xFFB1;
				c2&=0xffff;
				c2<<=1;
				return readShortfromRam(0x00062874+c2);
			} else {//jump 80023354
				c2+=0xFF61;
				c2&=0xffff;
				c2<<=1;
				return readShortfromRam(0x000628cc+c2);
			}
		} else if(c1<0x83){//TODO
			if(c2>=0x80){
				c2+=0xffbf;
				c2&=0xffff;
			}
			c2<<=0x10;
			c2>>>=0xf;
			short sh = readShortfromRam(0x000628cc+c2);
//			sh = (short) ((sh<<8)|(sh>>>8));
			sh+=0x20;
			return sh;
		} else {
			return 0;
		}
	}
	
	private static short readShortfromRam(int addr){
		RandomAccessFile file=null;
		try {
			file=new RandomAccessFile(Conf.desktop+"menu_ram.bin", "r");
			file.seek(addr);
			return file.readShort();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally{
			try {
				file.close();
			} catch (IOException e) {}
		}
	}
	
	public static void printRam() throws IOException {
		RandomAccessFile file=new RandomAccessFile(Conf.desktop+"ram.bin", "r");
		file.seek(0x628cc);
		int begin=0x9f;
		Charset2 cs=Charset2.loadVramCharset();
		for(int i=0;i<20;i++) {
			int x=file.read(),y=file.read();
			System.out.printf("%X=%s=(%d,%d)\n",begin,cs.getChar(0x8200+begin),x,y);
			begin++;
		}
		file.close();
	}
	
	public static void seek82xx() {
		int i=0x82a2;
		i=(short)(i&0xff + 0xff61);
		i<<=1;
		i += 0x800628cc;
		
		
	}
	
	public static void seek83xx() {
		int i=0x8369;
		i=i&0xff + 0xffc0;
		i<<=0x10;
		i>>>=0xf;
		i+=0x800628cc;
		//i=readShortInRam(i);  //8369对应0x828
		i+=0x2000;
		System.out.printf("%X",i);	//8369=40,40
	}

}
