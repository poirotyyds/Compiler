package midend.type;

public class PointerType extends Type {
    private Type pointedType;
    private int size = 4;
    public PointerType(Type pointedType) {
        this.pointedType = pointedType;
    }

    public Type getPointedType() {
        return pointedType;
    }

    @Override
    public String toString() {
        return pointedType.toString() + "*";
    }

    public int getSize() {
        return size;
    }
}
