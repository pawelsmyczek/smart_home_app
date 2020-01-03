
import json
import random
import w1thermsensor
import RPi.GPIO as GPIO
import time



def load_json(file_path):
    #open JSON file and parse contents
    with open(file_path, 'r+') as fh:
        dataf = json.load(fh)
        fh.close()
    return dataf

def set_to_file(data, file_path):
    with open(file_path, 'r+') as fh:
        fh.seek(0)
        json.dump(data, fh, indent=4)
        fh.truncate()
    return data

def data_update(data, file_path):
    GPIO.setmode(GPIO.BCM)
    GPIO.setwarnings(False)
    sensor = w1thermsensor.W1ThermSensor()
    temp = sensor.get_temperature()

    GPIO.setup(21, GPIO.OUT)
    GPIO.setup(26, GPIO.OUT)
    data["temp"] = temp
    alarms = data["alarms"]
    fire = data["fire_sensor"]
    if alarms == 1:
        GPIO.output(21, GPIO.HIGH)
    elif alarms == 0:
        GPIO.output(21, GPIO.LOW)

    if fire == 1:
        GPIO.output(26, GPIO.HIGH)
    elif fire == 0:
        GPIO.output(26, GPIO.LOW)
    with open(file_path, 'r+') as fh:
        fh.seek(0)
        json.dump(data, fh, indent=4)
        fh.truncate()
    return data

#def update_gpio(data):



if __name__ == '__main__':
    while True:
        data = load_json("data.json")
        updated_temp = data_update(data, "data.json")
        #update_gpio(data)
        print("new temperature value T[in Celcius degreees] = " + str(updated_temp["temp"]))
        time.sleep(1)
