import os
from fastapi import APIRouter, HTTPException, UploadFile, File
from typing import List
from app.settings import UPLOAD_DIR
from app.yolo_service import detect_objects, draw_detections

router = APIRouter()

@router.post("/predict_images")
async def predict_images(files: List[UploadFile] = File(...)):
    try:
        image_paths = []

        # 업로드된 파일을 로컬 디렉토리에 저장
        for file in files:
            file_path = os.path.join(UPLOAD_DIR, file.filename)
            with open(file_path, "wb") as f:
                f.write(await file.read())
            image_paths.append(file_path)

        # YOLO 모델로 탐지 수행
        results = []
        for image_path in image_paths:
            detections = detect_objects(image_path)
            result_image_path = draw_detections(image_path, detections)

            # 탐지 결과 추가
            results.append({
                "original_image": image_path,
                "result_image": result_image_path,
                "detections": detections,
            })

        return {"results": results}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error processing images: {str(e)}")