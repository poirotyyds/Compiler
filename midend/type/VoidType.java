package midend.type;

public class VoidType extends Type{
     @Override
    public int getSize() {
         return 0;
     }

    @Override
    public String toString() {
        return "void";
    }
}
