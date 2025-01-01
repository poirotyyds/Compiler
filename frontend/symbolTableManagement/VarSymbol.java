package frontend.symbolTableManagement;

public class VarSymbol extends Symbol {
    public VarSymbol(int id, int tableId, String name, int type, int bType, boolean isCon) {
        super(id, tableId, name, type, bType, isCon);
    }
}
