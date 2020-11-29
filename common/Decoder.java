package common;

import java.io.*;
import java.util.*;

import client.Frame;

public class Decoder {
    public static byte[] getBitStuffedFrame (InputStream in) throws IOException {
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        
        int b;
        while ((b = in.read()) != Frame.FLAG)
            ; // read until start of frame

        while ((b = in.read()) != Frame.FLAG)
            bytes.add((byte) b);

        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            res[i] = bytes.get(i);
        return res;
    }

    public static byte[] bitUnstuff (byte[] data) throws Exception {
        int successiveOnes = 0;
        int nBits = 0;
        int currentByte = 0;
        
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        
        for (int j = 0; j < data.length; j++) {
            byte b = data[j];
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                if (bit == 1)
                    successiveOnes++;
                else
                    successiveOnes = 0;
                if (successiveOnes == 5) {
                    if (i == 7) { // end of byte special case
                        if (j + 1 == data.length)
                            throw new Exception("corrupted data");

                        b = data[++j];
                        i = -1;
                    }
                    int nextBit = (b >> (7 - (++i))) & 1;
                    if (nextBit != 0)
                        throw new Exception("corrupted data");
                    // reaching here means we skipped a zero following five 1's
                    successiveOnes = 0;
                }
                currentByte = (currentByte << 1) | bit;
                nBits++;
                if (nBits == 8) {
                    bytes.add((byte) currentByte);
                    nBits = currentByte = 0;
                }
            }
        }

        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            res[i] = bytes.get(i);

        return res;
    }

    public static Frame getFrame (InputStream in) throws Exception {
        byte[] stuffedData = getBitStuffedFrame(in);
        return new Frame(bitUnstuff(stuffedData));
    }
}
