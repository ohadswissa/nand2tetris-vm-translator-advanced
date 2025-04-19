package vmtranslator;

import java.io.IOException;

public class TestParser {
    public static void main(String[] args) {
        try {
            // Update the file path to match your test file location
            String filePath = "src/test/resources/complexTest.vm"; // Adjust as needed
            Parser parser = new Parser(filePath);

            System.out.println("Testing Parser...");

            while (parser.hasMoreCommands()) {
                parser.advance(); // Move to the next command

                // Test current command
                System.out.println("Current Command: " + parser.getCurrentCommand());

                // Test commandType()
                String commandType = parser.commandType();
                if (commandType == null) {
                    System.out.println("Command Type: null (Unknown command)");
                } else {
                    System.out.println("Command Type: " + commandType);
                }

                // Test arg1()
                try {
                    System.out.println("Arg1: " + parser.arg1());
                } catch (IllegalStateException e) {
                    System.out.println("Arg1 Error: " + e.getMessage());
                }

                // Test arg2() - Stubbed for now
                try {
                    if (commandType.equals("C_PUSH") || commandType.equals("C_POP")) {
                        System.out.println("Arg2: " + parser.arg2());
                    }
                } catch (IllegalStateException e) {
                    System.out.println("Arg2 Error: " + e.getMessage());
                }

                System.out.println("-----");
            }

            parser.close(); // Close resources
            System.out.println("Parser Test Completed!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
