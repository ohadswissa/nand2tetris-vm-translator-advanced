package vmtranslator;

import  java.io.IOException;
import java.io.File;
public class VMTranslator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide exactly one .vm file or a directory to assemble");
            return;
        }
        File Path = new File(args[0]);
        //Checking for the different possibilities for inputs and per option - we will use the helper functions for  each scenario.
        if (Path.isFile() && Path.getName().endsWith(".vm")) {
            //One valid file option.
            FileTranslator(Path);
        } else if (Path.isDirectory()) {
            //Directory option. we will cover an edge case where the directory obtains only one vm file - then we treat it as a single file
            //for not summoning the bootstrap and getting an error.
            File[] vmFiles = Path.listFiles((dir, name) -> name.endsWith(".vm")); //
            if (vmFiles == null || vmFiles.length == 0) {
                System.out.println("No vm files found in the directory " + Path.getAbsolutePath());
            } else if (vmFiles.length == 1) {
                //Single vm file, we should treat it as a single file even tough he is inside a folder.
                System.out.println("Found a single vm file in the directory, treating as a single file.");
                FileTranslator(vmFiles[0]);
            } else {
                DirTranslator(Path);
            }
        } else {
            System.out.println("Please provide a .vm file or a directory to assemble");
        }
    }
    /**
     * Takes a single vm file  and return an asm file.
     * @param vmFile as the input vm file to be translated.
     */
        private static void FileTranslator(File vmFile) {
        String asmFile = vmFile.getAbsolutePath().replace(".vm", ".asm");
        try (CodeWriter codeWriter = new CodeWriter(asmFile)) {
            //Start by writing the bootstrap code.
            codeWriter.setFileName(vmFile.getName());
            Parser parser = new Parser(vmFile.getAbsolutePath());
            while (parser.hasMoreCommands()) {
                parser.advance(); //As long as the file is not empty of commands - advance.
                switch (parser.commandType()) {
                    case "C_ARITHMETIC" -> codeWriter.writeArithmetic(parser.arg1());
                    case  "C_PUSH" , "C_POP" -> codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    case "C_LABEL" -> codeWriter.Writelabel(parser.arg1());
                    case "C_GOTO" -> codeWriter.WriteGoto(parser.arg1());
                    case "C_IF" -> codeWriter.WriteIf(parser.arg1());
                    case "C_FUNCTION" -> codeWriter.WriteFunction(parser.arg1(), parser.arg2());
                    case "C_CALL" -> codeWriter.WriteCall(parser.arg1(), parser.arg2());
                    case "C_RETURN" -> codeWriter.WriteReturn();
                    default -> throw new IllegalStateException("Unexpected value: " + parser.commandType());
                }
            }
        } catch (IOException e) {
            System.out.println("Error while assembling VM: " + e.getMessage());
        }
    }
    /**
     * Takes all vm files within a directory and returns them as a one combined asm file.
     * @param directory is a given directory that we need to handle her vm files.
     */
    private static void DirTranslator(File directory) {
        // Gather all .vm files in the directory
        File[] vmFiles = directory.listFiles((dir, name) -> name.endsWith(".vm"));
        if (vmFiles == null || vmFiles.length == 0) {
            System.out.println("There are no .vm files found in directory: " + directory.getAbsolutePath());
            return;
        }
        String asmFile = new File(directory, directory.getName() + ".asm").getAbsolutePath(); //Going to be the Output combined file we need.
        try (CodeWriter codeWriter = new CodeWriter(asmFile)) {
            //Start by writing the bootstrap code.
            codeWriter.writeBootstrap();
            for (File vmFile : vmFiles) {
                System.out.println("Translating: " + vmFile.getName()); //For debugging purposes and for being able to see the transition process.
                codeWriter.setFileName(vmFile.getName());
                Parser parser = new Parser(vmFile.getAbsolutePath());
                while (parser.hasMoreCommands()) {
                    parser.advance(); // Advance through commands - as the single vm file scenario at main.
                    switch (parser.commandType()) {
                        case "C_ARITHMETIC" -> codeWriter.writeArithmetic(parser.arg1());
                        case "C_PUSH", "C_POP" -> codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                        case "C_LABEL" -> codeWriter.Writelabel(parser.arg1());
                        case "C_GOTO" -> codeWriter.WriteGoto(parser.arg1());
                        case "C_IF" -> codeWriter.WriteIf(parser.arg1());
                        case "C_FUNCTION" -> codeWriter.WriteFunction(parser.arg1(), parser.arg2());
                        case "C_CALL" -> codeWriter.WriteCall(parser.arg1(), parser.arg2());
                        case "C_RETURN" -> codeWriter.WriteReturn();
                        default -> throw new IllegalStateException("Unexpected command type: " + parser.commandType());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing directory: " + directory.getAbsolutePath());
        }
    }
}

