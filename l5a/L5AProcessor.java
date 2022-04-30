package lava5.l5a;

/**
* The L5AProcessor is the motherboard of the computer.  It needs a wrapper program, either
* on the command-line, or a windows program to interact with it.
*
* This has 6 registers.  The result of the last operation will be in A. If there is an error,
* it will display the ERR code (32768).
*
* To pass data in, set the IN register to the value, which is public.
* Then set the D register to the command to perform. Use PUSHC to pass the value on to the stack.
*/
public class L5AProcessor {
	public final static String MODEL="L5A";
	public final static char ERR=(char)32768;
	public final static char NIL=(char)0;
	public final static char M1=(char)65535;

	//opcodes
	public final static char CLS=(char)256;	//Clear Stack - non standard opcode
	public final static char PUSHC=(char)257;	//Push char onto stack
	public final static char IKM1=(char)2;
	public final static char IK0=(char)3;
	public final static char IK1=(char)4;
	public final static char IK2=(char)5;
	public final static char POP=(char)0x57;
	public final static char DUP=(char)0x59;
	public final static char SWAP=(char)0x5f;
	public final static char IADD=(char)0x60;
	public final static char ISUB=(char)0x64;
	public final static char IMUL=(char)0x68;
	public final static char IDIV=(char)0x6c;
	public final static char IREM=(char)0x70;
	public final static char INEG=(char)0x74;

	public static int fromChar(char c) {
		int a = (int)c;
		if (a>32767) {
			a=a-65536;
		}
		return a;
	}

	/**
	* The result is unpredictable if the int is out of range
	*/
	public static char toChar(int i) {
		if (i>65535 || i<-32768) {
			System.out.println("WARNING: "+i+" is out of range");
		}
		if (i<0) {
			i=i+65536;
		}
		return (char)i;
	}

	/**
	* This has 6 registers, A,B,C,D,E and IN.
	* A is top of stack
	* B is next to top
	* C is bottom of stack
	* D is the next instruction to execute.
	* E is the last instruction that was executed, for informational purposes.
	* IN is input from user
	*/
	private char A;
	private char B;
	private char C;
	/**
	* The D register contains the next command to perform
	*/
	public char D;
	private char E;

	/** The IN register can be directly modified from outside the computer.
	*/
	public char IN;

	public L5AProcessor() {}

	public char readA() {return A;}
	public char readB() {return B;}
	public char readC() {return C;}
	public char readD() {return D;}
	public char readE() {return E;}

	private void PUSH(char v) {
		C=B;
		B=A;
		A=v;
		//whatever was on registerC gets discarded
	}

	private char POP() {
		char v=A;
		A=B;
		B=C;
		C=NIL;
		return v;
	}



	//==============================
	/**
	* Execute is the ControlUnit.  Instead of running in a loop, like on more powerful computers,
	* this only runs 1 step at a time - the command in the D register.
	* The result of the operation will go onto A.
	*/
	public void execute() {
		switch (D) {
			case NIL: break; //do nothing
			case CLS: clearStack(); break;
			case PUSHC: pushChar(); break;
			case IKM1: PUSH((char)-1); break;
			case IK0: PUSH((char)0); break;
			case IK1: PUSH((char)1); break;
			case IK2: PUSH((char)2); break;
			case POP: POP(); break;
			case DUP: PUSH(A); break;
			case SWAP: swap(); break;
			case IADD: ADD(); break;
			case ISUB: SUB(); break;
			case IMUL: MUL(); break;
			case IDIV: DIV(); break;
			case IREM: REM(); break;
			case INEG: NEG(); break;
			default:
				System.out.println("unknown opcode "+(int)D);
		}
		E = D;
		D = NIL;
	}


	//==============================

	/**
	* Op CLS: Clear stack
	*/
	private void clearStack() {
		A = NIL;
		B = NIL;
		C = NIL;
		D = NIL;
		E = NIL;
	}

	/**
	* Op PUSHC: push a char onto stack
	*/
	private void pushChar() {
		PUSH(IN);
		IN = NIL;
	}

	/**
	* SWAP:
	* Swaps top two words on the stack
	*/
	private void swap() {
		char tmp = A;
		A = B;
		B = tmp;
	}

	/**
	* Op IADD
	* 0x60;	96
	*/
	public void ADD() {
		int b = fromChar(POP());
		int a = fromChar(POP());
		PUSH( toChar(a + b));
	}

	/**
	* Op ISUB
	* 0x64; 100
	*/
	public void SUB() {
		int b = fromChar(POP());
		int a = fromChar(POP());
		PUSH(toChar(a - b));
	}

	/**
	* Op IMUL
	* 0x68;	104
	*/
	public void MUL() {
		int b = fromChar(POP());
		int a = fromChar(POP());
		PUSH(toChar(a * b));
	}

	/**
	* Op IDIV
	* 0x6c;	108
	*/
	public void DIV() {
		int b = fromChar(POP());
		int a = fromChar(POP());
		PUSH(toChar(a / b));
	}

	/**
	* Op IREM
	* 0x70;	112
	*/
	public void REM() {
		int b = fromChar(POP());
		int a = fromChar(POP());
		PUSH(toChar(a % b));
	}

	/**
	* Op INEG
	* 0x74;	116
	*/
	public void NEG() {
		int a = fromChar(POP());
		PUSH(toChar(0 - a));
	}
}