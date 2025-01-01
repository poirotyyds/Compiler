package frontend.symbolTableManagement;

public class Symbol {
    private int id;
    private int tableId;
    private String name;
    private int type;//0->var,1->array,2->func
    private int bType;//0->int,1->char,2->void
    private boolean isCon = false;//true->const

    public Symbol(int id, int tableId, String name, int type, int bType, boolean isCon) {
        this.id = id;
        this.tableId = tableId;
        this.name = name;
        this.type = type;
        this.bType = bType;
        this.isCon = isCon;
    }

    public boolean isCon() {
        return isCon;
    }

    public int getbType() {
        return bType;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getTableId() {
        return tableId;
    }

    public int getId() {
        return id;
    }

    public String typeToString() {
        if (isCon && bType == 1 && type == 0) {
            return "ConstChar";
        } else if (isCon && bType == 0 && type == 0) {
            return "ConstInt";
        } else if (isCon && bType == 1 && type == 1) {
            return  "ConstCharArray";
        } else if (isCon && bType == 0 && type == 1) {
            return  "ConstIntArray";
        } else if (!isCon && bType == 1 && type == 0) {
            return "Char";
        } else if (!isCon && bType == 0 && type == 0) {
            return "Int";
        } else if (!isCon && bType == 1 && type == 1) {
            return "CharArray";
        } else if (!isCon && bType == 0 && type == 1) {
            return "IntArray";
        } else if (bType == 2 && type == 2) {
            return "VoidFunc";
        } else if (bType == 1 && type == 2) {
            return "CharFunc";
        } else if (bType == 0 && type == 2) {
            return "IntFunc";
        } else {
            return "WrongType";
        }
    }
}
