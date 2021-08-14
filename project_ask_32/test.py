import sys
import datetime

# print("start test.py")
# args = sys.argv[1:]
#
# for i in args:
#     print(i)

now = datetime.datetime.now()
today = now.strftime("%Y%m%d")

print(today)