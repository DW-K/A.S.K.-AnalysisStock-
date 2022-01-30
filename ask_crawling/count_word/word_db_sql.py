import pandas as pd
import pymysql
import pandas as pd

# mysql password : ask1234!
# sql = f'''CREATE TABLE word_count (
        #         id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
        #         name varchar(255)
        #       )
        #       '''
import sqlalchemy
from sqlalchemy import create_engine

table_name = 'word_count'


def get_db_obj():
    ask_db = pymysql.connect(
        user='root',
        passwd='ask1234!',
        host='13.209.122.152',
        db='ASK',
        charset='utf8'
    )

    return ask_db


def get_word(word):
    ask_db = get_db_obj()

    try:
        cursor = ask_db.cursor(pymysql.cursors.DictCursor)

        sql = f'''SELECT {word} FROM {table_name}'''

        cursor.execute(sql)

        result = cursor.fetchall()

    finally:
        ask_db.close()

    return result


def update_word_db(df):
    # ask_db = get_db_obj()
    db_user_name = 'root'
    db_password = 'ask1234!'
    host_address = '13.209.122.152'
    db_name = 'ASK'

    db_connection_str = f'mysql+pymysql://[{db_user_name}]:[{db_password}]@[{host_address}]/[{db_name}]'
    db_connection = create_engine(db_connection_str)
    conn = db_connection.connect()

    # dtypesql = {'exclusive': sqlalchemy.types.VARCHAR(10),
    #             'cost': sqlalchemy.types.VARCHAR(10),
    #             'contractedAt': sqlalchemy.Date(),
    #             'createdAt': sqlalchemy.DateTime(),
    #             }
    # df.to_sql(name=f'{table_name}', con=db_connection, if_exists='append', index=False, dtype=dtypesql)

    df.to_sql(name=f'{table_name}', con=db_connection, if_exists='append', index=False)

