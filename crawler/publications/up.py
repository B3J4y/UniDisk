import urllib.request
import requests
import csv
from io import StringIO
import time
import glob

output_dir = "output_up"

def page_url(year,page=0):
    limit = 100
    offset = page * limit
    return f"https://publishup.uni-potsdam.de/opus4-ubp/export/index/csv/searchtype/simple/query/%2A%3A%2A/browsing/true/yearfq/{year}/rows/{limit}/start/{offset}/languagefq/deu"
  
def has_next_page(content):
    rows  = list(csv.reader(StringIO(content), delimiter='\t'))[1:]
    return len(rows) > 0


years = [2019,2020,2021]

def transform(csv_content,result_name):
    rows = list(csv.reader(StringIO(csv_content), delimiter='\t'))[1:]
    results = []
    for row in rows:
        try:
            doc_id = row[0].split("-")[-1]
            title = row[4].strip()
            abstract = row[5].strip()
            if abstract == "":
                continue
            link = f"https://publishup.uni-potsdam.de/frontdoor/index/index/searchtype/simple/query/%2A%3A%2A/browsing/true/yearfq/2021/start/101/rows/20/languagefq/deu/docId/{doc_id}"
            results.append([title,abstract,link])
        except:
            print(row)
    with open("exports/"+result_name, 'w', newline='') as csvfile:
        spamwriter = csv.writer(csvfile, delimiter='\t')
        spamwriter.writerow(["title","abstract","pdf"])
        for result in results:
            spamwriter.writerow(result)



def crawl():
    for year in years:
        page = 0
        while True:
            url = page_url(year,page)
            r = requests.get(url)
            str_content = r.text
            has_next = has_next_page(str_content)
            if has_next:
                with open(f"{output_dir}/{year}_{page}.csv","w") as f:
                    f.write(r.text)
            if not has_next:
                break
            page += 1
            time.sleep(1)

def parse():
    for filename in glob.glob("output_up/2020*.csv"):
        with open(filename,"r") as file:
            transform(file.read(),filename.replace("output_up/",""))

parse()