from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy.ext.declarative import declarative_base

pw = 'ask1234!'
db_connection_address = f'mysql+pymysql://root:{pw}@localhost:3306/ASK'

engine = create_engine(db_connection_address, convert_unicode=False)
db_session = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=engine))

Base = declarative_base()
Base.query = db_session.query_property()


def init_db():
    import models
    Base.metadata.create_all(engine)

