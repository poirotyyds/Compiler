package midend.value;

import midend.type.IntegerType;

public class ConstantInt extends Constant {
    private int val;  // 保存整数的值

    public ConstantInt(int bits, int val) {
        super(Integer.toString(val), null, new IntegerType(bits));
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
