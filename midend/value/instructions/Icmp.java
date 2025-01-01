package midend.value.instructions;

import midend.type.IntegerType;
import midend.value.BasicBlock;
import midend.value.Value;

public class Icmp extends Instruction {
    private IcmpType type;

    public Icmp(String name, BasicBlock parent, IcmpType type, Value leftVal, Value rightVal) {
        super(name, parent, new IntegerType(1));
        super.setValues(leftVal, rightVal);
        this.type = type;
    }

    @Override
    public String toString() {
        return this.getName() + " = icmp "
                + this.type.toString() + " "
                + this.getValues().get(0).getType() + " "
                + this.getValues().get(0).getName() + ", "
                + this.getValues().get(1).getName();
    }
}
