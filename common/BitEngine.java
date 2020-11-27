package common;

public class BitEngine {
    public static int getBits (byte[] data, int from, int to) {
	int startByte = from / 8;
	int startOffset = from % 8;
	int endByte = to / 8 - 1;
	int endOffset = to % 8;

	int res = (data[startByte] >> startOffset);

	for (int b = startByte + 1; b < endByte; b++) {
	    res = (res << 8) | data[b];
	}

	if (startByte < endByte || endOffset > 0) {
	    return (res << endOffset) | (data[endByte] >> (8 - endOffset));
	} else {
	    return res;
	}
    }

    public static boolean getBit(byte[] data, int i) {
	int byteIndex = i / 8;
	int byteOffset = 7 - (i % 8);
	return (data[byteIndex] & (1 << byteOffset)) == 1;
    }
}
