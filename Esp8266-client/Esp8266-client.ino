#include <ESP8266WiFi.h>

const char *ssid = "tieunguunhi";
const char *password = "TreTrau1235";
int status = WL_IDLE_STATUS;     // the Wifi radio's status

const uint16_t port = 8080;         // port tương ứng với server
const char * host = "192.168.1.250"; // ip của raspberry

byte hay = 127;
uint8_t nhung = 127;
char *message = "Tao la ESP ne, Server oi";
uint8_t *valueReturn = 0;
int a = 1;

void setup() {
    Serial.begin(115200);
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
    Serial.print("Hay = ");
    Serial.println(hay,DEC);
    Serial.print("Nhung = ");
    Serial.println(nhung,DEC);
    // Ready to read data sent from server
    delay(20);
    while(client.available()){
      byte c = client.read();
      Serial.print("c ========================================== ");
      Serial.println(c,DEC);
    }
    Serial.println("closing connection");
    client.stop();
    delay(10);
}
