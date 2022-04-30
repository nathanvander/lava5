package lava5.l5a;
/**
* Java Calculator, modified from:
* https://dev.to/rohitk570/creating-a-calculator-using-java-awt-16ll
*/

import java.awt.*;
import java.awt.event.*;

public class L5ACalculator extends Frame implements ActionListener{
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

	public final static String MODEL="L5A";
	L5AProcessor p;

	//--------------------------------------------
	//Layout
	//DATA
	Label lblData;
	TextField txtData;
	Label lblMsg;

	//number buttons
	Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b0;

	//other buttons
	Button badd,bsub,bmul,bdiv,bmod,bcalc,bclr,bpts,bneg,bback;

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

	public L5ACalculator(){
		super(MODEL);
		addWindowListener(new WindowClosingListener());
		p = new L5AProcessor();

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
		bpts=new Button(".");
  		bpts.setBounds(190,350,50,50);
  		bpts.setEnabled(false);
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

		bpts.addActionListener(this);
		bneg.addActionListener(this);
		bback.addActionListener(this);

		badd.addActionListener(this);
		bsub.addActionListener(this);
		bmul.addActionListener(this);
		bdiv.addActionListener(this);
		bmod.addActionListener(this);
		bcalc.addActionListener(this);
		bclr.addActionListener(this);

		bEnter.addActionListener(this);

		//ADDING TO FRAME
		add(lblData);  add(txtData);
		add(b1); add(b2); add(b3); add(b4); add(b5);add(b6); add(b7); add(b8);add(b9);add(b0);
		add(badd); add(bsub); add(bmod); add(bmul); add(bdiv); add(bmod);add(bcalc);
		add(bclr); add(bpts);add(bneg); add(bback);
		add(bEnter);
		add(lblIn); add(lblD);
		add(lblA); add(lblB); add(lblC); add(lblE);
		add(lblMsg);

		setSize(400,600);
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

		if (e.getSource()==bEnter) {
			z=txtData.getText();
			//reset data
			txtData.setText("");
			try {
				int i = Integer.parseInt(z);
				char v = p.toChar(i);
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
			char op = p.IADD;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bsub){
			char op = p.ISUB;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bmul){
			char op = p.IMUL;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bdiv){
			char op = p.IDIV;
			p.D=op;
			//display it
			lblD.setText( toHexString(op));
		}
		if(e.getSource()==bmod){
			char op = p.IREM;
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
			int result = (int)p.readA();
			lblMsg.setText(String.valueOf(result));
		}
		if(e.getSource()==bclr){
			char op = p.CLS;
			p.D=op;
			p.execute();
			refresh();
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
		new L5ACalculator();
	}
}

/**

                //ARITHMETIC BUTTON
if(e.getSource()==badd){                     //FOR ADDITION
  try{
    num1=Double.parseDouble(l1.getText());
    }catch(NumberFormatException f){
      l1.setText("Invalid Format");
      return;
    }
  z="";
  l1.setText(z);
  check=1;
}
if(e.getSource()==bsub){                    //FOR SUBTRACTION
  try{
    num1=Double.parseDouble(l1.getText());
    }catch(NumberFormatException f){
      l1.setText("Invalid Format");
      return;
    }
  z="";
  l1.setText(z);
  check=2;
}
if(e.getSource()==bmult){                   //FOR MULTIPLICATION
  try{
    num1=Double.parseDouble(l1.getText());
    }catch(NumberFormatException f){
      l1.setText("Invalid Format");
      return;
    }
  z="";
  l1.setText(z);
  check=3;
}
if(e.getSource()==bdiv){                   //FOR DIVISION
  try{
    num1=Double.parseDouble(l1.getText());
    }catch(NumberFormatException f){
      l1.setText("Invalid Format");
      return;
    }
  z="";
  l1.setText(z);
  check=4;
}
if(e.getSource()==bmod){                  //FOR MOD/REMAINDER
  try{
    num1=Double.parseDouble(l1.getText());
    }catch(NumberFormatException f){
      l1.setText("Invalid Format");
      return;
    }
  z="";
  l1.setText(z);
  check=5;
}
                         //RESULT BUTTON
if(e.getSource()==bcalc){
  try{
    num2=Double.parseDouble(l1.getText());
    }catch(Exception f){
      l1.setText("ENTER NUMBER FIRST ");
      return;
    }
  if(check==1)
    xd =num1+num2;
  if(check==2)
    xd =num1-num2;
  if(check==3)
    xd =num1*num2;
  if(check==4)
    xd =num1/num2;
  if(check==5)
    xd =num1%num2;
  l1.setText(String.valueOf(xd));
}
                        //FOR CLEARING THE LABEL and Memory
if(e.getSource()==bclr){
  num1=0;
  num2=0;
  check=0;
  xd=0;
   z="";
   l1.setText(z);
   }

}

**/
