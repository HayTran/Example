#include <ESP8266WiFi.h>

// Variable for connect to wifi
const char *ssid = "tieunguunhi";
const char *password = "tretrau1235";
int status = WL_IDLE_STATUS;     

// Variable for connect to Socket Server
const uint16_t port = 8080;         
const char * host = "192.168.1.200"; 

// Variable for storing data sent to Raspberry
byte humidity = 55;
byte temperature = 55;
byte flameValue0 = 55; 
byte flameValue1 = 0;
byte lightIntensity0 = 55;
byte lightIntensity1 = 0;
byte humiditySolid0 = 55;
byte humiditySolid1 = 0;

int countOfServer = -1;
int countAtCurrent = 0;
int countOfESP = 0;

// Variable sleep time for ESP8266
const int sleepTimeS = 100;

void setup() {   
    wifiSetUp();
    serialSetUp();
    pinMode(2,OUTPUT);
//    ESP.deepSleep(1000000);
}
void loop() {
    runSerial();
    runWifi();
    digitalWrite(2,HIGH);
    delay(500);
    digitalWrite(2,LOW);
    delay(500);
}

void wifiSetUp(){
    delay(10);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
     delay(500);
   }
}
void runWifi(){
    // Use WiFiClient class to create TCP connections
    WiFiClient client;
    //Increase counter variable 
    delay(30);
    while(!client.connect(host,port)){
       delay(300);
    }
    // Ready to send data to server
    delay(10);
    client.flush();
    client.write(humidity);
    client.flush();
    client.write(temperature);
    client.flush();
    client.write(flameValue0);
    client.flush();
    client.write(flameValue1);
    client.flush();
    client.write(lightIntensity0);
    client.flush();
    client.write(lightIntensity1);
    client.flush();
    client.write(humiditySolid0);
    client.flush();
    client.write(humiditySolid1);
    client.flush();
    delay(30);
    // Ready to read data sent from server
    while(client.available()){
      countOfServer = client.read();
      if(countAtCurrent != countOfServer){
        countOfESP++;
      }
      countAtCurrent = countOfServer;
    }
    delay(5);
    client.stop();
}
void serialSetUp(){
    Serial.begin(115200);
}
void runSerial(){
  // Begin communicate serial
    if (Serial.available()) { 
       temperature = Serial.read();   
       humidity = Serial.read();
       flameValue0 = Serial.read();
       flameValue1 = Serial.read();
       lightIntensity0 = Serial.read();
       lightIntensity1 = Serial.read();
       humiditySolid0 = Serial.read();
       humiditySolid1 = Serial.read();
    }
     if(countOfServer >=0){
           Serial.write(countOfServer);
       }
    Serial.flush(); // This action will refresh buffer in serial communication
}

