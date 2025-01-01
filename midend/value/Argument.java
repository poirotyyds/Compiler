package midend.value;

import midend.type.Type;

public class Argument extends Value{
    private Function parentFunction;

    public Argument(String name, Value parent, Type type) {
        super(name, parent, type);
    }
}
