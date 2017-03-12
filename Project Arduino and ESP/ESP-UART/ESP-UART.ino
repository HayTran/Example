#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>

const char *ssid = "tieunguunhi";
const char *password = "TreTrau1235";
int status = WL_IDLE_STATUS;     // the Wifi radio's status

const uint16_t port = 8080;         // port tương ứng với server
const char * host = "192.168.1.250"; // ip của raspberry

byte hay = 127;
byte nhung = 127;
byte valueR = 0;
uint8_t *valueReturn = 0;
bool a = 1;
byte count = 0;

SoftwareSerial mySerial(D6,D7); // RX, TX

void setup() {
    Serial.begin(115200);
    mySerial.begin(115200);
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
 
void loop() {
    // Use WiFiClient class to create TCP connections
    WiFiClient client;
    //Increase counter variable 
    hay++;
    if(hay >= 255){
      hay = 0;
    }
    nhung --;
    if(nhung <= 0){
      nhung = 255;
    }
    
    // Connect to server
    delay(30);
    Serial.println("Connecting to server socket: ");
    Serial.println(host);
    while(!client.connect(host,port)){
       Serial.print(".");
       delay(300);
    }
    // Ready to send data to server
    delay(20);
    client.flush();
    client.write(hay); 
    client.flush();
    client.write(nhung);
    client.flush();
    client.write(valueR);
    client.flush();
    Serial.print("Hay = ");
    Serial.println(hay,DEC);
    Serial.print("Nhung = ");
    Serial.println(nhung,DEC);
    Serial.print("ValueR = ");
    Serial.println(valueR,DEC);
    
    // Ready to read data sent from server
    delay(20);
    while(client.available()){
      count = client.read();
      Serial.print("========================================  count = ");
      Serial.println(count,DEC);
    }
    // Begin communicate serial
    if (mySerial.available()) { 
       valueR = mySerial.read();   
       mySerial.write(count);
    }
    Serial.println("closing connection");
    client.stop();
    delay(200);
}