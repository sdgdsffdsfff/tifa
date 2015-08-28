package walker.basewf.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;


/**
 * 装载并管理配置信息
 *
 * @author HuQingmiao
 */
public class ConfigMgr {

    private static final Logger log = LoggerFactory.getLogger(ConfigMgr.class);

    public static final String CONFIG_DIRC = "conf";  //部署目录下的配置文件的存放目录

    private static final String COMMON_CONFIG = "config.properties";

    //private static final String CORE_CONFIG = "act-core-config.properties";

    //private static final String ENGINE_CONFIG = "act-engine-config.properties";

    private static Properties configProps = new Properties();

    static {
        try {
            load(new File(CONFIG_DIRC + File.separator + COMMON_CONFIG));
            // load(new File(CONFIG_DIRC + File.separator + CORE_CONFIG));
            // load(new File(CONFIG_DIRC + File.separator + ENGINE_CONFIG));

        } catch (Exception e) {
            log.error("", e);
        }
    }

    public static String getProperty(String key) {
        String value = configProps.getProperty(key);
        if (value.equals("")) {
            return null;
        }
        return value.trim();
    }


    private static void load(File file) throws Exception {
        log.info("Load {} begin... ", file.getName());
        try {
            if (file.exists()) {
                InputStream is = new FileInputStream(file);
                configProps.load(is);
            } else {
                InputStream is = ConfigMgr.class.getClassLoader().getResourceAsStream(file.getName());
                configProps.load(is);
            }
        } catch (Exception e) {
            log.error("Load {} successful! ", file.getName(), e);
            throw e;
        }
        log.info("Load {} successful! ", file.getName());
    }


    public static void main(String[] args) {
        //System.out.println(ConfigMgr.getProperty("sftp_config_file"));

        System.out.println("\n>>>>>");
        Properties envProps = System.getProperties();
        for (Iterator it = envProps.keySet().iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            System.out.println(">>>" + key + ": " + envProps.get(key));
        }

        System.out.println("\n>>>>>");
        Map<String, String> envMap = System.getenv();
        for (Iterator<String> it = envMap.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            System.out.println(">>>" + key + ": " + envMap.get(key));
        }


        System.out.println("\n>>>>>");
        System.out.println(System.getProperty("user.dir"));

        String jarPath = ConfigMgr.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        if (jarPath.endsWith(".jar")||jarPath.endsWith(".war")) {
            int idx = jarPath.lastIndexOf("\\");
            if (idx < 0) {
                idx = jarPath.lastIndexOf("/");
            }
            System.out.println(">>" + jarPath.substring(idx + 1));
        }
    }
}
