package vmtranslator;
import java.io.*;
import java.lang.*;
/**
 * Handles the parsing of a single .vm file.
 * Reads a VM command, parses the command into lexical components and provides convenient access to these components.
 * Ignores white space and comments.
 */
public class Parser {
    private BufferedReader reader;
    private String CurrentCommand;
    /**
     * Opens the input file/stream, and gets ready to parse it.
     *
     * @param inputFile will be the path to the '.vm' file.
     * @throws FileNotFoundException if the file isn't found.
     */
    public Parser(String inputFile) throws FileNotFoundException {
        //we will initialize the file reader we declare before and set thr Current instruction to null.
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            CurrentCommand = null;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File not found: " + inputFile);
        }
    }
    /**
     * Checks if there are more lines un the input
     *
     * @return true/false.
     */
    public boolean hasMoreCommands() throws IOException {
        return reader.ready(); //checks if the reader has more data to read.
    }
    /**
     * Reads the next command from the input file and makes it the current command.
     * This method should be called only if hasMoreCommands() is true.
     * Initially, there is no current command.
     */
    public void advance() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            //Removes things after '//' and trims white spaces.
            line = line.split("//")[0].trim();
            if (!line.isEmpty()) {
                CurrentCommand = line; //Sets the CurrentCommand and finishing the current advancing.
                break;
            }
        }
    }
    /**
     * Returns the current command that was most recently read by advance().
     *
     * @return the current command as a String
     */
    public String getCurrentCommand() {
        return CurrentCommand; // Return the current command
    }
    /**
     * Returns a constant representing the type of the current command. if the current command is an arithmetic logical command, returns C_ARITHMETIC.
     *
     * @return the command type as a String (e.g., "C_PUSH", "C_ARITHMETIC", "C_GOTO" etc.).
     */
    public String commandType() {
        if (CurrentCommand.startsWith("push")) {
            return "C_PUSH";
        }
        if (CurrentCommand.startsWith("pop")) {
            return "C_POP";
        }
        if (CurrentCommand.matches("add|sub|neg|eq|gt|lt|and|or|not")) {
            return "C_ARITHMETIC";
        }
        if (CurrentCommand.startsWith("label")) {
            return "C_LABEL";
        }
        if (CurrentCommand.startsWith("goto")) {
            return "C_GOTO";
        }
        if (CurrentCommand.startsWith("if-goto")) {
            return "C_IF";
        }
        if (CurrentCommand.startsWith("call")) {
            return "C_CALL";
        }
        if (CurrentCommand.startsWith("function")) {
            return "C_FUNCTION";
        }
        if (CurrentCommand.startsWith("return")) {
            return "C_RETURN";
        }
        return null;
    }
    /**
     * Returns the first argument of the current command.
     * In the case of C_ARITHMETIC, the command itself (e.g., "add") is returned.
     * Should not be called if the current command is C_RETURN.
     *
     * @return the first argument as a string.
     * @throws IllegalStateException if called when the command isn't valid.
     */
    public String arg1() {
        String type = commandType();
        if (type == null) {
            throw new IllegalStateException("Command not defined");
        }
        if (type.equals("C_ARITHMETIC")) {
            return CurrentCommand;
        }
        if (type.equals("C_RETURN")) {
            throw new IllegalStateException("arg1() should not be called for 'C_RETURN'");
        }
        String[] split = CurrentCommand.split(" ");
        if (split.length < 2) {
            throw new IllegalArgumentException("Command not defined: " + CurrentCommand);
        }
            return split[1].trim();
    }
    /**
     * Returns the second argument of the current command.
     * Should be called only if the current command is C_PUSH, C_POP, C_FUNCTION or C_CALL.
     *
     * @return the second argument as an int
     * @throws IllegalStateException if the command type does not support arg2.
     */
    public int arg2() {
        String type = commandType();
        if (type.equals("C_PUSH") || type.equals("C_POP") || type.equals("C_FUNCTION") || type.equals("C_CALL")) {
            try {
                return Integer.parseInt(CurrentCommand.split(" ")[2].trim());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
                throw new IllegalStateException("Invalid or missing 2nd argument for args2: " + CurrentCommand);
            }
        }
        //If the Command type is unsupported.
        throw new IllegalStateException("Command type does not support args2: " + CurrentCommand);
    }
    public void close() {
        // Close any resources
    }
}
