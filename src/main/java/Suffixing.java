import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Suffixing {
    private static final Logger logger = Logger.getLogger(Suffixing.class.getName());

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            String pathToConfig = args[0];
            InputStream inputStream = new FileInputStream(pathToConfig);
            properties.load(inputStream);
            String mode = properties.getProperty("mode");
            String suffix = properties.getProperty("suffix");
            String files = properties.getProperty("files");
            if (!Objects.equals(mode, "copy")) {
                logger.log(Level.SEVERE, "Mode is not recognized: " + mode);
            }
            if (Objects.equals(suffix, null) || suffix.isBlank()) {
                logger.log(Level.SEVERE, "No suffix is configured");
            }
            if (Objects.equals(files, null) || files.isBlank()) {
                logger.log(Level.WARNING, "No files are configured to be copied/moved");
            }
            String[] filesArray = files.split(":(?=[A-Za-z]:\\/)");
            for (String file : filesArray) {
                File filename = new File(file);
                if (!filename.exists()) {
                    logger.log(Level.SEVERE, "No such file ", filename);
                } else {
                    int index = file.lastIndexOf(".");
                    String suffixedFile = file.substring(0, index) + "-" + file.substring(index);
                    File path = new File(suffixedFile);
                    logger.log(Level.INFO, suffixedFile + "-->" + path);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
