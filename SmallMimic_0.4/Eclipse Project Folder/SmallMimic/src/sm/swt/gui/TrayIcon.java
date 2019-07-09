package sm.swt.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.wb.swt.SWTResourceManager;

import sm.main.SmallMimic;

public class TrayIcon {
	protected Display display;
	protected Shell shell;
	protected SmallMimic parent;
	protected MouseSpyWindow spy;
	private MenuItem mnuResumeListener;
	private MenuItem mnuPauseListener;
	private TrayIcon self;
	private TrayItem smTray;
	private int iconState = 0;
	
	public TrayIcon(SmallMimic parent) {
		display = Display.getDefault();
		shell = new Shell(display);
		this.parent = parent;
		self = this;
	}
	
	public void open() {
		final Tray winTray = Display.getDefault().getSystemTray();
	    smTray = new TrayItem(winTray, SWT.NONE);
	    final Menu menu = new Menu(shell, SWT.POP_UP);
	    smTray.setToolTipText("SmallMimic");
	    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon_green.png"));
	    smTray.addListener(SWT.MenuDetect, new Listener() {
	      public void handleEvent(Event event) {
	        menu.setVisible(true);
	      }
	    });
	    smTray.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	  	    menu.setVisible(true);
	      }
	    });

	    mnuResumeListener = new MenuItem(menu, SWT.PUSH);
	    mnuResumeListener.setText("Resume listener");
	    mnuResumeListener.setEnabled(false);
	    
	    mnuPauseListener = new MenuItem(menu, SWT.PUSH);
	    mnuPauseListener.setText("Pause listener");
	    
	    mnuResumeListener.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event event) {
	    		resumeMain();
	    		setIconState(3);
	    	}
	    });
	    mnuPauseListener.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event event) {
	    		parent.setListening(false);
		    	mnuPauseListener.setEnabled(false);
	    		mnuResumeListener.setEnabled(true);
	    		setIconState(1);
	    	}
	    });

	    new MenuItem(menu, SWT.SEPARATOR);
	    
	    MenuItem mnuMouseSpy = new MenuItem(menu, SWT.PUSH);
	    mnuMouseSpy.setText("Mouse spy");
	    mnuMouseSpy.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event event) {
	    		parent.setListening(false);
		    	mnuPauseListener.setEnabled(false);
	    		mnuResumeListener.setEnabled(true);
	    		setIconState(1);
	    		if (spy != null) {
	    			spy.shell.setVisible(true);
	    			spy.startMousePolling();
	    			spy.shell.forceActive();
	    		} else {
		    		try {
		    			spy = new MouseSpyWindow(self);
		    			spy.open();
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			parent.shutdown();
		    		}
	    		}
	    	}
	    });
	    
	    new MenuItem(menu, SWT.SEPARATOR);
	    
	    MenuItem mnuQuit = new MenuItem(menu, SWT.PUSH);
	    mnuQuit.setText("Quit");
	    mnuQuit.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event event) {
	    		smTray.setVisible(false);
	    		parent.shutdown();
	    	}
	    });

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public SmallMimic getParent() {
		return this.parent;
	}
	
	public void resumeMain() {
		parent.setListening(true);
    	mnuPauseListener.setEnabled(true);
		mnuResumeListener.setEnabled(false);
		setIconState(3);
	}

	public void ping() {
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
			    try {
				    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/ping.png"));
					Thread.sleep(350);
				    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon.png"));
					Thread.sleep(200);
				    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/ping.png"));
					Thread.sleep(350);
				    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon.png"));
					Thread.sleep(200);
				    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/ping.png"));
					Thread.sleep(350);
				    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon_green.png"));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		    }
		});
	}
	
	public void setIconState(int s) {
		this.iconState = s;
		drawIcon();
	}
	
	private void drawIcon() {
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
				switch (iconState) {
					case 0:
					    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon.png"));
					    break;
					case 1:
					    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon_red.png"));
					    break;
					case 2:
					    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon_orange.png"));
					    break;
					case 3:
					    smTray.setImage(SWTResourceManager.getImage(SettingsWindow.class, "/sm/img/icon_green.png"));
					    break;
				}
		    }
		});
	}
}
