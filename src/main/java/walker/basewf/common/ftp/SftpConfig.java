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
public class SftpConfig {

    private static Logger log = LoggerFactory.getLogger(SftpConfig.class);

    private static final String CONFIG_FILENAME = "sftp-config.xml";

    private static Map<String, String> configMap = new HashMap<String, String>();

    static {
        load();
    }

    private SftpConfig() {
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


    //默认的FTP服务器上的上传目录
    public static String getServerUploadDirc() {
        return configMap.get("serverUploadDirc");
    }

    //默认的FTP服务器上的下载目录
    public static String getServerDnloadDirc() {
        return configMap.get("serverDnloadDirc");
    }

    //默认的存放下载文件的本地目录
    public static String getLocalDnloadDirc() {
        return configMap.get("localDnloadDirc");
    }


    //从data-handler装载handlers类
    private static void load() {
        log.info("Load {} begin... ", CONFIG_FILENAME);

        Document doc = null;
        try {
            File file = new File(CONFIG_FILENAME);
            if (!file.exists()) {
                URL url = SftpConfig.class.getClassLoader().getResource(CONFIG_FILENAME);
                file = new File(url.getPath());
            }
            doc = XmlUtil.read(file);

            Element root = doc.getRootElement();

            for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                Element e = it.next();
                configMap.put(e.attribute("name").getValue(), e.attribute("value").getValue().trim());
            }

            log.info("Load {} successful! ", CONFIG_FILENAME);

        } catch (Exception e) {
            log.error("Load {} failed! ", CONFIG_FILENAME, e);
        } finally {
            if (doc != null) {
                doc.clearContent();
            }
        }
    }


    public static void main(String[] args) {

        System.out.println(SftpConfig.getHost());
    }

}
