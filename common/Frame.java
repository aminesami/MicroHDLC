package common;
import java.util.*;
import java.nio.*;

public class Frame {
    public static final int META_DATA_SIZE = 6;
    private static final int CHECKSUM_WIDTH = 2;
    private static final int CRC_CCITT = (1 << 16) | (1 << 12) | (1 << 5) | 1;
    public static final byte FLAG = 0b01111110;
    public static enum Type { I, C, A, R, F, P };

    private byte[] data;

    public Frame (Type type, char num, byte[] data) {
	byte[] rawData = new byte[META_DATA_SIZE + data.length - 2]; // without flags

	rawData[0] = (byte) type.ordinal();
	rawData[1] = (byte) num;
	for (int i = 0; i < data.length; i++) {
	    rawData[i + 2] = data[i];
	}
        
	rawData[rawData.length - 2] = 0;
	rawData[rawData.length - 1] = 0;
        
	calculateChecksum(rawData);
	this.data = bitStuff(rawData);
    }

    private void calculateChecksum (byte[] data) {
        int[] bytes = new int[data.length];
        for (int i = 0; i < data.length; i++)
            bytes[i] = data[i];
        int crc = 0;

        for (int b : bytes) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                int top = (crc >> 15) & 1;
                crc <<= 1;
                if ((bit ^ top) == 1)
                    crc ^= CRC_CCITT;
            }
        }

        data[data.length - 2] = (byte) ((crc & 0xff00) >> 8);
        data[data.length - 1] = (byte) (crc & 0xff);
    }
    
    private byte[] bitStuff (byte[] data) {
        int successiveOnes = 0;
        int nBits = 0;
        int currentByte = 0;

        ArrayList<Byte> bytes = new ArrayList<Byte>();
        
        for (byte b : data) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                if (bit == 1)
                    successiveOnes++;
                else
                    successiveOnes = 0;
                currentByte = (currentByte << 1) | bit;
                nBits++;
                if (nBits == 8) {
                    bytes.add((byte) currentByte);
                    nBits = currentByte = 0;
                }

                if (successiveOnes == 5) {
                    currentByte <<= 1;
                    nBits++;
                    successiveOnes = 0;
                    if (nBits == 8) {
                        bytes.add((byte) currentByte);
                        nBits = currentByte = 0;
                    }
                }
            }
        }

        byte[] res = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
            res[i] = bytes.get(i);
        return res;
    }

    public byte[] getData () {
        return data;
    }
}
