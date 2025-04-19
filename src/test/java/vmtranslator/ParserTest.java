package vmtranslator;
import java.io.IOException;

import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser("src/test/resources/TestCommands.vm");

        System.out.println("Testing commandType():");
        while (parser.hasMoreCommands()) {
            parser.advance();
            String currentCommand = parser.getCurrentCommand();
            System.out.println("Command: " + currentCommand + " -> Type: " + parser.commandType());
        }

        // Reset parser
        parser = new Parser("src/test/resources/TestCommands.vm");
        System.out.println("\nTesting arg1() and arg2():");
        while (parser.hasMoreCommands()) {
            parser.advance();
            String currentCommand = parser.getCurrentCommand();
            String type = parser.commandType();
            System.out.print("Command: " + currentCommand + " -> ");
            System.out.print("arg1: " + (type.equals("C_RETURN") ? "N/A" : parser.arg1()));
            if (type.equals("C_PUSH") || type.equals("C_POP") || type.equals("C_FUNCTION") || type.equals("C_CALL")) {
                System.out.println(", arg2: " + parser.arg2());
            } else {
                System.out.println();
            }
        }
    }
}
