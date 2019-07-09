package sm.swt.gui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class RenameWindow {
	protected Display display;
	protected Shell shell;
	private MouseSpyWindow parent;
	private String placeholderName = "";
	private Text txtName;
	private Point p;
	
	public RenameWindow(String initial, Point p, MouseSpyWindow parent) {
		this.parent = parent;
		this.p = p;
		this.placeholderName = initial;
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon.png"));
		shell.setSize(194, 92);
		shell.setText("Target Name (" + p.x + ", " + p.y + ")");
		
		// Open the dialog towards the center of the screen so it doesn't go beyond the edge
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point c = pi.getLocation();
		int hdir = (c.x > display.getClientArea().width / 2) ? 1 : 0;
		int vdir = (c.y > display.getClientArea().height / 2) ? 1 : 0;
		shell.setLocation(c.x - shell.getBounds().width * hdir, c.y - shell.getBounds().height * vdir);
		
		Label lblNewTargetName = new Label(shell, SWT.NONE);
		lblNewTargetName.setBounds(10, 13, 87, 13);
		lblNewTargetName.setText("New target name:");
		
		txtName = new Text(shell, SWT.BORDER);
		txtName.setBounds(103, 10, 76, 19);
		txtName.setText(placeholderName);
		
		Button btnOkay = new Button(shell, SWT.NONE);
		btnOkay.setBounds(10, 38, 169, 20);
		btnOkay.setText("OK");
		btnOkay.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event event) {
	    		parent.addNewTarget(txtName.getText(), p);
	    		shell.dispose();
	    	}
	    });
		
		txtName.selectAll();
		
		shell.open();
		shell.layout();
		shell.forceActive();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
