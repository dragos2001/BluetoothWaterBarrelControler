#include "Arduino.h"
#include <SoftwareSerial.h>
#include "Math.h"
const byte receive_pin=9;
const byte transmit_pin=8;
int valve_pin=7;


SoftwareSerial BTSerial(receive_pin, transmit_pin); // RX TX


int trig_pin=12;
int echo_pin=11;
int travel_time;
int valve_on=101;
int valve_off=100;
float distance;


void setup() {
  // put your setup code here, to run once:
  pinMode(receive_pin,INPUT);
  pinMode(valve_pin,OUTPUT);
  pinMode(transmit_pin,OUTPUT);
  pinMode(trig_pin,OUTPUT);
  pinMode(echo_pin,INPUT);
  BTSerial.begin(9600);
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeated
    
  
  while(BTSerial.available()>0)
  {
    int data=BTSerial.read();
    Serial.println(data);
  

    if(data==1)
    {
    digitalWrite(trig_pin,LOW);
    delayMicroseconds(20);
    digitalWrite(trig_pin,HIGH);
    delayMicroseconds(20);
    digitalWrite(trig_pin,LOW);
    travel_time=pulseIn(echo_pin,HIGH);
    delay(25);
    distance= travel_time * 0.0343 / 2.2;
  
   Serial.println(distance);
   byte b= byte(distance);
   Serial.println(b);
   BTSerial.write(b);
    
    }

    if(data==valve_off)
    {
      digitalWrite(valve_pin,LOW);
       Serial.println("LOW");
    }
       

      if(data==valve_on)
      {
      digitalWrite(valve_pin,HIGH);
      Serial.println("HIGH");
      }

      delay(100);
    }

   

  
  }
   
