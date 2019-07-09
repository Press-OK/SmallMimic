/*  This C++ file defines the functionality of the library
 *  and the very simple string-based message protocol that
 *  operates of serial (USB).
 */

#include "Arduino.h"
#include "SmallMimic.h"

#define DEFAULT_DELAY_MS 35

SmallMimic::SmallMimic() {
	pinMode(ledPin, OUTPUT);
}

void blinkLED() {
  if (ledBlink == true) {
    digitalWrite(ledPin, ledOnState);
    delay(1);
    digitalWrite(ledPin, !ledOnState);
  }
}

int getCharsExcludingSpecial(char* s) {
  int n = 0, sp = 0, e = 0;
  for (int i = 0; i < strlen(s); i++) {
    char c = s[i];
    if (c == '\\') {
      e = 1;
    } else if (c == '{' && e == 0) {
      sp = 1;
    } else if (c == '}' && e == 0 && sp == 1) {
      sp = 0;
      n++;
    } else if (sp == 0) {
      n++;
      e = 0;
    }
  }
  return n;
}

void SmallMimic::ping() {
  Serial.printf("p\n");
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::sendKeys(char* s) {
  Serial.printf("ks%05d%s\n", DEFAULT_DELAY_MS, s);
  blinkLED();
  delay(DEFAULT_DELAY_MS * getCharsExcludingSpecial(s));
}

void SmallMimic::sendKeys(char* s, short t) {
  t = t < 0 ? 0 : t > 30000 ? 30000 : t;
  Serial.printf("ks%05d%s\n", t, s);
  blinkLED();
  delay(t * getCharsExcludingSpecial(s));
}

void SmallMimic::keyDown(char* s) {
  Serial.printf("kd%s\n", s);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::keyUp(char* s) {
  Serial.printf("ku%s\n", s);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}
void SmallMimic::mouseMove(int x, int y) {
  x = x < 0 ? 0 : x > 4000 ? 4000 : x;
  y = y < 0 ? 0 : y > 4000 ? 4000 : y;
  Serial.printf("mm%04d%04d%05d\n", x, y, DEFAULT_DELAY_MS);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::mouseMove(int x, int y, short t) {
  x = x < 0 ? 0 : x > 4000 ? 4000 : x;
  y = y < 0 ? 0 : y > 4000 ? 4000 : y;
  t = t < 0 ? 0 : t > 30000 ? 30000 : t;
  Serial.printf("mm%04d%04d%05d\n", x, y, t);
  blinkLED();
  delay(t);
}

void SmallMimic::mouseNudge(int x, int y) {
  x = x < -4000 ? -4000 : x > 4000 ? 4000 : x;
  y = y < -4000 ? -4000 : y > 4000 ? 4000 : y;
  Serial.printf("mn%d%04d%d%04d\n", x<0, abs(x), y<0, abs(y));
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::mouseTarget(char* s) {
  Serial.printf("mt%05d%s\n", DEFAULT_DELAY_MS, s);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::mouseTarget(char* s, short t) {
  t = t < 0 ? 0 : t > 30000 ? 30000 : t;
  Serial.printf("mt%05d%s\n", t, s);
  blinkLED();
  delay(t);
}

void SmallMimic::mouseClick(MouseButton b) {
  Serial.printf("mc%d\n", b);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::mouseDown(MouseButton b) {
  Serial.printf("md%d\n", b);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}

void SmallMimic::mouseUp(MouseButton b) {
  Serial.printf("mu%d\n", b);
  blinkLED();
  delay(DEFAULT_DELAY_MS);
}
