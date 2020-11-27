package common;

public class Utils {
    public static String leftPad (String in, int size) {
	String out = in;
	for (int i = size - in.length(); i > 0; i--) {
	    out = "0" + out;
	}
	return out;
    }
}
