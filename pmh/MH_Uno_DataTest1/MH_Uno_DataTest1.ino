#include <Sensirion.h>
#include<SoftwareSerial.h>

SoftwareSerial bluetooth(13,12);
 
int dataPin  =  2; //데이터 핀
int clockPin =  3; //클럭 핀
 
float temp; //온도 변수
float humi;
float dew;
Sensirion tempSensor = Sensirion(dataPin, clockPin);  //센서 라이브러리 연결 
 
void setup()
{
  Serial.begin(9600);
  bluetooth.begin(9600);
}
 
void loop()
{
  //섭C 온도값 받아오기
  tempSensor.measure(&temp, &humi, &dew );
  //온도 출력
  Serial.print("Temperature: ");
  Serial.println(temp);
  bluetooth.print("Temperature: ");
  bluetooth.println(temp);
  
  //10초간 대기
  delay(10000);  
}
