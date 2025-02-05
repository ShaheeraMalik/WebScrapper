# Web Scrapper in Java and in Python

## Project Overview
This repository contains two scripts for web scraping research paper metadata from the NeurIPS conference website:

- `scraper.java`: Java-based implementation for scraping paper metadata.
- `scraper.py`: Python-based implementation for scraping paper metadata.

The scripts extract metadata such as paper titles, authors, and PDF URLs and store the results in `output.csv` or `output.json` formats.

## Repository Structure
```
.
├── scraper.java         # Java scraper script
├── scraper.py           # Python scraper script
├── README.md            # Instructions for running the scripts
├── output.csv           # Extracted metadata (if CSV format)
└── output.json          # Extracted metadata (if JSON format)
```

## Requirements

### Python Script Requirements
- Python 3.x
- Required libraries: `requests`, `beautifulsoup4`

Install the dependencies by running:
```bash
pip install -r requirements.txt
```

### Java Script Requirements
- JDK 8 or higher
- External library: [Jsoup](https://jsoup.org/)

Ensure that `jsoup-<version>.jar` is included in your classpath.

## Running the Scripts

### Running `scraper.java`
1. Compile the Java file:
   ```bash
   javac -cp .:jsoup-<version>.jar scraper.java
   ```
2. Run the script:
   ```bash
   java -cp .:jsoup-<version>.jar scraper
   ```
3. The extracted metadata will be saved as `output.csv`.

### Running `scraper.py`
1. Run the script:
   ```bash
   python scraper.py
   ```
2. The extracted metadata will be saved as `output.json` or `output.csv`, depending on the output format specified in the code.

## Notes
- Ensure a stable internet connection while running the scripts.
- If scraping fails due to connection timeouts or website restrictions, consider adding retry logic or adjusting request headers.
- Review the target website’s terms of service before scraping and ensure compliance with ethical web scraping practices.

## Blog Post Link
[Read the blog post here](<https://medium.com/@shaheeramalik533/web-scrapper-15b65e582c55>)

## Responsible Web Scraping Practices
- Respect `robots.txt` rules and avoid overloading the server.
- Use appropriate user-agent headers.
- Implement rate limiting to prevent being blocked.

## License
This project is licensed under the MIT License.

