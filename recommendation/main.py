import uvicorn
from fastapi import FastAPI
from typing import Optional
from timeit import default_timer as timer
from pydantic import BaseModel
import uuid
from fastapi.middleware.cors import CORSMiddleware
from model_bert import BERTRecommender

app = FastAPI()

#https://fastapi.tiangolo.com/tutorial/cors/
origins = [
    "*"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

model = BERTRecommender()

@app.get("/hello")
def hello():
    return {"Hello": "World"}


def store_request(request_id : str, query: str):
    with open("requests.txt", "a") as myfile:
        myfile.write(query+";"+request_id+"\n")

@app.get("/similiar")
def similiar(topic: str, q: Optional[str] = "", count: Optional[int] = 10, keywords: Optional[str] = ""):
    similiar_words = model.get_recommendations(q,topic,n=count,keywords=keywords.split(","))

    response_words = [(x[0].capitalize(), x[1]) for x in similiar_words]
    request_id = uuid.uuid4()
    #store_request(str(request_id),q)

    return {"requestId": request_id, "similiar": response_words}

class KeywordUsedDto(BaseModel):
    keyword: str
    requestId: str

def store_used_keyword(dto : KeywordUsedDto):
     with open("used.txt", "a") as myfile:
        myfile.write(dto.keyword+";"+dto.requestId+"\n")

@app.post("/recommendation")
def used_keyword(dto: KeywordUsedDto):
    #store_used_keyword(dto)
    return {}

if __name__ == "__main__":
    model.setup()
    uvicorn.run(app, host="0.0.0.0", port=8000)