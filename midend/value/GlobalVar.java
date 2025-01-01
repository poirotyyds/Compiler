package midend.value;

import midend.type.PointerType;
import midend.type.Type;

public class GlobalVar extends User {
    private boolean isConst;

    public GlobalVar(String name, boolean isConst,Type type, Value initVal) {
        super("@" + name, Module.getInstance(), new PointerType(type));
        super.setValues(initVal);
        this.isConst = isConst;
    }

    public GlobalVar(String name, Type type) {
        super("@" + name, Module.getInstance(), new PointerType(type));
        super.setValues(Constant.initDefaultVal(type));
        isConst = false;
    }

    public Value getVal() {
        return this.getValues().get(0);
    }

    @Override
    public String toString() {
        if (this.getName().charAt(1) == '.') {
            return this.getName()
                    + " = private unnamed_addr "
                    + (isConst ? "constant" : "global") + " "
                    + ((PointerType) this.getType()).getPointedType().toString() + " "
                    + getValues().get(0).toString();
        } else {
            return this.getName()
                    + " = dso_local "
                    + (isConst ? "constant" : "global") + " "
                    + ((PointerType) this.getType()).getPointedType().toString() + " "
                    + getValues().get(0).toString();
        }
    }
}
