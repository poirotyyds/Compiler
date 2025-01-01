package midend.value.instructions;

import midend.type.PointerType;
import midend.type.Type;
import midend.value.BasicBlock;
import midend.value.Value;

public class Load extends Instruction {
    /*name用于其他user调用load值*/
    public Load(String name, BasicBlock parent, Type type, Value location) {
        super(name, parent, type);
        setValues(location);
    }

    @Override
    public String toString() {
        return getName() + " = load " + getType().toString() + ", " + getValues().get(0).getType() + " " + getValues().get(0).getName();
    }
}
