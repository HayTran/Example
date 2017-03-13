#include <SoftwareSerial.h>
SoftwareSerial mySerial(2,3);  // RX, TX
byte valueR = 0;
byte valueW = 255;
bool a = 1;

void setup() {
  // put your setup code here, to run once:
    Serial.begin(115200);
    mySerial.begin(115200);
    
    Serial.println("Start connect between ESP and Arduino");
    pinMode(13,OUTPUT);
//    interruptSetUp();
}
void loop() {
    mySerial.write(valueW);  
    if (mySerial.available()) {  
       valueR = mySerial.read();
    }
    mySerial.flush(); // This action will refresh buffer in serial communication
    valueW--;
    if(valueW <= 0){
      valueW = 255;
    }
    Serial.print("Value is sent from Arduino to ESP: ");
    Serial.println(valueW);
    Serial.print("Value is read from ESP ( hay= ): ");
    Serial.println(valueR,DEC);   // read it and send it out Serial1 (pins 0 & 1)
    delay(50);
}
void interruptSetUp(){
    cli();                                  // tắt ngắt toàn cục 
    /* Reset Timer/Counter1 */
    TCCR1A = 0;
    TCCR1B = 0;
    TIMSK1 = 0;
    /* Setup Timer/Counter1 */
    TCCR1B |= (1 << CS12) | (1 << CS10);    // prescale = 1024  
    TCNT1 = 1;
    TIMSK1 = (1 << TOIE1);                  // Overflow interrupt enable 
    sei();                                  // cho phép ngắt toàn cục
}
ISR (TIMER1_OVF_vect) 
{
    TCNT1 = 1;
    digitalWrite(13,HIGH);
    delay(100);
    digitalWrite(13,LOW);
}
