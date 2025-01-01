package midend.value.instructions;

import midend.type.ArrayType;
import midend.type.Type;
import midend.value.User;
import midend.value.Value;

import java.util.ArrayList;

public class Instruction extends User {
    public Instruction(String name, Value parent, Type type) {
        super(name, parent, type);//返回值，parent是BasicBlock,返回类型
    }

    @Override
    public void setValues(Value... values) {
        super.setValues(values);//指令用到的values
    }

    @Override
    public ArrayList<Value> getValues() {
        return super.getValues();
    }

    @Override
    public void setName(String count) {
        super.setName("%" + count);
    }
}
