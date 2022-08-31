# BluetoothHomeApp

The project consists of two parts:

First part is represented by a microcontroller which reads data from an ultrasonic sensor - senses the water level of a barell - and can initiate filling of the barell(Bluetooth_Arduino_Controller).

The second part consists of an Java Android app connected with the microcontroller through a HC-05 bluetooth module using the Bluetooth API. App receives and sends commands to the microcontroller in order to control the flow of water insde the barrel and monitor the level (MyApplication).
