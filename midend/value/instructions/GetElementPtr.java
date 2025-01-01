package midend.value.instructions;

import midend.type.ArrayType;
import midend.type.PointerType;
import midend.type.Type;
import midend.value.BasicBlock;
import midend.value.Value;

import java.util.ArrayList;

public class GetElementPtr extends Instruction{

    //values为基址、指针寻址下标、数组寻址下标
    /*%2 = getelementptr inbounds [4 x i8], [4 x i8]* @.str, i32 0, i32 0
call void @putstr(i8* %2)
*/
    public GetElementPtr(String name, BasicBlock parent, PointerType locationType, Value baseAdr, Value pointIndex, Value arrayIndex) {
        super("%" + name, parent, locationType);
        super.setValues(baseAdr, pointIndex, arrayIndex);
    }

    public GetElementPtr(String name, BasicBlock parent, Type type, Value baseAdr, Value pointIndex) {
        super("%" + name, parent, type);
        super.setValues(baseAdr, pointIndex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(" = getelementptr inbounds ");
        sb.append(((PointerType)getValues().get(0).getType()).getPointedType());
        sb.append(", ");
        for (int i = 0; i < this.getValues().size(); i++) {
            sb.append(getValues().get(i).getType());
            sb.append(" ");
            sb.append(getValues().get(i).getName());
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    @Override
    public void setValues(Value... values) {
        super.setValues();
    }

    @Override
    public ArrayList<Value> getValues() {
        return super.getValues();
    }
}
