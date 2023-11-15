import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebSnippetExtractor {

    public static String getWebContent(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    public static String extractSnippet(String htmlContent, List<String> terms) {
        Document doc = Jsoup.parse(htmlContent);
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            String text = p.text().toLowerCase();
            boolean allTermsPresent = true;
            for (String term : terms) {
                if (!text.contains(term.toLowerCase())) {
                    allTermsPresent = false;
                    break;
                }
            }
            if (allTermsPresent) {
                return text; // Returns the first paragraph containing all the terms
            }
        }
        return "";
    }

    public static void main(String[] args) {
        try {
            String url = "https://people.com/sports/lebron-james-reveals-the-one-regret-he-has-about-kobe-bryant-i-wish-i-had-that-moment/"; // Replace with your target URL
            List<String> terms = Arrays.asList("lebron", "kobe"); // Replace with your target terms

            String webContent = getWebContent(url);
            String snippet = extractSnippet(webContent, terms);

            System.out.println("Snippet: " + snippet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
