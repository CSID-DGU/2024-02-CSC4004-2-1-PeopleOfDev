from pydantic import BaseModel # 데이터 검증을 위한 라이브러리
from typing import List

# 탐지 결과 스키마
class DetectionSchema(BaseModel):
    class_name: str
    confidence: float
    rate: str
    image_url: str

# 탐지 결과 응답 스키마
class DetectionResponse(BaseModel):
    original_image: str
    result_image: str
    detections: List[DetectionSchema]
