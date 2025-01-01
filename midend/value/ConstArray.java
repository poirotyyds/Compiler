package midend.value;

import midend.type.ArrayType;

import java.util.ArrayList;

public class ConstArray extends User {
    private boolean existZeroInitializer = false;

    //无初值则初始化为0
    public ConstArray(ArrayType arrayType) {
        super(null, null, arrayType);
        setValues(new ZeroInitializer(arrayType));
        this.existZeroInitializer = true;
    }

    public ConstArray(ArrayType arrayType, ArrayList<Value> values)  {
        super(null, null,arrayType);
        for (Value value : values) {
            setValues(value);
        }
        if (values.size() == 0) {
            this.existZeroInitializer = true;
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (existZeroInitializer) {
            sb.append("zeroinitializer");
        } else {
            sb.append("[");
            for (int i = 0; i < getValues().size(); i++) {
                sb.append(((ArrayType) this.getType()).getElementType());//this.getValues().get(i).getType());
                sb.append(" ");
                sb.append(this.getValues().get(i));
                sb.append(", ");
            }
            for (int i = getValues().size(); i < ((ArrayType) this.getType()).getCapacity(); i++) {
                sb.append(((ArrayType) this.getType()).getElementType());
                sb.append(" ");
                sb.append("0");
                sb.append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public ArrayList<Value> getValues() {
        return super.getValues();
    }
}
