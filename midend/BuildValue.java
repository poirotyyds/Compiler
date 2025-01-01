package midend;

import midend.type.*;
import midend.value.*;
import midend.value.instructions.*;

import java.util.ArrayList;

public class BuildValue {
    private static int countForName = 0;
    private static int countForStrName = 0;

    public static ConstantInt buildConstantInt(int bitSize, int val) {
         return new ConstantInt(bitSize, val);
    }

    public static Function buildFunction(String name, FunctionType functionType) {
        Function function = new Function(functionType, name);
        midend.value.Module.getInstance().addFunc(function);
        countForName = function.getArguments().size();
        return function;
    }

    public static Ret buildRetWithReturn(BasicBlock parent, Value returnVal) {
        Ret ret = new Ret(parent, returnVal);
        parent.addInstruction(ret);
        return ret;
    }

    public static Ret buildRetVoid(BasicBlock parent) {
        Ret ret = new Ret(parent);
        parent.addInstruction(ret);
        return ret;
    }

    public static Value buildAdd(BasicBlock parent, Value leftValue, Value rightValue) {
        Value lVal = leftValue;
        Value rVal = rightValue;
        if (!leftValue.getType().toString().equals("i32")) {
            //需要扩展
            lVal = BuildValue.buildZeroExtend(parent,leftValue);
        }
        if (!rightValue.getType().toString().equals("i32")) {
            //需要扩展
            rVal = BuildValue.buildZeroExtend(parent,rightValue);
        }
        Add add = new Add("%" + countForName, parent, lVal, rVal);
        parent.addInstruction(add);
        countForName++;
        return add;
    }

    public static Value buildSub(BasicBlock parent, Value leftVal, Value rightVal) {
        Value lVal = leftVal;
        Value rVal = rightVal;
        if (!leftVal.getType().toString().equals("i32")) {
            //需要扩展
            lVal = BuildValue.buildZeroExtend(parent,leftVal);
        }
        if (!rightVal.getType().toString().equals("i32")) {
            //需要扩展
            rVal = BuildValue.buildZeroExtend(parent,rightVal);
        }
        Sub sub = new Sub("%" + countForName, parent, lVal, rVal);
        parent.addInstruction(sub);
        countForName++;
        return sub;
    }

    public static Value buildMul(BasicBlock parent, Value leftVal, Value rightVal) {
        Value lVal = leftVal;
        Value rVal = rightVal;
        if (!leftVal.getType().toString().equals("i32")) {
            //需要扩展
            lVal = BuildValue.buildZeroExtend(parent,leftVal);
        }
        if (!rightVal.getType().toString().equals("i32")) {
            //需要扩展
            rVal = BuildValue.buildZeroExtend(parent,rightVal);
        }
        Mul mul = new Mul("%" + countForName, parent, lVal, rVal);
        parent.addInstruction(mul);
        countForName++;
        return mul;
    }

    public static Value buildSDiv(BasicBlock parent, Value leftVal, Value rightVal) {
        Value lVal = leftVal;
        Value rVal = rightVal;
        if (!leftVal.getType().toString().equals("i32")) {
            //需要扩展
            lVal = BuildValue.buildZeroExtend(parent,leftVal);
        }
        if (!rightVal.getType().toString().equals("i32")) {
            //需要扩展
            rVal = BuildValue.buildZeroExtend(parent,rightVal);
        }
        SDiv sDiv = new SDiv("%" + countForName, parent, lVal, rVal);
        parent.addInstruction(sDiv);
        countForName++;
        return sDiv;
    }

    public static Value buildSRem(BasicBlock parent, Value leftVal, Value rightVal) {
        Value lVal = leftVal;
        Value rVal = rightVal;
        if (!leftVal.getType().toString().equals("i32")) {
            //需要扩展
            lVal = BuildValue.buildZeroExtend(parent,leftVal);
        }
        if (!rightVal.getType().toString().equals("i32")) {
            //需要扩展
            rVal = BuildValue.buildZeroExtend(parent,rightVal);
        }
        SRem sRem = new SRem("%" + countForName, parent, lVal, rVal);
        parent.addInstruction(sRem);
        countForName++;
        return sRem;
    }

    public static BasicBlock buildBasicBlock(Function parent) {
        BasicBlock basicBlock = new BasicBlock("" + countForName, parent);
        countForName++;
        parent.addBasicBlock(basicBlock);
        return basicBlock;
    }

    public static Alloca buildAlloca(Type allocatedType, BasicBlock parent) {
        BasicBlock realParent = ((Function) parent.getParent()).getBasicBlocks().get(0);
        Alloca alloca = new Alloca("%" + countForName, allocatedType, realParent);
        countForName++;
        parent.addInstruction(alloca);
        return alloca;
    }

    public static void buildStore(BasicBlock parent, Value curValue, Value location) {
        Value typeChangedVal = curValue;
        if (curValue.getType().toString().equals("i32") && ((PointerType)location.getType()).getPointedType().toString().equals("i8")) {
            typeChangedVal = BuildValue.buildTrunc(parent, curValue);
        } else if (curValue.getType().toString().equals("i8") && ((PointerType)location.getType()).getPointedType().toString().equals("i32")) {
            typeChangedVal = BuildValue.buildZeroExtend(parent, curValue);
        }
        Store store = new Store(parent, typeChangedVal, location);
        parent.addInstruction(store);
    }

    public static Load buildLoad(BasicBlock parent, Value location) {
        //alloca是location where to loa
        Load load = new Load("%" + countForName, parent, ((PointerType) location.getType()).getPointedType(), location);
        countForName++;
        parent.addInstruction(load);
        return load;
    }

    public static Function buildFunciton(String funcName, FunctionType functionType) {
        Function function = new Function(functionType, funcName);
        midend.value.Module.getInstance().addFunc(function);
        countForName = function.getArguments().size();
        return function;
    }

    public static Call buildCall(BasicBlock parent, Type returnType, String callFuncName,ArrayList<Value> realArguments, FunctionType functionType) {
        Call call;
        ArrayList<Value> typeChangedRealArguments = new ArrayList<>();
        for (int i = 0; i < realArguments.size(); i++) {
            if (realArguments.get(i).getType().toString().equals("i32") && functionType.getArgumentTypes().get(i).toString().equals("i8")) {
                typeChangedRealArguments.add(BuildValue.buildTrunc(parent, realArguments.get(i)));
            }  else if (realArguments.get(i).getType().toString().equals("i8") && functionType.getArgumentTypes().get(i).toString().equals("i32")) {
                typeChangedRealArguments.add(BuildValue.buildZeroExtend(parent, realArguments.get(i)));
            } else {
                typeChangedRealArguments.add(realArguments.get(i));
            }
        }
        if (returnType instanceof VoidType) {
            call = new Call("", parent, callFuncName, returnType, typeChangedRealArguments);
        } else {
            call = new Call("%" + countForName, parent, callFuncName, returnType, typeChangedRealArguments);
            countForName++;
        }
        parent.addInstruction(call);
        return call;
    }

    public static GlobalVar buildPrintfStringGlobalVar(ConstantString constantString) {
        GlobalVar globalVar = new GlobalVar(".str" + (countForStrName == 0 ? "" : ("." + countForStrName)), true, constantString.getType(), constantString);
        midend.value.Module.getInstance().addGlobalVar(globalVar);
        countForStrName++;
        return globalVar;
    }
//bug没有类型转换：：

    public static GetElementPtr buildGetElementPtr(BasicBlock parent, Value baseAdr, Value pointIndex, Value arrayIndex) {
        GetElementPtr getElementPtr = new GetElementPtr("" + countForName, parent, new PointerType(((ArrayType)((PointerType) baseAdr.getType()).getPointedType()).getElementType()), baseAdr, pointIndex, arrayIndex);
        parent.addInstruction(getElementPtr);
        countForName++;
        return getElementPtr;
    }

    //如果是%1 = getelementptr i32, i32*, index偏移量,baseAdr是指针
    public static GetElementPtr buildGetElementPtr(BasicBlock parent, Value baseAdr, Value pointIndex) {//函数中使用数组
        GetElementPtr getElementPtr = new GetElementPtr("" + countForName, parent, baseAdr.getType(), baseAdr, pointIndex);
        parent.addInstruction(getElementPtr);
        countForName++;
        return getElementPtr;
    }

    //由于Sys文法中只有char和int相关的类型转换
    public static Zext  buildZeroExtend (BasicBlock parent, Value oldValue) {
        Zext zext = new Zext("%" + countForName, parent, new IntegerType(32), oldValue);
        countForName++;
        parent.addInstruction(zext);
        return zext;
    }

    public static Zext buildZext(BasicBlock parent, Value oldValue, Type extToType) {
        Zext zext = new Zext("%" + countForName, parent, extToType, oldValue);
        countForName++;
        parent.addInstruction(zext);
        return zext;
    }

    public static Trunc buildTrunc (BasicBlock parent, Value oldValue) {
        Trunc trunc = new Trunc("%" + countForName, parent, new IntegerType(8), oldValue);
        countForName++;
        parent.addInstruction(trunc);
        return trunc;
    }

    public static Br buildBr(BasicBlock parent, BasicBlock toBasicBlock) {
        Br br = new Br(parent, toBasicBlock);
        parent.addInstruction(br);
        return br;
    }

    public static Br buildBr(BasicBlock parent, Value cond, BasicBlock trueToBasicBlock, BasicBlock falseToBasicBlock) {
        Br br = new Br(parent, cond, trueToBasicBlock, falseToBasicBlock);
        parent.addInstruction(br);
        return br;
    }

    public static Icmp buildIcmp(BasicBlock parent, IcmpType type, Value leftVal, Value rightVal) {
        Value lVal = leftVal;
        Value rVal = rightVal;
        if (!leftVal.getType().toString().equals(rightVal.getType().toString())) {
            if (leftVal.getType().toString().equals("i1") && !rightVal.getType().toString().equals("i1")) {
                lVal = buildZext(parent, leftVal, rightVal.getType());
            } else if (rightVal.getType().toString().equals("i1") && !leftVal.getType().toString().equals("i1")) {
                rVal = buildZext(parent, rightVal, leftVal.getType());
            } else if (leftVal.getType().toString().equals("i8") && !rightVal.getType().toString().equals("i8")) {
                lVal = buildZext(parent, leftVal, rightVal.getType());
            } else if (rightVal.getType().toString().equals("i8") && !leftVal.getType().toString().equals("i8")) {
                rVal = buildZext(parent, rightVal, leftVal.getType());
            }
        }
        Icmp icmp = new Icmp("%" + countForName, parent, type, lVal, rVal);
        countForName++;
        parent.addInstruction(icmp);
        return icmp;
    }

}
