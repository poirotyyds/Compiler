package midend.value;

import java.util.ArrayList;
import java.util.LinkedList;

public class Module extends Value {
    private static final Module module = new Module();
    private LinkedList<Function> functionList = new LinkedList<>();
    private LinkedList<GlobalVar> globalVars = new LinkedList<>();

    public Module() { super("MODULE", null, null); }

    public static Module getInstance() {
        return module;
    }

    public void addFunc(Function function) {
        this.functionList.add(function);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }

    public LinkedList<Function> getFunctionList() {
        return functionList;
    }

    public LinkedList<GlobalVar> getGlobalVars() {
        return globalVars;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (GlobalVar globalVar : globalVars) {
            sb.append(globalVar.toString());
            sb.append("\n");
        }
        sb.append("\n");
        for (Function function : functionList) {
            sb.append(function.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
