package brm.dump;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Uncompresser {
	
	static final int 
		POS_BITS = 10,
		THRESHOLD = 2,
		N = 1<<POS_BITS,
		F = (1 << (16-POS_BITS))-1 + THRESHOLD;	//MAX_STORE_LENGH

    static final short MASK_POS = (1<<POS_BITS) - 1;
	
    /**
     * 和 4/6/1989 Haruhiko Okumura的LZSS的不同点:
     * threshold不同
     * pos_len二元组中,pos和len各自所占的位置和位数都不同
     * len值要加1??
     * 不填充空格
     * r的初始值为0,不是N-F
     */
    public static void uncompress(InputStream input, OutputStream out) throws IOException {
    	DataInputStream in = new DataInputStream(input);
    	/**
    	 * 解压时不会用到压缩时全部的环状缓冲,因为索引r<=MASK_POS,所以不需要定义为N+F-1.
    	 * 该环状缓冲的填充顺序为0,1,....,F,0,1,....,F,0,1.....
    	 */
    	byte[] ringBuf = new byte[N];	
    	int r = 1;	//环状缓冲的初始索引值. before: int r=N-F;
    	byte[] uncompressed=new byte[F];	//读出原始内容或查找到二元组指示的内容后,暂存到这里,再立即输出到环状缓冲和out,容量=最大len值+THRESHOLD,
        int flags=0, flagCount=0; // 8 bits of flags
        byte[] tuple = new byte[2];
        int pos,len,readsize;

        while (true) {
        	 if (flagCount > 0) {
                 flags = (byte) (flags >> 1);
                 flagCount--;
             } else {
            	 flags=in.read();	// Next byte must be a flag.
         		 if(flags==-1) break;
                 flagCount = 7;
             }
        	
            if ((flags&1) != 0) {	//输出原始字节
            	in.read(uncompressed, 0, 1);
                out.write(uncompressed[0]);
                ringBuf[r++] = uncompressed[0];
                r &= MASK_POS;	//超出MASK_POS范围后归0
            } else {	//输出二元组
				try {
					readsize = in.read(tuple);	// size must equals 2 or -1
					if(readsize == -1){	//end of file
						break;
					} else if(readsize != 2){	//unknown error
						throw new RuntimeException("readsize="+readsize);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				/**
				 * 假如二元组为1000 0100 0010 0001
				 * 标准LZSS解压规则:  [1000 0100] [0010] [0001], 位置=0010 1000 0100, 长度=0001+THRESHOLD
				 * 解压规则修改成: [1000 0100] [0010 00][01], 位置=[01 1000 0100], 长度=[0010 00]+THRESHOLD
				 * 实际测试中最小长度为2
				 */
				pos = (tuple[1]&3)<<8|tuple[0]&0xFF;
				len = tuple[1]>>>2&0x3F;
//                if(pos==0) {	//pos为0代表已到达文件末尾
//                	out.write(new byte[]{0,0});
//                	break;
//                }
				
                len += THRESHOLD;
                for (int i=0; i<len; i++) {
            		uncompressed[i] = ringBuf[(pos + i) & MASK_POS];
                    ringBuf[r++] = uncompressed[i];
                    r &= MASK_POS;
                }
                out.write(uncompressed,0,len);
            }
        }
    }
    
    public static void main(String[] args) {
		byte[] bs = new byte[]{(byte)0x57,(byte)0x01};
		System.out.println("pos="+((bs[1]&3)<<8|bs[0]&0xff));
		System.out.println("len="+(bs[1]>>>2&0x3F));
	}


}
