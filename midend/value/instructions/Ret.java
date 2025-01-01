package midend.value.instructions;

import midend.type.VoidType;
import midend.value.BasicBlock;
import midend.value.Value;

public class Ret extends Instruction {
    private boolean existReturnVal;

    public Ret(BasicBlock parent) {
        super("", parent, new VoidType());
        existReturnVal = false;
    }

    public Ret(BasicBlock parent, Value returnVal) {
        super("", parent, new VoidType());
        setValues(returnVal);
        existReturnVal = true;
    }

    @Override
    public String toString() {
        if (existReturnVal) {
            return "ret " + this.getValues().get(0).getType() + " " + this.getValues().get(0).getName();
        } else {
            return "ret void";
        }
    }
}
