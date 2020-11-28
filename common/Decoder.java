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
}
