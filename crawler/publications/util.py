import csv
import requests
import time
import sys

def fetch_content(url_file,output_file,extract_article_page_content,max_urls=-1):
    with open(url_file,"r") as file:
        with open("exports/"+output_file, 'w', newline='') as csvfile:
            spamwriter = csv.writer(csvfile, delimiter='\t')
            spamwriter.writerow(["title","abstract","pdf"])
            index = 0
            for url in file:
                try:
                    content = requests.get(url).text
                    data = extract_article_page_content(content)
                    if data is not None:
                        print("is german")
                        spamwriter.writerow(data)
                    print(f"completed {index + 1}")
                    if max_urls > 0 and index >= max_urls:
                        break
                except:
                    print("Unexpected error:", sys.exc_info()[0])
                time.sleep(0.05)
                index += 1