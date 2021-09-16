import requests
from bs4 import BeautifulSoup
import time
import csv

fields = ["Medizin","Informatik","Sonstige","Physik","Biologie"]

def get_page_url(page,field):
    limit = 25
    p = page + 1
    return f"https://tobias-lib.uni-tuebingen.de/xmlui/handle/10900/42126/discover?rpp={limit}&page={p}&group_by=none&etal=0&filtertype_0=fachbereich&filter_0={field}&filter_relational_operator_0=equals"

def is_pdf_link(url):
    name = url.split("?")[0]
    return name.endswith(".pdf")

def unique_list_values(lst):
    return list(dict.fromkeys(lst))


def extract_article_page_content(page_content):
    content_soup = BeautifulSoup(page_content, 'html.parser')
    rows = content_soup.select_one('table').find_all("tr")
    data_map = {}
    for row in rows:
        columns = row.find_all("td")
        property_name = columns[0].text.strip()
        property_value = columns[1].text.strip()
        data_map[property_name] = property_value
    language = data_map["dc.language.iso"]
    is_german =  "de" in language
    if not is_german:
        return None

    title = data_map["dc.title"]
    abstract = data_map["dc.description.abstract"]
    page_links = unique_list_values([link["href"] for link in content_soup.find_all("a")])
    pdf_links =  list(filter(lambda x: is_pdf_link(x),page_links))
    pdf_link = None
    if len(pdf_links) == 1:
        pdf_link = "https://tobias-lib.uni-tuebingen.de" + pdf_links[0]
    return [title,abstract,pdf_link]

def extract_overview_page_content(content):
    if "Die Suche führte zu keinem Treffer." in content:
        return []
    soup = BeautifulSoup(content, 'html.parser')
    container = soup.select_one('.ds-artifact-list')
    links = container.find_all("a")
    urls = ["https://tobias-lib.uni-tuebingen.de" + link["href"] + "?show=full" for link in links]
    # remove duplicates
    urls = list(dict.fromkeys(urls))
    return urls

def crawl_urls():
    max_pages = 10
    urls = []
    for field in fields:
        page = 1
        while page < max_pages:
            print(f"{field} page {page}")
            response = extract_overview_page_content(requests.get(get_page_url(page,field)).text)
            if len(response) == 0:
                break
            urls = urls + response
            time.sleep(.3)
            page += 1

    with open("tübingen_urls.txt","w")as file:
        content = "\n".join(urls)
        file.write(content)

def fetch_content():
    with open("tübingen_urls.txt","r") as file:
        with open('tübingen.csv', 'w', newline='') as csvfile:
            spamwriter = csv.writer(csvfile, delimiter='\t')
            spamwriter.writerow(["title","abstract","pdf"])
            index = 0
            for url in file:
                content = requests.get(url).text
                data = extract_article_page_content(content)
                if data is not None:
                    print("is german")
                    spamwriter.writerow(data)
                print(f"completed {index + 1}")    
                time.sleep(0.03)
                index += 1

fetch_content()        
# crawl_urls()