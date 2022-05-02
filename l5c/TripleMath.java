package lava5.l5c;

/**
* TripleMath is straight-forward, since it uses integer operations on longs;
* However, we have to correct for twos-complement, overflows and underflows.
*/
public class TripleMath {
	// if you add 2 negative numbers, remove the duplicate sign flag
	//also, this could cause an overflow, but I am not worried about that
	public static long ADD(long a, long b) {
		long c = a + b;
		if (c >= Triple.FULL48) {
			c = c - Triple.FULL48;
		}
		return c;
	}

	public static long SUB(long a, long b) {
		long c = a - b;
		if (c < 0) {
			c = c + Triple.FULL48;
		}
		return c;
	}

	public static long MUL(long a, long b) {
		long c = a * b;
		if (c >= Triple.FULL48) {
			c = c - Triple.FULL48;
		}
		long result = (long)(c / 64000);
		System.out.println("MUL: ="+result);
		return result;
	}

	//Integer division - produces unusual results
	public static long IDIV(long a, long b) {
		long c = a / b;
		if (c >= Triple.FULL48) {
			c = c - Triple.FULL48;
		}
		return (long)(c * 64000);
	}

	//different from integer division
	public static long FDIV(long a, long b) {
		double c = (double)a / (double)b;
		return (long)(c * 64000);
	}

	//only for use with integer division
	public static long IREM(long a, long b) {
		long c = a % b;
		//note not multiplied by 64000
		return c;
	}

	//change sign from Negative to positive and vice versa
	public static long NEG(long a) {
		if (a >= Triple.NEG_POINT ) {
			//if it is a negative number
			//make it positive
			a = a - Triple.FULL48;
		} else {
			a = a + Triple.FULL48;
		}
		return a;
	}

	public static long SQRT(long a) {
		double da = (double)a / 64000.0D;
		double dv = Math.sqrt(da);
		return (long)(dv * 64000L);
	}

	//==============================

	public static void main(String[] args) {
		int a = Integer.parseInt(args[0]);
		int b = Integer.parseInt(args[1]);
		long ta = Triple.intToTriple(a);
		System.out.println(ta);
		long tb = Triple.intToTriple(b);
		System.out.println(tb);
		//long tc = ADD(ta,tb);
		long tc = FDIV(ta,tb);
		System.out.println("tc="+tc);
		//int result = Triple.toInt(tc);
		float result = Triple.toFloat(tc);
		System.out.println(result);
		System.out.println("--------");


	}

}