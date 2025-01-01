package midend.value;

import midend.type.LabelType;
import midend.type.Type;
import midend.type.VoidType;
import midend.value.instructions.Instruction;

import java.util.LinkedList;

public class BasicBlock extends Value {
    private LinkedList<Instruction> instructions = new LinkedList<>();
    private Function parentFunction;
    private int countForName = 0;

    public BasicBlock(String name, Function parent) {
        super(name, parent, new LabelType());
    }

    public void addInstruction(Instruction instruction) {
        this.instructions.add(instruction);
    }

    public LinkedList<Instruction> getInstructions() {
        return instructions;
    }

    public void setName(int count) {
        super.setName(""+count);
        this.countForName = count + 1;
    }

    public int getCount() {
        return countForName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(":\n");
        for (Instruction instruction : instructions) {
            sb.append("\t");
            /*if (!(instruction.getType() instanceof VoidType)) {
                instruction.setName(""+countForName);
                countForName++;
            }*/
            sb.append(instruction.toString());
            sb.append("\n");
        }
        if (!instructions.isEmpty()) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }
}
