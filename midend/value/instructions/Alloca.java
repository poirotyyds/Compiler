package midend.value.instructions;

import midend.type.PointerType;
import midend.type.Type;
import midend.value.BasicBlock;

public class Alloca extends Instruction {
    public Alloca(String name, Type allocatedType, BasicBlock parent) {
        super(name, parent,  new PointerType(allocatedType));
    }

    @Override
    public String toString() {
        return this.getName() + " = alloca " + ((PointerType) this.getType()).getPointedType();
    }
}
