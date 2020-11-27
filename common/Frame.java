package common;
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
	int x = BitEngine.getBits(data, 0, 17) ^ CRC_CCITT;
	for (int i = 17; i < data.length * 8; i++) {
	    // maybe should skip leading zeros as done in the book
	    x = (((x << 1) | (BitEngine.getBit(data, i) ? 1 : 0)) ^ CRC_CCITT) & 0xffff;
	}
	data[data.length - 2] = (byte) ((x >> 16) & 0xff);
	data[data.length - 1] = (byte) (x & 0xff);
    }

    private byte[] bitStuff (byte[] data) {
	StringBuilder s = new StringBuilder();
	int successiveOnes = 0;
	for (int i = 0; i < data.length; i++) {
	    String cur = Utils.leftPad(Integer.toBinaryString(data[i]), 8);
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
	ByteBuffer r = ByteBuffer.allocate(2 + (int) Math.ceil((float)s.length() / 8.f));
	r.put(FLAG);
	try {
	    for (int i = 0; i < s.length(); i += 8) {
		r.put(Byte.parseByte(s.substring(i, i + 8)));
	    }
	} catch (Exception e) { }
	r.put(FLAG);

	return r.array();
    }
}
