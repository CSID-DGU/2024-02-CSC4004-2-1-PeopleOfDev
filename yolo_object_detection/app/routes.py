import os
from fastapi import APIRouter, HTTPException, UploadFile, File
from typing import List
from app.settings import UPLOAD_DIR
from app.yolo_service import detect_objects, draw_detections, determine_activity
import logging

# 로깅 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

router = APIRouter()

@router.post("/predict_images")
async def predict_images(files: List[UploadFile] = File(...)):
    try:
        logger.info(f"UPLOAD_DIR is set to: {UPLOAD_DIR}") # 로그 출력
        image_paths = []

        # 업로드된 파일을 로컬 디렉토리에 저장
        for file in files:
            file_path = os.path.join(UPLOAD_DIR, file.filename)
            logger.info(f"Saving file to: {file_path}") # 로그 출력
            with open(file_path, "wb") as f:
                f.write(await file.read())
            image_paths.append(file_path)

        results = []
        for image_path in image_paths:
            # yolo 탐지
            detections = detect_objects(image_path)
            logger.info(f"Detections: {detections}")
            result_image_path = draw_detections(image_path, detections)
            activity = determine_activity(detections)

            # 탐지 결과 추가
            results.append({
                "original_image": image_path,
                "result_image": result_image_path,
                "detections": detections,
                "activity": activity,
            })
            print(f"UPLOAD_DIR: {UPLOAD_DIR}") # 테스트

        return {"results": results}
    except Exception as e:
        logger.error(f"Error processing images: {e}")
        raise HTTPException(status_code=500, detail=f"Error processing images: {str(e)}")