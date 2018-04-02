package neuronix.utils;

import com.alibaba.fastjson.JSON;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import neuronix.models.json.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    
    public static void writeJSON (String path, String json) throws IOException {
        FileWriter file = new FileWriter(path);
        file.write(json);
        file.close();
    }
    
    public static String readJSON (String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.forName("UTF8"));
    }
    
    public static String encodeJson (Model model) {
        return JSON.toJSONString(model, true);
    }
    
    public static Model decodeJson(String json) {
        return JSON.parseObject(json, Model.class);
    }
    
    public static List<String> findFilesInDirectory (String directoryPath) {
        File directory = new File(directoryPath);
        
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        
        File[] fileListAsFile = directory.listFiles(fileFilter);
        String[] filesInDirectory = new String[fileListAsFile.length];
        int index = 0;
        for (File fileAsFile : fileListAsFile) {
            filesInDirectory[index] = directoryPath + "\\" + fileAsFile.getName();
            index++;
        }
        Arrays.sort(filesInDirectory);
        return Arrays.asList(filesInDirectory);
    }
    
    public static Logger getLogger () {
        return logger;
    }
    
} 