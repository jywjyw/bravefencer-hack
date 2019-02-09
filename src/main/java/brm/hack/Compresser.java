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
package brm.hack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Compresser  {
	
	static final int 
		POS_BITS = 10,
		THRESHOLD = 2,
		MAX_STORE_LENGTH = (1 << (16 - POS_BITS))-1 + THRESHOLD;;
	static final short 
		RING_SIZE = 1<<POS_BITS,	//N
		RING_WRAP = RING_SIZE - 1,
		NOT_USED = RING_SIZE;
    
    public Compresser() {
        ringBuffer = new byte[RING_SIZE + MAX_STORE_LENGTH - 1];
        dad = new short[RING_SIZE + 1];
        leftSon = new short[RING_SIZE + 1];
        rightSon = new short[RING_SIZE + 257];
    }

    public byte[] compress(InputStream in) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        short i; // an iterator
//        short r=RING_SIZE - MAX_STORE_LENGTH; // node number in the binary tree
//        short s = 0;
        short r=1;
        short s=(short) (MAX_STORE_LENGTH+r); // position in the ring buffer
        short len; // length of initial string
        short lastMatchLength; // length of last match
        short codeBufPos=1; // position in the output buffer, always >=1
        byte[] codeBuff = new byte[17]; // one byte flags + 8*pos_len
        byte mask=1; // bit mask for byte 0 of out input
        byte c; // character read from string
        
        initTree();

        int readResult = in.read(ringBuffer, r, MAX_STORE_LENGTH);
        if (readResult <= 0) {
            return out.toByteArray();
        }
        len = (short) readResult;

        insertNode(r);	// Finally, insert the whole string just read. The member variables matchLength and matchPosition are set.
        do {
            if (matchLength > len) {
                matchLength = len;
            }
            if (matchLength < THRESHOLD) {
                matchLength = 1;
                codeBuff[0] |= mask;
                codeBuff[codeBufPos++] = ringBuffer[r];
            } else {
            	codeBuff[codeBufPos++] = (byte)((matchPosition));	//二元组pos=0时,即代表解压结束,已到了压缩文件的末尾,通常末尾为0000,所以matchPosition不可能为0
            	codeBuff[codeBufPos++] = (byte)((matchLength-THRESHOLD)<<2|matchPosition>>>8);
            }
            mask <<= 1;
            if (mask == 0) {
                out.write(codeBuff, 0, codeBufPos);
                codeBuff[0] = 0;
                codeBufPos = 1;
                mask = 1;
            }

            lastMatchLength = matchLength;

            // Delete old strings and read new bytes...
            for (i = 0; i < lastMatchLength; i++) {
                readResult = in.read();
                if (readResult == -1) break;
                c = (byte) readResult;
                
                deleteNode(s);
                
                ringBuffer[s] = c;
                if (s < MAX_STORE_LENGTH - 1) {
                    ringBuffer[s + RING_SIZE] = c;
                }
                s = (short) ((s + 1) & RING_WRAP);
                r = (short) ((r + 1) & RING_WRAP);
                
                insertNode(r);
            }

            while (i++ < lastMatchLength) {
                deleteNode(s);
                s = (short) ((s + 1) & RING_WRAP);
                r = (short) ((r + 1) & RING_WRAP);
                if (--len != 0) {
                    insertNode(r); /* buffer may not be empty. */
                }
            }
        } while (len > 0);

        // There could still be something in the output buffer. Send it now.
        if (codeBufPos > 1) {
            out.write(codeBuff, 0, codeBufPos);
//        } else {	//if(codeBufPos==1)	代表上一个flags已用足8个,需要一个新flag
//        	out.write(0);
        }
//        out.write(new byte[]{0,0});	//末尾补0,通知停止解压
        return out.toByteArray();
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
    public void insertNode(short pos) {
        int cmp = 1;
        short key = pos;
        // The last 256 entries in rightSon contain the root nodes for
        // strings that begin with a letter. Get an index for the
        // first letter in this string.
        short p = (short) (RING_SIZE + 1 + (ringBuffer[key] & 0xFF));
        if(pos<0||pos>=RING_SIZE||p<=RING_SIZE) throw new RuntimeException();
        	

        // Set the left and right tree nodes for this position to "not used."
        leftSon[pos] = NOT_USED;
        rightSon[pos] = NOT_USED;

        // Haven't matched anything yet.
        matchLength = 0;

        while (true) {
            if (cmp >= 0) {
                if (rightSon[p] != NOT_USED) {
                    p = rightSon[p];
                } else {
                    rightSon[p] = pos;
                    dad[pos] = p;
                    return;
                }
            } else {
                if (leftSon[p] != NOT_USED) {
                    p = leftSon[p];
                } else {
                    leftSon[p] = pos;
                    dad[pos] = p;
                    return;
                }
            }
            
            if(p==0) break;

            // Should we go to the right or the left to look for the next match?
            short i = 0;
            for (i = 1; i < MAX_STORE_LENGTH; i++) {
                cmp = (ringBuffer[key + i] & 0xFF) - (ringBuffer[p + i] & 0xFF);
                if (cmp != 0) {
                    break;
                }
            }
            if (i >  matchLength) {
                matchPosition = p;
                matchLength = i;
                if (i >= MAX_STORE_LENGTH) {
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
        dad[p] = NOT_USED;
    }

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
    private short matchPosition;
    private short matchLength;

    private short[] dad;
    private short[] leftSon;
    private short[] rightSon;

}
