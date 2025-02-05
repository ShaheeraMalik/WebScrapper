import os
import requests
from bs4 import BeautifulSoup
from concurrent.futures import ThreadPoolExecutor

BASE_URL = "https://papers.nips.cc"
SAVE_DIRECTORY = "E:\\pdfs\\"
THREAD_POOL_SIZE = 10
TIMEOUT = 10  # Timeout for requests in seconds


def create_directory(path):
    if not os.path.exists(path):
        os.makedirs(path)
        print(f"Created directory: {path}")


def fetch_main_page(url):
    print(f"Fetching main page: {url}")
    response = requests.get(url, timeout=TIMEOUT)
    response.raise_for_status()
    return BeautifulSoup(response.text, 'html.parser')


def fetch_year_page(url):
    print(f"Processing year page: {url}")
    response = requests.get(url, timeout=TIMEOUT)
    response.raise_for_status()
    return BeautifulSoup(response.text, 'html.parser')


def fetch_paper_page(url):
    print(f"Processing paper page: {url}")
    response = requests.get(url, timeout=TIMEOUT)
    response.raise_for_status()
    return BeautifulSoup(response.text, 'html.parser')


def download_file(pdf_url, save_directory):
    try:
        file_name = pdf_url.split('/')[-1]
        file_path = os.path.join(save_directory, file_name)

        response = requests.get(pdf_url, stream=True, timeout=TIMEOUT)
        response.raise_for_status()

        with open(file_path, 'wb') as file:
            for chunk in response.iter_content(chunk_size=4096):
                file.write(chunk)

        print(f"Saved: {file_path}")
    except requests.RequestException as e:
        print(f"Failed to download: {pdf_url}\nError: {e}")


if __name__ == "__main__":
    create_directory(SAVE_DIRECTORY)

    try:
        main_page = fetch_main_page(BASE_URL)

        year_links = main_page.select("a[href^=/paper_files/paper/]")
        print(f"Found {len(year_links)} year links.")

        with ThreadPoolExecutor(max_workers=THREAD_POOL_SIZE) as executor:
            for year_link in year_links:
                year_url = BASE_URL + year_link['href']

                year_name = year_url.split('/')[-1]
                year_folder_path = os.path.join(SAVE_DIRECTORY, year_name)
                create_directory(year_folder_path)

                year_page = fetch_year_page(year_url)
                paper_links = year_page.select("a[href^=/paper_files/paper/]")

                for paper_link in paper_links:
                    paper_page_url = BASE_URL + paper_link['href']
                    paper_page = fetch_paper_page(paper_page_url)
                    
                    download_button = paper_page.select_one("a[href$=.pdf]")
                    if download_button:
                        pdf_url = BASE_URL + download_button['href']
                        print(f"Downloading paper: {pdf_url}")
                        executor.submit(download_file, pdf_url, year_folder_path)
                    else:
                        print(f"Download button not found for paper: {paper_page_url}")
    except requests.exceptions.RequestException as e:
        print(f"Error fetching main page or processing: {e}")
