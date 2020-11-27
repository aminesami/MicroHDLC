package client;
import java.nio.*;

public class Frame {
    private static final int META_DATA_SIZE = 6;
    private static final int CHECKSUM_WIDTH = 2;
    private static final int CRC_CCITT = (1 << 16) | (1 << 12) | (1 << 5) | 1;
    public static final byte FLAG = 0b01111110;
    public static enum Type { I, C, A, R, F, P };
    
    private Type type;
    private byte num;
    private byte[] data;
    private short checksum;

    public Frame (Type type, char num, byte[] data) {
	byte[] rawData = new byte[META_DATA_SIZE + data.length - 2]; // without flags

	rawData[0] = (byte) type.ordinal();
	rawData[1] = (byte) num;
	for (int i = 0; i < data.length; i++) {
	    rawData[i + 2] = data[i];
	}
	calculateChecksum(data);
	
    }

    private void calculateChecksum (byte[] data) {
	for (int i = 0; i < (data.length - CHECKSUM_WIDTH); i += 2) {
	    // suppose it works
	}
    }

    private static String leftPad (String in, int size) {
	String out = in;
	for (int i = size - in.length(); i > 0; i--) {
	    out = "0" + out;
	}
	return out;
    }

    private byte[] bitStuff (byte[] data) {
	StringBuilder s = new StringBuilder();
	int successiveOnes = 0;
	for (int i = 0; i < data.length; i++) {
	    String cur = leftPad(Integer.toBinaryString(data[i]), 8);
	    for (char c : cur.toCharArray()) {
		if (successiveOnes == 5) {
		    s.append('0');
		    successiveOnes = 0;
		}
		
		if (c == '0')
		    successiveOnes = 0;
		else
		    successiveOnes++;

		s.append(c);
	    }
	}
	// XXX : could be better
	ByteBuffer r = ByteBuffer.allocate((int) Math.ceil((float)s.length() / 8.f));
	try {
	    for (int i = 0; i < s.length(); i += 8) {
		r.put(Byte.parseByte(s.substring(i, i + 8)));
	    }
	} catch (Exception e) { }

	return r.array();
    }

    private byte[] bitUnstuff(byte[] data) {
	StringBuilder s = new StringBuilder();
	int successiveOnes = 0;
	for (int i = 0; i < data.length; i++) {
	    String cur = leftPad(Integer.toBinaryString(data[i]), 8);
	    for (int j = 0; j < cur.length(); j++) {
		char c = cur.charAt(j);
		if (successiveOnes == 5) {
		    j++;
		    successiveOnes = 0;
		}
		
		if (c == '0')
		    successiveOnes = 0;
		else
		    successiveOnes++;

		s.append(c);
	    }
	}
    }
}
