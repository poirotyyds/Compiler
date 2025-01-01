package midend.value.instructions;

import midend.type.VoidType;
import midend.value.BasicBlock;
import midend.value.Value;

public class Store extends Instruction {
    /*
    int a = 1 + 2;
    %1 = alloca i32
    %2 = add i32 1, 2
    store i32 %2, i32* %1*/
    //存指令需要求值指令和存值地址(分配得来)
    public Store(BasicBlock parent, Value valToStore, Value location) {
        super("", parent, new VoidType());
        setValues(valToStore, location);
    }

    @Override
    public String toString() {
        return "store " + this.getValues().get(0).getType() + " " + this.getValues().get(0).getName() +
                ", " + this.getValues().get(1).getType() + " " + this.getValues().get(1).getName();
    }
}
