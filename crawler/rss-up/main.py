import urllib.request
import requests
import csv
from io import StringIO
import time

output_dir = "output"

def page_url(year,page=0):
    limit = 100
    offset = page * limit
    return f"https://publishup.uni-potsdam.de/opus4-ubp/export/index/csv/searchtype/simple/query/%2A%3A%2A/browsing/true/yearfq/{year}/rows/{limit}/start/{offset}/languagefq/deu"
  
def has_next_page(content):
    rows  = list(csv.reader(StringIO(content), delimiter='\t'))[:1]
    return len(rows) > 0

while True:
    page = 1
    url = page_url(2021,page)
    r = requests.get(url)
    with open(f"{output_dir}/2021_{page}.csv","w") as f:
        f.write(r.text)
    str_content = r.text

    if not has_next_page(str_content):
        break
    page += 1
    time.sleep(3)

