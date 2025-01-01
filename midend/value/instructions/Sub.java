package midend.value.instructions;

import midend.type.IntegerType;
import midend.value.BasicBlock;
import midend.value.Value;

public class Sub extends Instruction{
    public Sub(String name, BasicBlock parent, Value leftVal, Value rightVal) {
        super(name, parent, new IntegerType(32));
        super.setValues(leftVal, rightVal);
    }

    @Override
    public String toString() {
        return this.getName() + " = sub " + this.getType().toString() + " " +
                this.getValues().get(0).getName() + ", "  + this.getValues().get(1).getName();
    }
}
