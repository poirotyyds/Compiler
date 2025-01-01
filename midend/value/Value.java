package midend.value;

import midend.type.Type;

import java.util.ArrayList;

public class Value {
    //每个value都有使用其的user,user可以多个
    private ArrayList<User> users = new ArrayList<>();
    private String name;
    private Value parent;
    private Type type;

    public Value(String name, Value parent, Type type) {
        this.name = name;
        this.parent = parent;
        this.type = type;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public Value getParent() {
        return parent;
    }

    public Type getType() {
        return type;
    }

    public void addUser(User user) {
        this.users.add(user);
    }

    protected void setName(String count) {
        this.name = "" + count;
    }
}
