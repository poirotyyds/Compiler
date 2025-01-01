package midend.value.instructions;

import midend.type.Type;
import midend.value.BasicBlock;
import midend.value.Value;

public class Zext extends Instruction {
    //Zero Extend
    public Zext(String name, BasicBlock parent, Type zextToType, Value oldValue) {
        super(name, parent, zextToType);
        setValues(oldValue);
    }

    @Override
    public String toString() {
        //%8 = zext i8 %6 to i32
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = zext ");
        sb.append(getValues().get(0).getType());
        sb.append(" ");
        sb.append(getValues().get(0).getName());
        sb.append(" to ");
        sb.append(getType());
        return sb.toString();
    }
}
