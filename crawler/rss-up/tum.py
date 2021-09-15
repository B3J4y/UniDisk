from bs4 import BeautifulSoup
import requests

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


crawl_urls()