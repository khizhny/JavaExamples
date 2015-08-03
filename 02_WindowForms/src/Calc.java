import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;

public class Calc {

	protected Shell shlCalc;
	private Text tNum1;
	private Text tNum2;
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Table table;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Calc window = new Calc();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlCalc.open();
		shlCalc.layout();
		while (!shlCalc.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlCalc = new Shell();
		shlCalc.setSize(508, 202);
		shlCalc.setText("Calc");
		GridLayout gl_shlCalc = new GridLayout(4, false);
		gl_shlCalc.horizontalSpacing = 3;
		gl_shlCalc.marginHeight = 3;
		gl_shlCalc.verticalSpacing = 3;
		gl_shlCalc.marginWidth = 3;
		shlCalc.setLayout(gl_shlCalc);
		
		Label lblNum = new Label(shlCalc, SWT.NONE);
		lblNum.setText("NUm1");
		
		tNum1 = new Text(shlCalc, SWT.BORDER);
		
		Button btnPlus = new Button(shlCalc, SWT.NONE);
		btnPlus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int number1;
				int number2;
				try {
					number1 = Integer.parseInt( tNum1.getText());
				}
				catch (Exception exc)
				{
					MessageDialog.openError(shlCalc, "Error", "Bad number");
					return;
				}
				try {
					number2 = Integer.parseInt( tNum2.getText());
				}
				catch (Exception exc)
				{
					MessageDialog.openError(shlCalc, "Error", "Bad number");
					return;
				}
				int iRes=  number1+number2;
				String res="Answer is " +iRes;
				MessageDialog.openInformation(shlCalc, null, res);
			}
		});
		btnPlus.setText("+");
		
		table = new Table(shlCalc, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		formToolkit.adapt(table);
		formToolkit.paintBordersFor(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		Label lblNewLabel = new Label(shlCalc, SWT.NONE);
		lblNewLabel.setText("NUm2");
		
		tNum2 = new Text(shlCalc, SWT.BORDER);
		
		Button btnMinus = new Button(shlCalc, SWT.NONE);
		btnMinus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnMinus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		btnMinus.setText("-");
		new Label(shlCalc, SWT.NONE);
		
		Scale scale = new Scale(shlCalc, SWT.NONE);
		scale.addControlListener(new ControlAdapter() {
			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
		scale.setMaximum(70);
		scale.setMinimum(5);
		formToolkit.adapt(scale, true, true);
		new Label(shlCalc, SWT.NONE);
		new Label(shlCalc, SWT.NONE);
		new Label(shlCalc, SWT.NONE);
		new Label(shlCalc, SWT.NONE);
		new Label(shlCalc, SWT.NONE);
		Label label = new Label(shlCalc, SWT.NONE);
		formToolkit.adapt(label, true, true);
		Label label_1 = new Label(shlCalc, SWT.NONE);
		formToolkit.adapt(label_1, true, true);
		Label label_2 = new Label(shlCalc, SWT.NONE);
		formToolkit.adapt(label_2, true, true);
		new Label(shlCalc, SWT.NONE);
		Label label_3 = new Label(shlCalc, SWT.NONE);
		formToolkit.adapt(label_3, true, true);
		new Label(shlCalc, SWT.NONE);
		
		Menu menu = new Menu(shlCalc, SWT.BAR);
		shlCalc.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.RADIO);
		mntmFile.setText("File");
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("New SubMenu");
		
		Menu menu_2 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_2);
		
		MenuItem mntmNewItem_2 = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem_2.setText("New Item");
		
		MenuItem mntmNewItem_3 = new MenuItem(menu_2, SWT.NONE);
		mntmNewItem_3.setText("New Item");
		
		MenuItem mntmEdit = new MenuItem(menu, SWT.NONE);
		mntmEdit.setText("Edit");
		new Label(shlCalc, SWT.NONE);
		new Label(shlCalc, SWT.NONE);

	}
}
