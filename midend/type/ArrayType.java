package midend.type;

public class ArrayType extends Type {
    private Type elementType;

    private int capacity = 0;

    public ArrayType(Type elementType, int capacity) {
        this.capacity = capacity;
        this.elementType = elementType;
    }

    /*用于实现函数参数中的数组变量，int a[]无容量*/
    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    public Type getElementType() {
        return elementType;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getSize() {
        return capacity * elementType.getSize();
    }

    @Override
    public String toString() {
        return "[" + this.capacity + " x " + this.elementType.toString() + "]";
    }
}
