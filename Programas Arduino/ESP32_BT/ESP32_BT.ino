#include "BluetoothSerial.h"
BluetoothSerial BT;

String text;

void setup() {
  Serial.begin(9600);
  BT.begin("SCORBOT");
  Serial.println("");
  Serial.println("Iniciando Control ... ");
  delay(2000);
}

void loop() {
  if (BT.available()) {
    text = BT.readString();
    Serial.println(text);
  }
  if (Serial.available()) {
    text = Serial.readStringUntil('\n');
    //BT.print(text);
  }
  delay(1000);
}
