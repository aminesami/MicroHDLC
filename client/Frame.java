public class Frame {
    public static final FLAG = 0b01111110;
    public enum Type { I, C, A, R, F, P };
    
    private Type type;
    private byte num;
    private byte[] data;
    private short checksum;

    public Frame (Type type, byte num, byte[] data) {

    }
}
