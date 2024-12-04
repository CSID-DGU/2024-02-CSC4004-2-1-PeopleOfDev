import os

# 로컬 이미지 저장 디렉토리 설정
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
UPLOAD_DIR = os.path.join(BASE_DIR, "uploads")
os.makedirs(UPLOAD_DIR, exist_ok=True)  # 디렉토리 생성

# 디렉토리가 없으면 생성
if not os.path.exists(UPLOAD_DIR):
    os.makedirs(UPLOAD_DIR)