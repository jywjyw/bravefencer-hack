package brm.script;

import java.io.RandomAccessFile;

import brm.Script;

public interface TextAddrGetter{
	int getFontPointerOffset();
	int[] getText1Addr();
	int[] getText2Addr();
}
//seek font pointer & text addr manually
class AddrManual implements TextAddrGetter{
	String[] addr;
	public AddrManual(String[] addr) {
		this.addr = addr;
	}
	@Override
	public int getFontPointerOffset() {
		return Integer.parseInt(addr[0],16);
	}
	
	@Override
	public int[] getText1Addr() {
		return new int[]{
				Integer.parseInt(addr[1],16),
				Integer.parseInt(addr[2],16)};
	}

	@Override
	public int[] getText2Addr() {
		if(addr.length>3){
			return new int[]{
				Integer.parseInt(addr[addr.length-2],16),
				Integer.parseInt(addr[addr.length-1],16)};
		}
		return null;
	}
}

/**
 * text start addr is near by font pointer
 */
class FontPointerNearbyText implements TextAddrGetter{
	String splitDir;
	Script script;
	public FontPointerNearbyText(String splitDir, Script script) {
		this.splitDir = splitDir;
		this.script = script;
	}

	@Override
	public int getFontPointerOffset() {
		return Integer.parseInt(script.addr[0],16);
	}
	
	@Override
	public int[] getText1Addr() {
		try {
			RandomAccessFile file = new RandomAccessFile(splitDir+script.file, "r");
			int fontPointer = Integer.parseInt(script.addr[0],16);
			file.seek(fontPointer+24);
			long textAddr = file.getFilePointer();
			file.close();
			return new int[]{(int)textAddr, Integer.parseInt(script.addr[1],16)};
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public int[] getText2Addr() {
		if(script.addr.length>3){
			return new int[]{
				Integer.parseInt(script.addr[script.addr.length-2],16),
				Integer.parseInt(script.addr[script.addr.length-1],16)};
		}
		return null;
	}
}
