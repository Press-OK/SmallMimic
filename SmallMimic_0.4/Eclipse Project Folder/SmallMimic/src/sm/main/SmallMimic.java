package sm.main;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import gnu.io.CommPortIdentifier;
import sm.data.PresetFile;
import sm.swt.gui.SettingsWindow;
import sm.swt.gui.TrayIcon;

public class SmallMimic {
	private SerialManager serial;
	private TrayIcon tray;

	private HashSet<CommPortIdentifier> ports = new HashSet<>();
	private CommPortIdentifier portID = null;
	private PresetFile preset = null;
	private int rate = 0;

	public static void main(String[] args) {
		SmallMimic top = new SmallMimic();
		top.init();
	}
	
    private void init() {
    	// Initialize a serial manager
    	serial = new SerialManager(this);
    	
    	// Enumerate available ports and offer to retry if none available
    	while ((ports = serial.queryPorts()).size() == 0) {
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.YES | SWT.NO);
			msgBox.setText("SmallMimic");
			msgBox.setMessage("Unable to find any usable serial devices. Try again?");
			int result = msgBox.open();
			switch (result) {
			    case SWT.YES:
			    	break;
			    case SWT.NO:
					shutdown();
			    	break;
		    }
    	}
		
		// Display the settings window
		try {
			SettingsWindow window = new SettingsWindow(ports, this);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			shutdown();
		}

		// Open the specified COM port and create handlers
		serial.openPort(portID, rate);
		serial.setListening(true);
		
		// Initialize the tray icon
		try {
			tray = new TrayIcon(this);
			tray.open();
			tray.setIconState(3);
		} catch (Exception e) {
			e.printStackTrace();
			shutdown();
		}
	}
	
	public void relayIconChange(int i) {
		if (i == 4) {
			tray.ping();
		} else {
			tray.setIconState(i);
		}
	}
	
	public void setPort(CommPortIdentifier portID) {
		this.portID = portID;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public void setPreset(PresetFile preset) {
		this.preset = preset;
	}

	public PresetFile getPreset() {
		return this.preset;
	}

	public void setListening(boolean l) {
		serial.setListening(l);
	}
	
	public void shutdown() {
		serial.closePort();
		System.exit(0);
	}
}