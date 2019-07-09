package sm.swt.gui;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import sm.data.PresetFile;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseEvent;

public class MouseSpyWindow {
	protected TrayIcon parent;
	protected Display display;
	protected Shell shell;
	private CLabel lblPositionHint;
	private Thread mousePollingThread;
	private boolean isPollingMouse = false;
	private PresetFile preset;
	private List lstTargets;
	
	public MouseSpyWindow(TrayIcon parent) {
		this.parent = parent;
		this.preset = parent.getParent().getPreset();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		display = Display.getDefault();
		shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon.png"));
		shell.setSize(244, 392);
		shell.setText("SmallMimic - Mouse Spy");
		shell.setLocation(display.getClientArea().width - shell.getBounds().width, display.getClientArea().height - shell.getBounds().height - 16);
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				closeSpy();
		    }
		});
		
		lblPositionHint = new CLabel(shell, SWT.NONE);
		lblPositionHint.setFont(SWTResourceManager.getFont("Tahoma", 12, SWT.NORMAL));
		lblPositionHint.setBounds(10, 10, 218, 25);
		lblPositionHint.setText("Mouse position: (0, 0)");
		
		Label lblTargetHint = new Label(shell, SWT.NONE);
		lblTargetHint.setAlignment(SWT.CENTER);
		lblTargetHint.setBounds(10, 49, 218, 13);
		lblTargetHint.setText("(CTRL+F12 adds new target at this pos.)");
		
		Label lblBtnTip = new Label(shell, SWT.NONE);
		lblBtnTip.setBounds(10, 316, 218, 13);
		
		Button btnLoad = new Button(shell, SWT.NONE);
		btnLoad.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				lblBtnTip.setText("Load target list");
			}
			@Override
			public void mouseExit(MouseEvent e) {
				lblBtnTip.setText("");
			}
		});
		btnLoad.setImage(SWTResourceManager.getImage(MouseSpyWindow.class, "/javax/swing/plaf/metal/icons/ocean/directory.gif"));
		btnLoad.setBounds(10, 335, 25, 25);
		btnLoad.addListener(SWT.Selection, new Listener() {
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
						updateTargetsList();
						lblBtnTip.setText("Loaded preset: " + path.substring(path.lastIndexOf('\\') + 1));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
		        }
	        }
	    });
		
		Button btnSave = new Button(shell, SWT.NONE);
		btnSave.setImage(SWTResourceManager.getImage(MouseSpyWindow.class, "/javax/swing/plaf/metal/icons/ocean/floppy.gif"));
		btnSave.setBounds(36, 335, 25, 25);
		btnSave.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				lblBtnTip.setText("Save target list");
			}
			@Override
			public void mouseExit(MouseEvent e) {
				lblBtnTip.setText("");
			}
		});
		btnSave.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (!preset.getTargets().isEmpty()) {
					FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				    dialog.setFilterNames(new String[] { "SmallMimic Presets" });
				    dialog.setFilterExtensions(new String[] { "*.smp" });
				    dialog.setFilterPath(System.getProperty("user.dir"));
				    dialog.setFileName("NewPreset.smp");
				    String path = dialog.open();
					try {
						FileOutputStream fileOut = new FileOutputStream(path);
						ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
						objectOut.writeObject(preset);
						objectOut.close();
						lblBtnTip.setText("Saved preset: " + path.substring(path.lastIndexOf('\\') + 1));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
	        }
	    });
		
		Button btnDelete = new Button(shell, SWT.NONE);
		btnDelete.setImage(SWTResourceManager.getImage(MouseSpyWindow.class, "/org/eclipse/jface/dialogs/images/message_error.png"));
		btnDelete.setBounds(62, 335, 25, 25);
		btnDelete.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseEnter(MouseEvent e) {
				lblBtnTip.setText("Delete selected target");
			}
			@Override
			public void mouseExit(MouseEvent e) {
				lblBtnTip.setText("");
			}
		});
		btnDelete.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int sel = lstTargets.getSelectionIndex();
				if (sel >= 0) {
					preset.getTargets().remove(preset.getTargets().keySet().toArray()[sel]);
					updateTargetsList();
					lblBtnTip.setText("Deleted target #" + sel);
				}
	        }
	    });
		
		lstTargets = new List(shell, SWT.BORDER);
		lstTargets.setBounds(10, 81, 218, 229);
		updateTargetsList();
		
		Button btnClose = new Button(shell, SWT.NONE);
		btnClose.setBounds(160, 336, 68, 23);
		btnClose.setText("Close");
		
		btnClose.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				closeSpy();
	        }
		});
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				event.doit = false;
				closeSpy();
			}
	    });
		
		startMousePolling();
		
	    display.addFilter(SWT.KeyDown, e -> {
	    	if (shell.equals(Display.getCurrent().getActiveShell())) {
				if ((e.stateMask & SWT.CTRL) != 0) {
					if (e.keyCode == SWT.F12) {
						try {
							stopMousePolling();
							Point p = MouseInfo.getPointerInfo().getLocation();
							int c = preset.getTargets().size();
							while (preset.getTargets().containsKey("Target " + c)) {
								c++;
							}
							RenameWindow renameWindow = new RenameWindow("Target " + c, p, this);
							renameWindow.open();
							startMousePolling();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
	        }
	    });
		
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		closeSpy();
	}
	
	private void updateTargetsList() {
		lstTargets.removeAll();
		int c = 0;
		for (String s : preset.getTargets().keySet()) {
			lstTargets.add(c + ":   " + s + "  (" + preset.getTargets().get(s).x + ", " + preset.getTargets().get(s).y + ")");
			c++;
		}
	}
	
	public void addNewTarget(String s, Point p) {
		preset.getTargets().put(s, p);
		updateTargetsList();
		
	}

	public synchronized void updateMouseCoords() {
		display.asyncExec (new Runnable () {
	    	public void run () {
				PointerInfo pi = MouseInfo.getPointerInfo();
				Point c = pi.getLocation();
	    		lblPositionHint.setText("Mouse position: ("+c.x+", "+c.y+")");
	    		if (!shell.isDisposed()) shell.redraw();
	    	}
	    });
	}
	
	public void startMousePolling() {
		if (!isPollingMouse) {
			isPollingMouse = true;
			mousePollingThread = new Thread() {
				public void run() {
					while (isPollingMouse) {
						updateMouseCoords();
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			mousePollingThread.start();
		}
	}
	
	public void stopMousePolling() {
		isPollingMouse = false;
	}

	private void closeSpy() {
		stopMousePolling();
		parent.resumeMain();
		shell.setVisible(false);
		parent.setIconState(3);
	}
}