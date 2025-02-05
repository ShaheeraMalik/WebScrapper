import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class scrapper {

    private static final String BASE_URL = "https://papers.nips.cc";
    private static final String SAVE_DIRECTORY = "E:\\pdfs\\";
    private static final ExecutorService executor = Executors.newFixedThreadPool(10); // Adjust pool size as needed

    public static void main(String[] args) {
        try {
            Path savePath = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
                System.out.println("Created save directory: " + savePath);
            }

            System.out.println("Fetching main page: " + BASE_URL);
            Document mainPage = Jsoup.connect(BASE_URL).get();

            Elements yearLinks = mainPage.select("a[href^=/paper_files/paper/]");
            System.out.println("Found " + yearLinks.size() + " year links.");

            for (Element yearLink : yearLinks) {
                String yearUrl = BASE_URL + yearLink.attr("href");

                // Extract the year from the link text (e.g., "2024" from the link)
                String yearName = yearUrl.substring(yearUrl.lastIndexOf('/') + 1);
                Path yearFolderPath = Paths.get(SAVE_DIRECTORY, yearName);

                // Create the year folder if it doesn't exist
                if (!Files.exists(yearFolderPath)) {
                    Files.createDirectories(yearFolderPath);
                    System.out.println("Created year folder: " + yearFolderPath);
                }

                System.out.println("Processing year: " + yearUrl);
                Document yearPage = Jsoup.connect(yearUrl).get();

                Elements paperLinks = yearPage.select("a[href^=/paper_files/paper/]");

                for (Element paperLink : paperLinks) {
                    String paperPageUrl = BASE_URL + paperLink.attr("href");
                    System.out.println("Processing paper page: " + paperPageUrl);

                    Document paperPage = Jsoup.connect(paperPageUrl).get();

                    Element downloadButton = paperPage.selectFirst("a[href$=.pdf]");
                    if (downloadButton != null) {
                        String pdfUrl = BASE_URL + downloadButton.attr("href");
                        System.out.println("Downloading paper: " + pdfUrl);

                        executor.submit(() -> downloadFile(pdfUrl, yearFolderPath.toString()));
                    } else {
                        System.err.println("Download button not found for paper: " + paperPageUrl);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void downloadFile(String fileUrl, String saveDirectory) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(saveDirectory, fileName);

            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Saved: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to download: " + fileUrl);
            e.printStackTrace();
        }
    }
}
