package surya.project.dirtype;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocalDir {

    public File[] getContent(String path){
        try{
            File[] file = new File(path).listFiles();
            return file;
        }catch(Exception e){
            System.out.println(e);
        }
        return null;
    }

    public void createDir(String name, String path) throws IOException {
        String dirName = path + "/" + name;
        Path dir = Path.of(dirName);

        Files.createDirectories(dir);
    }

    public void deleteDir(String name, String path) throws IOException {
        String dirName = path + "/" + name;
        Path dir = Path.of(dirName);
        Files.deleteIfExists(dir);
    }
}
