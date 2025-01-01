package midend.value;

import midend.type.Type;

import java.util.ArrayList;

public class User extends Value {
    private ArrayList<Value> values = new ArrayList<>();

    public User(String name, Value parent, Type type) {
        super(name, parent, type);
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void setValues(Value... values) {
        for (Value value : values) {
            this.values.add(value);
            value.addUser(this);
        }
    }
}
