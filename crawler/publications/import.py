import glob
import csv
from io import StringIO
import solr
from config import solr_core,solr_url 

def parse_csv(content):
    # skip header row
    rows  = list(csv.reader(StringIO(content), delimiter='\t'))[1:]
    pages = []
    for row in rows:
        title = row[0]
        abstract = row[1]
        pdf = row[2]
        pages.append([title,abstract,pdf])
    return pages

for filepath in glob.glob("./*.csv"):
    print(filepath)
    with open(filepath,"r") as file:
        content = file.read()
        solr_client = solr.SolrConnection(solr_url)
        rows = parse_csv(content)


        solr_docs = []
        for row in rows:
            title = row[0]
            if title.strip() == "" or title is None:
                continue
            solr_doc = {
                "id": title,
                "title": title,
                "name": title,
                "content": row[1],
                "url": row[2]
            }
            solr_docs.append(solr_doc)
        solr_client.add_many(solr_docs)
        solr_client.commit()