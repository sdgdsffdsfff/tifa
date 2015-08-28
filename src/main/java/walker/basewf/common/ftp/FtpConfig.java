package walker.basewf.common.ftp;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import walker.basewf.common.utils.XmlUtil;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * 载入FTP配置信息
 * <p/>
 * Created by HuQingmiao on 2015-6-4.
 */
public class FtpConfig {

    private static Logger log = LoggerFactory.getLogger(FtpConfig.class);

    private static final String CONFI_FILENAME = "ftp-config.xml";

    private static Map<String, String> configMap = new HashMap<String, String>();

    static {
        load();
    }

    private FtpConfig() {
    }

    public static String getHost() {
        return configMap.get("host");
    }

    public static int getPort() {
        return Integer.parseInt(configMap.get("port"));
    }

    public static String getUsername() {
        return configMap.get("username");
    }

    public static String getPassword() {
        return configMap.get("password");
    }

    public static String getConnMode() {
        return configMap.get("connMode");
    }

    public static String getFileType() {
        return configMap.get("fileType");
    }


    //从data-handler装载handlers类
    private static void load() {
        log.info("Load {} begin... ", CONFI_FILENAME);

        Document doc = null;
        try {
            File file = new File(CONFI_FILENAME);
            if (!file.exists()) {
                URL url = FtpConfig.class.getClassLoader().getResource(CONFI_FILENAME);
                file = new File(url.getPath());
            }
            doc = XmlUtil.read(file);

            Element root = doc.getRootElement();

            for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                Element e = it.next();
                configMap.put(e.attribute("name").getValue(), e.attribute("value").getValue().trim());
            }

            log.info("Load {} successful! ", CONFI_FILENAME);

        } catch (Exception e) {
            log.error("Load {} failed! ", CONFI_FILENAME, e);
        } finally {
            if (doc != null) {
                doc.clearContent();
            }
        }
    }


    public static void main(String[] args) {

        System.out.println(FtpConfig.getHost());
    }

}
