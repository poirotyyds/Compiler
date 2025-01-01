package midend.value;

import midend.type.ArrayType;

public class ZeroInitializer extends Value {
    public ZeroInitializer(ArrayType arrayType) {
        super("zeroinitializer", null, arrayType);
    }

    public String toString() {
        return this.getName();
    }
}
