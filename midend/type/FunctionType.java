package midend.type;

import java.util.ArrayList;
import java.util.LinkedList;

public class FunctionType extends Type{
    private Type returnType;
    private ArrayList<Type> argumentTypes;

    public FunctionType(Type returnType, ArrayList<Type> argumentTypes) {
        this.returnType = returnType;
        this.argumentTypes = argumentTypes;
    }

    public ArrayList<Type> getArgumentTypes() {
        return argumentTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
