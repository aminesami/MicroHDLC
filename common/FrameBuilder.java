package common;

import java.util.*;
import static common.Frame.META_DATA_SIZE;

public class FrameBuilder {
    private static final int MAX_FRAME_SIZE = 128 * 1024; // 128 kB
    private static final int EFFECTIVE_MAX_DATA_SIZE = MAX_FRAME_SIZE - META_DATA_SIZE;
    private static final int MAX_NUM_FRAMES = 8; // 3 bits
    
    public static Frame[] splitFrames (byte[] data) {
	int frameNum = 0;
	ArrayList<Frame> frames = new ArrayList<Frame>();
	frames.add(new Frame(Frame.Type.C, (char) 0, null));
	for (int offset = 0; offset < data.length; offset += EFFECTIVE_MAX_DATA_SIZE) {
	    byte[] frameData = Arrays.copyOfRange(data, offset, offset + EFFECTIVE_MAX_DATA_SIZE);
	    // using unsafe char casting but should never exceed '7'
	    frames.add(new Frame(Frame.Type.I, (char) ('0' + frameNum), frameData));
	    frameNum = (frameNum + 1) % MAX_NUM_FRAMES;
	}
	frames.add(new Frame(Frame.Type.F, (char) 0, null));

	return frames.toArray(new Frame[frames.size()]);
    }
}
