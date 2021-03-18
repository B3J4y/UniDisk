import uvicorn
from fastapi import FastAPI
from typing import Optional
from gensim.models.fasttext import FastText
from timeit import default_timer as timer
from pydantic import BaseModel
import uuid
from fastapi.middleware.cors import CORSMiddleware

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


@app.get("/hello")
def hello():
    return {"Hello": "World"}


def store_request(request_id : str, query: str):
    with open("requests.txt", "a") as myfile:
        myfile.write(query+";"+request_id+"\n")

@app.get("/similiar")
def similiar(q: str, count: Optional[int] = 10):
    similiar_words = model.wv.most_similar(q,topn=count)
    request_id = uuid.uuid4()
    store_request(str(request_id),q)

    return {"requestId": request_id, "similiar": similiar_words}

class KeywordUsedDto(BaseModel):
    keyword: str
    requestId: str

def store_used_keyword(dto : KeywordUsedDto):
     with open("used.txt", "a") as myfile:
        myfile.write(dto.keyword+";"+dto.requestId+"\n")

@app.post("/recommendation")
def used_keyword(dto: KeywordUsedDto):
    store_used_keyword(dto)
    return {}

if __name__ == "__main__":
    model = FastText.load("fasttext.model") 
    uvicorn.run(app, host="0.0.0.0", port=8001)