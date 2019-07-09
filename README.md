# SmallMimic
A Java application/uC Library for emulating keyboard+mouse control in Windows based on serial messages received from a connected microcontroller.

This requires the Java listener to be running on the host. It contains a minimal UI for the initial setup and for some convenience functions.

TLDR: It is similar to using a microcontroller as a HID mouse/keyboard, but without having to wire the device & flash firmware to get windows to recognize it as the device it is emulating. Communicates over raw serial with user-selected baud rate and COM port.



# uC API (C++)
The following functions can be used in any Sketch that includes the SmallMimic library, which have the resulting effect on the host machine:

    void ping();
        Causes the tray icon of the listener to flash.
        
    void sendKeys(char*);
        Send a string of keys for the listener to press one at a time.
        
    void sendKeys(char*, short);
        Same as above, but with a specified delay (MS) between keys.
        
    void keyDown(char*);
        Send a single key press with no release.
        
    void keyUp(char*);
        Send a single key release.
        
    void mouseMove(int, int);
        Move mouse to the specified X,Y coordinate.
        
    void mouseMove(int, int, short);
        Same as above, but do it over time (MS).
        
    void mouseNudge(int, int);
        Move the mouse relative to it's current position.
        
    void mouseTarget(char*);
        Move the mouse to a named target specified in the "Mouse Spy" section of the Java app.
        
    void mouseTarget(char*, short);
        Same as above, but do it over time (MS).
        
    void mouseClick(MouseButton);
        Send a mouse click ([LEFT/RIGHT/MIDDLE]).
        
    void mouseDown(MouseButton);
        Send a mouse press with no release (ie. hold for dragging)
        
    void mouseUp(MouseButton);
        Send a mouse release action.

For the string syntax that is used for the key-sending functions, it mostly follows the syntax used by AutoHotKey which can be found here: https://www.autohotkey.com/docs/commands/Send.htm

# Known Issues
- Mouse sensitivity bug with Java's Robot class causes Windows sensitivity to occasionally be reset to it's default.
        https://bugs.openjdk.java.net/browse/JDK-8041463
        Work-around: Use time delays on mouse movement.

- Modifier keys are sometimes released by Java's Robot class.
        https://bugs.openjdk.java.net/browse/JDK-4908075
        Work-around: They work fine when NumLock is off. Send {numlock} to turn numlock off, then keyDown() the modifier, turn numlock back on after releasing the key. Modifiers for single keystrokes work fine (such as "^c" for ctrl+c).

- keyDown() does not emulate normal key-holding behavior, ie. normal windows repeating functionality when holding a key.
        Work-around: None. Some flaw in Java's Robot class (I chose a great path to take for this, huh).

- Reconnecting is impossible without restarting the listener.
        https://marc.info/?l=rxtx&m=125310032820956&w=2
        Work-around: None. This is an issue with the way Windows enumerates USB ports. The listener has to be restarted if the device is disconnected.

# License Information
This project uses the SWT library included with Eclipse IDE, as well as the RXTX library from GitHub here https://github.com/rxtx/rxtx, and their respective licenses are included.
This project uses the same license, I guess. Or let's just say, a good one. Whichever the best one is. That one.
