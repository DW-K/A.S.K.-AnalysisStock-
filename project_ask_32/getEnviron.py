import os
import sys
import json
from ast import literal_eval

import Path


def getEnviron():
    result = {}
    path = r'..\resource'
    Path.createFolder(path)

    with open(rf"{path}\environ_stock.json", "w") as f:
        environ = str(os.environ)
        environ = environ.split('{')[1]
        environ = environ.split('}')[0]
        environ = '{' + environ + '}'

        result['env'] = literal_eval(environ)

        result['interpreter'] = str(sys.executable)
        json.dump(result, f, ensure_ascii=False, indent='\t')

    return result


if __name__ == "__main__":
    getEnviron()
