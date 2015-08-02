import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class frmCalc {

	private JFrame frmCalc;
	private JTextField tValue;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmCalc window = new frmCalc();
					window.frmCalc.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public frmCalc() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmCalc = new JFrame();
		frmCalc.setResizable(false);
		frmCalc.setAlwaysOnTop(true);
		frmCalc.setTitle("Calc");
		frmCalc.setBounds(100, 100, 191, 210);
		frmCalc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCalc.getContentPane().setLayout(new MigLayout("", "[][][][]", "[20px:n][][][][][]"));
		
		final JLabel lblExNumber = new JLabel("");
		frmCalc.getContentPane().add(lblExNumber, "cell 0 0 3 1");
		
		final JLabel lblOperator = new JLabel("");
		frmCalc.getContentPane().add(lblOperator, "cell 3 0");
		
		tValue = new JTextField();
		frmCalc.getContentPane().add(tValue, "cell 0 1 4 1,growx");
		tValue.setColumns(10);
		
		JButton btn1 = new JButton("1");
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tValue.setText(tValue.getText()+"1");
			}
		});
		frmCalc.getContentPane().add(btn1, "cell 0 2,grow");
		
		JButton btn2 = new JButton("2");
		btn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"2");
			}
		});
		frmCalc.getContentPane().add(btn2, "cell 1 2,grow");
		
		JButton btn3 = new JButton("3");
		btn3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"3");
			}
		});
		frmCalc.getContentPane().add(btn3, "cell 2 2,grow");
		
		JButton btnPlus = new JButton("+");
		btnPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblExNumber.setText(tValue.getText());
				lblOperator.setText("+");
				tValue.setText("");
			}
		});
		frmCalc.getContentPane().add(btnPlus, "cell 3 2,grow");
		
		JButton btn4 = new JButton("4");
		btn4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"4");
			}
		});
		frmCalc.getContentPane().add(btn4, "cell 0 3,grow");
		
		JButton btn5 = new JButton("5");
		btn5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"5");
			}
		});
		frmCalc.getContentPane().add(btn5, "cell 1 3,grow");
		
		JButton btn6 = new JButton("6");
		btn6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"6");
			}
		});
		frmCalc.getContentPane().add(btn6, "cell 2 3,grow");
		
		JButton btnMinus = new JButton("-");
		btnMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblExNumber.setText(tValue.getText());
				lblOperator.setText("-");
				tValue.setText("");
			}
		});
		frmCalc.getContentPane().add(btnMinus, "cell 3 3,grow");
		
		JButton btn7 = new JButton("7");
		btn7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"7");
			}
		});
		frmCalc.getContentPane().add(btn7, "cell 0 4,grow");
		
		JButton btn8 = new JButton("8");
		btn8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"8");
			}
		});
		frmCalc.getContentPane().add(btn8, "cell 1 4,grow");
		
		JButton btn9 = new JButton("9");
		btn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"9");
			}
		});
		frmCalc.getContentPane().add(btn9, "cell 2 4,grow");
		
		JButton btnMultiply = new JButton("*");
		frmCalc.getContentPane().add(btnMultiply, "cell 3 4,grow");
		
		JButton btnEqual = new JButton("=");
		btnEqual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double a,b,c=0; 
				a=Double.parseDouble(lblExNumber.getText());
				b=Double.parseDouble(tValue.getText());
				String op=lblOperator.getText();
				switch (op) {
				case "+" :	c=a+b;
				break;
				case "-" :	c=a-b;
				break;
				case "*" :	c=a*b;
				break;
				case "/" :	c=a/b;
				break;
				default : return;
				}				
				lblExNumber.setText(Double.toString(c));
				lblOperator.setText("");
				tValue.setText("");
			}
		});
		frmCalc.getContentPane().add(btnEqual, "cell 0 5,grow");
		
		JButton btn0 = new JButton("0");
		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tValue.setText(tValue.getText()+"0");
			}
		});
		frmCalc.getContentPane().add(btn0, "cell 1 5,grow");
		
		JButton btnDot = new JButton(".");
		frmCalc.getContentPane().add(btnDot, "cell 2 5,grow");
		
		JButton btnDivide = new JButton("/");
		frmCalc.getContentPane().add(btnDivide, "cell 3 5,grow");
	}

}
