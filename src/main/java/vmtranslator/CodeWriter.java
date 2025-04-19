package vmtranslator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
/**
 * translates VM commands into Hack assembly code and writes to an output file - an .asm file.
 */
public class CodeWriter implements AutoCloseable {
    private BufferedWriter bw;
    private String currentFileName;
    private String currentFunction = "";
    private static int labelCounter = 0; // Static counter for unique labels, used in the helper function later on.
    /**
     * opens the output file/stream and gets ready to write into it.
     *
     * @param outputFile path to the output which is a '.asm' file.
     * @throws IOException if there is an error creating the wanted file.
     */
    public CodeWriter(String outputFile) throws IOException {
        bw = new BufferedWriter(new FileWriter(outputFile));
        currentFileName = ""; // Initialize file name.
    }
    public void setFileName(String fileName) {
        // Strip file extension (if present) and store the base name.
        currentFileName = new File(fileName).getName().replace(".vm", "");
    }

    /**
     * Handles eq, gt, lt operations with a helper function for better approach in the writeArithmetic function.
     *
     * @param command by string.
     * @throws IOException if the command is not valid.
     */
    private void writeComparison(String command) throws IOException {
        String jumpCommand = switch (command) {
            case "eq" -> "JEQ";
            case "gt" -> "JGT";
            case "lt" -> "JLT";
            default -> throw new IllegalArgumentException("Invalid comparison command: " + command);
        };
        String labelTrue = "TRUE" + labelCounter;
        String labelEnd = "END" + labelCounter;
        labelCounter++;
        bw.write("@SP\n");
        bw.write("AM=M-1\n"); // Decrement SP, access the top value
        bw.write("D=M\n"); // Store y in D
        bw.write("@SP\n");
        bw.write("A=M-1\n"); // Access x
        bw.write("D=M-D\n"); // Perform x - y, store in D
        bw.write("@" + labelTrue + "\n");
        bw.write("D;" + jumpCommand + "\n"); //if condition is true : jump.
        bw.write("@SP\n");
        bw.write("A=M-1\n");
        bw.write("M=0\n"); // Set false (0).
        bw.write("@" + labelEnd + "\n");
        bw.write("0;JMP\n"); // Unconditional jump to end.
        bw.write("(" + labelTrue + ")\n"); //True case.
        bw.write("@SP\n");
        bw.write("A=M-1\n"); //Access the top of the stack.
        bw.write("M=-1\n"); // Set true to (-1) as seen in class.
        //End label: marks the end of the true/false logic.
        bw.write("(" + labelEnd + ")\n");
    }
    /**
     * Writes to the output file the assembly code that implements the given arithmetic-logical command.
     *
     * @param command The arithmetic command (for example : "add").
     * @throws IOException if there is an error writing to the file.
     */
    public void writeArithmetic(String command) throws IOException {
        //We will start first with a row of comment for clarification.
        bw.write("//" + command + "\n");
        //Use switch case for convenient.
        switch (command) {
            case "add": //x + y
                bw.write("@SP\n");
                bw.write("AM=M-1\n"); //Decrement Sp and access the top value.
                bw.write("D=M\n"); //Store y value in D.
                bw.write("@SP\n");
                bw.write("A=M-1\n"); //Access x.
                bw.write("M=D+M\n"); //Perform x + y and store x.
                break;
            case "sub": //x - y
                bw.write("@SP\n");
                bw.write("AM=M-1\n"); //Decrement Sp and access the top value.
                bw.write("D=M\n"); //Store y value in D.
                bw.write("@SP\n");
                bw.write("A=M-1\n"); //Access x.
                bw.write("M=M-D\n"); //Perform x - y and store x.
                break;
            case "neg": // -x
                bw.write("@SP\n");
                bw.write("A=M-1\n"); //Access x.
                bw.write("M=-M\n"); //Perform -x (negation)
                break;
            case "eq": //x = y.
            case "lt": //x < y.
            case "gt": //x > y.
                writeComparison(command);//Using the helper function.
                break;
            case "and": //x & y
                bw.write("@SP\n");
                bw.write("AM=M-1\n"); //Decrement Sp and access the top value.
                bw.write("D=M\n"); //Store y value in D.
                bw.write("@SP\n");
                bw.write("A=M-1\n"); //Access x.
                bw.write("M=D&M\n"); //Perform x & y and store x.
                break;
            case "or": //x | y
                bw.write("@SP\n");
                bw.write("AM=M-1\n"); //Decrement Sp and access the top value.
                bw.write("D=M\n"); //Store y value in D.
                bw.write("@SP\n");
                bw.write("A=M-1\n"); //Access x.
                bw.write("M=D|M\n"); //Perform x | y and store x.
                break;
            case "not": // !x
                bw.write("@SP\n");
                bw.write("A=M-1\n"); //Access x.
                bw.write("M=!M\n"); //Perform !x (negation)
                break;
            default:
                throw new IllegalArgumentException("The arithmetic command is not supported: " + command);
        }
    }
    /**
     * Helper method for mapping VM segment names to Hack pointers.
     *
     * @param segment as a VM segment name.
     * @return the Hack pointer.
     */
    private String getSegmentP(String segment) {
        return switch (segment) {
            case "local" -> "LCL";
            case "argument" -> "ARG";
            case "this" -> "THIS";
            case "that" -> "THAT";
            default -> throw new IllegalArgumentException("Invalid segment: " + segment);
        };
    }
    /**
     * writes to the output file the assembly code that implements the given push or pop command.
     *
     * @param command The command type ("C_PUSH" or "C_POP").
     * @param segment The memory segment (e.g., "constant", "local").
     * @param index   The index within the segment.
     * @throws IOException if there is an error writing to the file.
     */
    public void writePushPop(String command, String segment, int index) throws IOException {
        //Condition for a push command
        if (command.equals("C_PUSH")) {
            switch (segment) {
                case "constant": //We will push the constant index onto the stack.
                    bw.write("// push constant " + index + "\n");
                    bw.write("@" + index + "\n"); //Load's the constant to A.
                    bw.write("D=A\n"); //Store it on D.
                    bw.write("@SP\n");
                    bw.write("A=M\n"); //Reach the  top of the stack.
                    bw.write("M=D\n"); //Push operation
                    bw.write("@SP\n");
                    bw.write("M=M+1\n"); //Increment the stack pointer.
                    break;

                case "local":
                case "argument":
                case "this":
                case "that":
                    //We will handle values from segment[index] onto the stack.
                    bw.write("// push " + segment + " " + index + "\n");
                    bw.write("@" + index + "\n"); //Loading the index.
                    bw.write("D=A\n"); //Store it on D.
                    bw.write("@" + getSegmentP(segment) + "\n"); //Using the helper function.
                    bw.write("D=D+M\n");  // Compute base + index in D
                    bw.write("A=D\n");    // Set A to the computed address
                    bw.write("D=M\n"); //Gets the value at the index.
                    bw.write("@SP\n");
                    bw.write("A=M\n"); //Stack's Top value.
                    bw.write("M=D\n"); //Push operation
                    bw.write("@SP\n");
                    bw.write("M=M+1\n"); //Increment the stack pointer.
                    break;

                case "temp": //Push the value from temp at place index onto the stack.
                    bw.write("// push temp " + index + "\n");
                    bw.write("@" + (5 + index) + "\n"); //The temp segment start's at RAM[5] so we use it like that.
                    bw.write("D=M\n"); //Gets the value at the index.
                    bw.write("@SP\n");
                    bw.write("A=M\n"); //Stack's Top value.
                    bw.write("M=D\n"); //Push operation
                    bw.write("@SP\n");
                    bw.write("M=M+1\n"); //Increment the stack pointer.
                    break;

                case "pointer": //Push the value from this/that pointer onto the stack.
                    bw.write("// push pointer " + index + "\n");
                    bw.write("@" + (3 + index) + "\n"); //The this/that segment start's at RAM[3] so we use it like that.
                    bw.write("D=M\n"); //Gets the value at the index.
                    bw.write("@SP\n");
                    bw.write("A=M\n"); //Stack's Top value.
                    bw.write("M=D\n"); //Push operation
                    bw.write("@SP\n");
                    bw.write("M=M+1\n"); //Increment the stack pointer.
                    break;

                case "static": //Push the value from static at place index onto the stack.
                    bw.write("// push static " + index + "\n");
                    bw.write("@" + currentFileName + "." + index + "\n"); //Using file name for prefix.
                    bw.write("D=M\n"); //Gets the value at the index.
                    bw.write("@SP\n");
                    bw.write("A=M\n"); //Stack's Top value.
                    bw.write("M=D\n"); //Push operation
                    bw.write("@SP\n");
                    bw.write("M=M+1\n"); //Increment the stack pointer.
                    break;

                default:
                    throw new IllegalArgumentException("Invalid segment: " + segment);
            }
            //Condition for a pop command
        } else if (command.equals("C_POP")) {
            switch (segment) {
                case "local":
                case "argument":
                case "this":
                case "that":
                    bw.write("// pop " + segment + " " + index + "\n");
                    bw.write("@" + index + "\n"); //Loading the index.
                    bw.write("D=A\n"); //Store it on D.
                    bw.write("@" + getSegmentP(segment) + "\n"); //Using the helper function.
                    bw.write("D=D+M\n"); // Compute base + index in D
                    bw.write("@R13\n");
                    bw.write("M=D\n"); // Store computed address in R13
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n"); //Decrement the stack pointer.
                    bw.write("D=M\n"); //Value that is going to be popped.
                    bw.write("@R13\n");
                    bw.write("A=M\n"); //Our target.
                    bw.write("M=D\n"); //Storing.
                    break;

                case "temp": //pop the value from stack into temp at place index.
                    bw.write("// pop temp " + index + "\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n"); //Decrement the stack pointer.
                    bw.write("D=M\n"); //Storing.
                    bw.write("@" + (5 + index) + "\n"); //The temp segment start's at RAM[5] so we use it like that.
                    bw.write("M=D\n"); // Store at temp[index].
                    break;
                case "pointer"://pop the value from stack into this/that at place index.
                    bw.write("// pop pointer " + index + "\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n"); //Decrement the stack pointer.
                    bw.write("D=M\n"); //Storing.
                    bw.write("@" + (3 + index) + "\n"); //The this/that segment start's at RAM[3] so we use it like that.
                    bw.write("M=D\n"); // Store at pointer[index].
                    break;

                case "static": //pop the value from stack into static at place index.
                    bw.write("// pop static " + index + "\n");
                    bw.write("@SP\n");
                    bw.write("AM=M-1\n"); //Decrement the stack pointer.
                    bw.write("D=M\n"); //Storing.
                    bw.write("@" + currentFileName + "." + index + "\n"); //Use file name as prefix.
                    bw.write("M=D\n"); // Store at static[index].
                    break;
                default:
                    throw new IllegalArgumentException("Invalid segment: " + segment);
            }
        } else {
            throw new IllegalArgumentException("Invalid command: " + command);
        }
    }
    /**
     * Writes assembly code that effects the label command.
     * @param label as the input string.
     */
    public void Writelabel(String label) {
        try {
            bw.write("// label " + label + "\n");
            bw.write("(" + currentFunction + "$" + label + ")\n");
        } catch (IOException e) {
            throw new RuntimeException("Error writing label: " + label, e);
        }
    }
    /**
     * Writes assembly code that effects the goto command.
     * @param label label as the input string.
     */
    public void WriteGoto(String label) {
        try {
            bw.write("// goto " + label + "\n");
            bw.write("@" + currentFunction + "$" + label + "\n"); //As the needed label.
            bw.write("0;JMP\n"); //An unconditional jump.
        } catch (IOException e) {
            throw new RuntimeException("Error writing goto: " + label, e);
        }
    }
    /**
     * Writes assembly code that effects the if-goto command.
     * @param label as the input string.
     */
    public void WriteIf(String label) {
        try {
            bw.write("// if-goto " + label + "\n");
            bw.write("@SP\n");
            bw.write("AM=M-1\n"); //We are decrementing the stack pointer for reaching the top value.
            bw.write("D=M\n"); //By that we are storing the current value at D.
            bw.write("@" + currentFunction + "$" + label + "\n"); //As the needed label.
            bw.write("D;JNE\n"); //if not zero means the boolean got back true.
        } catch (IOException e) {
            throw new RuntimeException("Error writing if-goto: " + label, e);
        }
    }
    /**
     * Writes assembly code that effects the function command.
     * @param functionName as the function string in a name.
     * @param nVars as an integer obtaining the number of variables the function gets.
     */
    public void WriteFunction(String functionName, int nVars) {
        try {
            currentFunction = functionName; //Updating regarding the input.
            bw.write("// function " + functionName + " " + nVars + "\n");
            bw.write("(" + functionName + ")\n");
            //initializing all the local variables nVars times.
            for (int i = 0; i < nVars; i++) {
                bw.write("@0\n"); //Start initializing.
                bw.write("D=A\n");
                bw.write("@SP\n");
                bw.write("A=M\n");
                bw.write("M=D\n"); //Push onto the stack
                bw.write("@SP\n");
                bw.write("M=M+1\n"); //Incrementing SP.
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing function: " + functionName, e);
        }
    }

    /**
     * Writes assembly code that effects the call command.
     * @param functionName
     * @param nArgs
     */
    public void WriteCall(String functionName, int nArgs) {
        //We will start by defining a string that will hold the return label and the label counter.
        String labeltoreturn = "RETURN" + labelCounter++;
        try {
            bw.write("// call " + functionName + " " + nArgs + "\n");
            // Handles Address
            bw.write("@" + labeltoreturn + "\n");
            bw.write("D=A\n");
            bw.write("@SP\n");
            bw.write("A=M\n");
            bw.write("M=D\n"); //Push onto the stack
            bw.write("@SP\n");
            bw.write("M=M+1\n"); //Incrementing SP.
            // Handles LCL
            bw.write("@LCL\n");
            bw.write("D=M\n");
            bw.write("@SP\n");
            bw.write("A=M\n");
            bw.write("M=D\n"); //Push onto the stack
            bw.write("@SP\n");
            bw.write("M=M+1\n"); //Incrementing SP.
            // Handles ARG
            bw.write("@ARG\n");
            bw.write("D=M\n");
            bw.write("@SP\n");
            bw.write("A=M\n");
            bw.write("M=D\n"); //Push onto the stack
            bw.write("@SP\n");
            bw.write("M=M+1\n"); //Incrementing SP.
            // Handles THIS
            bw.write("@THIS\n");
            bw.write("D=M\n");
            bw.write("@SP\n");
            bw.write("A=M\n");
            bw.write("M=D\n"); //Push onto the stack
            bw.write("@SP\n");
            bw.write("M=M+1\n"); //Incrementing SP.
            //Handles THAT
            bw.write("@THAT\n");
            bw.write("D=M\n");
            bw.write("@SP\n");
            bw.write("A=M\n");
            bw.write("M=D\n"); //Push onto the stack
            bw.write("@SP\n");
            bw.write("M=M+1\n"); //Incrementing SP.
            //Reposition to all of the above after pushing.
            //ARG.
            bw.write("@SP\n");
            bw.write("D=M\n");
            bw.write("@" + (nArgs + 5) + "\n"); //Comes after the call frame, and that is why we add 5.
            bw.write("D=D-A\n");
            bw.write("@ARG\n");
            bw.write("M=D\n");
            //LCL.
            bw.write("@SP\n");
            bw.write("D=M\n");
            bw.write("@LCL\n");
            bw.write("M=D\n");
            //JMP to the function.
            bw.write("@" + functionName + "\n");
            bw.write("0;JMP\n");
            //Last step - where to return.
            bw.write("(" + labeltoreturn + ")\n");
        } catch (IOException e) {
            throw new RuntimeException("Error writing call: " + functionName, e);
        }
    }
    /**
     * Writes assembly code that effects the return command.
     * We will replace the arguments that the caller pushed with the value returned by the callee.
     * recycle the memory used by the callee.
     * restore the caller's segment pointers and jump to thr return address.
     */
    public void WriteReturn() {
        try {
            bw.write("// return\n");
            //Storing endFrame = LCL.
            bw.write("@LCL\n");
            bw.write("D=M\n");
            bw.write("@R13\n"); //Temp storage.
            bw.write("M=D\n");
            //Storing retAddr = *(endFRAME -5).
            bw.write("@5\n");
            bw.write("A=D-A\n");
            bw.write("D=M\n");
            bw.write("@R14\n"); //Temp storage.
            bw.write("M=D\n");
            //Repositioning *ARG = pop().
            bw.write("@SP\n");
            bw.write("AM=M-1\n");
            bw.write("D=M\n");
            bw.write("@ARG\n");
            bw.write("A=M\n");
            bw.write("M=D\n");
            //Restore sp = ARG + 1.
            bw.write("@ARG\n");
            bw.write("D=M+1\n");
            bw.write("@SP\n");
            bw.write("M=D\n");
            //Restoring THAT : *(endFrame - 1)
            bw.write("@R13\n"); //Temp storage.
            bw.write("AM=M-1\n");
            bw.write("D=M\n");
            bw.write("@THAT\n");
            bw.write("M=D\n");
            //restoring THIS : *(endFrame - 2)
            bw.write("@R13\n"); //Temp storage.
            bw.write("AM=M-1\n");
            bw.write("D=M\n");
            bw.write("@THIS\n");
            bw.write("M=D\n");
            //Restore ARG : *(endFrame - 3)
            bw.write("@R13\n"); //Temp storage.
            bw.write("AM=M-1\n");
            bw.write("D=M\n");
            bw.write("@ARG\n");
            bw.write("M=D\n");
            //Restore LCL : *(endFrame - 4)
            bw.write("@R13\n"); //Temp storage.
            bw.write("AM=M-1\n");
            bw.write("D=M\n");
            bw.write("@LCL\n");
            bw.write("M=D\n");
            //Last step : go to retAddr.
            bw.write("@R14\n"); //Temp storage.
            bw.write("A=M\n");
            bw.write("0;JMP\n");
        } catch (IOException e) {
            throw new RuntimeException("Error write return: " + currentFunction, e);
        }
    }

    /**
     * Forcing the assembly code generated by the VM translator to start with a specific and needed code.
     */
    public void writeBootstrap() {
        try {
            bw.write("// Bootstrap code\n");
            // Setting SP to 256.
            bw.write("@256\n");
            bw.write("D=A\n");
            bw.write("@SP\n");
            bw.write("M=D\n");
            //After setting SP we'll call Sys.init.
            WriteCall("Sys.init", 0);
        } catch (IOException e) {
            throw new RuntimeException("Error writing the bootstrap code: " + currentFunction, e);
        }
    }
    /**
     * Closes the output file.
     *
     * @throws IOException if there is an error closing the file.
     */
    @Override
    public void close() throws IOException {
        if (bw != null) {
            bw.close();
        }
    }
}