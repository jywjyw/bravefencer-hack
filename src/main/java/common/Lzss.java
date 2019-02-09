/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 or later
 * as published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * © CrossWire Bible Society, 2007 - 2016
 *
 */
package common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import brm.Conf;

public class Lzss  {
	
	public static void main(String[] args) throws IOException {
		File raw=new File(Conf.desktop+"extracted");
		FileInputStream extractedIs = new FileInputStream(raw);
		byte[] comp = new Lzss(extractedIs).compress().toByteArray();
		extractedIs.close();
		byte[] reextracted = new Lzss(new ByteArrayInputStream(comp)).uncompress().toByteArray();
		System.out.println("原始文本 "+raw.length()+"="+Util.md5(raw));
		System.out.println("重解压的文本 "+reextracted.length+"="+Util.md5(reextracted));
	}
	
	
	 /**
     * This is the size of the ring buffer. It is set to 4K. It is important to
     * note that a position within the ring buffer requires 12 bits.
     */
    private static final short RING_SIZE = 4096;	//N

    /**
     * This is used to determine the next position in the ring buffer, from 0 to
     * RING_SIZE - 1. The idiom s = (s + 1) &amp; RING_WRAP; will ensure this. This
     * only works if RING_SIZE is a power of 2. Note this is slightly faster
     * than the equivalent: s = (s + 1) % RING_SIZE;
     */
    private static final short RING_WRAP = RING_SIZE - 1;

    /**
     * This is the maximum length of a character sequence that can be taken from
     * the ring buffer. It is set to 18. Note that a length must be 3 before it
     * is worthwhile to store a position/length pair, so the length can be
     * encoded in only 4 bits. Or, put yet another way, it is not necessary to
     * encode a length of 0-18, it is necessary to encode a length of 3-18,
     * which requires 4 bits.
     * <p>
     * Note that the 12 bits used to store the position and the 4 bits used to
     * store the length equal a total of 16 bits, or 2 bytes.
     * </p>
     */
    private static final int MAX_STORE_LENGTH = 18;

    /**
     * It takes 2 bytes to store an offset and a length. If a character sequence
     * only requires 1 or 2 characters to store uncompressed, then it is better
     * to store it uncompressed than as an offset into the ring buffer.
     * 如果未压缩字符串只有1个或2个字节的话,如果再使用二元组,将会浪费空间,因为二元组要占用2个字节
     */
    private static final int THRESHOLD = 3;	//当该值<3时,压缩再重解压后文本将不同,why??? 

    /**
     * Used to mark nodes as not used.
     */
    private static final short NOT_USED = RING_SIZE;

    
    /**
     * Create an LZSS that is capable of transforming the input.
     * 
     * @param input
     *            to compress or uncompress.
     */
	private InputStream input;
    public Lzss(InputStream input) {
    	this.input=input;
        ringBuffer = new byte[RING_SIZE + MAX_STORE_LENGTH - 1];
        dad = new short[RING_SIZE + 1];
        leftSon = new short[RING_SIZE + 1];
        rightSon = new short[RING_SIZE + 257];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.compress.Compressor#compress()
     */
    public ByteArrayOutputStream compress() throws IOException {
        out = new ByteArrayOutputStream();

        short i; // an iterator
        short r; // node number in the binary tree
        short s; // position in the ring buffer
        short len; // length of initial string
        short lastMatchLength; // length of last match
        short codeBufPos; // position in the output buffer
        byte[] codeBuff = new byte[17]; //压缩缓冲区. 存放标志字节+8个原始字符或编码后的2元组,每处理8个后输出并清空,容量为1+8*2=17Byte
        byte mask; // bit mask for byte 0 of out input
        byte c; // character read from string

        // Start with a clean tree.
        initTree();

        // codeBuff[0] works as eight flags. A "1" represents that the
        // unit is an unencoded letter (1 byte), and a "0" represents
        // that the next unit is a <position,length> pair (2 bytes).
        //
        // codeBuff[1..16] stores eight units of code. Since the best
        // we can do is store eight <position,length> pairs, at most 16
        // bytes are needed to store this.
        //
        // This is why the maximum size of the code buffer is 17 bytes.
        codeBuff[0] = 0;
        codeBufPos = 1;

        // Mask iterates over the 8 bits in the code buffer. The first
        // character ends up being stored in the low bit.
        //
        // bit 8 7 6 5 4 3 2 1
        // | |
        // | first sequence in code buffer
        // |
        // last sequence in code buffer
        mask = 1;

        s = 0;
        r = RING_SIZE - MAX_STORE_LENGTH;

        // Initialize the ring buffer with spaces...

        // Note that the last MAX_STORE_LENGTH bytes of the ring buffer are not
        // filled.
        // This is because those MAX_STORE_LENGTH bytes will be filled in
        // immediately
        // with bytes from the input stream.
        Arrays.fill(ringBuffer, 0, r, (byte) ' ');

        // Read MAX_STORE_LENGTH bytes into the last MAX_STORE_LENGTH bytes of
        // the ring buffer.
        //
        // This function loads the buffer with up to MAX_STORE_LENGTH characters
        // and returns
        // the actual amount loaded.
        int readResult = input.read(ringBuffer, r, MAX_STORE_LENGTH);

        // Make sure there is something to be compressed.
        if (readResult <= 0) {
            return out;
        }

        len = (short) readResult;

        // Insert the MAX_STORE_LENGTH strings, each of which begins with one or more 'space' characters. 
        // Note the order in which these strings are inserted. 
        // This way, degenerate trees will be less likely to occur.
        for (i = 1; i <= MAX_STORE_LENGTH; i++) {
            insertNode((short) (r - i));
        }

        // Finally, insert the whole string just read. The
        // member variables matchLength and matchPosition are set.
        insertNode(r);

        // Now that we're preloaded, continue till done.
        do {

            // matchLength may be spuriously long near the end of text.
            if (matchLength > len) {
                matchLength = len;
            }

            // Is it cheaper to store this as a single character? If so, make it
            // so.
            if (matchLength < THRESHOLD) {
                // Send one character. Remember that codeBuff[0] is the
                // set of flags for the next eight items.
                matchLength = 1;
                codeBuff[0] |= mask;
                codeBuff[codeBufPos++] = ringBuffer[r];
            } else {
                // Otherwise, we do indeed have a string that can be stored
                // compressed to save space.

                // The next 16 bits need to contain the position (12 bits)
                // and the length (4 bits).
            	/**
            	 * 第1个字节:pos的0~7位
            	 * 第2个字节:从低位到高位分别是:len0,len1,len2,len3,pos8,pos9,pos10,pos11
            	 * 由于pos占12位,所以只取pos的8~11位组成第2个字节
            	 * 由于len占4位,所以只取len的0~3位组成第2个字节
            	 */
                codeBuff[codeBufPos++] = (byte) matchPosition;
                codeBuff[codeBufPos++] = (byte) (((matchPosition >> 4) & 0xF0) | (matchLength - THRESHOLD));
            }

            // Shift the mask one bit to the left so that it will be ready
            // to store the new bit.
            mask <<= 1;

            // If the mask is now 0, then we know that we have a full set
            // of flags and items in the code buffer. These need to be
            // output.
            if (mask == 0) {
                // codeBuff is the buffer of characters to be output.
                // codeBufPos is the number of characters it contains.
                out.write(codeBuff, 0, codeBufPos);

                // Reset for next buffer...
                codeBuff[0] = 0;
                codeBufPos = 1;
                mask = 1;
            }

            lastMatchLength = matchLength;

            // Delete old strings and read new bytes...
            for (i = 0; i < lastMatchLength; i++) {

                // Get next character...
                readResult = input.read();
                if (readResult == -1) {
                    break;
                }
                c = (byte) readResult;

                // Delete "old strings"
                deleteNode(s);

                // Put this character into the ring buffer.
                //
                // The original comment here says "If the position is near
                // the end of the buffer, extend the buffer to make
                // string comparison easier."
                //
                // That's a little misleading, because the "end" of the
                // buffer is really what we consider to be the "beginning"
                // of the buffer, that is, positions 0 through MAX_STORE_LENGTH.
                //
                // The idea is that the front end of the buffer is duplicated
                // into the back end so that when you're looking at characters
                // at the back end of the buffer, you can index ahead (beyond
                // the normal end of the buffer) and see the characters
                // that are at the front end of the buffer without having
                // to adjust the index.
                //
                // That is...
                //
                // 1234xxxxxxxxxxxxxxxxxxxxxxxxxxxxx1234
                // | | |
                // position 0 end of buffer |
                // |
                // duplicate of front of buffer
                ringBuffer[s] = c;

                if (s < MAX_STORE_LENGTH - 1) {
                    ringBuffer[s + RING_SIZE] = c;
                }

                // Increment the position, and wrap around when we're at
                // the end. Note that this relies on RING_SIZE being a power of
                // 2.
//                System.out.println("r="+r+",s="+s);
                s = (short) ((s + 1) & RING_WRAP);
                r = (short) ((r + 1) & RING_WRAP);

                // Register the string that is found in
                // ringBuffer[r..r + MAX_STORE_LENGTH - 1].
                insertNode(r);
            }

            // If we didn't quit because we hit the lastMatchLength,
            // then we must have quit because we ran out of characters
            // to process.
            while (i++ < lastMatchLength) {
                deleteNode(s);
                s = (short) ((s + 1) & RING_WRAP);
                r = (short) ((r + 1) & RING_WRAP);
                // Note that len hitting 0 is the key that causes the
                // do...while() to terminate. This is the only place
                // within the loop that len is modified.
                //
                // Its original value is MAX_STORE_LENGTH (or a number less than
                // MAX_STORE_LENGTH for
                // short strings).
                if (--len != 0) {
                    insertNode(r); /* buffer may not be empty. */
                }
            }

            // End of do...while() loop. Continue processing until there
            // are no more characters to be compressed. The variable
            // "len" is used to signal this condition.
        } while (len > 0);

        // There could still be something in the output buffer. Send it now.
        if (codeBufPos > 1) {
            // codeBuff is the encoded string to send.
            // codeBufPos is the number of characters.
            out.write(codeBuff, 0, codeBufPos);
        }

        return out;
    }

    /**
     * Initializes the tree nodes to "empty" states.
     */
    private void initTree() {
        // For i = 0 to RING_SIZE - 1, rightSon[i] and leftSon[i] will be the
        // right
        // and left children of node i. These nodes need not be
        // initialized. However, for debugging purposes, it is nice to
        // have them initialized. Since this is only used for compression
        // (not decompression), I don't mind spending the time to do it.
        //
        // For the same range of i, dad[i] is the parent of node i.
        // These are initialized to a known value that can represent
        // a "not used" state.
        // For i = 0 to 255, rightSon[RING_SIZE + i + 1] is the root of the tree
        // for strings that begin with the character i. This is why
        // the right child array is larger than the left child array.
        // These are also initialized to a "not used" state.
        //
        // Note that there are 256 of these, one for each of the possible
        // 256 characters.
        Arrays.fill(dad, 0, dad.length, NOT_USED);
        Arrays.fill(leftSon, 0, leftSon.length, NOT_USED);
        Arrays.fill(rightSon, 0, rightSon.length, NOT_USED);
    }

    /**
     * Inserts a string from the ring buffer into one of the trees. It loads the
     * match position and length member variables for the longest match.
     * 
     * <p>
     * The string to be inserted is identified by the parameter pos, A full
     * MAX_STORE_LENGTH bytes are inserted. So, ringBuffer[pos ...
     * pos+MAX_STORE_LENGTH-1] are inserted.
     * </p>
     * 
     * <p>
     * If the matched length is exactly MAX_STORE_LENGTH, then an old node is
     * removed in favor of the new one (because the old one will be deleted
     * sooner).
     * </p>
     * 
     * @param pos
     *            plays a dual role. It is used as both a position in the ring
     *            buffer and also as a tree node. ringBuffer[pos] defines a
     *            character that is used to identify a tree node.
     */
    private void insertNode(short pos) {
        int cmp = 1;
        short key = pos;

        // The last 256 entries in rightSon contain the root nodes for
        // strings that begin with a letter. Get an index for the
        // first letter in this string.
        short p = (short) (RING_SIZE + 1 + (ringBuffer[key] & 0xFF));	//rson多分配的256个元素空间，P指向索引根节点
        if(pos<0||pos>=RING_SIZE||p<=RING_SIZE) throw new RuntimeException();

        // Set the left and right tree nodes for this position to "not used."
        leftSon[pos] = NOT_USED;
        rightSon[pos] = NOT_USED;//r扮演了两个角色，作为树的节点和缓冲区中的位置

        // Haven't matched anything yet.
        matchLength = 0;

        while (true) {
            if (cmp >= 0) {//这是进行匹配后寻径的方式，cmp大于等于0（除了根节点，cmp只在失配时改变，永远都不可能等于0），查找当前节点的右节点，反之查找左节点
                if (rightSon[p] == NOT_USED) {
                	rightSon[p] = pos;
                	dad[pos] = p;
                	return;
                } else {
                	p = rightSon[p];
                }
            } else {
                if (leftSon[p] == NOT_USED) {
                    leftSon[p] = pos;
                    dad[pos] = p;
                    return;
                } else {
                	p = leftSon[p];
                }
            }

            // Should we go to the right or the left to look for the
            // next match?
            short i = 0;
            for (i = 1; i < MAX_STORE_LENGTH; i++) {	 //对左节点进行匹配操作，更新cmp
                cmp = (ringBuffer[key + i] & 0xFF) - (ringBuffer[p + i] & 0xFF);
                if (cmp != 0) {
                    break;
                }
            }

            if (i > matchLength) {	//全局变量match_length就是最大匹配长度
                matchPosition = p;
                matchLength = i;

                if (i >= MAX_STORE_LENGTH) {//为什么mathch_length不能超过F，因为压缩元只为长度预留了4位空间，压缩的大小只能是3-18，比他小没意义，比他大装不下
                    break;
                }
            }
        }

        dad[pos] = dad[p];
        leftSon[pos] = leftSon[p];
        rightSon[pos] = rightSon[p];

        dad[leftSon[p]] = pos;
        dad[rightSon[p]] = pos;

        if (rightSon[dad[p]] == p) {
            rightSon[dad[p]] = pos;
        } else {
            leftSon[dad[p]] = pos;
        }

        // Remove "p"
        dad[p] = NOT_USED;//干净利索的删除
    }

    /**
     * Remove a node from the tree.
     * 
     * @param node
     *            the node to remove
     */
    private void deleteNode(short node) {
        if(node<0||node>=RING_SIZE+1) throw new RuntimeException();

        short q;

        if (dad[node] == NOT_USED) {
            // not in tree, nothing to do
            return;
        }

        if (rightSon[node] == NOT_USED) {
            q = leftSon[node];
        } else if (leftSon[node] == NOT_USED) {
            q = rightSon[node];
        } else {
            q = leftSon[node];
            if (rightSon[q] != NOT_USED) {
                do {
                    q = rightSon[q];
                } while (rightSon[q] != NOT_USED);

                rightSon[dad[q]] = leftSon[q];
                dad[leftSon[q]] = dad[q];
                leftSon[q] = leftSon[node];
                dad[leftSon[node]] = q;
            }

            rightSon[q] = rightSon[node];
            dad[rightSon[node]] = q;
        }

        dad[q] = dad[node];

        if (rightSon[dad[node]] == node) {
            rightSon[dad[node]] = q;
        } else {
            leftSon[dad[node]] = q;
        }

        dad[node] = NOT_USED;
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.compress.Compressor#uncompress(int)
     */
    public ByteArrayOutputStream uncompress() throws IOException {
        out = new ByteArrayOutputStream();

        byte[] c = new byte[MAX_STORE_LENGTH]; // an array of chars
        byte flags; // 8 bits of flags

        // Initialize the ring buffer with a common string.
        //
        // Note that the last MAX_STORE_LENGTH bytes of the ring buffer are not
        // filled.
        // r is a nodeNumber
        int r = RING_SIZE - MAX_STORE_LENGTH;
        Arrays.fill(ringBuffer, 0, r, (byte) ' ');

        flags = 0;
        int flagCount = 0; // which flag we're on

        while (true) {
            // If there are more bits of interest in this flag, then
            // shift that next interesting bit into the 1's position.
            //
            // If this flag has been exhausted, the next byte must be a flag.
            if (flagCount > 0) {
                flags = (byte) (flags >> 1);
                flagCount--;
            } else {
                // Next byte must be a flag.
                int readResult = input.read();
                if (readResult == -1) {
                    break;
                }

                flags = (byte) (readResult & 0xFF);

                // Set the flag counter. While at first it might appear
                // that this should be an 8 since there are 8 bits in the
                // flag, it should really be a 7 because the shift must
                // be performed 7 times in order to see all 8 bits.
                flagCount = 7;
            }

            // If the low order bit of the flag is now set, then we know
            // that the next byte is a single, unencoded character.
            if ((flags & 1) != 0) {
                if (input.read(c, 0, 1) != 1) {
                    break;
                }

                out.write(c[0]);

                // Add to buffer, and increment to next spot. Wrap at end.
                ringBuffer[r] = c[0];
                r = (short) ((r + 1) & RING_WRAP);
            } else {
                // Otherwise, we know that the next two bytes are a
                // <position,length> pair. The position is in 12 bits and
                // the length is in 4 bits.
                if (input.read(c, 0, 2) != 2) {
                    break;
                }

                // Convert these two characters into the position and
                // length in the ringBuffer. Note that the length is always at
                // least
                // THRESHOLD, which is why we're able to get a length
                // of 18 out of only 4 bits.
                short pos = (short) ((c[0] & 0xFF) | ((c[1] & 0xF0) << 4));
                short len = (short) ((c[1] & 0x0F) + THRESHOLD);

                // There are now "len" characters at position "pos" in
                // the ring buffer that can be pulled out. Note that
                // len is never more than MAX_STORE_LENGTH.
                for (int k = 0; k < len; k++) {
                    c[k] = ringBuffer[(pos + k) & RING_WRAP];

                    // Add to buffer, and increment to next spot. Wrap at end.
                    ringBuffer[r] = c[k];
                    r = (r + 1) & RING_WRAP;
                }

                // Add the "len" characters to the output stream.
                out.write(c, 0, len);
            }
        }
        return out;
    }

   
    /**
     * A text buffer. It contains "nodes" of uncompressed text that can be
     * indexed by position. That is, a substring of the ring buffer can be
     * indexed by a position and a length. When decoding, the compressed text
     * may contain a position in the ring buffer and a count of the number of
     * bytes from the ring buffer that are to be moved into the uncompressed
     * buffer.
     * 
     * <p>
     * This ring buffer is not maintained as part of the compressed text.
     * Instead, it is reconstructed dynamically. That is, it starts out empty
     * and gets built as the text is decompressed.
     * </p>
     * 
     * <p>
     * The ring buffer contain RING_SIZE bytes, with an additional
     * MAX_STORE_LENGTH - 1 bytes to facilitate string comparison.
     * </p>
     */
    private byte[] ringBuffer;

    /**
     * The position in the ring buffer. Used by insertNode.
     */
    private short matchPosition;

    /**
     * The number of characters in the ring buffer at matchPosition that match a
     * given string. Used by insertNode.
     */
    private short matchLength;

    /**
     * leftSon, rightSon, and dad are the Japanese way of referring to a tree
     * structure. The dad is the parent and it has a right and left son (child).
     * 
     * <p>
     * For i = 0 to RING_SIZE-1, rightSon[i] and leftSon[i] will be the right
     * and left children of node i.
     * </p>
     * 
     * <p>
     * For i = 0 to RING_SIZE-1, dad[i] is the parent of node i.
     * </p>
     * 
     * <p>
     * For i = 0 to 255, rightSon[RING_SIZE + i + 1] is the root of the tree for
     * strings that begin with the character i. Note that this requires one byte
     * characters.
     * </p>
     * 
     * <p>
     * These nodes store values of 0...(RING_SIZE-1). Memory requirements can be
     * reduces by using 2-byte integers instead of full 4-byte integers (for
     * 32-bit applications). Therefore, these are defined as "shorts."
     * </p>
     */
    private short[] dad;
    private short[] leftSon;
    private short[] rightSon;

    /**
     * The output stream containing the result.
     */
    private ByteArrayOutputStream out;
}
