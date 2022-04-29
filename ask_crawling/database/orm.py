from database import init_db
from database import db_session
from models import TbTest


def show_tables():
    queries = db_session.query(TbTest)
    entries = [dict(id=q.id, datetime=q.datetime, string=q.string) for q in queries]
    print(entires)


def add_entry(datetime, string):
    t = TbTest(datetime, string)
    db_session.add(t)
    db_session.commit()


def delete_entry(datetime, string):
    db_session.query(TbTest).filter(TbTest.datetime == datetime, TbTest.string == string).delete()
    db_session.commit()


def main():
    add_entry("2015-02-06 09:00:05", "test1")
    delete_entry("2015-02-06 09:00:05", "test1")
    db_session.close()


if __name__ == "__main__":
    main()