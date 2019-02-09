package common;

public class EpsxeState {
	
	public static void main(String[] args) {
		
	}
	
	public static int toStateOffset(int memAddr){
		return memAddr+0x1ba;
	}
	
	public static int toMemAddr(int stateOffset){
		return stateOffset-0x1ba;
	}

}
