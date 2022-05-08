L5DCalculator Users Guide
=========================

Welcome to the L5D Calculator!  This guide will helpfully make things easier
to use and understand.

The L5D Calculator lets you enter simple formulas and execute them.  The user inputs
data either directly into the IN box at the top of the screen, or by using the buttons.
When the ENTER button is pushed, the input is compiled into the internal machine language
for the processor and executed.  The result will be pushed onto the stack and also displayed
in the OUT field.

This uses the Railroad Shuntyard Algorithm as described at https://en.wikipedia.org/wiki/Shunting_yard_algorithm .

Parentheses
-------------
You can use parentheses to change the order of evaluation.

Base40
------
Internally, this uses Base40 to hold number formats.  All this means is that numbers are stored internally
as long integers, by multiplying them by 64000.  This is done for 2 reasons: first, to consolidate ints
and floats so they are treated alike, and second, so that fractions up to 0.001 have an exact representation.
This is a fixed point machine, not floating point.

Negative Numbers and Subtraction
--------------------------------
The internal parser gets confused between negative numbers and the minus sign.  To make the difference clear,
whenever you are doing subtraction, the minus sign should be surrounded by spaces.

Storage
---------
To store a number from the top of the stack, just enter the STORE button and then the variables (A, B or C).  This will
make a word like STORE_A.  Likewise LOAD plus the variable will load the number from memory and put it on the stack.
Variables can also be used in formulas, for example: A * 2.  LOAD_A and just A have the same meaning.

Oddities
--------
When you divide by zero, this just returns 0, with no error messages; however, an error does display to standard out.

The largest numbers you can use are about 2 billion; however, when this exceeds the maximum, no error is given, instead
it just displays a negative number.

The smallest numbers you can use are about 0.00002.  Smaller than this, it just displays zero, with no error given.

The display converts the internal format into floating point before displaying.  This is annoying when it shows very large or 
very small numbers because it uses scientific notation.

Square Root
-----------
There is no SQRT button, however, the square root function is built in (as an example) and you can type it into the input box like: SQRT(2).
