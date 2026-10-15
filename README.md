Automated Plant Watering System
A Java-controlled automated plant watering system using a Grove Arduino board, Firmata4j, and various hardware sensors. It monitors soil moisture, controls a pump via a state machine, displays live metrics on an OLED screen, and plots real-time voltage using JFreeChart.

Demo Video: https://www.youtube.com/watch?v=ewtW-MKIaDk

Key Features-
Automated Irrigation: Triggers the pump when soil voltage exceeds 3.5 V (dry) and stops below 2.8 V(wet).

Live Telemetry: Real-time OLED display (SSD1306) and live JFreeChart voltage graph.

Emergency Stop: Physical button (on D6) with a 500ms long-press check to instantly cut the pump.

OOP Architecture: Built using Java principles (Inheritance, Polymorphism, Encapsulation) with JUnit tests.

Tech Stack & Libraries-
Language: Java (Object-Oriented Programming principles: Inheritance, Encapsulation, Polymorphism)

Hardware Communication: Grove Arduino, Bosch BMP280 (Temperature/Pressure), SSD1306 OLED, Soil Moisture Sensor, Water Pump + MOSFET

Data Visualization: JFreeChart

Testing: JUnit

Data Structures: ArrayList, HashMap, Timer

Core Logic -
 
Voltage Formula : Voltage = raw * (5.0 / 1023.0)

State Machine: Switches between PUMP_OFF and WATERING_NOW based on sensor voltage and a 56-second cooldown between watering cycles.
