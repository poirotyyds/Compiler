package midend.value.instructions;

import midend.type.Type;
import midend.value.BasicBlock;
import midend.value.Value;

public class Trunc extends Instruction {
    public Trunc(String name, BasicBlock parent, Type truncToType, Value oldValue) {
        super(name, parent, truncToType);
        setValues(oldValue);
    }

    @Override
    public String toString() {
        //%8 = trunc i32 %6 to i8
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" = trunc ");
        sb.append(getValues().get(0).getType());
        sb.append(" ");
        sb.append(getValues().get(0).getName());
        sb.append(" to ");
        sb.append(getType());
        return sb.toString();
    }
}
