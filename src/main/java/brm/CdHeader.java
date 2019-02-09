package brm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.Util;

public class CdHeader {
	
	private List<Integer> subcdLens=new ArrayList<>();
	
	public void addSubcd(int len){
		subcdLens.add(len);
	}
	
	public byte[] build(){
		return _build().array();
	}
	
	public byte[] trimBuild(){
		ByteBuffer buf = _build();
		return Arrays.copyOfRange(buf.array(), 0, buf.position());
	}
	
	private ByteBuffer _build(){
		ByteBuffer buf = ByteBuffer.allocate(Conf.LOGIC_BLOCK);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putLong(subcdLens.size());
		int addr=1;
		for(int len:subcdLens){
			buf.putInt(addr);
			addr+=Util.get0x800Multiple(len)/Conf.LOGIC_BLOCK;
			buf.putInt(len);
		}
		return buf;
	}

}
