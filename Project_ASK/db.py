import pymysql

ask_db = pymysql.connect(
    user='root',
    passwd='ask1234!',
    host='13.209.122.152',
    db='ASK',
    charset='utf8'
)

cursor = ask_db.cursor(pymysql.cursors.DictCursor)
sql = '''CREATE TABLE test (
        id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
        name varchar(255)
      )
      '''

cursor.execute(sql)

# with ask_db:
#     with conn.cursor() as cur:
#         cur.excute(sql)
#         conn.commit()

result = cursor.fetchall()
print(result)