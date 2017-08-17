### MSW - Machine with Shuffled Wires
MSW is a CPU 16-bit, RISC, Unicycle, Harvard, built in [Logisim](http://www.cburch.com/logisim/).
It was developed in order to be as simple as possible, so also does not have pipeline, branch prediction, cache, among others
features.
<p align="center">
<img align="center" src="http://i.imgur.com/XRXsxBN.png" alt="msw overview">
</p>
#### Architecture
Inspired by the 8086 Intel and MIPS, but much simpler, its instruction set has 16 instructions and similar registers with Intel. Its general architecture looks like a MIPS unicycle.

It is divided into 4 parts:
- ALU - Arithmetic Logic Unit
- UControl - Unity Control, responsible for creating control flags throughout the CPU, according to the instruction currently running.
- BRegs - Bank registers
- CPU - With the full assembly of the other modules.

#### Instruction Set
The MSW's instructions follows the format below:

	 OpCode      R1   R2              Imm
	/-------\    /-\ /-\     /------------------\    - Immediate instructions.
 	 0 0 0 0     0 0 0 0     0 0 0 0      0 0 0 0

and

	OpCode      R1   R2           Constant
	/-------\    /-\ /-\     /------------------\    - Only registers.
	 0 0 0 0     0 0 0 0     1 1 1 1      1 1 1 1

Needless to say, supports 16 instructions and 4 registers: AX, BX, CX and DX. <br />
\* Due the use of 0x00FF to identify Register-only instructions, you **shouldn't** use 255 as immediate value.

The following table illustrates all instructions:

	|Op Code|Instruction|Behavior|
	|-------|-----------|--------|
	|0|OR REGDest,REGSource/Imm|REGDest <- REGDest \| REGSource|
	|1|NOT REGDest|REGDest <- ~REGDest|
	|2|AND REGDest,REGSource/Imm|REGDest <- REGDest & REGOri|
	|3|XOR REGDest,REGSource/Imm|REGDest <- REGDest ^ REGOri|
	|4|ADD REGDest,REGSource/Imm|REGDest <- REGDest + REGOri|
	|5|SUB REGDest,REGSource/Imm|REGDest <- REGDest - REGOri|
	|6|MULT REGDest,REGSource/Imm|REGDest <- REGDest * REGOri|
	|7|DIV REGDest,REGSource/Imm|REGDest <- REGDest / REGOri|
	|8|MOV REGDest, REGSource|REGDest <- REGSource|
	|9|MOV REGDest, Imm|REGDest <- Imm|
	|10|LOAD REGDest, REGSource|REGDest <- MEM[REGSource]|
	|11|STORE REGDest, REGSource|MEM[REGDest] <- REGSource|
	|12|JMPZ REGDest/Label|JuMP if Zero|
	|13|JMPN REGDest/Label|JuMP if Negative|
	|14|JMPP REGDest/Label|JuMP if Positive|
	|15|HALT|Halts CPU|
	
#### Assembler
In order to avoid programming in binary, it developed a Assembler, which takes a file as input and generates a hex file ready
to run and a code like below can be perfectly executed on the CPU:

	##################
	##FibonAsm, ^.^###
	##################

	#Calculation of Fibonacci

	mov ax,1   #i
	xor bx,bx  #j
	xor cx,cx  #<-- mem
	mov dx,14  #counter

	fibo:
		add   ax,bx  #i=i+j
		store cx,ax  #mem[0] = i
		mov   ax,bx  #i=j
		load  bx,cx  #j=mem[0]
		sub   dx,1   #count=count-1
		jmpp  fibo
	halt

	#Result in bx

The assembler is case insensitive for instruction names, supports labels and comments.

To run the generated program (.hex), just load it to the leftmost memory and activate the clock. 
[Here](https://www.youtube.com/watch?v=QfH0QN9yPt8) is a video of the CPU
in operation (Portuguese).

#### What comes next?

This is my first CPU and I believe there are some design issues in both the CPU and the Assembler (not bugs, just the way
I developed).

So I don't intend to go far with it, there are still some things I want to add such as stack and  I/O instructions, but
the ideal would be a re-work on a new CPU, very different from this first, starting with microcode addition eg.
***
That's it, if you have questions, suggestions or want to contribute, feel free to ask me, I would love hear from you.
