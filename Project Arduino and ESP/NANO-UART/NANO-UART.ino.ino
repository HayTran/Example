#include <SoftwareSerial.h>
#include "DHT.h"  
#include <Wire.h>
#include <BH1750FVI.h>

// Configure for Software UART
SoftwareSerial mySerial(2,3);  // RX, TX
byte valueR = 0;
byte valueW = 255;
bool a = 1;

// Configure for DHT11
const int DHTPIN = 4;       // Communicate by one-wire in pin 4 of Arduino
const int DHTTYPE = DHT11;  // Declare type of sensor
DHT dht(DHTPIN, DHTTYPE);

// Configure for Flame Sensor
const int flame_pin = A0; // Pin A0 of Arduino
int flame_value = 0;

// Configure for BH1750
/*Main address  0x23 
  secondary address 0x5C 
  connect this sensor as following :
  VCC >>> 3.3V
  SDA >>> A4 
  SCL >>> A5
  addr >> A3
  Gnd >>>Gnd */
BH1750FVI LightSensor;

void setup() {
    Serial.begin(115200);
    mySerial.begin(115200);
    Serial.println("Beginning...");
    // Set up for DTH11
    dht.begin(); 
    // Set up for Flame Sensor
    pinMode(flame_pin,INPUT);
    // Set up for BH1750
    LightSensor.begin();
    LightSensor.SetAddress(Device_Address_H);
    LightSensor.SetMode(Continuous_H_resolution_Mode);
}
void loop() {
   readUART();
   readDHT();
   readFlameSensor();
   readBH1750();
   delay(900);
}
void readUART(){
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
    delay(10);
}
void readFlameSensor(){
    flame_value = analogRead(flame_pin);
    Serial.print("Flame sensor: ");
    Serial.println(flame_value); 
    delay(10);        
}
void readDHT(){
    float h = dht.readHumidity();    //Ð?c d? ?m
    float t = dht.readTemperature(); //Ð?c nhi?t d?
    Serial.print("Nhiet do: ");
    Serial.println(t);               //Xu?t nhi?t d?
    Serial.print("Do am: ");
    Serial.println(h);               //Xu?t d? ?m
    delay(10);
}
void readBH1750(){
  uint16_t lux = LightSensor.GetLightIntensity();// Get Lux value
  Serial.print("Light: ");
  Serial.print(lux);
  Serial.println(" lux");
  delay(10);
}

