import os
import json
import Path


def writeJson(jsonDict, filePath, fileName):
    if os.path.isfile(fr'{filePath}\{fileName}'):
        with open(fr'{filePath}\{fileName}', "w", encoding='UTF8') as json_file:
            json.dump(jsonDict, json_file, ensure_ascii=False)
    else:
        if createJsonFile(filePath, fileName):
            writeJson(jsonDict, filePath, fileName)
        else:
            print("create json file error")


def createJsonFile(filePath, fileName):
    Path.createFolder(filePath)
    with open(fr'{filePath}\{fileName}', "w"):
        pass
    return True


def readJson():
    with open(jsonStock, 'r', encoding='UTF8') as f:
        json_data = json.load(f)

    return json_data
