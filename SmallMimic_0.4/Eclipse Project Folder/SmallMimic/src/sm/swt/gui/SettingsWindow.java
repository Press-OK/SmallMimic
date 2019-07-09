package sm.swt.gui;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import gnu.io.CommPortIdentifier;
import sm.data.PresetFile;
import sm.main.SmallMimic;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class SettingsWindow {
	protected Display display;
	protected Shell shell;
	private SmallMimic parent;
	private HashSet<CommPortIdentifier> ports = new HashSet<>();

	private CommPortIdentifier port = null;
	private PresetFile preset = null;
	private int rate = 0;
	private Text txtPreset;
	
	public SettingsWindow(HashSet<CommPortIdentifier> ports, SmallMimic parent) {
		this.ports = ports;
		this.parent = parent;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon.png"));
		shell.setSize(252, 212);
		shell.setText("SmallMimic");
		shell.setLocation(display.getClientArea().width/2 - shell.getBounds().width/2, display.getClientArea().height/2 - shell.getBounds().height/2);
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				System.exit(0);
		    }
		});
		
		Label lblPort = new Label(shell, SWT.NONE);
		lblPort.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));
		lblPort.setAlignment(SWT.RIGHT);
		lblPort.setBounds(10, 11, 107, 20);
		lblPort.setText("COM port:");
		
		Label lblBaudRate = new Label(shell, SWT.NONE);
		lblBaudRate.setText("Baud rate:");
		lblBaudRate.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));
		lblBaudRate.setAlignment(SWT.RIGHT);
		lblBaudRate.setBounds(10, 37, 107, 20);
		
		Label lblMouseTargetPresets = new Label(shell, SWT.NONE);
		lblMouseTargetPresets.setText("Mouse preset:");
		lblMouseTargetPresets.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));
		lblMouseTargetPresets.setAlignment(SWT.RIGHT);
		lblMouseTargetPresets.setBounds(10, 63, 107, 20);
		
		Combo cmdPort = new Combo(shell, SWT.READ_ONLY);
		cmdPort.setBounds(123, 10, 114, 21);
		
		Combo cmdRate = new Combo(shell, SWT.NONE);
		cmdRate.setBounds(123, 37, 114, 21);
		
		txtPreset = new Text(shell, SWT.BORDER);
		txtPreset.setEditable(false);
		txtPreset.setText("New Preset");
		txtPreset.setBounds(123, 64, 88, 21);
		
		Button btnLoadPreset = new Button(shell, SWT.NONE);
		btnLoadPreset.setBounds(213, 64, 24, 21);
		btnLoadPreset.setText("...");
		btnLoadPreset.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
		        fd.setText("Load preset");
		        fd.setFilterPath(System.getProperty("user.dir"));
		        String[] filterExt = { "*.smp" };
		        fd.setFilterExtensions(filterExt);
		        String path = fd.open();
		        if (path != null) {
		        	try {
						FileInputStream fileIn = new FileInputStream(path);
						ObjectInputStream objectIn = new ObjectInputStream(fileIn);
						preset = (PresetFile) objectIn.readObject();
						objectIn.close();
				        txtPreset.setText(path.substring(path.lastIndexOf('\\') + 1));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
		        }
	        }
	    });
		
		Button btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(10, 100, 227, 31);
		btnStart.setText("Start Listening");
		btnStart.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Validation TODO
				for (CommPortIdentifier i : ports) {
					if (i.getName().equals(cmdPort.getText())) port = i;
				}
				rate = Integer.parseInt(cmdRate.getText());
				if (preset == null) {
					preset = new PresetFile();
				}
				
				// Set
				parent.setPort(port);
				parent.setRate(rate);
				parent.setPreset(preset);
				parent.setListening(true);
				shell.dispose();
	        }
	    });
		
		Button btnQuit = new Button(shell, SWT.NONE);
		btnQuit.setBounds(10, 137, 227, 23);
		btnQuit.setText("Quit");
		btnQuit.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				System.exit(0);
	        }
	    });
		
		for (CommPortIdentifier i : ports) {
			cmdPort.add(i.getName());
		}
		cmdPort.select(0);
		
		cmdRate.add("9600");
		cmdRate.add("57600");
		cmdRate.add("115200");
		cmdRate.add("256000");
		cmdRate.add("512000");
		cmdRate.add("921600");
		cmdRate.select(2);
		
		Label lblAbout = new Label(shell, SWT.NONE);
		lblAbout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_INFORMATION | SWT.OK);
				msgBox.setText("SmallMimic");
				msgBox.setMessage("SmallMimic v0.3\nBy Sean Berwick, 2019\n\nFor license and more information,\nplease check the documentation\nor visit <link tbd>.");
				msgBox.open();
			}
		});
		lblAbout.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblAbout.setAlignment(SWT.RIGHT);
		lblAbout.setBounds(188, 166, 49, 13);
		lblAbout.setText("About");
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}