package frontend.symbolTableManagement;

import error.Error;
import error.ErrorType;
import frontend.OutWrongInformation;
import midend.value.Value;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private int id;
    private int fatherId;
    private HashMap<String, Symbol> dictionary = new HashMap<>();
    private HashMap<String, Value> dictionaryFoLLVMIR = new HashMap<>();
    private int now = 0;
    public ArrayList<String> symbols = new ArrayList<>();

    public SymbolTable(int id, int fatherId) {
        this.id = id;
        this.fatherId = fatherId;
    }

    public void addSymbol(Symbol symbol, int lineNum)  {
        if (dictionary.containsKey(symbol.getName())) {
            OutWrongInformation.addError(new Error(lineNum, ErrorType.b));
            return;
        }
        dictionary.put(symbol.getName(), symbol);
        symbols.add(symbol.getName());
        now++;
    }

    public int getNow() {
        return now;
    }

    public int getFatherId() {
        return fatherId;
    }

    public Symbol getSymbol(String identName) {
        return dictionary.get(identName);
    }

    public void print(int tableId, BufferedWriter symbolWriter) throws IOException {
        for (int i = 0; i < now; i++) {
            System.out.print(tableId);
            System.out.println(" " + symbols.get(i) + " " + dictionary.get(symbols.get(i)).typeToString());
        }
        FileWriter write = new FileWriter("symbol.txt", true);
        for (int i = 0; i < now; i++) {
            symbolWriter.write(tableId + " " + symbols.get(i) + " " + dictionary.get(symbols.get(i)).typeToString() + "\n");
        }
    }

    public Symbol findSymbol(String identName) {
        return dictionary.get(identName);
    }

    public void deleteSymbol(String identName) {
        dictionary.remove(identName);
        now--;
    }
}
