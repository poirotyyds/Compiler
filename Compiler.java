import backend.TranslateLLVMIRToMIPS;
import frontend.*;
import midend.Visitor;
import midend.value.Module;
import node.NodeOutput;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;

public class Compiler {
    public static void main (String[] args) throws IOException {
        /***********词法分析************/
        Lexer lexer = new Lexer("testfile.txt");
        String errorFilePath = "error.txt";
        String lexerFilePath = "lexer.txt";
        if (!lexer.getIsLexerWrong()) {
            FileWriter write = new FileWriter(lexerFilePath);
            try (BufferedWriter lexerWriter = Files.newBufferedWriter(Paths.get(lexerFilePath), java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)) {
                for (Token token : lexer.getTokens()) {
                    lexerWriter.write(token.getTypeString() + " " + token.getValue() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /***********语法分析************/
        Parser parser = new Parser(lexer.getTokens());
        if (!parser.getIsParserWrong()) {
            parser.getCompUnitNode().print();
            NodeOutput.writeToParser();
        }
        /***********语义分析：符号表管理和错误分析************/
        Visitor visitor = new Visitor(parser.getCompUnitNode());
        FileWriter write = new FileWriter("symbol.txt");
        try (BufferedWriter symbolWriter = Files.newBufferedWriter(Paths.get("symbol.txt"), java.nio.charset.StandardCharsets.UTF_8, StandardOpenOption.APPEND);) {
            visitor.printSymbolTable(symbolWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lexer.getIsLexerWrong() || parser.getIsParserWrong() || OutWrongInformation.isWrong) {
            FileWriter writer = new FileWriter(errorFilePath);
            ArrayList<Integer> keys = new ArrayList<>(OutWrongInformation.wrongInformation.keySet());
            // 对键token的pos进行排序
            Collections.sort(keys);
            try (BufferedWriter errorWriter = Files.newBufferedWriter(Paths.get(errorFilePath), java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)) {
                for (Integer key : keys) {
                    errorWriter.write(OutWrongInformation.wrongInformation.get(key) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*生成LLVMIR*/
        FileWriter writer = new FileWriter("llvm_ir.txt");
        try (BufferedWriter llvmIrWriter = Files.newBufferedWriter(Paths.get("llvm_ir.txt"), java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.APPEND)) {
            llvmIrWriter.write(Module.getInstance().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
//        TranslateLLVMIRToMIPS object = new TranslateLLVMIRToMIPS(Module.getInstance());
//        object.translateModule();
//        System.out.println(object.getMipsModule());
    }
}
