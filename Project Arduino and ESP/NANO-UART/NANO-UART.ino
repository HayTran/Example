#include <SoftwareSerial.h>
SoftwareSerial mySerial(2,3);  // RX, TX
byte valueR = 0;
byte valueW = 255;
void setup() {
  // put your setup code here, to run once:
    Serial.begin(115200);
    mySerial.begin(115200);
    Serial.println("Start connect between ESP and Arduino");
}
void loop() {
     mySerial.write(valueW);  
    if (mySerial.available()) {  
       valueR = mySerial.read();
    }
    valueW--;
    if(valueW <= 0){
      valueW = 255;
    }
    Serial.print("Value is sent from Arduino to ESP: ");
    Serial.println(valueW);
    Serial.print("Value is read from ESP ( hay= ): ");
    Serial.println(valueR,DEC);   // read it and send it out Serial1 (pins 0 & 1)
    delay(900);
}
