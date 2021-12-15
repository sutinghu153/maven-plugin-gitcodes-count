package sutinghu.tools.utils;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @data2021/12/15,14:10
 * @authorsutinghu
 */
public class FileUtils {


    public static String getDir() {

        String projectPath = System.getProperty("user.dir");

        File file = new File(projectPath);

        if (!file.isDirectory()) {
            throw new StringIndexOutOfBoundsException();
        }

        List<String> list = Arrays.asList(file.list());

//        if (!list.contains(".git")) {
//            throw new StringIndexOutOfBoundsException("no git Please check whether Git is initialized");
//        }

        return "D:\\osmp\\sanya_online\\osmp_java_basa";
    }

}
