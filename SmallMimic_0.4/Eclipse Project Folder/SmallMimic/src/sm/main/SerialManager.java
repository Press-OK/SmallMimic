package sm.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialManager implements SerialPortEventListener {
	
	private SmallMimic parent;
	private RobotManager r;
	
	private HashSet<CommPortIdentifier> ports = new HashSet<>();
	private CommPortIdentifier portID;
	private SerialPort port;
	private String portName;
	private int rate;
	private BufferedReader input;
	private boolean listening = false;

	public SerialManager (SmallMimic parent) {
		this.parent = parent;
		this.r = new RobotManager(parent);
	}
	
	public void setListening(boolean l) {
		listening = l;
	}
	
	public HashSet<CommPortIdentifier> queryPorts() {
        @SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        HashSet<CommPortIdentifier> ports = new HashSet<>();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier i = portEnum.nextElement();
            if (i.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	ports.add(i);
            }
        }
        return ports;
	}
	
	public boolean openPort(CommPortIdentifier pi, int r) {
		try {
			portID = pi;
			rate = r;
			port = (SerialPort) pi.open(this.getClass().getName(), 2000);
			port.setSerialPortParams(rate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			port.notifyOnDataAvailable(true);
			port.addEventListener(this);
			input = new BufferedReader(new InputStreamReader(port.getInputStream()));
			portName = portID.getName();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean closePort() {
		if (port != null) {
			ports = null;
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			port.notifyOnDataAvailable(false);
			port.removeEventListener();
			port.close();
			port = null;
			portID = null;
			listening = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean reconnectPort() {
		listening = false;
		closePort();
		try {
			while (true) {
				queryPorts();
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
					break;
				} else {
					Thread.sleep(1500);
				}
			}
			Thread.sleep(5000);
			openPort(portID, rate);
			return true;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent e) {
		if (listening) {
			if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					String s = input.readLine();
					String head = s.substring(0,1);
					String action = "";
					String text = "";
					int x = 0, y = 0, time = 0, button = 0,
						signX = 0, signY = 0;
					if (head.matches("k")) {
						action = s.substring(1,2);
						switch (action) {
							case "s":
								time = Integer.parseInt(s.substring(2, 7));
								text = s.substring(7);
								r.sendKeys(text, time);
								break;
							case "d":
								text = s.substring(2);
								r.keyDown(text);
								break;
							case "u":
								text = s.substring(2);
								r.keyUp(text);
								break;
							default:
								// Invalid msg
								break;
						}
						parent.relayIconChange(2);
					} else if (head.matches("m")) {
						action = s.substring(1,2);
						switch (action) {
							case "m":
								x = Integer.parseInt(s.substring(2, 6));
								y = Integer.parseInt(s.substring(6, 10));
								time = Integer.parseInt(s.substring(10));
								r.mouseMove(x, y, time);
								break;
							case "n":
								signX = Integer.parseInt(s.substring(2, 3));
								x = Integer.parseInt(s.substring(3, 7));
								signY = Integer.parseInt(s.substring(7, 8));
								y = Integer.parseInt(s.substring(8));
								x = signX == 1 ? -x : x;
								y = signY == 1 ? -y : y;
								r.mouseNudge(x, y);
								break;
							case "t":
								time = Integer.parseInt(s.substring(2, 7));
								text = s.substring(7);
								r.mouseTarget(text, time);
								break;
							case "c":
								button = Integer.parseInt(s.substring(2));
								r.mouseClick(button);
								break;
							case "d":
								button = Integer.parseInt(s.substring(2));
								r.mouseDown(button);
								break;
							case "u":
								button = Integer.parseInt(s.substring(2));
								r.mouseUp(button);
								break;
							default:
								// Invalid msg
								break;
						}
						parent.relayIconChange(2);
					} else if (head.matches("p")) {
						parent.relayIconChange(4);
					} else {
						// Discard invalid message
					}
				} catch (Exception x) {
					System.out.println("BAD ------------------------");
					parent.relayIconChange(0);
					// flush
					try {
						input.readLine();
					} catch (IOException e1) {
					}
					reconnectPort();
				}
			}
		}
	}
}
