package midend.value;

import midend.type.ArrayType;
import midend.type.IntegerType;
import midend.type.Type;

public class Constant extends Value {
     public Constant(String name, Value parent, Type type) {
         super(name, parent, type);
     }

    public static Value initDefaultVal(Type type) {
         if (type instanceof IntegerType) {
             return new ConstantInt(32, 0);
         } else {
             return new ConstArray((ArrayType) type);
         }
    }
}
