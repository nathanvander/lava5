package lava5.l5b;

/**
* HalfFloat is a 16-bit floating point number.
* See https://en.wikipedia.org/wiki/Half-precision_floating-point_format
*
* Code modified from https://itecnote.com/tecnote/java-half-precision-floating-point-in-java/
*/

public class HalfFloat {

// ignores the higher 16 bits
public static float toFloat( char v )
{
	int hbits = (int)v;
    int mant = hbits & 0x03ff;            // 10 bits mantissa
    int exp =  hbits & 0x7c00;            // 5 bits exponent
    if( exp == 0x7c00 )                   // NaN/Inf
        exp = 0x3fc00;                    // -> NaN/Inf
    else if( exp != 0 )                   // normalized value
    {
        exp += 0x1c000;                   // exp - 15 + 127
        if( mant == 0 && exp > 0x1c400 )  // smooth transition
            return Float.intBitsToFloat( ( hbits & 0x8000 ) << 16
                                            | exp << 13 | 0x3ff );
    }
    else if( mant != 0 )                  // && exp==0 -> subnormal
    {
        exp = 0x1c400;                    // make it normal
        do {
            mant <<= 1;                   // mantissa * 2
            exp -= 0x400;                 // decrease exp by 1
        } while( ( mant & 0x400 ) == 0 ); // while not normal
        mant &= 0x3ff;                    // discard subnormal bit
    }                                     // else +/-0 -> +/-0
    return Float.intBitsToFloat(          // combine all parts
        ( hbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
}


public static char fromFloat( float fval )
{
    int fbits = Float.floatToIntBits( fval );
    int sign = fbits >>> 16 & 0x8000;          // sign only
    int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value
    int result = 0;

    if( val >= 0x47800000 )               // might be or become NaN/Inf
    {                                     // avoid Inf due to rounding
        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
        {                                 // is or must become NaN/Inf
            if( val < 0x7f800000 ) {        // was value but too large
                result = sign | 0x7c00;     // make it +/-Inf
                return (char)result;
			} else {
            	result = sign | 0x7c00 |        // remains +/-Inf or NaN
            	    ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
            	return (char)result;
			}
        }
        result = sign | 0x7bff;             // unrounded not quite Inf
        return (char)result;
    }
    if( val >= 0x38800000 )  {             // remains normalized value
        result = sign | val - 0x38000000 >>> 13; // exp - 127 + 15
        return (char)result;
	}
    if( val < 0x33000000 ) {               // too small for subnormal
        result = sign;                      // becomes +/-0
        return (char)result;
	}
    val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
    result = sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
         + ( 0x800000 >>> val - 102 )     // round depending on cut off
      	>>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    return (char)result;
}

	public static String toHexString(char c) {
			return String.format("%04x",(int)c);
	}

	public static void main(String[] args) {
		float f = Float.parseFloat(args[0]);
		char hf = fromFloat(f);
		System.out.println( toHexString(hf));
		float f2 = toFloat(hf);
		System.out.println(f2);
	}

}
