from flask import Flask, redirect, url_for, request
from flask import render_template
import json
app = Flask(__name__)


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

def update(data_to_update):
    tmp = data_to_update
    if tmp["alarms"] == 1:
        tmp["alarms"] = 0
    elif tmp["alarms"] == 0:
        tmp["alarms"] = 1
    return tmp

def update_fire(data_to_update):
    tmp = data_to_update
    if tmp["fire_sensor"] == 1:
        tmp["fire_sensor"] = 0
    elif tmp["fire_sensor"] == 0:
        tmp["fire_sensor"] = 1
    return tmp

@app.route('/main')
def main():
    stat = {}
    stat["status"] = 'got request'
    json_data = json.dumps(stat)
    return json_data
@app.route('/main/sensors', methods = ['POST', 'GET'])
def sensors():
    if request.method == 'POST':
        if request.headers['Content-Type'] == 'application/json':
            data = load_json("data.json")
            update(data)
            updated = set_to_file(data, "data.json")
            json_data = json.dumps(updated)
        return json_data
    elif request.method == 'GET':
        data = load_json("data.json")
        stat = {}
        if data["alarms"] == 1:
            stat["status"] = "Is on"
            json_data = json.dumps(stat)
            return json_data
        elif data["alarms"] == 0:
            stat["status"] = "Is off"
            json_data = json.dumps(stat)
            return json_data
@app.route('/main/temp', methods = ['POST', 'GET'])
def temp():
    if request.method == 'POST':
        if request.headers['Content-Type'] == 'application/json':
            data = load_json("data.json")
            json_data = json.dumps(data)
        return json_data
    elif request.method == 'GET':
        data = load_json("data.json")
        json_data = json.dumps(data)
        return json_data
@app.route('/main/fire', methods = ['POST', 'GET'])
def fire():
    if request.method == 'POST':
        if request.headers['Content-Type'] == 'application/json':
            data = load_json("data.json")
            update_fire(data)
            updated = set_to_file(data, "data.json")
            json_data = json.dumps(updated)
        return json_data
    elif request.method == 'GET':
        data = load_json("data.json")
        stat = {}
        if data["fire_sensor"] == 1:
            stat["status"] = 'Is on'
            json_data = json.dumps(stat)
            return json_data
        elif data["fire_sensor"] == 0:
            stat["status"] = 'Is off'
            json_data = json.dumps(stat)
            return json_data

app.add_url_rule('/', 'main', main)
app.add_url_rule('/', 'sensors', sensors)
app.add_url_rule('/', 'temp', temp)
app.add_url_rule('/', 'fire', fire)

if __name__ == '__main__':
    app.run(host = '0.0.0.0')
