import glob
import csv
from io import StringIO
import solr
from config import solr_core,solr_url 

def parse_csv(content):
    rows  = list(csv.reader(StringIO(content), delimiter='\t'))
    for row in rows:
        authors = row[2]
        title = row[4]
        abstract = row[5]
        print(row)
        print((authors,title,abstract))
    return rows

for filepath in glob.glob("output/*.csv"):
    with open(filepath,"r") as file:
        content = file.read()
        rows = parse_csv(content)
