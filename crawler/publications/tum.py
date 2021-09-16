from bs4 import BeautifulSoup
import requests
from util import fetch_content

fields = [
    #Chemie
    603794,
    #Informatik
    603796,
    #Kerntechnik
    603807,
    #Landbau
    603810,
    #Mathematik
    603815,
    #Medizin
    603816,
    #Physik
    603821,
    #Sport
    603829
]

def get_page_url(field, after=None):
    limit = 50
    url = f"https://mediatum.ub.tum.de/{field}?nodes_per_page={limit}"
    if after is not None:
        url += f"&after={after}"
    return url

def extract_overview_page_content(content):
    content_soup = BeautifulSoup(content, 'html.parser')
    container = content_soup.find(id="nodes")
    urls = [ "https://mediatum.ub.tum.de" + link["href"] for link in container.find_all("a")]
    next_page_link = content_soup.select_one(".page-nav-next")
    if next_page_link is not None:
        next_page_link = "https://mediatum.ub.tum.de" + next_page_link["href"]
    return (urls,next_page_link)

def export_page_content(content):
    content_soup = BeautifulSoup(content, 'html.parser')
    labels = [x.text.replace(":","").strip() for x in content_soup.select(".mask_label")]
    values = [ x.text for x in content_soup.select(".mask_value")]
    value_map = {}
    label_value_map = list(zip(labels,values))
    for label,value in label_value_map:
        value_map[label] = value
    language = value_map["Sprache"]
    is_german = "de" in language
    if not is_german:
        return None
    title = value_map["Originaltitel"]
    abstract = value_map["Kurzfassung"]
    pdf_link = "https://mediatum.ub.tum.de" + content_soup.select_one(".document_download").select_one("a")["href"]

    return [title,abstract,pdf_link]


def crawl_urls():
    max_pages = 5
    urls = []
    for field in fields:
        page = 0
        next_page_link = get_page_url(field)
        while next_page_link is not None:
            url = next_page_link
            if url is None:
                url = get_page_url(field)
            response = requests.get(url)
            page_urls, new_link = extract_overview_page_content(response.text)
            next_page_link = new_link
            urls += page_urls
    with open("tum_urls.txt","w") as file:
        content = "\n".join(urls)
        file.write(content)

def fetch():
    fetch_content("tum_urls.txt","tum.csv",export_page_content)

#crawl_urls()

fetch()