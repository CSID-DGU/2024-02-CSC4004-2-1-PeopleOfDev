import os
import torch
from PIL import Image, ImageDraw
from app.settings import UPLOAD_DIR

# YOLO 모델 로드
model = torch.hub.load('ultralytics/yolov5', 'custom', path='yolov5s.pt')

# YOLO로 객체 탐지
def detect_objects(image_path: str):
    results = model(image_path)
    detections = []
    for box in results.xyxy[0]:
        x_min, y_min, x_max, y_max, confidence, class_id = box[:6]
        detections.append({
            "class_name": results.names[int(class_id)],
            "confidence": float(confidence),
            "box": [int(x_min), int(y_min), int(x_max), int(y_max)]
        })
    return detections

# 탐지 결과 이미지에 표시
def draw_detections(image_path: str, detections: list):
    image = Image.open(image_path)
    draw = ImageDraw.Draw(image)

    for detection in detections:
        x_min, y_min, x_max, y_max = detection["box"]
        class_name = detection["class_name"]
        confidence = detection["confidence"]

        # 경계 상자 그리기
        draw.rectangle([x_min, y_min, x_max, y_max], outline="red", width=2)
        # 클래스 이름 및 신뢰도 표시
        draw.text((x_min, y_min - 10), f"{class_name} {confidence:.2f}%", fill="red")

    # 결과 이미지를 로컬 디렉토리에 저장
    result_image_path = os.path.join(UPLOAD_DIR, f"result_{os.path.basename(image_path)}")
    image.save(result_image_path)
    return result_image_path