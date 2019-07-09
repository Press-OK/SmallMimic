package sm.main;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class RobotManager {
	private SmallMimic parent;
	private Robot r;
	
	public RobotManager(SmallMimic parent) {
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		this.parent = parent;
	}
	
	public void sendKeys(String s, int t) throws InterruptedException {
		if (!s.isEmpty()) {
			String special = "";
			boolean esc = false;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (special.isEmpty()) {
					if (c == '\\') {
						esc = true;
					} else if (c == '{' && esc == false) {
						special = "{";
					} else if (c == '^' && esc == false) {
						r.keyPress(KeyEvent.VK_CONTROL);
					} else if (c == '!' && esc == false) {
						r.keyPress(KeyEvent.VK_ALT);
					} else if (c == '+' && esc == false) {
						r.keyPress(KeyEvent.VK_SHIFT);
					} else if (c == '#' && esc == false) {
						r.keyPress(KeyEvent.VK_WINDOWS);
					} else if ("~!@#$%^&*()_+{}|:\"<>?".indexOf(c) != -1) {
						if (getBaseKey(c) != 0) {
							r.keyPress(KeyEvent.VK_SHIFT);
							r.keyPress(getBaseKey(c));
							r.keyRelease(getBaseKey(c));
						}
						releaseModifierKeys();
						esc = false;
						Thread.sleep(t-5);
					} else {
				        if (Character.isUpperCase(c)) {
				            r.keyPress(KeyEvent.VK_SHIFT);
				        }
				        int code = KeyEvent.getExtendedKeyCodeForChar(Character.toUpperCase(c));
				        r.keyPress(code);
				        r.keyRelease(code);
				        releaseModifierKeys();
				        esc = false;
		                Thread.sleep(t-5);
					}
				} else {
					if (c == '}' && esc == false) {
						int specialKeycode = getSpecialKeycode(special.substring(1));
						if (specialKeycode != 0) {
							r.keyPress(specialKeycode);
							r.keyRelease(specialKeycode);
						}
						special = "";
				        releaseModifierKeys();
				        esc = false;
		                Thread.sleep(t-5);
					} else {
						special = special + c;
					}
				}
			}
			parent.relayIconChange(3);
		}
	}
	
	public void keyDown(String s) {
		if (!s.isEmpty()) {
			String special = "";
			boolean esc = false;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (special.isEmpty()) {
					if (c == '\\') {
						esc = true;
					} else if (c == '{' && esc == false) {
						special = "{";
					} else if (c == '^' && esc == false) {
						r.keyPress(KeyEvent.VK_CONTROL);
					} else if (c == '!' && esc == false) {
						r.keyPress(KeyEvent.VK_ALT);
					} else if (c == '+' && esc == false) {
						r.keyPress(KeyEvent.VK_SHIFT);
					} else if (c == '#' && esc == false) {
						r.keyPress(KeyEvent.VK_WINDOWS);
					} else if ("~!@#$%^&*()_+{}|:\"<>?".indexOf(c) != -1) {
						if (getBaseKey(c) != 0) {
							r.keyPress(KeyEvent.VK_SHIFT);
							r.keyPress(getBaseKey(c));
						}
						break;
					} else {
				        if (Character.isUpperCase(c)) {
				            r.keyPress(KeyEvent.VK_SHIFT);
				        }
				        int code = KeyEvent.getExtendedKeyCodeForChar(Character.toUpperCase(c));
				        r.keyPress(code);
				        break;
					}
				} else {
					if (c == '}' && esc == false) {
						int specialKeycode = getSpecialKeycode(special.substring(1));
						if (specialKeycode != 0) {
							r.keyPress(specialKeycode);
						}
				        break;
					} else {
						special = special + c;
					}
				}
			}
			parent.relayIconChange(3);
		}
	}
	
	public void keyUp(String s) {
		if (!s.isEmpty()) {
			String special = "";
			boolean esc = false;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (special.isEmpty()) {
					if (c == '\\') {
						esc = true;
					} else if (c == '{' && esc == false) {
						special = "{";
					} else if (c == '^' && esc == false) {
						r.keyPress(KeyEvent.VK_CONTROL);
					} else if (c == '!' && esc == false) {
						r.keyPress(KeyEvent.VK_ALT);
					} else if (c == '+' && esc == false) {
						r.keyPress(KeyEvent.VK_SHIFT);
					} else if (c == '#' && esc == false) {
						r.keyPress(KeyEvent.VK_WINDOWS);
					} else if ("~!@#$%^&*()_+{}|:\"<>?".indexOf(c) != -1) {
						if (getBaseKey(c) != 0) {
							r.keyRelease(getBaseKey(c));
						}
						releaseModifierKeys();
						esc = false;
					} else {
				        int code = KeyEvent.getExtendedKeyCodeForChar(Character.toUpperCase(c));
				        r.keyRelease(code);
				        releaseModifierKeys();
				        esc = false;
					}
				} else {
					if (c == '}' && esc == false) {
						int specialKeycode = getSpecialKeycode(special.substring(1));
						if (specialKeycode != 0) {
							r.keyRelease(specialKeycode);
						}
						special = "";
				        releaseModifierKeys();
				        esc = false;
					} else {
						special = special + c;
					}
				}
			}
			parent.relayIconChange(3);
		}
	}
	
	public void mouseMove(int x, int y, int t) throws InterruptedException {
		if (t == 0) {
			r.mouseMove(x, y);
		} else {
			float mouseTickMS = 25;	// Move the mouse in increments of this (milliseconds):
			PointerInfo pi = MouseInfo.getPointerInfo();
			Point origin = pi.getLocation();
			int xdiff = x - origin.x, ydiff = y - origin.y;
			float xstep = xdiff / (t / mouseTickMS), ystep = ydiff / (t / mouseTickMS);
			for (int i = 0; i <= t; i+=mouseTickMS) {
				r.mouseMove(origin.x + (int)(Math.floor(xstep * (i / mouseTickMS))), origin.y + (int)(Math.floor(ystep * (i / mouseTickMS))));
				Thread.sleep((int)mouseTickMS);
			}
			r.mouseMove(x, y);
		}
		parent.relayIconChange(3);
	}
	
	public void mouseTarget(String text, int t) throws InterruptedException {
		for (String s : parent.getPreset().getTargets().keySet()) {
			if (text.equals(s)) {
				Point p = parent.getPreset().getTargets().get(s);
				if (t == 0) {
					r.mouseMove(p.x, p.y);
				} else {
					float mouseTickMS = 25;	// Move the mouse in increments of this (milliseconds):
					PointerInfo pi = MouseInfo.getPointerInfo();
					Point origin = pi.getLocation();
					int xdiff = p.x - origin.x, ydiff = p.y - origin.y;
					float xstep = xdiff / (t / mouseTickMS), ystep = ydiff / (t / mouseTickMS);
					for (int i = 0; i <= t; i+=mouseTickMS) {
						r.mouseMove(origin.x + (int)(Math.floor(xstep * (i / mouseTickMS))), origin.y + (int)(Math.floor(ystep * (i / mouseTickMS))));
						Thread.sleep((int)mouseTickMS);
					}
					r.mouseMove(p.x, p.y);
				}
				parent.relayIconChange(3);
				break;
			}
		}
	}
	
	public void mouseNudge(int x, int y) {
		PointerInfo pi = MouseInfo.getPointerInfo();
		Point origin = pi.getLocation();
		r.mouseMove(origin.x + x, origin.y + y);
		parent.relayIconChange(3);
	}
	
	public void mouseClick(int b) {
		if (b == 0) {
			r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} else if (b == 1) {
			r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
			r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		} else if (b == 2) {
			r.mousePress(InputEvent.BUTTON2_DOWN_MASK);
			r.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
		}
		parent.relayIconChange(3);
	}
	
	public void mouseDown(int b) {
		if (b == 0) {
			r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		} else if (b == 1) {
			r.mousePress(InputEvent.BUTTON3_DOWN_MASK);
		} else if (b == 2) {
			r.mousePress(InputEvent.BUTTON2_DOWN_MASK);
		}
		parent.relayIconChange(3);
	}
	
	public void mouseUp(int b) {
		if (b == 0) {
			r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		} else if (b == 1) {
			r.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
		} else if (b == 2) {
			r.mouseRelease(InputEvent.BUTTON2_DOWN_MASK);
		}
		parent.relayIconChange(3);
	}
	
	private int getSpecialKeycode(String s) {
		switch (s.toLowerCase()) {
			case "esc":
			case "escape":
				return KeyEvent.VK_ESCAPE;
			case "f1":
				return KeyEvent.VK_F1;
			case "f2":
				return KeyEvent.VK_F2;
			case "f3":
				return KeyEvent.VK_F3;
			case "f4":
				return KeyEvent.VK_F4;
			case "f5":
				return KeyEvent.VK_F5;
			case "f6":
				return KeyEvent.VK_F6;
			case "f7":
				return KeyEvent.VK_F7;
			case "f8":
				return KeyEvent.VK_F8;
			case "f9":
				return KeyEvent.VK_F9;
			case "f10":
				return KeyEvent.VK_F10;
			case "f11":
				return KeyEvent.VK_F11;
			case "f12":
				return KeyEvent.VK_F12;
			case "prtscn":
			case "prntscr":
			case "printscreen":
				return KeyEvent.VK_PRINTSCREEN;
			case "scrolllock":
				return KeyEvent.VK_SCROLL_LOCK;
			case "pause":
				return KeyEvent.VK_PAUSE;
			case "bcksp":
			case "backspace":
				return KeyEvent.VK_BACK_SPACE;
			case "ins":
			case "insert":
				return KeyEvent.VK_INSERT;
			case "home":
				return KeyEvent.VK_HOME;
			case "pgup":
			case "pageup":
				return KeyEvent.VK_PAGE_UP;
			case "numlock":
				return KeyEvent.VK_NUM_LOCK;
			case "tab":
				return KeyEvent.VK_TAB;
			case "del":
			case "delete":
				return KeyEvent.VK_DELETE;
			case "end":
				return KeyEvent.VK_END;
			case "pgdn":
			case "pagedown":
				return KeyEvent.VK_PAGE_DOWN;
			case "capslock":
				return KeyEvent.VK_CAPS_LOCK;
			case "ret":
			case "return":
			case "enter":
				return KeyEvent.VK_ENTER;
			case "shift":
				return KeyEvent.VK_SHIFT;
			case "ctrl":
			case "control":
				return KeyEvent.VK_CONTROL;
			case "alt":
				return KeyEvent.VK_ALT;
			case "win":
			case "windows":
				return KeyEvent.VK_WINDOWS;
			case "menu":
			case "context":
			case "contextmenu":
				return KeyEvent.VK_CONTEXT_MENU;
			case "up":
			case "uparrow":
				return KeyEvent.VK_UP;
			case "down":
			case "downarrow":
				return KeyEvent.VK_DOWN;
			case "left":
			case "leftarrow":
				return KeyEvent.VK_LEFT;
			case "right":
			case "rightarrow":
				return KeyEvent.VK_RIGHT;
			case "numpadup":
			case "numpaduparrow":
				return KeyEvent.VK_KP_UP;
			case "numpaddown":
			case "numpaddownarrow":
				return KeyEvent.VK_KP_DOWN;
			case "numpadleft":
			case "numpadleftarrow":
				return KeyEvent.VK_KP_LEFT;
			case "numpadright":
			case "numpadrightarrow":
				return KeyEvent.VK_KP_RIGHT;
			case "num0":
			case "numpad0":
				return KeyEvent.VK_NUMPAD0;
			case "num1":
			case "numpad1":
				return KeyEvent.VK_NUMPAD1;
			case "num2":
			case "numpad2":
				return KeyEvent.VK_NUMPAD2;
			case "num3":
			case "numpad3":
				return KeyEvent.VK_NUMPAD3;
			case "num4":
			case "numpad4":
				return KeyEvent.VK_NUMPAD4;
			case "num5":
			case "numpad5":
				return KeyEvent.VK_NUMPAD5;
			case "num6":
			case "numpad6":
				return KeyEvent.VK_NUMPAD6;
			case "num7":
			case "numpad7":
				return KeyEvent.VK_NUMPAD7;
			case "num8":
			case "numpad8":
				return KeyEvent.VK_NUMPAD8;
			case "num9":
			case "numpad9":
				return KeyEvent.VK_NUMPAD9;
			default:
				return 0;
		}
	}
	
	private int getBaseKey(char c) {
		switch (c) {
			case '~':
				return KeyEvent.VK_BACK_QUOTE;
			case '!':
				return '1';
			case '@':
				return '2';
			case '#':
				return '3';
			case '$':
				return '4';
			case '%':
				return '5';
			case '^':
				return '6';
			case '&':
				return '7';
			case '*':
				return '8';
			case '(':
				return '9';
			case ')':
				return '0';
			case '_':
				return KeyEvent.VK_MINUS;
			case '+':
				return KeyEvent.VK_EQUALS;
			case '{':
				return KeyEvent.VK_OPEN_BRACKET;
			case '}':
				return KeyEvent.VK_CLOSE_BRACKET;
			case '|':
				return KeyEvent.VK_BACK_SLASH;
			case ':':
				return KeyEvent.VK_SEMICOLON;
			case '\"':
				return KeyEvent.VK_QUOTE;
			case '<':
				return KeyEvent.VK_COMMA;
			case '>':
				return KeyEvent.VK_PERIOD;
			case '?':
				return KeyEvent.VK_SLASH;
			default:
				return 0;
		}
	}
	
	private void releaseModifierKeys() {
		r.keyRelease(KeyEvent.VK_SHIFT);
		r.keyRelease(KeyEvent.VK_CONTROL);
		r.keyRelease(KeyEvent.VK_ALT);
		r.keyRelease(KeyEvent.VK_WINDOWS);
	}
}
