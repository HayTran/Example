#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>

// Set up software serial for ESP
SoftwareSerial mySerial(D6,D7); // RX, TX

// Variable for connect to wifi
const char *ssid = "tieunguunhi";
const char *password = "TreTrau1235";
int status = WL_IDLE_STATUS;     

// Variable for connect to Socket Server
const uint16_t port = 8080;         
const char * host = "192.168.1.200"; 

// Variable for storing data sent to Raspberry
byte humidity = 0;
byte temperature = 0;
byte flameValue0 = 0; 
byte flameValue1 = 0;
byte lux0 = 0;
byte lux1 = 0;
byte humiditySolid0 = 0;
byte humiditySolid1 = 0;

int countOfServer = -1;
int countAtCurrent = 0;
int countOfESP = 0;

// Variable sleep time for ESP8266
const int sleepTimeS = 100;

void setup() {   
    wifiSetUp();
    serialSetUp();
    runWifi();
    runSerial();
//    ESP.deepSleep(1000000);
}
void loop() {
    runSerial();
    runWifi();
    delay(2000);
}

void wifiSetUp(){
    delay(10);
    // We start by connecting to a WiFi network
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) {
     delay(500);
     Serial.print(".");
   }
    Serial.println("");
    Serial.println("WiFi connected");  
    Serial.println("IP address: ");
    Serial.println(WiFi.localIP());
}
void runWifi(){
    // Use WiFiClient class to create TCP connections
    WiFiClient client;
    //Increase counter variable 
    delay(30);
    Serial.println("Connecting to server socket: ");
    Serial.println(host);
    while(!client.connect(host,port)){
       Serial.print(".");
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
    client.write(lux0);
    client.flush();
    client.write(lux1);
    client.flush();
    client.write(humiditySolid0);
    client.flush();
    client.write(humiditySolid1);
    client.flush();
    delay(30);
    // Ready to read data sent from server
    while(client.available()){
      countOfServer = client.read();
      Serial.print("========================================  countOfServer = ");
      Serial.println(countOfServer,DEC);
      if(countAtCurrent != countOfServer){
        countOfESP++;
      }
      countAtCurrent = countOfServer;
    }
    delay(5);
    Serial.println("closing connection");
//    client.end();
    client.stop();
}
void serialSetUp(){
    Serial.begin(115200);
    mySerial.begin(115200);
}
void runSerial(){
  // Begin communicate serial
    if (mySerial.available()) { 
       temperature = mySerial.read();   
       humidity = mySerial.read();
       flameValue0 = mySerial.read();
       flameValue1 = mySerial.read();
       lux0 = mySerial.read();
       lux1 = mySerial.read();
       humiditySolid0 = mySerial.read();
       humiditySolid1 = mySerial.read();
    }
     if(countOfServer >=0){
           mySerial.write(countOfServer);
       }
    mySerial.flush(); // This action will refresh buffer in serial communication
    Serial.print("Temperature: ");
    Serial.println(temperature);
    Serial.print("Humidity: ");
    Serial.println(humidity);
    Serial.print("Flame: ");
    Serial.println(flameValue0,DEC);
    Serial.print("Flame: ");
    Serial.println(flameValue1,DEC);
    Serial.print("Light: ");
    Serial.println(lux0,DEC);
}

