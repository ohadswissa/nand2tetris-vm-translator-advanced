# Advanced VM Translator – Nand2Tetris Project 8

This project is part of the [Nand2Tetris](https://www.nand2tetris.org/) course.  
It implements the **second stage** of a full Virtual Machine Translator in Java, supporting complete program control, function calls, and bootstrap initialization.

---

## 🛠 Features

- Translates `.vm` code to Hack Assembly (`.asm`)
- Supports:
  - Arithmetic and memory access commands
  - Flow control: `label`, `goto`, `if-goto`
  - Function calls: `function`, `call`, `return`
  - Bootstrap initialization code
- Written in modular Java using Maven
- Includes unit tests and example `.vm` test programs

---

## 📂 Folder Structure

- `src/main/java/vmtranslator/VMTranslator.java` – Entry point  
- `src/main/java/vmtranslator/Parser.java` – Parses VM commands  
- `src/main/java/vmtranslator/CodeWriter.java` – Generates Hack assembly output  
- `src/test/java/vmtranslator/TestParser.java` – Unit tests for parsing logic  
- `src/test/java/vmtranslator/TestCodeWriter.java` – Unit tests for code generation  
- `src/test/resources/` – Sample `.vm` input files and expected output  
- `pom.xml` – Maven build file  
- `.gitignore` – Ignored files (compiled classes, IDE junk)

---

## ▶️ How to Run

Compile and run using Maven:

```bash
mvn compile
mvn exec:java -Dexec.mainClass="vmtranslator.VMTranslator" -Dexec.args="src/test/resources/FunctionTest.vm"
```
## 🧪 Example Input (FunctionTest.vm)
```
function SimpleFunction.test 2
push constant 3
push constant 4
add
pop local 0
return
```

➡️ Output (snippet)
```
(SimpleFunction.test)
@SP
D=M
@LCL
M=D
```

## 👨‍💻 Author

Ohad Swissa
Honors Student – Computer Science & Entrepreneurship
Ex-IDF Special Forces Major | Problem Solver
[LinkedIn](https://www.linkedin.com/in/ohad-swissa-54728a2a6)
