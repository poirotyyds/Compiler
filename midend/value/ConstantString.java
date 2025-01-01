package midend.value;

import midend.type.ArrayType;
import midend.type.IntegerType;

public class ConstantString extends Value{
    private String string;
    private int initLength;

    public ConstantString(String s) {
        super("c\"" + stringToValueFormatInLLVMIR(s, s.length()+1) + "\\00\"", null, new ArrayType(new IntegerType(8), s.length() + 1));
        this.string = s;
        this.initLength = s.length()+1;
    }

    public ConstantString(String s, int initLength) {
        super("c\"" + stringToValueFormatInLLVMIR(s,initLength + 1) + "\"", null, new ArrayType(new IntegerType(8), initLength));
        this.string = s;
        this.initLength = initLength;
    }

    public String getString() {
        return this.string;
    }

    public static String stringToValueFormatInLLVMIR(String s, int length) {
        /*将s中的换行符更新成llvmir中value的形式*/
        StringBuilder sb = new StringBuilder();
        char[] sArray = s.toCharArray();
        /*
        replace.replace("\\n", "\n")
                .replace("\\0","\0")
                .replace("\\\\","\\")
                .replace("\\a","" + (char)7)
                .replace("\\b","\b")
                .replace("\\t","\t")
                .replace("\\v","" + (char)11)
                .replace("\\\"","\"")
                .replace("\\\'","\'")
                .replace("\\f","\f");
        * */
        boolean flagZero = false;
        for (int i = 0; i < length - 1; i++) {
            if (i >= sArray.length || flagZero) {
                sb.append("\\00");
            } else if (sArray[i] == '\n') {
                sb.append("\\0A");
            } else if (sArray[i] == '\0') {
                sb.append("\\00");
                flagZero = true;
            } else if (sArray[i] == '\\') {
                sb.append("\\5C");
            } else if (sArray[i] == 7) {
                sb.append("\\07");
            } else if (sArray[i] == 8) {
                sb.append("\\08");
            } else if (sArray[i] == 9) {
                sb.append("\\09");
            } else if (sArray[i] == 11) {
                sb.append("\\0B");
            } else if (sArray[i] == 12) {
                sb.append("\\0C");
            } else if (sArray[i] == '\"') {
                sb.append("\\22");
            }  else if (sArray[i] == '\'') {
                sb.append("\\27");
            } else {
                sb.append(sArray[i]);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getName() + ", align 1";
    }
}
