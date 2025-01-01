package midend.value.instructions;

import midend.type.FunctionType;
import midend.type.Type;
import midend.type.VoidType;
import midend.value.Argument;
import midend.value.BasicBlock;
import midend.value.Function;
import midend.value.Value;

import java.util.ArrayList;

public class Call extends Instruction {
    private String callFuncName;
    public Call(String name, BasicBlock parent, String funcName,Type returnType, ArrayList<Value> realArguments) {
        super(name, parent, returnType);
        callFuncName = funcName;
        //values是实参
        for (Value realArgument : realArguments) {
            setValues(realArgument);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.getType() instanceof VoidType) {
            sb.append("call ");
        } else {
            sb.append(this.getName());
            sb.append(" = call ");
        }
        sb.append(this.getType());
        sb.append(" ");
        sb.append(this.callFuncName);
        sb.append("(");
        for (int i = 0; i < this.getValues().size(); i++) {
            sb.append(this.getValues().get(i).getType());
            sb.append(" ");
            sb.append(this.getValues().get(i).getName());
            sb.append(", ");
        }
        if (!this.getValues().isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }
}
