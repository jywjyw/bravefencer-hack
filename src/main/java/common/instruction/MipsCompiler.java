package common.instruction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;

import common.Util;

public class MipsCompiler {
	
	public static void main(String[] args) throws FileNotFoundException {
		MipsCompiler c = new MipsCompiler();
		c.setAddress(0x80032a38);
		System.out.println(Util.hexEncode(c.compileLine("lui a2,",0)));//
//		InputStream asm = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.asm");
//		System.out.println(Util.hexEncode(c.compileFile(asm)));
	}
	
	private Integer address;
	
	public byte[] compileResource(String rsc){
		return compileFile(Thread.currentThread().getContextClassLoader().getResourceAsStream(rsc));
	}
	
	public byte[] compileFile(InputStream file){
		String line;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file, "iso8859-1"));
			ByteBuffer ret = ByteBuffer.allocate(5000);
			int lineNum=1;
			while((line=br.readLine())!=null){
				if(line.length()>0 && !line.startsWith("//")){
					if(line.startsWith("address=")){
						address=Integer.parseUnsignedInt(line.split("=")[1],16);
					} else {
						ret.put(compileLine(line,lineNum));
						if(address!=null) address+=4;
					}
				}
				lineNum++;
			}
			return Arrays.copyOf(ret.array(), ret.position());
		} catch(IOException e){
			e.printStackTrace();
			return null;
		} finally {
			try {
				file.close();
			} catch (IOException e) {}
		}
	}
	
	public byte[] compileLine(String s){
		if(s.contains("//")){
			s=s.substring(0, s.indexOf("//")).trim().toLowerCase();
		} else {
			s=s.trim().toLowerCase();
		}
		int end=s.length();
		int blank = s.indexOf(" ");
		String op = s.substring(0,blank==-1?end:blank);
		try {
			return (byte[])this.getClass().getMethod(op, String.class, String.class, int.class, int.class)
					.invoke(this, op, s, blank, end);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public byte[] compileLine(String s, int lineNum){
		try {
			return compileLine(s);
		} catch (Exception e) {
			throw new RuntimeException(String.format("line %d error: [%s]", lineNum, s), e);
		}
	}
	
	public MipsCompiler setAddress(Integer address) {
		this.address = address;
		return this;
	}

	public byte[] nop(String method, String s, int blank, int end){
		return new byte[4];
	}
	public byte[] lb(String method, String s, int blank, int end){
		String rt = s.substring(s.indexOf(" ")+1, s.indexOf(","));
		String offset = s.substring(s.indexOf(",")+1, s.indexOf("("));
		String base = s.substring(s.indexOf("(")+1, s.indexOf(")"));
		return typeI(method, base, rt, Integer.parseInt(offset,16));
	}
	public byte[] lbu(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] lh(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] lhu(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] lw(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] lwl(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] lwr(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] sb(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] sh(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] sw(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] swl(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	public byte[] swr(String method, String s, int blank, int end){
		return lb(method,s,blank,end);
	}
	
	
	
	
	public byte[] addi(String method, String s, int blank, int end){
		String[] arr = s.substring(blank+1,end).split(",");
		return typeI(method, arr[1], arr[0], Integer.parseInt(arr[2],16));
	}
	public byte[] addiu(String method, String s, int blank, int end){
		return addi(method, s, blank, end);
	}
	public byte[] slti(String method, String s, int blank, int end){
		return addi(method, s, blank, end);
	}
	public byte[] sltiu(String method, String s, int blank, int end){
		return addi(method, s, blank, end);
	}
	public byte[] andi(String method, String s, int blank, int end){
		return addi(method, s, blank, end);
	}
	public byte[] ori(String method, String s, int blank, int end){
		return addi(method, s, blank, end);
	}
	public byte[] xori(String method, String s, int blank, int end){
		return addi(method, s, blank, end);
	}
	public byte[] lui(String method, String s, int blank, int end){
		String rt = s.substring(s.indexOf(" ")+1, s.indexOf(","));
		String imdt = s.substring(s.indexOf(",")+1, s.length());
		return typeI(method, null, rt, Integer.parseInt(imdt,16));
	}
	
	
	public byte[] add(String method, String s, int blank, int end){
		String[] arr = s.substring(blank+1,end).split(",");
		return typeR(method, arr[1], arr[2], arr[0], null);
	}
	public byte[] addu(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] sub(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] subu(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] slt(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] sltu(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] and(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] or(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] xor(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	public byte[] nor(String method, String s, int blank, int end){
		return add(method, s, blank, end);
	}
	
	
	
	
	
	public byte[] sll(String method, String s, int blank, int end){
		String[] arr = s.substring(blank+1,end).split(",");
		return typeR(method, null, arr[1], arr[0], arr[2]);
	}
	public byte[] srl(String method, String s, int blank, int end){
		return sll(method,s,blank,end);
	}
	public byte[] sra(String method, String s, int blank, int end){
		return sll(method,s,blank,end);
	}
	
	
	public byte[] mult(String method, String s, int blank, int end){
		String[] arr = s.substring(blank+1,end).split(",");
		return typeR(method, arr[0], arr[1], null, null);
	}
	public byte[] multu(String method, String s, int blank, int end){
		return mult(method, s, blank, end);
	}
	public byte[] div(String method, String s, int blank, int end){
		return mult(method, s, blank, end);
	}
	public byte[] divu(String method, String s, int blank, int end){
		return mult(method, s, blank, end);
	}
	public byte[] mfhi(String method, String s, int blank, int end){
		String rd = s.substring(blank+1,end);
		return typeR(method, null, null, rd, null);
	}
	public byte[] mflo(String method, String s, int blank, int end){
		return mfhi(method, s, blank, end);
	}
	public byte[] mthi(String method, String s, int blank, int end){
		return mfhi(method, s, blank, end);
	}
	public byte[] mtlo(String method, String s, int blank, int end){
		return mfhi(method, s, blank, end);
	}
	
	
	
	public byte[] j(String method, String s, int blank, int end){
		return typeJ(method, s.split(" ")[1]);
	}
	public byte[] jal(String method, String s, int blank, int end){
		return j(method, s, blank, end);
	}
	public byte[] jr(String method, String s, int blank, int end){
		return toBytes(0x03e00008);
	}
	
	/**
	 * required address=80xxxxxx
	 * example 1: beq a1,a2,80123456	//branch to memory address:80123456
	 * example 2: beq a1,a2,5			//jump ahead 5 instructions
	 * example 3: beq a1,a2,-5			//jump back 5 instructions
	 */
	public byte[] beq(String method, String s, int blank, int end){
		if(address==null) throw new UnsupportedOperationException("branch instruction required address property");
		String[] arr = s.substring(blank+1,end).split(",");
		int offset;
		if(arr[2].startsWith("-")){
			offset=Integer.parseInt(arr[2])-1;
		} else {
			int target=Integer.parseUnsignedInt(arr[2],16); 
			if(target>>>24==0x80){		//it's mem addr
				offset=(target-address)/4-1;//以程序计数器$PC为基准,向前或向后跳跃N个指令
			} else {
				offset=Integer.parseInt(arr[2])-1;
			}
		}
		if(offset>Short.MAX_VALUE || offset<Short.MIN_VALUE)
			throw new RuntimeException("branch instruction offset is out of range:"+offset);
		return typeI(method, arr[0], arr[1], offset);
	}
	public byte[] bne(String method, String s, int blank, int end){
		return beq(method, s, blank, end);
	}
	
	private byte[] typeI(String op, String rs, String rt, int immediate){
		int opcode = Op.index(op);
		int i = opcode<<26;
		int rsIndex = 0;
		if(rs!=null) rsIndex = Register.index(rs);
		i |= rsIndex<<21;
		int rtIndex = Register.index(rt);
		i |= rtIndex<<16;
		i |= (immediate&0xffff);
		return toBytes(i);
	}
	
	private byte[] typeJ(String op, String target){
		int opcode=Op.index(op);
		int i=opcode<<26;
		int addr=(int)(Long.parseLong(target,16)) & 0xffffff;
		if(addr%4!=0) 
			throw new RuntimeException("j target must be multiply 4 bytes : "+target);
		i|=(addr>>>2);
		return toBytes(i);
	}
	
	private byte[] typeR(String op, String rs, String rt, String rd, String shamt){
		int i=0;
		if(rs!=null)  i |= Register.index(rs)<<21;
		if(rt!=null)  i |= Register.index(rt)<<16;
		if(rd!=null)  i |= Register.index(rd)<<11;
		if(shamt!=null)i |= (Integer.parseInt(shamt,16)&0x1f)<<6;
		i |= Special.index(op)&0x3f;
		return toBytes(i);
	}
	
	
	private byte[] toBytes(int i){
		return new byte[]{(byte)i, (byte)(i>>>8), (byte)(i>>>16), (byte)(i>>>24)};
	}

}
