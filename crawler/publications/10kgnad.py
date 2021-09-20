import csv
import solr
from config import solr_core,solr_url 


def prepare():
    solr_docs = []
    solr_client = solr.SolrConnection(solr_url)
    url = "https://www.uni-potsdam.de"
    with open('exports/articles.csv', newline='') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=';', quotechar='\'')
        for row in spamreader:
            content = row[1]
            title = content.split(".")[0]
            body = content.replace(title+".","")
            solr_doc = {
                "id": title,
                "title": title,
                "name": title,
                "content": body,
                "url": url
            }
            solr_docs.append(solr_doc)
    solr_client.add_many(solr_docs)
    solr_client.commit()

prepare()