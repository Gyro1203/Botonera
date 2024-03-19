int mode = 0;

void setup() {
  Serial.begin(9600);
  Serial1.begin(9600);
  delay(2000);
}

void loop() {
  
  while(Serial.available()==0){
    if(mode==1){
          String mensaje = "u";
          for(int i=0; i<10; i++){
            Serial1.print(mensaje);
            delay(100);
          }
        }
    if(Serial1.available()>0){
        String s1 = Serial1.readString();
        Serial.println(s1);
      };
  };
  String mensaje = Serial.readString() + "\r";
  if(mensaje.equals("~\r") || mensaje.equals("~")){
    if(mode==0) mode = 1;
    else mode = 0;
    Serial.println("Modo Manual: ");
    Serial.println(mode);
  }
  if(mensaje=="\n"){}
  else{
//      Serial.println( Serial1.available());
        Serial1.print(mensaje);
//      Serial.println(Serial1.available());
//      String s1 = Serial1.readString();
//      Serial.println(s1);
//      Serial.println(Serial1.available());
    }
}
