# SmallMimic
A Java application/uC Library for emulating keyboard+mouse control in Windows based on serial messages received from a connected microcontroller

This requires the Java listener to be running on the host. It contains a minimal UI for the initial setup and for some convenience functions.



# uC API (C++)
The following functions can be used in any Sketch that includes the SmallMimic library, which have the resulting effect on the host machine:

    void ping();
        Causes the tray icon of the listener to flash.
        
    void sendKeys(char*);
        Send a string of keys for the listener to press one at a time.
        
    void sendKeys(char*, short);
        Same as above, but with a specified delay (MS).
        
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
        Send a mouse click (LEFT, RIGHT, MIDDLE).
        
    void mouseDown(MouseButton);
        Send a mouse press with no release (ie. hold for dragging)
        
    void mouseUp(MouseButton);
        Send a mouse release action.
        
# License Information
This project uses the SWT library included with Eclipse IDE, as well as the RXTX library from GitHub, and their respective licenses are included.
This project uses the same license, I guess. Or let's just say, a good one. Whichever the best one is. That one.
