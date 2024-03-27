package Util.FileUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileIndexExtractor {
    public static int extractNumber(String filename) {
        Pattern pattern = Pattern.compile("temp(\\d+)\\.txt");
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return -1;
        }
    }
}
