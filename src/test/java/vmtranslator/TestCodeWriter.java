package vmtranslator;

import java.io.IOException;

            public class TestCodeWriter {
                public static void main(String[] args) {
                    try {
                        // Create CodeWriter instance and specify the output file
                        CodeWriter codeWriter = new CodeWriter("src/test/resources/TestOutput.asm");

                        // Test Arithmetic Commands
                        codeWriter.writeArithmetic("add");
                        codeWriter.writeArithmetic("sub");
                        codeWriter.writeArithmetic("neg");
                        codeWriter.writeArithmetic("eq");
                        codeWriter.writeArithmetic("gt");
                        codeWriter.writeArithmetic("lt");
                        codeWriter.writeArithmetic("and");
                        codeWriter.writeArithmetic("or");
                        codeWriter.writeArithmetic("not");

                        // Test Push Commands
                        codeWriter.writePushPop("C_PUSH", "constant", 10);
                        codeWriter.writePushPop("C_PUSH", "local", 2);
                        codeWriter.writePushPop("C_PUSH", "argument", 3);
                        codeWriter.writePushPop("C_PUSH", "this", 1);
                        codeWriter.writePushPop("C_PUSH", "that", 4);
                        codeWriter.writePushPop("C_PUSH", "temp", 6);
                        codeWriter.writePushPop("C_PUSH", "pointer", 0);
                        codeWriter.writePushPop("C_PUSH", "pointer", 1);
                        codeWriter.writePushPop("C_PUSH", "static", 7);

                        // Test Pop Commands
                        codeWriter.writePushPop("C_POP", "local", 2);
                        codeWriter.writePushPop("C_POP", "argument", 3);
                        codeWriter.writePushPop("C_POP", "this", 1);
                        codeWriter.writePushPop("C_POP", "that", 4);
                        codeWriter.writePushPop("C_POP", "temp", 6);
                        codeWriter.writePushPop("C_POP", "pointer", 0);
                        codeWriter.writePushPop("C_POP", "pointer", 1);
                        codeWriter.writePushPop("C_POP", "static", 7);

                        // Close the writer
                        codeWriter.close();

                        System.out.println("Tests completed. Check 'TestOutput.asm' for results.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
