#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>

// Set up software serial for ESP
SoftwareSerial mySerial(D6,D7); // RX, TX

// Variable for connect to wifi
const char *ssid = "tieunguunhi";
const char *password = "tretrau1235";
int status = WL_IDLE_STATUS;     

// Variable for connect to Socket Server
const uint16_t port = 8080;         
const char * host = "192.168.1.200"; 

// Variable for storing data sent to Raspberry
byte humidity = 0;
byte temperature = 0;
byte flameValue0_0 = 0; 
byte flameValue0_1 = 0;
byte flameValue1_0 = 0;
byte flameValue1_1 = 0;
byte lightIntensity0 = 0;
byte lightIntensity1 = 0;
byte mq2Value0 = 0;
byte mq2Value1 = 0;
byte mq7Value0 = 0;
byte mq7Value1 = 0;

int countOfServer = -1;
int countOfArduino = 0;

// Variable sleep time for ESP8266
const int sleepTimeS = 100;

void setup() { 
    Serial.begin(115200);
    mySerial.begin(115200);
      // D1 for UART communiation signal
    pinMode(D1,OUTPUT);
      // D2 for Wifi communication signal
    pinMode(D2,OUTPUT);
    wifiSetUp();
}
void loop() {
    comUART();
    runWifi();
    delay(1000);
}

void wifiSetUp(){
    delay(10);
    // We start by connecting to a WiFi network
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
     digitalWrite(D2,HIGH);
     delay(400);
     digitalWrite(D2,LOW);
     delay(100);
     Serial.print(".");
   }
    Serial.println("");
    Serial.println("WiFi connected");  
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
}
void runWifi(){
    // Use WiFiClient class to create TCP connections
    digitalWrite(D2,HIGH);
    WiFiClient client;
    //Increase counter variable 
    delay(30);
    Serial.println("Connecting to server socket: ");
    Serial.println(host);
    while(!client.connect(host,port)){
       Serial.print(".");
       digitalWrite(D2,HIGH);
    }
    byte macAddr[6];  
    WiFi.macAddress(macAddr);
    Serial.print("MAC: ");
    Serial.print(macAddr[5],HEX);
    Serial.print(":");
    Serial.print(macAddr[4],HEX);
    Serial.print(":");
    Serial.print(macAddr[3],HEX);
    Serial.print(":");
    Serial.print(macAddr[2],HEX);
    Serial.print(":");
    Serial.print(macAddr[1],HEX);
    Serial.print(":");
    Serial.println(macAddr[0],HEX);
    // Ready to send data to server
    delay(10);
    client.flush();
    client.write(humidity);
    client.write(temperature);
    client.write(flameValue0_0);
    client.write(flameValue0_1);
    client.write(flameValue1_0);
    client.write(flameValue1_1);
    client.write(lightIntensity0);
    client.write(lightIntensity1);
    client.write(mq2Value0);
    client.write(mq2Value1);
    client.write(mq7Value0);
    client.write(mq7Value1);
    client.write(macAddr[5]);
    client.write(macAddr[4]);
    client.write(macAddr[3]);
    client.write(macAddr[2]);
    client.write(macAddr[1]);
    client.write(macAddr[0]);
    client.flush();
    delay(30);
    // Ready to read data sent from server
    while(client.available()){
      countOfServer = client.read();
    }
    delay(5);
    Serial.println("closing connection");
    client.stop();
    digitalWrite(D2,LOW);
}
void comUART(){
      // Begin communicate serial
    digitalWrite(D1,HIGH);
    if (mySerial.available()) { 
       temperature = mySerial.read();   
       humidity = mySerial.read();
       flameValue0_0 = mySerial.read();
       flameValue0_1 = mySerial.read();
       flameValue1_0 = mySerial.read();
       flameValue1_1 = mySerial.read();
       lightIntensity0 = mySerial.read();
       lightIntensity1 = mySerial.read();
       mq2Value0 = mySerial.read();
       mq2Value1 = mySerial.read();
       mq7Value0 = mySerial.read();
       mq7Value1 = mySerial.read();
       countOfArduino = mySerial.read();
    }
    if(countOfServer >=0){
           mySerial.write(countOfServer);
       }
    Serial.print("========================================  countOfServer = ");
    Serial.println(countOfServer,DEC);
    Serial.print("========================================  countOfArduino = ");
    Serial.println(countOfArduino,DEC);
    
    mySerial.flush(); // This action will refresh buffer in serial communication
    Serial.print("Temperature: ");
    Serial.println(temperature);
    Serial.print("Humidity: ");
    Serial.println(humidity);
    Serial.print("Flame 1 : ");
    int flameValue0 = flameValue0_0 + flameValue0_1*256;
    Serial.println(flameValue0,DEC);
    int flameValue1 = flameValue1_0 + flameValue1_1*256;
    Serial.print("Flame 2: ");
    Serial.println(flameValue1,DEC);
    Serial.print("Light: ");
    int lightIntensity = lightIntensity0+ lightIntensity1*256;
    Serial.println(lightIntensity,DEC);
    Serial.print("MQ2: ");
    int mq2Value = mq2Value0 + mq2Value1*256;
    Serial.println(mq2Value,DEC);
    Serial.print("MQ7: ");
    int mq7Value = mq7Value0 + mq7Value1*256;
    Serial.println(mq7Value,DEC);
    digitalWrite(D1,LOW);
}

