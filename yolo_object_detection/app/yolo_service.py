import os
import torch
from PIL import Image, ImageDraw, ImageFont
from app.settings import UPLOAD_DIR
from typing import Dict, List

# YOLO 모델 로드
model = torch.hub.load('ultralytics/yolov5', 'yolov5l', pretrained=True)

# YOLO로 객체 탐지
def detect_objects(image_path: str) -> List[Dict]:
    results = model(image_path)
    detections = []
    confidence_threshold = 0.5 # 신뢰도 임계값
    
    # 탐지 결과를 반복하면서 필요한 정보 추출
    for box in results.xyxy[0]:
        x_min, y_min, x_max, y_max, confidence, class_id = box[:6]
        if confidence >= confidence_threshold:
            detections.append({
                "class_name": results.names[int(class_id)],
                "confidence": float(confidence),
                "box": [int(x_min), int(y_min), int(x_max), int(y_max)]
            })
    print(f"Detections: {detections}") # 로그 출력
    return detections

# 탐지 결과 이미지에 표시
def draw_detections(image_path: str, detections: list):
    image = Image.open(image_path)
    draw = ImageDraw.Draw(image)

    # 폰트 설정 
    font = ImageFont.load_default()  # 기본 폰트를 사용

    # 탐지 결과를 반복하면서 경계 상자 및 클래스 이름 표시
    for detection in detections:
        x_min, y_min, x_max, y_max = detection["box"]
        class_name = detection["class_name"]
        confidence = detection["confidence"]
        
        # 신뢰도에 따라 색상 설정 (높을수록 녹색)
        color = (int(255 * (1 - confidence)), int(255 * confidence), 0) # RGB
        draw.rectangle([x_min, y_min, x_max, y_max], outline=color, width=2) # 경계 상자 그리기
        # 클래스 이름과 신뢰도 표시
        draw.text((x_min, y_min - 10), f"{class_name} {confidence:.2f}%", fill=color, font=font)

    # 결과 이미지를 로컬 디렉토리에 저장
    result_image_path = os.path.join(UPLOAD_DIR, f"result_{os.path.basename(image_path)}")
    image.save(result_image_path)
    return result_image_path

# 활동 결정
def determine_activity(detections: List[dict]) -> str:
    activity_score = {
        "study": 0,  # 공부 가중치
        "exercise": 0,  # 운동 가중치
    }
    total_confidence = sum([d["confidence"] for d in detections]) # 신뢰도 합계 계산

    # 객체별로 활동 가중치 계산
    weights = {
    "laptop": {"study": 4, "exercise": 0},
    "book": {"study": 4, "exercise": 0},
    "chair": {"study": 1, "exercise": 1},
    "person": {"study": 1, "exercise": 2},
    "bottle": {"study": 1, "exercise": 1},
    "bench": {"study": 0, "exercise": 2},
    "dining table": {"study": 2, "exercise": 0},
    }

    for detection in detections:
        class_name = detection["class_name"]
        confidence = detection["confidence"] # 신뢰도

        # 신뢰도 기반 필터링(0.5 이상)
        if confidence > 0.5 and class_name in weights:
            # 신뢰도 정규화
            normalized_confidence = confidence / total_confidence
            activity_score["study"] += weights[class_name]["study"] * normalized_confidence
            activity_score["exercise"] += weights[class_name]["exercise"] * normalized_confidence

    # 활동 점수 출력 (디버깅용)
    print(f"공부 점수: {activity_score['study']}, 운동 점수: {activity_score['exercise']}")
    
    # 활동 종류 결정
    if activity_score["study"] > activity_score["exercise"]:
        print("공부 중입니다.")
        return "공부 중입니다."
    elif activity_score["exercise"] > activity_score["study"]:
        print("운동 중입니다.")
        return "운동 중입니다."
    else:
        print("활동을 판단할 수 없습니다.")
        return "활동을 판단할 수 없습니다."
