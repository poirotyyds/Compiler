package backend.MIPS;

import midend.type.ArrayType;
import midend.type.IntegerType;
import midend.type.PointerType;
import midend.value.*;

public class MipsGlobalVar {
    private GlobalVar llvmirGlobalVar;

    public MipsGlobalVar(GlobalVar llvmirGlobalVar) {
        this.llvmirGlobalVar = llvmirGlobalVar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(llvmirGlobalVar.getName().substring(1));//去除@

        sb.append(": ");

        if (((PointerType) llvmirGlobalVar.getType()).getPointedType() instanceof IntegerType) {
            if (((PointerType) llvmirGlobalVar.getType()).getPointedType().toString().equals("i32")) {
                sb.append(".word ");
            } else if (((PointerType) llvmirGlobalVar.getType()).getPointedType().toString().equals("i8")) {
                sb.append(".byte ");
            }
            sb.append(((ConstantInt) llvmirGlobalVar.getVal()).getVal());
            sb.append("\n");
        } else {//全局数组
            if (llvmirGlobalVar.getName().charAt(1) == '.') {
                sb.append(".asciiz ");
                sb.append("\"" + ((ConstantString)llvmirGlobalVar.getVal()).getString() + "\"");
                sb.append("\n");
            } else if (!(llvmirGlobalVar.getVal() instanceof ZeroInitializer)) {
                if (((ArrayType)((PointerType) llvmirGlobalVar.getType()).getPointedType()).getElementType().toString().equals("i32")) {
                    sb.append(".word ");
                } else if (((ArrayType)((PointerType) llvmirGlobalVar.getType()).getPointedType()).getElementType().toString().equals("i8")) {
                    sb.append(".byte ");
                }
                sb.append(((ConstantInt)(((ConstArray) llvmirGlobalVar.getVal()).getValues().get(0))).getVal());
                for (int i = 1; i < ((ConstArray) llvmirGlobalVar.getVal()).getValues().size(); i++) {
                    sb.append(", ");
                    sb.append(((ConstantInt)(((ConstArray) llvmirGlobalVar.getVal()).getValues().get(i))).getVal());
                }
                sb.append("\n");
            } else {
                if (((ArrayType)((PointerType) llvmirGlobalVar.getType()).getPointedType()).getElementType().toString().equals("i32")) {
                    sb.append(".word ");
                } else if (((ArrayType)((PointerType) llvmirGlobalVar.getType()).getPointedType()).getElementType().toString().equals("i8")) {
                    sb.append(".byte ");
                }
                sb.append("0:");
                sb.append(((ArrayType)((ZeroInitializer)llvmirGlobalVar.getVal()).getType()).getCapacity());
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
