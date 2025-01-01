package midend.type;

public class IntegerType extends Type {
    private int bitSize;
    private int size;

    public IntegerType(int bitSize) {
        this.bitSize = bitSize;
        size = bitSize / 8;
    }

    public int getBitSize() {
        return bitSize;
    }

    @Override
    public String toString() {
        return "i" + bitSize;
    }

    public int getSize() {
        return size;
    }
}
