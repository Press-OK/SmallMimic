package sm.main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import sm.data.PresetFile;
import sm.swt.gui.SettingsWindow;
import sm.swt.gui.TrayIcon;

public class SmallMimic implements SerialPortEventListener {
	private Display display = new Display();
	private Shell shell = new Shell(display);
	
	private HashSet<CommPortIdentifier> ports = new HashSet<>();
	private CommPortIdentifier portID = null;
	private PresetFile preset = null;
	private SerialPort port = null;
	private String portName = "";
	private int rate = 0;
	private boolean attemptReconnects = true;
	private BufferedReader input;
	private RobotHandler r = new RobotHandler(this);
	
	private boolean isListening = false;
	private TrayIcon tray;

	public static void main(String[] args) {
		SmallMimic top = new SmallMimic();
		top.init();
	}
	
    private void init() {    	
    	// Start by reading ports since we can't do anything without a device...
		commQueryPorts();
		while (ports.size() == 0) {
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
		
		// If there are ports available, display the initial settings window
		try {
			SettingsWindow window = new SettingsWindow(ports, this);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			shutdown();
		}

		// Open the serial port and set parameters, create a listener for serial
		commConnectToSelectedPort();
		
		// Initialize the sys tray icon and keep alive as long as it exists
		try {
			tray = new TrayIcon(this);
			tray.open();
			tray.setIconState(3);
		} catch (Exception e) {
			e.printStackTrace();
			shutdown();
		}
	}

	private void commQueryPorts() {
        @SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        ports = new HashSet<>();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier i = portEnum.nextElement();
            if (i.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	ports.add(i);
            }
        }
    }
	
	private void commConnectToSelectedPort() {
		System.out.println("CONNECTING");
		try {
			port = (SerialPort) portID.open(this.getClass().getName(), 2000);
			port.setSerialPortParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			input = new BufferedReader(new InputStreamReader(port.getInputStream()));
			port.addEventListener(this);
			port.notifyOnDataAvailable(true);
			port.notifyOnFramingError(true);
			port.notifyOnOverrunError(true);
			port.notifyOnParityError(true);
		} catch (Exception e) {
			e.printStackTrace();
			shutdown();
		}
		System.out.println("CONNECTED TO " + port.getName());
	}
	
	private void commReconnect(int et) {
		setListening(false);
		commClosePort();
		tray.setIconState(0);
		if (this.attemptReconnects) {
			while (true) {
				System.out.println("Looking for: " + portName);
				commQueryPorts();
				boolean f = false;
				for (CommPortIdentifier id : ports) {
					System.out.println("Found: " + id.getName());
					if (id.getName().matches(portName)) {
						f = true;
						portID = id;
						port = null;
						break;
					}
				}
				if (f) {
					System.out.println("Port recovered");
					break;
				} else {
					System.out.println("Port not in list");
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("Reconnecting");
			commConnectToSelectedPort();
		}
	}
	
	private synchronized void commClosePort() {
		if (port != null) {
			System.out.println("PORT CLOSED BY APP");
			port.notifyOnDataAvailable(false);
			port.notifyOnFramingError(false);
			port.notifyOnOverrunError(false);
			port.notifyOnParityError(false);
			port.removeEventListener();
			port.close();
			port = null;
			portID = null;
		}
	}

	@Override
	public synchronized void serialEvent(SerialPortEvent e) {
		System.out.println("SERIALEVENT: " + e.toString());
		int et = e.getEventType();
		if (et == SerialPortEvent.FE || et == SerialPortEvent.OE || et == SerialPortEvent.PE) {
			commReconnect(et);
		} else if (this.isListening) {
			if (et == SerialPortEvent.DATA_AVAILABLE) {
				try {
					String s = input.readLine();
					String head = s.substring(0,1);
					String action = "";
					String text = "";
					int x = 0, y = 0, time = 0, target = 0, button = 0,
						signX = 0, signY = 0;
					if (head.matches("k")) {
						action = s.substring(1,2);
						switch (action) {
							case "s":
								time = Integer.parseInt(s.substring(2, 7));
								text = s.substring(7);
								r.sendKeys(text, time);
								tray.setIconState(2);
								break;
							case "d":
								text = s.substring(2);
								r.keyDown(text);
								tray.setIconState(2);
								break;
							case "u":
								text = s.substring(2);
								r.keyUp(text);
								tray.setIconState(2);
								break;
							default:
								// Invalid msg
								break;
						}
					} else if (head.matches("m")) {
						action = s.substring(1,2);
						switch (action) {
							case "m":
								x = Integer.parseInt(s.substring(2, 6));
								y = Integer.parseInt(s.substring(6, 10));
								time = Integer.parseInt(s.substring(10));
								r.mouseMove(x, y, time);
								tray.setIconState(2);
								break;
							case "n":
								signX = Integer.parseInt(s.substring(2, 3));
								x = Integer.parseInt(s.substring(3, 7));
								signY = Integer.parseInt(s.substring(7, 8));
								y = Integer.parseInt(s.substring(8));
								x = signX == 1 ? -x : x;
								y = signY == 1 ? -y : y;
								r.mouseNudge(x, y);
								tray.setIconState(2);
								break;
							case "t":
								target = Integer.parseInt(s.substring(2, 7));
								time = Integer.parseInt(s.substring(7));
								tray.setIconState(2);
								break;
							case "c":
								button = Integer.parseInt(s.substring(2));
								r.mouseClick(button);
								tray.setIconState(2);
								break;
							case "d":
								button = Integer.parseInt(s.substring(2));
								r.mouseDown(button);
								tray.setIconState(2);
								break;
							case "u":
								button = Integer.parseInt(s.substring(2));
								r.mouseUp(button);
								tray.setIconState(2);
								break;
							default:
								// Invalid msg
								break;
						}
					} else if (head.matches("p")) {
						handlePing();
					} else {
						// Quickly discard invalid message
					}
				} catch (Exception x) {
					// flush
					try {
						input.readLine();
					} catch (IOException e1) {
					}
					commReconnect(et);
				}
			}
		}
	}
	
	private void handlePing() {
		tray.ping();
	}
	
	public void relayIconChange(int i) {
		tray.setIconState(i);
	}
	
	public void setPort(CommPortIdentifier portID) {
		this.portID = portID;
		this.portName = portID.getName();
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public void setPreset(PresetFile preset) {
		this.preset = preset;
	}
	
	public void setReconnects(boolean r) {
		this.attemptReconnects = r;
	}

	public PresetFile getPreset() {
		return this.preset;
	}

	public boolean isListening() {
		return isListening;
	}

	public void setListening(boolean isListening) {
		this.isListening = isListening;
	}
	
	public void shutdown() {
		commClosePort();
		System.exit(0);
	}
}