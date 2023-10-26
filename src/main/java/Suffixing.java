import java.io.*;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Suffixing {
    private static final Logger logger = Logger.getLogger(Suffixing.class.getName());

    public static void main(String[] args) {
        Properties properties = new Properties();
        String pathToConfig = args[0];
        try (InputStream inputStream = new FileInputStream(pathToConfig)){
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String mode = properties.getProperty("mode");
        String suffix = properties.getProperty("suffix");
        String files = properties.getProperty("files");
        checkCorrectFieldsForFile(mode, suffix, files);
    }

    private static void checkCorrectFieldsForFile(String mode, String suffix, String files) {
        if (Objects.equals(suffix, null) || suffix.isBlank()) {
            logger.log(Level.SEVERE, "No suffix is configured");
        } else if (Objects.equals(files, null) || files.isBlank()) {
            logger.log(Level.WARNING, "No files are configured to be copied/moved");
        } else if (!Objects.equals(mode.toUpperCase(), "COPY") && !Objects.equals(mode.toUpperCase(), "MOVE")) {
            logger.log(Level.SEVERE, "Mode is not recognized: {0}", mode);
        } else {
            suffixFiles(files, suffix, mode);
        }
    }

    private static void suffixFiles(String files, String suffix, String mode) {
        String[] filesArray = files.split(":");
        for (String file : filesArray) {
            File filename = new File(file);
            if (!filename.exists()) {
                logger.log(Level.SEVERE, "No such file: {0}", file);
                continue;
            }
            int index = file.lastIndexOf(".");
            String suffixedFilePath = file.substring(0, index) + suffix + file.substring(index);
            File path = new File(suffixedFilePath);
            copyFileAndDisplayInfo(mode, file, filename, path, suffixedFilePath);
        }
    }

    private static void copyFileAndDisplayInfo(String mode, String file, File filename, File path, String suffixedFilePath) {
        try {
            Files.copy(filename.toPath(), path.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Objects.equals(mode.toUpperCase(), "MOVE")) {
            try {
                Files.delete(filename.toPath());
                logger.log(Level.INFO, file + " => " + suffixedFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(Objects.equals(mode.toUpperCase(), "COPY")){
            logger.log(Level.INFO, file + " -> " + suffixedFilePath);
        }
    }
}
