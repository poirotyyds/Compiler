package midend.value;

import midend.type.FunctionType;
import midend.type.Type;
import midend.type.VoidType;
import midend.value.instructions.Instruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class Function extends Value {
    private FunctionType functionType;
    private LinkedList<BasicBlock> basicBlocks = new LinkedList<>();
    private LinkedList<Argument> arguments = new LinkedList<>();
    private boolean isLibFunc = false;
    private int countForName = 0;

    public Function(FunctionType functionType,  String name) {
        super(name, null, functionType);
        this.functionType = functionType;
        for (int i = 0; i < functionType.getArgumentTypes().size(); i++) {
            arguments.add(new Argument("%" + i, this, functionType.getArgumentTypes().get(i)));
        }
        this.countForName = functionType.getArgumentTypes().size();
    }

    public Function(FunctionType functionType,  String name, boolean isLibFunc) {
        super(name, null, functionType);
        this.functionType = functionType;
        this.isLibFunc = isLibFunc;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public LinkedList<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(LinkedList<Argument> arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.setName(countForName);
            countForName++;
            String a = basicBlock.toString();
            for (Instruction instruction : basicBlock.getInstructions()) {
                if (!(instruction.getType() instanceof VoidType)) {
                    instruction.setName(""+countForName);
                    countForName++;
                }
            }
            //countForName = basicBlock.getCount();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(isLibFunc ? "declare" : "\ndefine");
        sb.append(" dso_local ");
        sb.append(((FunctionType) this.getType()).getReturnType().toString());
        sb.append(" ");
        sb.append(this.getName());
        sb.append("(");
        for (Argument argument : this.arguments) {
            sb.append(argument.getType());
            if (!isLibFunc) {
                sb.append(" ");
                sb.append(argument.getName());
                sb.append(", ");
            }
        }
        if (!arguments.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        if (isLibFunc && !functionType.getArgumentTypes().isEmpty()) {
            sb.append(functionType.getArgumentTypes().get(0).toString());
        }
        sb.append(")");
        sb.append(" ");
        if (!isLibFunc) {
            sb.append("{");
            sb.append("\n");
            for (BasicBlock basicBlock : basicBlocks) {
                sb.append(basicBlock.toString());
                sb.append("\n");
            }
            sb.append("}");
        }
        return sb.toString();
        /*StringBuilder sb = new StringBuilder();
        sb.append(isLibFunc ? "declare" : "\ndefine");
        sb.append(" dso_local ");
        sb.append(((FunctionType) this.getType()).getReturnType().toString());
        sb.append(" ");
        sb.append(this.getName());
        sb.append("(");
        for (Argument argument : this.arguments) {
            sb.append(argument.getType());
            if (!isLibFunc) {
                sb.append(" ");
                sb.append(argument.getName());
                sb.append(", ");
            }
        }
        if (!arguments.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }
        if (isLibFunc && !functionType.getArgumentTypes().isEmpty()) {
            sb.append(functionType.getArgumentTypes().get(0).toString());
        }
        sb.append(")");
        sb.append(" ");
        if (!isLibFunc) {
            sb.append("{");
            sb.append("\n");
            for (BasicBlock basicBlock : basicBlocks) {
                basicBlock.setName(countForName);
                countForName++;
                sb.append(basicBlock.toString());
                sb.append("\n");
                countForName = basicBlock.getCount();
            }
            sb.append("}");
        }
        return sb.toString();*/
    }

    public LinkedList<BasicBlock> getBasicBlocks() {
        return this.basicBlocks;
    }
}


