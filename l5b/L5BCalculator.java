package lava5.l5b;
/**
* Java Calculator, modified from:
* https://dev.to/rohitk570/creating-a-calculator-using-java-awt-16ll
*
* The L5BCalculator is almost identical to the L5ACalculator.  The main difference
* is that is uses (half)floating point, instead of int.
*/

import java.awt.*;
import java.awt.event.*;

public class L5BCalculator extends Frame implements ActionListener{
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

	public final static String MODEL="L5B";
	L5BProcessor p;

	//--------------------------------------------
	//Layout
	//DATA
	Label lblData;
	TextField txtData;
	Label lblMsg;

	//number buttons
	Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0;

	//other buttons
	Button badd,bsub,bmul,bdiv,bmod,bcalc,bclr,bpt,bneg,bback;
	Button bsqrt;

	//Enter button
	Button bEnter;

	//IN
	Label lblIn;

	//CMD
	Label lblD;	//holds D register

	//A,B,C registers for the stack.  All on the same line
	Label lblA;
	Label lblB;
	Label lblC;

	//E registers for the prev commands
	Label lblE;

	public L5BCalculator(){
		super(MODEL);
		addWindowListener(new WindowClosingListener());
		p = new L5BProcessor();

		lblData=new Label();
		lblData.setBackground(Color.LIGHT_GRAY);
		lblData.setBounds(50,40,50,20);
		lblData.setText(" DATA");
		txtData=new TextField(10);
		txtData.setBounds(120,40,100,20);

		lblMsg=new Label();
		lblMsg.setBackground(Color.LIGHT_GRAY);
		lblMsg.setBounds(50,80,200,20);
		lblMsg.setText(" MSG");

		bclr=new Button("CE");
  		bclr.setBounds(50,110,65,50);
		bback=new Button("BACK");
 		bback.setBounds(120,110,50,50);
		bmod=new Button("REM");
  		bmod.setBounds(190,110,50,50);
		bdiv=new Button("/");
  		bdiv.setBounds(260,110,50,50);
  		bsqrt=new Button("SQRT");
  		bsqrt.setBounds(330,110,50,50);

		b7=new Button("7");
  		b7.setBounds(50,170,50,50);
		b8=new Button("8");
  		b8.setBounds(120,170,50,50);
		b9=new Button("9");
  		b9.setBounds(190,170,50,50);
		bmul=new Button("*");
  		bmul.setBounds(260,170,50,50);

		b4=new Button("4");
  		b4.setBounds(50,230,50,50);
		b5=new Button("5");
  		b5.setBounds(120,230,50,50);
		b6=new Button("6");
  		b6.setBounds(190,230,50,50);
		bsub=new Button("-");
  		bsub.setBounds(260,230,50,50);

		b1=new Button("1");
  		b1.setBounds(50,290,50,50);
		b2=new Button("2");
  		b2.setBounds(120,290,50,50);
		b3=new Button("3");
		b3.setBounds(190,290,50,50);
		badd=new Button("+");
  		badd.setBounds(260,290,50,50);

		bneg=new Button("+/-");
  		bneg.setBounds(50,350,50,50);
		b0=new Button("0");
  		b0.setBounds(120,350,50,50);
		bpt=new Button(".");
  		bpt.setBounds(190,350,50,50);
  		bpt.setEnabled(true);
		bcalc=new Button("=");
  		bcalc.setBounds(245,350,65,50);

		bEnter=new Button("ENTER");
		bEnter.setBounds(50,410,100,50);

		//IN, CMD
		lblIn=new Label();
		lblIn.setBackground(Color.LIGHT_GRAY);
		lblIn.setBounds(50,470,50,30);
		lblIn.setText("IN");
		lblD=new Label();
		lblD.setBackground(Color.LIGHT_GRAY);
		lblD.setBounds(120,470,50,30);
		lblD.setText("D");

		//A,B,C registers for the stack and E register.  All on the same line
		lblA=new Label();
		lblA.setBackground(Color.LIGHT_GRAY);
		lblA.setBounds(50,520,50,30);
		lblA.setText("A");
		lblB=new Label();
		lblB.setBackground(Color.LIGHT_GRAY);
		lblB.setBounds(120,520,50,30);
		lblB.setText("B");
		lblC=new Label();
		lblC.setBackground(Color.LIGHT_GRAY);
		lblC.setBounds(190,520,50,30);
		lblC.setText("C");
		lblE=new Label();
		lblE.setBackground(Color.LIGHT_GRAY);
		lblE.setBounds(260,520,50,30);
		lblE.setText("E");

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

		bpt.addActionListener(this);
		bneg.addActionListener(this);
		bback.addActionListener(this);

		badd.addActionListener(this);
		bsub.addActionListener(this);
		bmul.addActionListener(this);
		bdiv.addActionListener(this);
		bmod.addActionListener(this);
		bcalc.addActionListener(this);
		bclr.addActionListener(this);

		bsqrt.addActionListener(this);

		bEnter.addActionListener(this);

		//ADDING TO FRAME
		add(lblData);  add(txtData);
		add(b1); add(b2); add(b3); add(b4); add(b5);add(b6); add(b7); add(b8);add(b9);add(b0);
		add(badd); add(bsub); add(bmod); add(bmul); add(bdiv); add(bcalc);
		add(bclr); add(bpt);add(bneg); add(bback);
		add(bsqrt);
		add(bEnter);
		add(lblIn); add(lblD);
		add(lblA); add(lblB); add(lblC); add(lblE);
		add(lblMsg);

		setSize(500,600);
		setLayout(null);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e){
		String z;

		if (e.getSource()==b0){
  			z=txtData.getText();
  			txtData.setText(z+"0");
		}
		if (e.getSource()==b1){
  			z=txtData.getText();
  			txtData.setText(z+"1");
		}
		if (e.getSource()==b2){
  			z=txtData.getText();
  			txtData.setText(z+"2");
		}
		if (e.getSource()==b3){
  			z=txtData.getText();
  			txtData.setText(z+"3");
		}
		if (e.getSource()==b4){
  			z=txtData.getText();
  			txtData.setText(z+"4");
		}
		if (e.getSource()==b5){
  			z=txtData.getText();
  			txtData.setText(z+"5");
		}
		if (e.getSource()==b6){
  			z=txtData.getText();
  			txtData.setText(z+"6");
		}
		if (e.getSource()==b7){
  			z=txtData.getText();
  			txtData.setText(z+"7");
		}
		if (e.getSource()==b8){
  			z=txtData.getText();
  			txtData.setText(z+"8");
		}
		if (e.getSource()==b9){
  			z=txtData.getText();
  			txtData.setText(z+"9");
		}
		if(e.getSource()==bback){  // FOR  BACKSPACE
			z=txtData.getText();
			if (z.length()>0) {
				try {
					z=z.substring(0, z.length()-1);
					txtData.setText(z);
				} catch (StringIndexOutOfBoundsException x) {
					lblMsg.setText("BACKSPACE ERROR");
				}
			}
		}
		if(e.getSource()==bneg){ //FOR NEGATIVE
		  z=txtData.getText();
		  txtData.setText("-"+z);
		}
		if(e.getSource()==bpt){ //FOR decimal point
		  z=txtData.getText();
		  txtData.setText(z+".");
		}


		if (e.getSource()==bEnter) {
			z=txtData.getText();
			//reset data
			txtData.setText("");
			try {
				float f = Float.parseFloat(z);
				char v = HalfFloat.fromFloat(f);
				//first put it in the IN register
				p.IN = v;
				//display it
				lblIn.setText( toHexString(v));
				//set the OP
				char op = p.PUSHC;
				p.D=op;
				//display it
				lblD.setText( toHexString(op));
				//execute it to move the number on to the stack
				p.execute();
				//refresh the labels
				refresh();
			} catch (NumberFormatException x) {
				lblMsg.setText("NUMBER FORMAT ERROR");
			}
		}

		if(e.getSource()==badd){
			char op = p.FADD;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bsub){
			char op = p.FSUB;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bmul){
			char op = p.FMUL;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bdiv){
			char op = p.FDIV;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bmod){
			char op = p.FREM;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bsqrt){
			char op = p.SQRT;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}

		if(e.getSource()==bcalc){
			//the op is already set.  nothing to do but execute and display results
			p.execute();
			//refresh the labels
			refresh();
			//also display result in msg
			char result = p.readA();
			//what format is result in?  half-float
			float f = HalfFloat.toFloat(result);
			lblMsg.setText(String.valueOf(f));
		}
		if(e.getSource()==bclr){
			char op = p.CLS;
			p.D=op;
			p.execute();
			refresh();
			txtData.setText("");
		}
	}

	public void refresh() {
		lblMsg.setText("");
		lblIn.setText(toHexString(p.IN));
		lblA.setText(toHexString(p.readA()));
		lblB.setText(toHexString(p.readB()));
		lblC.setText(toHexString(p.readC()));
		lblD.setText(toHexString(p.readD()));
		lblE.setText(toHexString(p.readE()));
	}

	public static void main(String[] args) {
		new L5BCalculator();
	}
}

