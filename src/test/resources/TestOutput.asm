//add
@SP
AM=M-1
D=M
@SP
A=M-1
M=M+D
//sub
@SP
AM=M-1
D=M
@SP
A=M-1
M=M-D
//neg
@SP
A=M-1
M=-M
//eq
@SP
AM=M-1
D=M
@SP
A=M-1
D=M-D
@TRUE0
D;JEQ
@SP
A=M-1
M=0
@END0
0;JMP
(TRUE0)
@SP
A=M-1
M=-1
(END0)
//gt
@SP
AM=M-1
D=M
@SP
A=M-1
D=M-D
@TRUE1
D;JGT
@SP
A=M-1
M=0
@END1
0;JMP
(TRUE1)
@SP
A=M-1
M=-1
(END1)
//lt
@SP
AM=M-1
D=M
@SP
A=M-1
D=M-D
@TRUE2
D;JLT
@SP
A=M-1
M=0
@END2
0;JMP
(TRUE2)
@SP
A=M-1
M=-1
(END2)
//and
@SP
AM=M-1
D=M
@SP
A=M-1
M=M&D
//or
@SP
AM=M-1
D=M
@SP
A=M-1
M=M|D
//not
@SP
A=M-1
M=!M
// push constant 10
@10
D=A
@SP
A=M
M=D
@SP
M=M+1
// push local 2
@2
D=A
@ LCL
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
// push argument 3
@3
D=A
@ ARG
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
// push this 1
@1
D=A
@ THIS
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
// push that 4
@4
D=A
@ THAT
A=M+D
D=M
@SP
A=M
M=D
@SP
M=M+1
// push temp 6
@11
D=M
@SP
A=M
M=D
@SP
M=M+1
// push pointer 0
@3
D=M
@SP
A=M
M=D
@SP
M=M+1
// push pointer 1
@4
D=M
@SP
A=M
M=D
@SP
M=M+1
// push static 7
@7
D=M
@SP
A=M
M=D
@SP
M=M+1
// pop local 2
@2
D=A
@ LCL
A=M+D
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// pop argument 3
@3
D=A
@ ARG
A=M+D
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// pop this 1
@1
D=A
@ THIS
A=M+D
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// pop that 4
@4
D=A
@ THAT
A=M+D
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// pop temp 6
@SP
AM=M-1
D=M
@11
M=D
// pop pointer 0
@SP
AM=M-1
D=M
@3
M=D
// pop pointer 1
@SP
AM=M-1
D=M
@4
M=D
// pop static 7
@SP
AM=M-1
D=M
@static7
M=D
