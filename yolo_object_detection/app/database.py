from sqlalchemy import create_engine, Column, Integer, String, Float
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import os

# 추후 수정 필요: DB 연결 정보 설정

# DB 연결 정보(h2 database)
DATABASE_URL = os.getenv("DATABASE_URL")

# SQLAlchemy 데이터베이스 엔진 생성
engine = create_engine(DATABASE_URL, echo=True)

# 데이터베이스 세션 생성
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Base 클래스 생성
Base = declarative_base()

def get_db():
    db = SessionLocal() # 세션 생성 함수
    try:
        yield db
    finally:
        db.close()
