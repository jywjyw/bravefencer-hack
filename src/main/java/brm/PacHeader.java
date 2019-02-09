package brm;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import common.Util;

public class PacHeader {
	public static final byte[] MAGIC = new byte[]{0x50,0x41,0x43,00};
	public static final int SIZE = Conf.LOGIC_BLOCK;
	
	public byte pacType;
	public byte endFlag;
	private byte[] unknown = new byte[4];
	public int len;
	private byte[] unknownSuffix=new byte[SIZE-16];
	
	public static PacHeader read(RandomAccessFile file) throws IOException{
		byte[] buf = new byte[SIZE];
		file.read(buf);
		return parse(buf);
	}
	
	public static PacHeader parse(byte[] bytes) throws IOException{
		DataInputStream is = new DataInputStream(new ByteArrayInputStream(bytes));
		PacHeader h = new PacHeader();
		is.readInt();	//magic
		h.pacType=is.readByte();
		h.endFlag=is.readByte();
		is.readShort();//zero
		is.read(h.unknown);
		h.len=Util.hilo(is.readInt());
		is.read(h.unknownSuffix);
		is.close();
		return h;
	}
	
	public byte[] toBytes(){
		ByteBuffer buf = ByteBuffer.allocate(SIZE);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.put(MAGIC);
		buf.put(pacType);
		buf.put(endFlag);
		buf.putShort((short) 0);
		buf.put(unknown);
		buf.putInt(len);
		buf.put(unknownSuffix);
		return buf.array();
	}

}
