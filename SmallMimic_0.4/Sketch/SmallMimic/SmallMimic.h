#ifndef SmallMimic_h
#define SmallMimic_h

#include "Arduino.h"

#define ledBlink true   // 1/0 to enable or disable LED blinking
#define ledPin 2        // Pin for the onboard LED (Wemos D1 Mini)
#define ledOnState LOW  // Pin state for LED on (Wemos D1 Mini is kind of backwards)

enum MouseButton { LEFT = 0 , RIGHT = 1 , MIDDLE = 2 };

class SmallMimic {
	public:
		SmallMimic();
    void ping();
    void sendKeys(char*);
		void sendKeys(char*, short);
		void keyDown(char*);
		void keyUp(char*);
    void mouseMove(int, int);
		void mouseMove(int, int, short);
		void mouseNudge(int, int);
    void mouseTarget(char*);
    void mouseTarget(char*, short);
		void mouseClick(MouseButton);
		void mouseDown(MouseButton);
		void mouseUp(MouseButton);
	private:
};

#endif
