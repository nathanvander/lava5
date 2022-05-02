package lava5.l5c;
/**
* Java Calculator, modified from:
* https://dev.to/rohitk570/creating-a-calculator-using-java-awt-16ll
*
* This uses the L5CProcessor, which has 8 registers.
* We don't display the IN register, because we don't need it
* We also don't display the ST2 register (bottom of stack)
*/

import java.awt.*;
import java.awt.event.*;

public class L5CCalculator extends Frame implements ActionListener{
	//==========================
	static class WindowClosingListener extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
			System.out.println("window closing");
			System.exit(0);
		}
	}
	//==========================
	public static String toHexString(char c) {
		return String.format("%04x",(int)c);
	}

	public final static String MODEL="L5C";
	L5CProcessor p;

	//--------------------------------------------
	//Layout
	//Line 1: INPUT and OP
	Label lblInput;
	TextField txtInput;
	Label lblOP; //the old version called this D

	//Line 2:  stack
	Label lblS0;
	Label lblS1;

	//Line 3: output
	Label lblOutput, lblMsg;

	//line 4: store/load
	Button bSTOA, bLDA, bSTOB, bLDB, bSTOC, bLDC;

	//line 5: clear, back, rem, IDIV, SQRT
	Button bclr, bback, brem, bidiv, bsqrt;

	//line 6: 7,8,9,* FDIV
	Button b7,b8,b9,bmul,bfdiv;

	//line 7: 4,5,6, -,
	Button b4,b5,b6, bsub, bneg;

	//line 8: 1,2,3 +
	Button b1,b2,b3, badd;

	//line 9: +/- 0 . ENTER, EXEC
	Button bsign, b0, bpt;
	Button bEnter, bExec;

	//A,B,C registers for the local variables.  Off to the right
	Label lblA;
	Label lblB;
	Label lblC;

	public L5CCalculator(){
		super(MODEL);
		addWindowListener(new WindowClosingListener());
		p = new L5CProcessor();

		//line 1: INPUT
		//40 pixels down
		lblInput=new Label();
		lblInput.setBackground(Color.LIGHT_GRAY);
		lblInput.setBounds(50,40,50,20);
		lblInput.setText("INPUT");
		txtInput=new TextField(10);
		txtInput.setBounds(120,40,100,20);
		lblOP=new Label();
		lblOP.setBackground(Color.LIGHT_GRAY);
		lblOP.setBounds(260,40,50,20);
		lblOP.setText("OP");

		//line 2: stack
		//75 pixels down
		lblS0=new Label();
		lblS0.setBackground(Color.LIGHT_GRAY);
		lblS0.setBounds(50,75,120,20);
		lblS0.setText("S0");
		lblS1=new Label();
		lblS1.setBackground(Color.LIGHT_GRAY);
		lblS1.setBounds(200,75,120,20);
		lblS1.setText("S1");
		//we could also add bottom of stack here

		//line 3: output
		//110 pixels down
		lblOutput=new Label();
		lblOutput.setBackground(Color.LIGHT_GRAY);
		lblOutput.setBounds(50,110,50,20);
		lblOutput.setText("OUTPUT");
		lblMsg=new Label();
		lblMsg.setBackground(Color.LIGHT_GRAY);
		lblMsg.setBounds(120,110,200,20);

		//line 4: store/load
		//145 Pixels down
		bSTOA = new Button("STOA");
		bSTOA.setBounds(50,145,40,40);
		bLDA= new Button("LDA");
		bLDA.setBounds(100,145,40,40);
		bSTOB= new Button("STOB");
		bSTOB.setBounds(150,145,40,40);
		bLDB= new Button("LDB");
		bLDB.setBounds(200,145,40,40);
		bSTOC= new Button("STOC");
		bSTOC.setBounds(250,145,40,40);
		bLDC= new Button("LDC");
		bLDC.setBounds(300,145,40,40);

		//line 5: clear, back, rem, IDIV, SQRT
		//200 pixels down
		bclr=new Button("CE");
  		bclr.setBounds(50,200,65,50);
		bback=new Button("BACK");
 		bback.setBounds(120,200,50,50);
		brem=new Button("REM");
  		brem.setBounds(190,200,50,50);
		bidiv=new Button("IDIV");
  		bidiv.setBounds(260,200,50,50);
  		bsqrt=new Button("SQRT");
  		bsqrt.setBounds(330,200,50,50);

		//line 6: 7,8,9,* FDIV
		//260 pixels down
		b7=new Button("7");
  		b7.setBounds(50,260,50,50);
		b8=new Button("8");
  		b8.setBounds(120,260,50,50);
		b9=new Button("9");
  		b9.setBounds(190,260,50,50);
		bmul=new Button("*");
  		bmul.setBounds(260,260,50,50);
  		bfdiv=new Button("FDIV");
  		bfdiv.setBounds(330,260,50,50);

		//line 7: 4,5,6, -, NEG
		//320 pixels down
		b4=new Button("4");
  		b4.setBounds(50,320,50,50);
		b5=new Button("5");
  		b5.setBounds(120,320,50,50);
		b6=new Button("6");
  		b6.setBounds(190,320,50,50);
		bsub=new Button("-");
  		bsub.setBounds(260,320,50,50);
		bneg=new Button("NEG");
  		bneg.setBounds(330,320,50,50);

		//line 8: 1,2,3 +
		//380 pixels down
		b1=new Button("1");
  		b1.setBounds(50,380,50,50);
		b2=new Button("2");
  		b2.setBounds(120,380,50,50);
		b3=new Button("3");
		b3.setBounds(190,380,50,50);
		badd=new Button("+");
  		badd.setBounds(260,380,50,50);

		//line 9: +/- 0 .
		//440 pixels down
		bsign=new Button("SIGN");
  		bsign.setBounds(50,440,50,50);
		b0=new Button("0");
  		b0.setBounds(120,440,50,50);
		bpt=new Button(".");
  		bpt.setBounds(190,440,50,50);
		bEnter=new Button("ENTER");
		bEnter.setBounds(260,440,65,50);
		bExec=new Button("EXEC");
		bExec.setBounds(340,440,65,50);

		//A,B,C variables
		lblA=new Label();
		lblA.setBackground(Color.LIGHT_GRAY);
		lblA.setBounds(400,200,120,30);
		lblA.setText("A");
		lblB=new Label();
		lblB.setBackground(Color.LIGHT_GRAY);
		lblB.setBounds(400,260,120,30);
		lblB.setText("B");
		lblC=new Label();
		lblC.setBackground(Color.LIGHT_GRAY);
		lblC.setBounds(400,320,120,30);
		lblC.setText("C");

		//add action listeners
		bSTOA.addActionListener(this);
		bLDA.addActionListener(this);
		bSTOB.addActionListener(this);
		bLDB.addActionListener(this);
		bSTOC.addActionListener(this);
		bLDC.addActionListener(this);

		bclr.addActionListener(this);
		bback.addActionListener(this);
		brem.addActionListener(this);
		bidiv.addActionListener(this);
		bsqrt.addActionListener(this);

		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		b5.addActionListener(this);
		b6.addActionListener(this);
		b7.addActionListener(this);
		b8.addActionListener(this);
		b9.addActionListener(this);
		b0.addActionListener(this);

		badd.addActionListener(this);
		bsub.addActionListener(this);
		bmul.addActionListener(this);
		bfdiv.addActionListener(this);
		bsign.addActionListener(this);
		bpt.addActionListener(this);
		bneg.addActionListener(this);
		bEnter.addActionListener(this);
		bExec.addActionListener(this);

		//ADDING TO FRAME
		add(lblInput);  add(txtInput); add(lblOP);
		add(lblS0); add(lblS1);
		add(lblOutput); add(lblMsg);
		add(bSTOA); add(bLDA); add(bSTOB); add(bLDB); add(bSTOC); add(bLDC);
		add(bclr); add(bback); add(brem); add(bidiv); add(bsqrt);
		add(b1); add(b2); add(b3); add(b4); add(b5);add(b6); add(b7); add(b8);add(b9);add(b0);
		add(badd); add(bsub); add(bmul); add(bfdiv); add(bneg);
		add(bsign); add(bpt);
		add(bEnter); add(bExec);
		add(lblA); add(lblB); add(lblC);

		setSize(600,550);
		setLayout(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e){
		String z;

		if (e.getSource()==b0){
  			z=txtInput.getText();
  			txtInput.setText(z+"0");
		} else
		if (e.getSource()==b1){
  			z=txtInput.getText();
  			txtInput.setText(z+"1");
		} else
		if (e.getSource()==b2){
  			z=txtInput.getText();
  			txtInput.setText(z+"2");
		} else
		if (e.getSource()==b3){
  			z=txtInput.getText();
  			txtInput.setText(z+"3");
		} else
		if (e.getSource()==b4){
  			z=txtInput.getText();
  			txtInput.setText(z+"4");
		} else
		if (e.getSource()==b5){
  			z=txtInput.getText();
  			txtInput.setText(z+"5");
		} else
		if (e.getSource()==b6){
  			z=txtInput.getText();
  			txtInput.setText(z+"6");
		} else
		if (e.getSource()==b7){
  			z=txtInput.getText();
  			txtInput.setText(z+"7");
		} else
		if (e.getSource()==b8){
  			z=txtInput.getText();
  			txtInput.setText(z+"8");
		} else
		if (e.getSource()==b9){
  			z=txtInput.getText();
  			txtInput.setText(z+"9");
		} else

		if(e.getSource()==bback){  // FOR  BACKSPACE
			z=txtInput.getText();
			if (z.length()>0) {
				try {
					z=z.substring(0, z.length()-1);
					txtInput.setText(z);
				} catch (StringIndexOutOfBoundsException x) {
					lblMsg.setText("BACKSPACE ERROR");
				}
			}
		} else

		if(e.getSource()==bsign){ //to change sign
			z=txtInput.getText();
		  	if (z.startsWith("-")) {
				z=z.substring(1, z.length());
			  	txtInput.setText(z);
		  	} else {
		  		txtInput.setText("-"+z);
			}
		} else
		if(e.getSource()==bpt){ //FOR decimal point
		  z=txtInput.getText();
		  txtInput.setText(z+".");
		} else

		if (e.getSource()==bEnter) {
			z=txtInput.getText();
			//reset data
			txtInput.setText("");
			try {
				long lin=0L;
				if (z.contains(".")) {
					//it's a float
					float f = Float.parseFloat(z);
					lin=Triple.floatToTriple(f);
				} else {
					//it's an int
					int i = Integer.parseInt(z);
					lin=Triple.intToTriple(i);
				}
				//first put it in the IN register
				p.IN = lin;
				//set the OP
				char op = p.PUSHT;
				p.OP=op;
				//display it
				lblOP.setText( toHexString(op));
				//execute it to move the number on to the stack
				p.execute();
				//refresh the labels
				refresh();
			} catch (NumberFormatException x) {
				lblMsg.setText("NUMBER FORMAT ERROR");
			}
		} else

		if(e.getSource()==badd){
			char op = p.FADD;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else


		if(e.getSource()==bsub){
			char op = p.FSUB;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else
		if(e.getSource()==bmul){
			char op = p.FMUL;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else

		if(e.getSource()==bfdiv){
			char op = p.FDIV;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else
		if(e.getSource()==bidiv){
			char op = p.IDIV;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else

		if(e.getSource()==brem){
			char op = p.IREM;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else
		if(e.getSource()==bsqrt){
			char op = p.SQRT;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else
		if(e.getSource()==bneg){
			char op = p.FNEG;
			p.OP=op;
			//display it
			lblOP.setText( toHexString(op));
		} else
		if(e.getSource()==bclr){
			p.OP=p.CLR;
			p.execute();
			refresh();
			txtInput.setText("");
		} else
		if (e.getSource()==bSTOA) {
			p.OP=p.FSTORE_0;
			lblOP.setText( toHexString(p.OP));
		} else
		if (e.getSource()==bSTOB) {
			p.OP=p.FSTORE_1;
			lblOP.setText( toHexString(p.OP));
		} else
		if (e.getSource()==bSTOC) {
			p.OP=p.FSTORE_2;
			lblOP.setText( toHexString(p.OP));
		} else
		if (e.getSource()==bLDA) {
			p.OP=p.FLOAD_0;
			lblOP.setText( toHexString(p.OP));
		} else
		if (e.getSource()==bLDB) {
			p.OP=p.FLOAD_1;
			lblOP.setText( toHexString(p.OP));
		} else
		if (e.getSource()==bLDC) {
			p.OP=p.FLOAD_2;
			lblOP.setText( toHexString(p.OP));
		} else
		if(e.getSource()==bExec){
			//the op is already set.  nothing to do but execute and display results
			p.execute();
			//refresh the labels
			refresh();
			//also display result in msg
			long result = p.readS0();
			float f = Triple.toFloat(result);
			lblMsg.setText(String.valueOf(f));
		}

	}

	public void refresh() {
		lblMsg.setText("");
		lblOP.setText(toHexString(p.OP));
		lblS0.setText(Triple.toHex(p.readS0()));
		lblS1.setText(Triple.toHex(p.readS1()));
		lblA.setText(Triple.toHex(p.readA()));
		lblB.setText(Triple.toHex(p.readB()));
		lblC.setText(Triple.toHex(p.readC()));
	}

	public static void main(String[] args) {
		new L5CCalculator();
	}
}

