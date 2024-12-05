from sqlalchemy import Column, Integer, String, Float
from app.database import Base

# DB 모델 생성 (임의로 설정, 추후 수정 필요)
class DetectionResult(Base):
    __tablename__ = "detection_results" # 테이블 이름

    id = Column(Integer, primary_key=True, index=True)
    class_name = Column(String(50), nullable=False)
    accuracy = Column((Float), nullable=False)
    rate = Column(String(20))
    image_url = Column(String(255))
