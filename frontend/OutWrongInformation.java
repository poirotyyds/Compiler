package frontend;

import error.Error;

import java.util.HashMap;

public class OutWrongInformation {
    public static HashMap<Integer, String> wrongInformation = new HashMap<>();
    public static boolean isWrong = false;

    public static void addError(Error error) {
        isWrong = true;
        if (wrongInformation.containsKey(error.getLineNum())) {
            return;
        }
        wrongInformation.put(error.getLineNum(), error.getLineNum() + " " + error.getType().toString());
    }
}
