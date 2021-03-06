package client;

import java.io.*;
import java.util.*;
import java.nio.*;

public class Frame {
    public static final int META_DATA_SIZE = 4;
    private static final int CHECKSUM_WIDTH = 2;
    private static final int CRC_CCITT = /*(1 << 16) | */(1 << 12) | (1 << 5) | 1;
    public static final byte FLAG = 0b01111110;
    public static enum Type { I, C, A, R, F, P };

    private static byte[] dummyData = { 0 };
    
    private Type type;
    private int num;
    private byte[] data;

    public Frame (Type type, int num, byte[] data) {
        this.type = type;
        this.num = num;
	this.data = data;
    }

    public Frame (byte[] data) throws Exception {
        switch (data[0]) {
        case 0: type = Type.I; break;
        case 1: type = Type.C; break;
        case 2: type = Type.A; break;
        case 3: type = Type.R; break;
        case 4: type = Type.F; break;
        case 5: type = Type.P; break;
        default: throw new Exception("invalid frame type");
        }
        num = type == Type.I ? data[1] - '0' : data[1];
        if (type == Type.I) {
            int crc;
            if ((crc = calculateChecksum(data)) != 0)
                ;// throw new Exception("corrupted data: invalid checksum (" + Integer.toBinaryString(crc) + ")");
            
            this.data = new byte[data.length - META_DATA_SIZE];
            for (int i = 0; i < this.data.length; i++)
                this.data[i] = data[i+2];
        }
    }

    public Type getType () {
        return type;
    }

    public int getNum () {
        return num;
    }
    
    public byte[] getData () {
        return data;
    }

    public void send (OutputStream out) throws IOException {
	byte[] rawData = new byte[META_DATA_SIZE + (data == null ? 0 : data.length)];

        switch (type) {
        case I: rawData[0] = 0; break;
        case C: rawData[0] = 1; break;
        case A: rawData[0] = 2; break;
        case R: rawData[0] = 3; break;
        case F: rawData[0] = 4; break;
        case P: rawData[0] = 5; break;
        default: rawData[0] = -1; break; // impossible case
        }
	rawData[1] = (byte) (type == Type.I ? num + '0' : num);
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                rawData[i + 2] = data[i];
            }
        }
	rawData[rawData.length - 2] = 0;
	rawData[rawData.length - 1] = 0;
        
	int crc = calculateChecksum(rawData);
        rawData[rawData.length - 2] = (byte) ((crc & 0xff00) >> 8);
        rawData[rawData.length - 1] = (byte) (crc & 0xff);

        out.write(bitStuff(rawData));
    }

    public static int calculateChecksum (byte[] data) {
        int[] bytes = new int[data.length];
        for (int i = 0; i < data.length; i++)
            bytes[i] = data[i];
        int crc = 0xffff;

        for (int b : bytes) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                int top = (crc >> 15) & 1;
                crc <<= 1;
                if ((bit ^ top) == 1)
                    crc ^= CRC_CCITT;
            }
        }

        return crc & 0xffff;
    }
    
    public static byte[] bitStuff (byte[] data) {
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
        if (nBits != 0)
            bytes.add((byte) (currentByte << (8 - nBits)));

        byte[] res = new byte[bytes.size() + 2];
        res[0] = FLAG;
        for (int i = 0; i < bytes.size(); i++)
            res[i + 1] = bytes.get(i);
        res[res.length - 1] = FLAG;
        return res;
    }
}
