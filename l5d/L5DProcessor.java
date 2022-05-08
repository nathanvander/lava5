package lava5.l5d;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.IOException;
import java.util.Stack;

/**
* The L5DProcessor is the motherboard of the computer.  It needs a wrapper program, either
* on the command-line, or a windows program, to interact with it.
*
* This version has a JIT formula parser.
* I disable the SQRT function to simplify this.
*
*/
public class L5DProcessor {
	public final static String MODEL="L5D";
	public L5DProcessor() {}
	//=================================
	//Registers.  I put these at the top so they are visible
	//there are only 8
	public String INPUT;
	private long S0;	//top of stack
	private long S1;
	private long S2;
	private long A;		//local variable A
	private long B;
	private long C;
	private int IP;		//instruction pointer, always 0 unless the program is running

	public long readS0() {return S0;}
	public long readS1() {return S1;}
	public long readS2() {return S2;}
	public long readA() {return A;}
	public long readB() {return B;}
	public long readC() {return C;}
	//=================================
	//Memory
	//This is used to temporarily hold the compiled program
	//We officially have a memory of 128 bytes
	private long[] memory = new long[32];

	//=================================
	/**
	* Op CLR: Clear everything
	*/
	public void clear() {
		INPUT="";
		S0=0L;
		S1=0L;
		S2=0L;
		A=0L;
		B=0L;
		C=0L;
	}
	//==================================
	//Stack functions
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
	/**
	* SWAP:
	* Swaps top two words on the stack
	*/
	private void SWAP() {
		long tmp = S0;
		S0 = S1;
		S1 = tmp;
	}
	//duplicate the top of stack
	private void DUP() {PUSH(S0);}
	//============================
	//now to define tokens in our language.
	//Everything is a long
	//OPs use the first char of the long and and optional arg in the 4th

	public final static long POP=0x0057_0000_0000_0000L;
	public final static long DUP=0x0059_0000_0000_0000L;
	public final static long SWAP=0x005f_0000_0000_0000L;

	public final static long FLOAD_0=0x0022_0000_0000_0000L;	//	-> value	load a float value from local variable 0
	public final static long FLOAD_1=0x0023_0000_0000_0000L;	//	-> value	load a float value from local variable 1
	public final static long FLOAD_2=0x0024_0000_0000_0000L;	//	-> value	load a float value from local variable 2
	public final static long FSTORE_0=0x0043_0000_0000_0000L;	//	value ->	store a float value into local variable 0
	public final static long FSTORE_1=0x0044_0000_0000_0000L;	//	value ->	store a float value into local variable 1
	public final static long FSTORE_2=0x0045_0000_0000_0000L;	//	value ->	store a float value into local variable 2

	public final static long FADD=0x0062_0000_0000_0000L;
	public final static long FDIV=0x006e_0000_0000_0000L;
	public final static long FMUL=0x006a_0000_0000_0000L;
	public final static long FNEG=0x0076_0000_0000_0000L;
	public final static long FSUB=0x0066_0000_0000_0000L;
	//Clear - non standard opcode
	public final static long CLR=0x0100_0000_0000_0000L;
	public final static long SQRT=0x0101_0000_0000_0000L;
	public final static long HALT=0x01FF_0000_0000_0000L;

	//special symbols.  keep these to a bare minimum
	public final static long OPEN_PAREN = 0x2800_0000_0000_0000L;	// "("

	//=================================================
	/**
	* Compile the "source code", actually just a formula, into machine language.
	*/
	public void compile() {
		Shunt railyard = new Shunt(memory,INPUT);
		railyard.run();
	}

	//================================
	public static class LongStack {
		long[] mem;
		//ptr holds the address of the next entry to be added, also it contains the size
		int ptr=0;

		//* pass in the backing array
		public LongStack(long[] m) {
			mem=m;
		}

		//I could just use LinkedList here, but I am trying to keep this simple
		public void add(long lo) {mem[ptr++]=lo; }
		public void push(long lo) {mem[ptr++]=lo; }

		public int size() {return ptr;}

		public void clear() {
			for (int i=0;i<ptr;i++) {
				mem[i]=0L;
			}
			ptr=0;
		}

		public long peek() {
			if (ptr>0) {return mem[ptr-1];}
			else {
				//this is illegal array access, but just return 0
				return 0L;
			}
		}

		public long pop() {
			//this could probably be combined into 1 line
			long tmp = mem[ptr-1];
			ptr--;
			return tmp;
		}
	}

	//==============================
	//this could be in a separate file, but let's keep it all together
	//create a new Shunt object each time you compile
	//this uses the memory from the enclosing class
	//see https://en.wikipedia.org/wiki/Shunting_yard_algorithm
	public static class Shunt implements Runnable {
		StreamTokenizer toker;
		LongStack operatorStack;
		LongStack outputQueue;

		public Shunt(long[] mem,String in) {
			toker = new StreamTokenizer(new StringReader(in));
			toker.eolIsSignificant(true);
			toker.wordChars(95,95);	//make underscore part of a word
			toker.ordinaryChar(47); //make forward slash an ordinary char
			operatorStack = new LongStack(new long[10]);
			outputQueue=new LongStack(mem);
		}

		public void run() {
			int tok = 0;
			try {
				tok=toker.nextToken();
			} catch (IOException x) {
				x.printStackTrace();
			}
			while (tok!= StreamTokenizer.TT_EOF) {
				System.out.println("DEBUG: tok = "+tok);
				switch (tok) {
					case StreamTokenizer.TT_NUMBER:
						//make it into a triple
						long num = Base40.toBase40((float)toker.nval);
						//add it to memory
						System.out.println("DEBUG: adding "+ toker.nval +" to ouput as "+ num);
						outputQueue.add(num);
						break;
					case StreamTokenizer.TT_WORD:
						String word = toker.sval;
						System.out.println("DEBUG: word = "+word);
						if (word.equals("A") || word.equals("LOAD_A")) {
							//this means to retrieve the value of A
							outputQueue.add(FLOAD_0);
						} else if (word.equals("B") || word.equals("LOAD_B")) {
							outputQueue.add(FLOAD_1);
						} else if (word.equals("C") || word.equals("LOAD_C")) {
							outputQueue.add(FLOAD_2);
						} else if (word.equals("STORE_A")) {
							outputQueue.add(FSTORE_0);
						} else if (word.equals("STORE_B")) {
							outputQueue.add(FSTORE_1);
						} else if (word.equals("STORE_C")) {
							outputQueue.add(FSTORE_2);
						} else if (word.equals("SQRT")) {
							operatorStack.push(SQRT);
						} else {
							System.out.println("WARNING: unrecognized word "+word);
						}
						break;
					case 40:	// (
						operatorStack.push(OPEN_PAREN); break;
					case 41: 	// )
						doCloseParen(); break;
					case 42:	// *
						//go ahead and add the opcode
						operatorStack.push(FMUL); break;
					case 43:	// +
						doPlusOrMinus(tok); break;
					case 45:	// -
						doPlusOrMinus(tok); break;
					case 47:	// /
						operatorStack.push(FDIV); break;
					default:
						System.out.println("WARNING: unrecognized character "+tok+"("+(char)tok+")");
				} //end switch
				try {
					tok = toker.nextToken();
				} catch (IOException x) {
					x.printStackTrace();
				}
			} //end while
			//finally
			//When all the tokens have been read:
			//While there are still operator tokens in the stack:
			//Pop the operator on the top of the stack, and append it to the output.
			while (operatorStack.size()>0) {
				outputQueue.add(operatorStack.pop());
			}
			//now add HALT instruction
			outputQueue.add(HALT);
		}	//end run

		/**
		* While there is an operator B of higher or equal precidence than A at the top of the stack,
		* pop B off the stack and append it to the output.
		* For this purpose, we only use 2 levels of precedence
		*/
		public boolean higher() {
			if (operatorStack.size()==0) {
				return false;
			} else {
				long e = operatorStack.peek();
				if ( (e==FMUL) || (e==FDIV)) {
					return true;
				} else {
					return false;
				}
			}
		}

		public void doPlusOrMinus(int op) {
			while (higher()) {
				outputQueue.add(operatorStack.pop());
			};
			if (op==43) {	// +
				operatorStack.push(FADD);
			} else if (op==45) {	// -
				operatorStack.push(FSUB);
			}
		}

		public void doCloseParen() {
			//If the token is a closing bracket:
			//Pop operators off the stack and append them to the output, until the operator at the top of the stack is a opening bracket.
			//Pop the opening bracket off the stack.
			while (operatorStack.size()>0) {
				long e = operatorStack.peek();
				if (e==OPEN_PAREN) {
					operatorStack.pop();
					return;
				} else {
					outputQueue.add(operatorStack.pop());
				}
			}
		}
	}	//close class Shunt

	//for debugging
	public void dumpOutput() {
		System.out.println("dump output");
		System.out.println("===========");
		for (int i=0;i<memory.length;i++) {
			long instruction = memory[i];
			if (instruction == HALT) {
				System.out.println(i+": "+instruction + " (HALT)");
				break;
			} else {
				System.out.println(i+": "+instruction);
			}
		}
		System.out.println("===========");
	}

	//============================================
	//run the program, which is in queue
	boolean running = false;

	/**
	* execute the program one instruction at a time
	*/
	public void execute() {
		System.out.println("execute");
		IP=-1;
		running = true;
		while (running) {
			//fetch the next byte
			long op = memory[++IP];
			System.out.println(IP+": "+op);
			switch_op(op);
		}
	}

	//you can't use a switch with a long for some reason
	private void switch_op(long op) {
		long a = 0L;
		long b = 0L;
		long c = 0L;
		if (op==HALT) { running=false; return;}
		if (op==CLR) { clear(); return; }
		if (op==FADD) {
					b = POP();
					a = POP();
					c = Base40.ADD(a,b);
					PUSH( c );
					return;
		}
		if (op==FSUB) {
					b = POP(); a = POP();
					c = Base40.SUB(a,b);
					PUSH( c );
					return;
		}
		if (op==FMUL) {
					b = POP(); a = POP();
					c = Base40.MUL(a,b);
					PUSH( c );
					return;
		}
		if (op==FDIV) {
					b = POP(); a = POP();
					c = Base40.DIV(a,b);
					PUSH( c );
					return;
		}
		if (op==FNEG) {
					a = POP();
					PUSH( Base40.NEG(a) );
					return;
		}
		if (op==SQRT) {
					a = POP();
					double da = (double)a / 64000.0D;
					double dv = Math.sqrt(da);
					PUSH( (long)(dv * 64000L));
					return;
		}
		if (op==FLOAD_0) { PUSH(A); return;}
		if (op==FLOAD_1) { PUSH(B); return;}
		if (op==FLOAD_2) { PUSH(C); return;}
		if (op==FSTORE_0) { A=POP(); return;}
		if (op==FSTORE_1) { B=POP(); return;}
		if (op==FSTORE_2) { C=POP(); return;}

		if (op<Base40.FULL48) {
			//it's a number
			PUSH(op); return;
		} else {
			System.out.println("WARNING: unrecognized instruction "+op);
		}
	}
}