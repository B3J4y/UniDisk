
import glob
import pathlib
from bs4 import BeautifulSoup

file_directory = "./../../wikicrawl/pages"
output_directory = "output"


pathlib.Path(output_directory).mkdir(parents=True, exist_ok=True) 


def write_to_output(filename, content):
    with open(output_directory+"/"+filename,"w") as file:
        file.write(content)

def extract_page_content(html_body):
    soup = BeautifulSoup(html_body, 'html.parser')
    content = soup.find(id="mw-content-text")
    content_elements = content.find_all('p')
    body = "".join([element.getText() for element in content_elements])
    return body

file_content = []
dir_glob = file_directory+"/*.html"
for filepath in glob.glob(dir_glob):
    with open(filepath,"r") as file:
        content = extract_page_content(file.read())
        write_to_output(filepath.split("/")[-1].replace(".html",".txt"), content)