/*
Copyright (C) 2016  Davidson Francis <davidsondfgl@gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/
import java.util.*;
import java.io.*;

public class MSW
{
	public static boolean isRegister(String reg)
	{
		return (reg.equals("ax") || reg.equals("bx") || reg.equals("cx") || reg.equals("dx") );
	}

	public static String getCodeReg(String reg)
	{
		String code = "";
		if(reg.equals("ax"))
			code = "00";
		else if(reg.equals("bx"))
			code = "01";
		else if(reg.equals("cx"))
			code = "10";
		else if(reg.equals("dx"))
			code = "11";

		return code;
	}

	public static boolean isImmediateValid(String imm)
	{
		boolean valid = false;

		try
		{
			int im = Integer.parseInt(imm);

			if(im < 0 || im > 254)
				valid = false;
			else
				valid = true;
		}
		catch(NumberFormatException nfe)
		{
			System.out.println("> Error, register and/or immediate value doesnt exist!");
			System.exit(0);
		}

		return valid;
	}

	public static String encodeALU(String inst1, String inst2, String opcode, String[]args, int num)
	{
		String hex = "";
		if( args.length == 2 && isRegister(args[0]) )
		{
			if(isRegister(args[1]))
			{
				int regsTmp = Integer.parseInt(getCodeReg(args[0]) + getCodeReg(args[1]), 2);
				String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
				hex = inst1 + regs + "ff";
			}
			else
			{
				if(isImmediateValid(args[1]))
				{
					int regsTmp = Integer.parseInt(getCodeReg(args[0]) + "00", 2);
					String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
					String imm = Integer.toString(Integer.parseInt(args[1]),16);
					if(imm.length() == 1)
						imm = "0"+imm;

					hex = inst2 + regs + imm;
				}
				else
				{
					System.out.println(num+": "+opcode+" "+args[0]+",¿"+args[1]+"?");
					System.out.println("> Error, immediate value is out of limits: 0-254!");
					System.exit(0);
				}
			}
		}
		else
		{
			System.out.println(num+": ¿"+opcode+" "+args[0]+"?");
			System.out.println("> Error, instruction has invalid arguments!");
			System.exit(0);
		}

		return hex;
	}

	public static String encode(String opcode, String[] args,int num,Map<String, Integer> tabLabels)
	{
		String hex = "";
		opcode = opcode.toLowerCase();
		if(args.length > 1)
		{
			args[0] = args[0].toLowerCase();
			args[1] = args[1].toLowerCase();
		}
		else
		{
			args[0] = args[0].toLowerCase();
		}

		switch(opcode)
		{
			case "or":
			hex = encodeALU("0","0","OR",args,num);
			break;
			case "not":
			if(args.length == 1 && isRegister(args[0]))
			{
				int regsTmp = Integer.parseInt(getCodeReg(args[0]) + "00", 2);
				String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
				hex = "1" + regs + "ff";
			}
			else
			{
				System.out.println(num+": ¿"+opcode+" "+args[0]+"?");
				System.out.println("> Error, instruction has invalid registers!");
				System.exit(0);
			}
			break;
			case "and":
			hex = encodeALU("2","2","AND",args,num);
			break;
			case "xor":
			hex = encodeALU("3","3","XOR",args,num);
			break;
			case "add":
			hex = encodeALU("4","4","ADD",args,num);
			break;
			case "sub":
			hex = encodeALU("5","5","SUB",args,num);
			break;
			case "mult":
			hex = encodeALU("6","6","MULT",args,num);
			break;
			case "div":
			hex = encodeALU("7","7","DIV",args,num);
			break;
			case "mov":
			hex = encodeALU("8","9","MOV",args,num);
			break;
			case "load":
			if(args.length == 2 && isRegister(args[0]) && isRegister(args[1]) )
			{
				int regsTmp = Integer.parseInt(getCodeReg(args[0]) + getCodeReg(args[1]), 2);
				String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
				hex = "a" + regs + "ff";
			}
			else
			{
				System.out.println(num+": ¿LOAD?");
				System.out.println("> Error, instruction has invalid arguments, its correct form: STORE REGDest, REGSource");
				System.exit(0);
			}
			break;
			case "store":
			if(args.length == 2 && isRegister(args[0]) && isRegister(args[1]) )
			{
				int regsTmp = Integer.parseInt(getCodeReg(args[0]) + getCodeReg(args[1]), 2);
				String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
				hex = "b" + regs + "ff";
			}
			else
			{
				System.out.println(num+": ¿STORE?");
				System.out.println("> Error, instruction has invalid arguments, its correct form: STORE REGDest, REGSource");
				System.exit(0);
			}
			break;
			case "jmpz":
			if(args.length == 1)
			{
				if(isRegister(args[0]))
				{
					int regsTmp = Integer.parseInt("00" + getCodeReg(args[0]), 2);
					String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
					hex = "c" + regs + "ff";
				}
				else
				{
					if(tabLabels.containsKey(args[0]))
					{
						String imm = Integer.toString(tabLabels.get(args[0]),16);
						if(imm.length() == 1)
							imm = "0"+imm;

						hex = "c" + "0" + imm;
					}
					else
					{
						System.out.println(num+": ¿JMPZ "+args[0]+"?");
						System.out.println("> Error, label doesnt exist, check your code!");
						System.exit(0);
					}
				}
			}
			else
			{
				System.out.println("> Error, instruction has invalid registers!");
				System.exit(0);
			}
			break;
			case "jmpn":
			if(args.length == 1)
			{
				if(isRegister(args[0]))
				{
					int regsTmp = Integer.parseInt("00" + getCodeReg(args[0]), 2);
					String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
					hex = "d" + regs + "ff";
				}
				else
				{
					if(tabLabels.containsKey(args[0]))
					{
						String imm = Integer.toString(tabLabels.get(args[0]),16);
						if(imm.length() == 1)
							imm = "0"+imm;

						hex = "d" + "0" + imm;
					}
					else
					{
						System.out.println(num+": ¿JMPN "+args[0]+"?");
						System.out.println("> Error, label doesnt exist, check your code!");
						System.exit(0);
					}
				}
			}
			else
			{
				System.out.println("> Error, instruction has invalid registers!");
				System.exit(0);
			}
			break;
			case "jmpp":
			if(args.length == 1)
			{
				if(isRegister(args[0]))
				{
					int regsTmp = Integer.parseInt("00" + getCodeReg(args[0]), 2);
					String regs = (regsTmp > 9) ? ""+((char)(regsTmp+87)) : ""+regsTmp;
					hex = "e" + regs + "ff";
				}
				else
				{
					if(tabLabels.containsKey(args[0]))
					{
						String imm = Integer.toString(tabLabels.get(args[0]),16);
						if(imm.length() == 1)
							imm = "0"+imm;

						hex = "e" + "0" + imm;
					}
					else
					{
						System.out.println(num+": ¿JMPP "+args[0]+"?");
						System.out.println("> Error, label doesnt exist, check your code!");
						System.exit(0);
					}
				}
			}
			else
			{
				System.out.println("> Error, instruction has invalid registers!");
				System.exit(0);
			}
			break;
			case "halt":
			hex = "f000";
			break;
			default:
			System.out.println("> ErRoR: I Give UP");
			System.exit(0);
			break;
		}

		return hex;
	}

	public static void processar(String file)
	{
		RandomAccessFile asm,hex;
		Map<String, Integer> tabLabels = new HashMap<String, Integer>(); 

		try
		{  
			asm = new RandomAccessFile(new File(file),"r");  
			hex = new RandomAccessFile(new File(file+".hex"),"rw");
			hex.setLength(0);

			hex.write( "v2.0 raw\n0000 ".getBytes() );

			String line = "";

			/* Scan for labels. */
			int lineCount = 1;
			while(asm.getFilePointer() < asm.length())
			{
				line = asm.readLine().trim();

				if(line != null && !line.equals("") && line.charAt(0) != '#')
				{
					boolean isLabel = (line.charAt(line.length()-1) == ':') ? true : false;

					if(isLabel)
					{
						String[] tokens = line.split("\\s+");
						if(tokens.length == 1)
						{
							tabLabels.put(line.substring(0,line.length()-1),lineCount);
						}
						else
						{
							System.out.println("> Error in '"+line+"', label cannot contain spaces!");
							System.exit(0);
							break;
						}
					}
					else
					{
						lineCount++;
					}
				}
			}

			/* Scans in decoding instructions. */
			asm.seek(0);
			lineCount = 1;
			int instCount = 1;
			while(asm.getFilePointer() < asm.length())
			{
				line = asm.readLine().trim();
				
				if(line != null && !line.equals("") && line.charAt(0) != '#')
				{	
					boolean isLabel = (line.charAt(line.length()-1) == ':') ? true : false;

					if(!isLabel)
					{
						if(line.indexOf("#") != -1)
						{
							int start = line.indexOf("#");
							int end = line.length() - start + 1;

							line = (""+new StringBuffer(line).delete(start,start+end)).trim();
						}

						String[] tokens = line.split("\\s+");

						String inst = tokens[0];
						String[] ops = (""+new StringBuffer(line).delete(0,0+tokens[0].length())).replaceAll("\\s+","").split(",+");

						String instruction = encode(tokens[0],ops,lineCount,tabLabels);
						hex.write( (instruction+" ").getBytes() );

						System.out.println("Instruction: {"+instruction+"} --- "+line);

						instCount++;
					}
				}
				lineCount++;
			}

			System.out.println("------------------------------------------------------------------");
			System.out.println("'Assembling' successfully done, memory consumption: "+(instCount*2)+"bytes");
			System.out.println("Have a nice day, :D");
			System.out.println("------------------------------------------------------------------");

			asm.close();
			hex.close();
		}  
		catch (FileNotFoundException fnfe)
		{  
			System.out.println("Input file does not exist, check the entered name!");  
			System.exit(0);  
		}
		catch (IOException ioe)
		{
			System.out.println("I/O error!");
			ioe.printStackTrace();
			System.exit(0);
		}
	}


	public static void main(String[]args)
	{
		Scanner in = new Scanner(System.in);

		System.out.println("-----------------------------------------------");
		System.out.println(" MSW 16bits Assembler v1.0 by Davidson Francis");
		System.out.println("-----------------------------------------------");

		System.out.println("Enter the file name to be processed: ");
		String file = in.next();

		processar(file);
	}
}
