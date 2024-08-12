package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static String readFileContent(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
