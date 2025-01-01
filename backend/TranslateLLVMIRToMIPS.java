package backend;

import backend.MIPS.MIPSModule;
import backend.MIPS.MipsGlobalVar;
import midend.value.BasicBlock;
import midend.value.Function;
import midend.value.GlobalVar;
import midend.value.Module;

public class TranslateLLVMIRToMIPS {
    Module module;
    MIPSModule mipsModule;

    public TranslateLLVMIRToMIPS(Module module) {
        this.module = module;
        mipsModule = new MIPSModule();
    }

    public void translateModule() {
        //翻译全局变量
        for (GlobalVar globalVar : module.getGlobalVars()) {
            mipsModule.addGlobalVar(new MipsGlobalVar(globalVar));
        }
        //翻译函数和主函数
        /*for (Function function : module.getFunctionList()) {
            translateFunction(function);
        }**/
    }

    public void translateFunction(Function llvmirFunction) {
        //形参
        //基本块
        for (BasicBlock llvmirBB : llvmirFunction.getBasicBlocks()) {
            translateBB(llvmirBB);
        }
    }

    private void translateBB(BasicBlock llvmirBB) {
        //翻译所有指令
    }

    public MIPSModule getMipsModule() {
        return this.mipsModule;
    }
}
