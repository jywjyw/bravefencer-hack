package brm.hack;

import java.io.IOException;
import java.io.RandomAccessFile;

import brm.Conf;
import common.instruction.MipsCompiler;

public class CheatCode {
	
	public static void cheat(String exe) throws IOException {
		RandomAccessFile file=new RandomAccessFile(exe, "rw");
		file.seek(Conf.getExeOffset(0x80029000));
		MipsCompiler mips=new MipsCompiler();
		file.write(mips.compileLine("sll v0,v0,1"));	//musashi's injury*2
		file.close();
	}

}
