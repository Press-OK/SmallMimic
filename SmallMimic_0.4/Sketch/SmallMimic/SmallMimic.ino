/*  SmallMimic demo sketch by Sean Berwick 2019
 * 
 *  This just goes through all the basic features of the .cpp library
 *  intended to be used in sketches that are uploaded to programmable
 *  devices. It requires the desktop counterpart - the Java app - to
 *  be listening on the host computer.
 * 
 *  NOTE: This is set up for the Wemos D1 Mini. You can change the set
 *  on-board LED pin in the SmallMimic.h file (or disable blinking).
 *  
 *  License and more information TBD
 */

#include "SmallMimic.h"

SmallMimic sm;

void setup() {
  digitalWrite(2, HIGH);
  Serial.begin(115200);
}

void loop() {
  // Some time to open the Java listener on the host
    delay(10000);

  // The tray icon should flash if the ping is successful
    sm.ping();
    delay(2000);

  // Open notepad using WIN+R (run), "notepad", Enter
    sm.sendKeys("#rnotepad{ret}");
    delay(200);

  // Maximize the window using ALT+Space, X
    sm.sendKeys("! x");
    delay(200);

  // Keyboard demo
    sm.sendKeys("=== SmallMimic Demo\\! ==={ret}{ret}", 50);
    delay(1800);
    // Using modifier keys
      sm.sendKeys("- Lowercase \" a \", uppercase \" +a \", using the SHIFT modifier{ret}", 50);
      delay(1800);
    // Using non-alphanumeric ("special") keys
      sm.sendKeys("- Opening notepad 'about' using ALT\\+Arrow Keys{ret}", 50);
      delay(1800);
      sm.sendKeys("{alt}{left}{left}{down}{down}{enter}", 250);
      delay(1200);
      sm.sendKeys("{enter}");
      delay(100);
      sm.sendKeys("- Malformed special characters will be ignored: {this is an invalid identifier}{ret}", 50);
      delay(1800);
      sm.sendKeys("- Special characters can have more than one convenience name, ie. \\{ret\\} \\{return\\} \\{enter\\}{enter}", 50);
      delay(1800);
      sm.sendKeys("- See the documentation for full information on sending keystrokes{ret}{ret}", 50);
      delay(1800);
  // Mouse demo
    sm.sendKeys("- Let's move the mouse. First, instantly snap the mouse with no delay...{ret}", 50);
    delay(1600);
    sm.mouseMove(100, 500);
    delay(200);
    sm.mouseMove(200, 600);
    delay(200);
    sm.mouseMove(300, 500);
    delay(200);
    sm.mouseMove(400, 600);
    delay(200);
    sm.mouseMove(500, 500);
    delay(1600);
    sm.mouseMove(100, 500);
    sm.sendKeys("- It's not very human-like. Use the optional third parameter for time (milliseconds){ret}", 50);
    delay(1600);
    sm.mouseMove(200, 800, 150);
    sm.mouseMove(300, 500, 350);
    sm.mouseMove(400, 800, 150);
    sm.mouseMove(500, 500, 350);
    delay(300);
    sm.mouseMove(100, 100);
    sm.sendKeys("- Unfortunately, due to a bug in Java's Robot class, this will *sometimes* reset your Windows mouse sensitivity. =({ret}", 50);
    delay(800);
    sm.sendKeys("     Using mouseMove with a delay seems to decrease the likelihood that this will happen.{ret}", 50);
    delay(1500);
    sm.sendKeys("- Lastly, you can use mouseDown() and mouseUp() for the mouse buttons...", 50);
    delay(1800);
    sm.mouseMove(120, 120);
    sm.mouseDown(LEFT);
    sm.mouseMove(120, 800, 4000);
    sm.mouseUp(LEFT);
    delay(100);
    sm.sendKeys("{end}");
    delay(200);
    sm.sendKeys("{ret}{ret}Thanks for watching the demo\\! I will now ALT\\+F4 out of Notepad for you. Good luck\\!", 50);
    delay(5000);
    sm.sendKeys("!{F4}n", 100);
    delay(60000);
}
