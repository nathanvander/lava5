package lava5.l5c;

/**
* Triple is the intrinsic numeric type for my Lava5 calculator. It is a 48-bit number that is stored
* in 3 chars.  Each char only holds 64000 (instead of 65536 like you would expect), for exact representation of
* decimal numbers.
*
* Negative numbers are stored in twos-complement.
*/
public class Triple {
	public final static long FULL48= 64000L * 64000L * 64000L ;
	public final static long NEG_POINT = 32000L * 64000L * 64000L;
	public final static int MAX_INT = (32000*64000)-1;
	//MAX_FLOAT is less than MAX_INT because float rounds numbers up
	//I'm not sure the exact cutoff and this is an approximation
	public final static float MAX_FLOAT = (float)MAX_INT - 32.0F;
	public final static long K64 = 64000L;
	public final static float K64F = 64000.0F;

	/**
	* Print to hex, after splitting into powers of 32000
	*/
	public static String toHex(long lv) {
		int h0 = (int)(lv / (K64 * K64));
		long lv2 = lv - (h0 * K64 * K64);
		int h1 = (int)( lv2 / K64);
		int h2 = (int)( lv2 % K64);
		String hex0 = String.format("%04x", h0);
		String hex1 = String.format("%04x", h1);
		String hex2 = String.format("%04x", h2);
		return hex0 +"."+hex1+"."+hex2;
	}

	public static long intToTriple(int i) {
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

	public static long floatToTriple(float f) {
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
		//System.out.println("toFloat value = "+value);
		//System.out.println("toFloat K64F = "+ K64F);
		return (float)( value / K64F);
	}

	//===========================
	public static void main(String[] args) {
		float f = Float.parseFloat(args[0]);
		long v = floatToTriple(f);
		//int i = Integer.parseInt(args[0]);
		//long v = intToTriple(i);
		System.out.println("long: "+v);
		System.out.println("hex: "+toHex(v));
		//System.out.println("int: "+toInt(v));
		System.out.println("float: "+toFloat(v));
	}

}