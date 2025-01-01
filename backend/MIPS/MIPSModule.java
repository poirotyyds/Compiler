package backend.MIPS;

import java.util.ArrayList;

public class MIPSModule {
    private ArrayList<MipsGlobalVar> mipsGlobalVars = new ArrayList<>();

    public void addGlobalVar(MipsGlobalVar mipsGlobalVar) {
        this.mipsGlobalVars.add(mipsGlobalVar);
    }

    public String toString() {
        String s = "";
        s += ".data:\n";
        for (MipsGlobalVar mipsGlobalVar : mipsGlobalVars) {
            s += mipsGlobalVar.toString();
        }
        return s;
    }
}
