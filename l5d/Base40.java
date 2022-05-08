package lava5.l5d;

/**
* I like equations that are simple and elegant, which this is. Floating point is not elegant.
* This uses twos complement for negative numbers.
*/

public class Base40 {
	public final static long K64 = 64000L;
	public final static int MAX_INT = (32000*64000)-1;
	public final static long FULL48= 64000L * 64000L * 64000L ;
	public final static long NEG_POINT = 32000L * 64000L * 64000L;
	public final static float MAX_FLOAT = (float)MAX_INT - 32.0F;

	public static long toBase40(int i) {
		if (i> MAX_INT || i < (0 - MAX_INT)) {
			throw new IllegalArgumentException(i+ " is out of bounds");
		}
		if (i<0) {
			i = 0 - i;
			return FULL48 - (i * K64);
		} else {
			return (long)(i * K64);
		}
	}

	public static long toBase40(float f) {
		if (f> MAX_FLOAT || f < (0 - MAX_FLOAT)) {
			throw new IllegalArgumentException(f+ " is out of bounds");
		}
		if (f < 0.0F) {
			f = 0.0F - f;
			return FULL48 - (long)(f * K64);
		} else {
			return (long)(f * K64);
		}
	}

	public static int toInt(long value) {
		if (value >= NEG_POINT) {
			value = value - FULL48;
		}
		return (int)(value / K64);
	}

	public static float toFloat(long value) {
		//System.out.println("toFloat value passed in = "+value);
		if (value >= NEG_POINT) {
			value = value - FULL48;
		}
		return (float)( (double)value / 64000.0F);
	}

	public static long ADD(long a, long b) {
		long c = a + b;
		if (c >= FULL48) {
			c = c - FULL48;
		}
		return c;
	}

	public static long SUB(long a, long b) {
		long c = a - b;
		if (c < 0) {
			c = c + FULL48;
		}
		return c;
	}

	public static long NEG(long a) {
		if (a >= NEG_POINT ) {
			//its a negative number, change to positive
			a = 0 - (a - FULL48);
		} else {
			//its a postive number, change to negative
			a = (0 - a) + FULL48;
		}
		return a;
	}

	public static long MUL(long a, long b) {
		//step 1 - fix the signs
		boolean diff = false;
		if (a >= NEG_POINT) {
			a = 0 - (a - FULL48);
			diff = true;
		}
		if (b >= NEG_POINT) {
			b = 0 - (b - FULL48);
			if (diff==false) {
				diff = true;
			} else {
				diff = false;
			}
		}

		//step 2 - do the multiplication
		long c = a * b / K64;

		//step 3 - fix the signs again
		if (diff) {
			c = (0 - c) + FULL48;
		}
		return c;
	}

	/**
	* DIV - Divide a by b
	* If you divide by 0, this only gives a warning and returns zero.
	*/
	public static long DIV(long a, long b) {
		if (b==0L) {
			System.out.println("ERROR: in DIV trying to divide by zero");
			return 0L;
		}
		long result = 0L;
		//step 1 - look at the signs
		//diff is true if and only if the inputs have different signs
		//usually this is when the dividend is negative
		boolean diff = false;
		if (a >= NEG_POINT) {
			a = 0 - (a - FULL48);
			diff = true;
		}
		if (b >= NEG_POINT) {
			b = 0 - (b - FULL48);
			if (diff==false) {
				diff = true;
			} else {
				diff = false;
			}
		}

		//step 2 - do the integer division
		long c = a / b;

		//step 3 - also do more work on the remainder
		long d = a % b;
		long e = (d * K64) / b;

		//step 4 - combine them
		result = c * K64 + e;

		//step 5 - fix the signs again
		if (diff) {
			result = (0 - result) + FULL48;
		}
		return result;
	}

	public static void main(String[] args) {
		float fa = Float.parseFloat(args[0]);
		float fb = Float.parseFloat(args[1]);
		long la = toBase40(fa);
		System.out.println("a = "+ la);
		long lb = toBase40(fb);
		System.out.println("b = "+ lb);
		//long d = DIV(la,lb);
		long d = ADD(la,lb);
		System.out.println("d = "+ d);
		float fc = toFloat(d);
		System.out.println(fc);
	}

}