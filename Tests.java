import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import client.*;
import common.*;

public class Tests {
    public static void main (String[] args) {
        System.out.println(testResult("bit stuff", bitStuffTest()));
        System.out.println(testResult("checksum", checksumTest()));
        System.out.println(testResult("bit unstuff", bitUnstuffTest()));
        System.out.println(testResult("frame split", frameSplitTest()));
        System.out.println(testResult("double checksum", doubleChecksumTest()));
        System.out.println(testResult("gulliver frame split", gulliverFrameSplitTest()));
        System.out.println(testResult("gulliver stuffing", gulliverStuffingTest()));
    }

    public static String testResult (String name, boolean result) {
        return "test " + (result ? "succeeded" : "failed   ") + " (" + name + ")";
    }
            
    public static boolean bitStuffTest () {
        byte[] input = { 0b01111110, 0b01111110, 0b01111110, 0b01111110 };
        byte[] output = {
            Frame.FLAG,
            0b01111101, 0b00111110, (byte) 0b10011111, 0b01001111, (byte) 0b10100000,
            Frame.FLAG
        };
        return Arrays.equals(output, Frame.bitStuff(input));
    }

    public static boolean bitUnstuffTest () {
        byte[] input = {
            0b01111101, 0b00111110, (byte) 0b10011111, 0b01001111, (byte) 0b10100000,
        };
        byte[] output = { 0b01111110, 0b01111110, 0b01111110, 0b01111110 };
        try {
            byte[] res = Decoder.bitUnstuff(input);
            return Arrays.equals(output, res);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checksumTest () {
        byte[] data = "123456789".getBytes();
        int v = Frame.calculateChecksum(data);
        // System.out.println(Integer.toHexString(v));
        return v == 0x29b1;
    }

    public static boolean doubleChecksumTest () {
        byte[] data = "123456789".getBytes();
        int crc = Frame.calculateChecksum(data);
        byte[] in = new byte[data.length + 2];
        for (int i = 0; i < data.length; i++)
            in[i] = data[i];
        in[data.length] = (byte) ((crc >> 8) & 0xff);
        in[data.length + 1] = (byte) (crc & 0xff);
        return Frame.calculateChecksum(in) == 0;
    }

    public static boolean frameSplitTest () {
        byte[] testInput = new byte[2 * FrameBuilder.MAX_FRAME_SIZE];
        Arrays.fill(testInput, (byte) '!');
        Frame[] frames = FrameBuilder.splitFrames(testInput);
        return frames.length == 5;
    }
    
    public static boolean gulliverFrameSplitTest () {
        try {
            byte[] data = Files.readAllBytes(Paths.get("tests/gulliver.txt"));
            Frame[] frames = FrameBuilder.splitFrames(data);
            return frames.length == 7;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean gulliverStuffingTest () {
        try {
            byte[] data = Files.readAllBytes(Paths.get("tests/gulliver.txt"));
            Frame[] frames = FrameBuilder.splitFrames(data);
                
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (Frame f : frames)
                f.send(out);

            InputStream in = new ByteArrayInputStream(out.toByteArray());
            byte[] output;
            Frame f;
            int n = 0;

            ArrayList<Frame> fs = new ArrayList<Frame>();
            do {
                output = Decoder.bitUnstuff(Decoder.getBitStuffedFrame(in));
                f = new Frame(output);

                if (f.getType() == Frame.Type.I) {
                    fs.add(f);
                    n += f.getData().length;
                }
            } while (f.getType() != Frame.Type.F);

            output = new byte[n];                    

            n = 0;
            for (Frame frm : fs) {
                byte[] bs = frm.getData();
                for (int i = 0; i < bs.length; i++) {
                    output[n++] = bs[i];
                }
            }
            // System.out.write(output, 0, 530);
            // System.out.println(data.length + " / " + output.length);
            return Arrays.equals(data, output);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
