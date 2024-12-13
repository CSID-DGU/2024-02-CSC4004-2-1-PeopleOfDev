from fastapi import FastAPI
from app.routes import router

app = FastAPI()

# API 라우터
app.include_router(router, prefix="/api")


if __name__ == "__main__":
    import uvicorn # 서버 실행
    uvicorn.run(app, host="127.0.0.1", port=8000)

# http://127.0.0.1:8000/api/predict_images