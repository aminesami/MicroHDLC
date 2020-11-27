public class ByteReader {
    private InputStream in;
    private int successiveOnes = 0;
    
    public ByteReader (InputStream in) {
	this.in = in;
    }

    private byte read () throws Exception {
	int val = in.read();
	if (val < 0 && val > 255)
	    throw Exception();

	String cur = Utils.leftPad(Integer.toBinaryString(val), 8);
	byte res = 0;
	for (int j = 0; j < cur.length(); j++) {
	    char c = cur.charAt(j);
	    if (successiveOnes == 5) {
		if (cur.charAt(j+1) == '0') {
		    j++;
		    continue;
		} else if (cur.charAt(j+2) == '1') {
		    System.err.println("Abort Condition...");
		    throw Exception();
		} else {
		    return Frame.FLAG;
		}
	    }
	    if (c == '0')
		successiveOnes = 0;
	    else
		successiveOnes++;

	    res = res << 1 | (c == '0' ? 0 : 1);
	}

	return res;
    }

}
