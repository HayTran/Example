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

// Configure for Humidity Solid Sensor
const int humiditySolid_pin = A1; // Pin A0 of Arduino

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

// Variable to store value from sensors
int flameValue = 0;
uint16_t lux = 0;
byte temperature = 0;
byte humidity = 0;
uint16_t humiditySolid = 0; 

// Variable for sent to ESP
byte flameValue0 = 0; 
byte flameValue1 = 0;
byte lux0 = 0;
byte lux1 = 0;
byte humiditySolid0 = 0;
byte humiditySolid1 = 0;

void setup() {
    Serial.begin(115200);
    mySerial.begin(115200);
    Serial.println("Beginning...");   
       // Set up for DTH11
    dht.begin();
       // Set up for Flame Sensor   
    pinMode(flame_pin,INPUT);
       // Set up for Humidity Solid
    pinMode(humiditySolid_pin,INPUT);
       // Set up for BH1750
    LightSensor.begin();
    LightSensor.SetAddress(Device_Address_H);
    LightSensor.SetMode(Continuous_H_resolution_Mode);
}
void loop() {
   readDHT();
   readFlameSensor();
   readBH1750();
   readHumiditySolid();
   readUART();
   delay(200);
}
void readUART(){
  delay(10);
   mySerial.write(temperature);
   mySerial.write(humidity);
   mySerial.write(flameValue0);
   mySerial.write(flameValue1);
   mySerial.write(lux0);
   mySerial.write(lux1);
   mySerial.write(humiditySolid0);
   mySerial.write(humiditySolid1);
   delay(5);
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
    flameValue = analogRead(flame_pin);
    flameValue0 = flameValue % 256;
    flameValue1 = flameValue / 256;
    Serial.print("Flame sensor: ");
    Serial.println(flameValue); 
    delay(10);        
}
void readDHT(){
    humidity = dht.readHumidity();   
    temperature = dht.readTemperature(); 
    Serial.print("Temperature: ");
    Serial.println(temperature);               
    Serial.print("Humidity: ");
    Serial.println(humidity);               
    delay(10);
}
void readBH1750(){
  lux = LightSensor.GetLightIntensity();// Get Lux value
  lux0 = lux % 256;
  lux1 = lux / 256;
  Serial.print("Light: ");
  Serial.print(lux);
  Serial.println(" lux");
  delay(10);
}
void readHumiditySolid(){
  humiditySolid = analogRead(humiditySolid_pin);
  Serial.print("Humidity Solid: ");
  humiditySolid0 = humiditySolid % 256;
  humiditySolid1 = humiditySolid / 256; 
  Serial.println(humiditySolid); 
  delay(10);      
}

