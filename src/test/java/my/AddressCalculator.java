package my;

import org.junit.Test;

import common.RscLoader;
import common.RscLoader.Callback;

public class AddressCalculator {

	@Test
	public void getEpsxeStateAddr(){
		int ramAddr = 0x79023;
		print(ramAddr+0x1BA);
	}
	
	private void print(int i){
		System.out.printf("%08X",i);
	}
	
	@Test
	public void get(){
		RscLoader.load("aaa", "utf-8", new Callback() {
			@Override
			public void doInline(String line) {
				System.out.printf("%X,\n",Integer.parseInt(line,16)+24);
			}
		});
	}
}
