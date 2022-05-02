package lava5.l5c;

/**
* The L5CProcessor is the motherboard of the computer.  It needs a wrapper program, either
* on the command-line, or a windows program, to interact with it.
*
* The driving force behind this, the reason, is because of difficulties with floating point
* math.  Instead I am using fixed point math.
*
*/
public class L5CProcessor {
	public final static String MODEL="L5C";
	public final static char ERR=(char)32768;
	public final static char NIL=(char)0;
	//public final static char M1=(char)65535;

	//opcodes
	public final static char CLR=(char)256;	//Clear - non standard opcode
	//public final static char PUSHC=(char)257;	//Push char onto stack
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

	//L5B opcodes
	//public final static char I2F=(char)0x86;
	//public final static char F2I = (char)0x8b;
	public final static char FADD=(char)0x62;
	public final static char FDIV=(char)0x6e;
	public final static char FMUL=(char)0x6a;
	public final static char FNEG=(char)0x76;
	//public final static char FREM=(char)0x72;
	public final static char FSUB=(char)0x66;
	public final static char SQRT = (char)258;

	//L5C opcodes
	public final static char PUSHT=(char)259;	//push Triple onto stack
	public final static char FCONST_0	=(char)0x0b;	//	-> 0.0f	push 0.0f on the stack
	public final static char FCONST_1	=(char)0x0c;	//	-> 1.0f	push 1.0f on the stack
	public final static char FCONST_2	=(char)0x0d;	//	-> 2.0f	push 2.0f on the stack
	public final static char FLOAD_0	=(char)0x22;	//	-> value	load a float value from local variable 0
	public final static char FLOAD_1	=(char)0x23;	//	-> value	load a float value from local variable 1
	public final static char FLOAD_2	=(char)0x24;	//	-> value	load a float value from local variable 2
	//public final static char FLOAD_3	=(char)0x25;	//	-> value	load a float value from local variable 3
	public final static char FSTORE_0	=(char)0x43;	//	value ->	store a float value into local variable 0
	public final static char FSTORE_1	=(char)0x44;	//	value ->	store a float value into local variable 1
	public final static char FSTORE_2	=(char)0x45;	//	value ->	store a float value into local variable 2
	//public final static char FSTORE_3	=(char)0x46;	//	value ->	store a float value into local variable 3
	public final static char ILOAD_0	=(char)0x1a;	//	-> value	load an int value from local variable 0
	public final static char ILOAD_1	=(char)0x1b;	//	-> value	load an int value from local variable 1
	public final static char ILOAD_2	=(char)0x1c;	//	-> value	load an int value from local variable 2
	//public final static char ILOAD_3	=(char)0x1d;	//	-> value	load an int value from local variable 3
	public final static char ISTORE_0	=(char)0x3b;	//	value ->	store int value into variable 0
	public final static char ISTORE_1	=(char)0x3c;	//	value ->	store int value into variable 1
	public final static char ISTORE_2	=(char)0x3d;	//	value ->	store int value into variable 2
	//public final static char ISTORE_3	=(char)0x3e;	//	value ->	store int value into variable 3

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
	* This has 8 registers: IN,OP,S0,S1,S2,A,B,C
	* IN is the input register
	* OP holds the next opcode to execute
	* S0..S2 are the stack
	* A..C are local variables
	*/

	//=============
	public long IN;
	public char OP;
	private long S0;
	private long S1;
	private long S2;
	private long A;
	private long B;
	private long C;
	//=============

	public L5CProcessor() {}

	public long readS0() {return S0;}
	public long readS1() {return S1;}
	public long readS2() {return S2;}
	public long readA() {return A;}
	public long readB() {return B;}
	public long readC() {return C;}

	/**
	* PUSH Triple onto stack
	*/
	private void PUSH(long v) {
		S2=S1;
		S1=S0;
		S0=v;
		//whatever was on registerC gets discarded
	}

	private long POP() {
		long v=S0;
		S0=S1;
		S1=S2;
		S2=0L;
		return v;
	}

	//==============================
	/**
	* Execute is the ControlUnit.  Instead of running in a loop, like on more powerful computers,
	* this only runs 1 step at a time - the command in the D register.
	* The result of the operation will go onto A.
	*/
	public void execute() {
		switch (OP) {
			case NIL: break; //do nothing
			case CLR: clear(); break;
			case IKM1: PUSH(Triple.intToTriple(-1)); break;
			case IK0:
			case FCONST_0: PUSH(0L); break;
			case IK1: PUSH(Triple.intToTriple(1)); break;
			case IK2: PUSH(Triple.intToTriple(2)); break;
			case POP: POP(); break;
			case DUP: PUSH(S0); break;
			case SWAP: swap(); break;
			case IADD:
			case FADD: ADD(); break;
			case ISUB:
			case FSUB: SUB(); break;
			case IMUL:
			case FMUL: MUL(); break;
			case IDIV: IDIV(); break;
			case FDIV: FDIV(); break;
			case IREM: IREM(); break;
			case INEG:
			case FNEG: NEG(); break;
			case SQRT: SQRT(); break;

			case PUSHT: pushTriple(); break;
			case FCONST_1: PUSH(Triple.floatToTriple(1.0f)); break;
			case FCONST_2: PUSH(Triple.floatToTriple(2.0f)); break;
			case ILOAD_0:
			case FLOAD_0: PUSH(A); break;
			case ILOAD_1:
			case FLOAD_1: PUSH(B); break;
			case ILOAD_2:
			case FLOAD_2: PUSH(C); break;
			case ISTORE_0:
			case FSTORE_0: A=POP(); break;
			case ISTORE_1:
			case FSTORE_1: B=POP(); break;
			case ISTORE_2:
			case FSTORE_2: C=POP(); break;
			default:
				System.out.println("unknown opcode "+(int)OP);
		}
		OP = NIL;
	}


	//==============================

	/**
	* Op CLR: Clear
	*/
	private void clear() {
		IN=0L;
		OP=(char)0;
		S0=0L;
		S1=0L;
		S2=0L;
		A=0L;
		B=0L;
		C=0L;
	}

	/**
	* SWAP:
	* Swaps top two words on the stack
	*/
	private void swap() {
		long tmp = S0;
		S0 = S1;
		S1 = tmp;
	}

	/**
	* Op IADD
	* 0x60;	96
	* I am using Fixed Point addition. Integer addition and float addition are identical
	*/
	private void ADD() {
		long b = POP();
		long a = POP();
		long c = TripleMath.ADD(a,b);
		PUSH( c );
	}

	/**
	* Op ISUB
	* 0x64; 100
	*/
	private void SUB() {
		long b = POP();
		long a = POP();
		long c = TripleMath.SUB(a,b);
		PUSH( c );
	}

	/**
	* Op IMUL
	* 0x68;	104
	*/
	private void MUL() {
		long b = POP();
		long a = POP();
		long c = TripleMath.MUL(a,b);
		PUSH( c );
	}

	/**
	* Op IDIV
	* 0x6c;	108
	*/
	private void IDIV() {
		long b = POP();
		long a = POP();
		long c = TripleMath.IDIV(a,b);
		PUSH( c );
	}

	/**
	* FDIV is different
	*/
	private void FDIV() {
		long b = POP();
		long a = POP();
		long c = TripleMath.FDIV(a,b);
		PUSH( c );
	}


	/**
	* Op IREM
	* 0x70;	112
	* Called IREM because FREM doesn't make sense
	*/
	private void IREM() {
		long b = POP();
		long a = POP();
		long c = TripleMath.IREM(a,b);
		PUSH( c );
	}

	/**
	* Op INEG
	* 0x74;	116
	*/
	private void NEG() {
		long a = POP();
		long c = TripleMath.NEG(a);
		PUSH( c );
	}

	private void SQRT() {
		long a = POP();
		long c = TripleMath.SQRT(a);
		PUSH( c );
	}

	private void pushTriple() {
		PUSH(IN);
		IN = 0L;
	}

}