from datetime import date
from pathlib import Path
import os
import json
from datetime import datetime

log_dir = "logs"

def get_log_filename():
    today = date.today()
    return today.strftime("%Y-%m-%d")

def timestamp():
    now = datetime.now()
    return now.strftime("%H:%M:%S")

def log_request(topic:str, query: str, keywords: [str], results):
    Path(log_dir).mkdir(parents=True, exist_ok=True)
    data = {'topic': topic,'query':query,'keywords':keywords,'results':results}
    file_path = os.path.join(log_dir,get_log_filename())
    request_content = json.dumps(data)
    tmstmp = timestamp()
    content = tmstmp + ": " + request_content
    with open(file_path,"a") as file:
        file.write(content+"\n")