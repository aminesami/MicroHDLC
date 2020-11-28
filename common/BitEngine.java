package common;

// this class might be useless now

public class BitEngine {
    public static int getBits (byte[] data, int from, int to) {
        int res = 0;
        for (int i = from; i < to; i++)
            res = (res << 1) | getBit(data, i);
        return res;
    }
    
    public static int getBit(byte[] data, int i) {
	int byteIndex = i / 8;
	int byteOffset = 7 - (i % 8);
	return data[byteIndex] & (1 << byteOffset);
    }
}
