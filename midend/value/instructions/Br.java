package midend.value.instructions;

import midend.type.VoidType;
import midend.value.BasicBlock;
import midend.value.Value;

public class Br extends Instruction {
    public Br(BasicBlock parent, Value cond, BasicBlock trueToBasicBlock, BasicBlock falseToBasicBlock) {
        super("", parent, new VoidType());
        setValues(cond, trueToBasicBlock, falseToBasicBlock);
    }

    public Br(BasicBlock parent, BasicBlock jumpToBsicBlock) {
        super("", parent, new VoidType());
        setValues(jumpToBsicBlock);
    }

    @Override
    public String toString() {
        if (getValues().size() == 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("br ");
            stringBuilder.append(getValues().get(0).getType());
            stringBuilder.append(" %");
            stringBuilder.append(getValues().get(0).getName());
            return stringBuilder.toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("br ");
            stringBuilder.append(getValues().get(0).getType());
            stringBuilder.append(" ");
            stringBuilder.append(getValues().get(0).getName());
            stringBuilder.append(", ");
            stringBuilder.append(getValues().get(1).getType());
            stringBuilder.append(" %");
            stringBuilder.append(getValues().get(1).getName());
            stringBuilder.append(", ");
            stringBuilder.append(getValues().get(2).getType());
            stringBuilder.append(" %");
            stringBuilder.append(getValues().get(2).getName());
            return stringBuilder.toString();
        }
    }
}
