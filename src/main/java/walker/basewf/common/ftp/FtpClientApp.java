package walker.basewf.common.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * FTP 客户端工具，提供上传下载等功能
 *
 * @author HuQingmiao
 */
public class FtpClientApp {

    private static Logger log = LoggerFactory.getLogger(FtpConfig.class);

    //连接模式, passive:被动模式; active:主动模式
    public final static String CONN_MODE_ACTIVE = "active";
    public final static String CONN_MODE_PASSIVE = "passive";

    // 文件类型, asc:文本文件; bin:二进制文件
    public final static String FILE_TYPE_ASC = "asc";
    public final static String FILE_TYPE_BIN = "bin";

    private final static int DEFAULT_TIMEOUT = 60 * 1000;// 1分钟


    // 上传或下载文件过程中，将文件名改为以.tmp结尾的临时文件，以防其它线程对文件进行操作
    private static final String EXTEND_NAME_TMP = ".tmp";

    private String host;
    private int port;
    private String username;
    private String password;
    private String connMode;
    private String fileType;

    private FTPClient client;


    public FtpClientApp() throws Exception {
        this.host = FtpConfig.getHost();
        this.port = FtpConfig.getPort();
        this.username = FtpConfig.getUsername();
        this.password = FtpConfig.getPassword();
        this.connMode = FtpConfig.getConnMode();
        this.fileType = FtpConfig.getFileType();

        if (this.connMode == null || "".equals(this.connMode)) {
            this.connMode = CONN_MODE_PASSIVE;//若不指定，则默认为被动模式
        }

        if (this.fileType == null || "".equals(this.fileType)) {
            this.fileType = FILE_TYPE_BIN;    //若不指定，则默认为二进制文件
        }
        this.client = new FTPClient();
    }


    public FtpClientApp(String host, int port, String username, String password){
        this(host, port, username, password, CONN_MODE_PASSIVE, FILE_TYPE_BIN);
        this.client = new FTPClient();
    }

    public FtpClientApp(String host, int port, String username, String password, String connMode){
        this(host, port, username, password, connMode, FILE_TYPE_BIN);
        this.client = new FTPClient();
    }

    public FtpClientApp(String host, int port, String username, String password, String connMode, String fileType){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.connMode = connMode;
        this.fileType = fileType;

        this.client = new FTPClient();
    }


    private void connect() throws IOException {
        final long RECONNECT_INTERVAL = 3 * 1000;  // 重连间隔时间
        final int MAX_RECONNECT_TIMES = 2;   // 最大重连次数

        int reConnectTimes = 0;
        try {
            this.newConnection();

            // 如果不处于正常连接状态, 则重连
            while (!client.isConnected()
                    && reConnectTimes < MAX_RECONNECT_TIMES) {
                Thread.sleep(RECONNECT_INTERVAL);// 等待几秒后重连
                newConnection();
                reConnectTimes++;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // 多次重连失败, 则抛出异常
        if (reConnectTimes >= MAX_RECONNECT_TIMES) {
            String err = "多次重连失败, 请检查Ftp服务器是否正常!";
            log.error(err);
            throw new IOException(err);
        }

        int replyCode = client.pwd();
        log.debug("pwd reply code: " + replyCode);
        log.debug("work dirc: " + client.printWorkingDirectory());
    }

    private void disConnect() throws IOException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }


    private void newConnection() throws IOException {
        try {
            // 设置通讯的字符集
            //ftpClient.setControlEncoding("GBK");
            client.setDefaultPort(this.port);
            client.connect(this.host);
            log.info(">>正在连接FTP Server({}:{})...", this.host, this.port);

            if (!client.login(this.username, this.password)) {
                log.error(">>连接FTP服务器失败!");
                if (client.isConnected()) {
                    client.disconnect();
                }
            }

            // 发出链接请求后，应马上获取replyCode，检验链接是否登录成功
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.logout();
                client.disconnect();
                log.error(">>FTP服务器没有积极响应连接请求, 连接失败!");
                return;
            }

            // Use passive mode as default because most of us are behind firewalls these days.
            // 有防火墙的客户端不能使用主动模式，这是因为防火墙不允许有来自网外的主动连接，所以用户必须同使用被动模式。
            if (this.connMode.equalsIgnoreCase(CONN_MODE_ACTIVE)) {
                client.enterLocalActiveMode();
                log.info(">>已启用主动模式...");
            } else {
                client.enterLocalPassiveMode();
                log.info(">>已启用被动模式...");
            }

            if (this.fileType.equalsIgnoreCase(FILE_TYPE_ASC)) {
                client.setFileType(FTP.ASCII_FILE_TYPE);
                log.info(">>采用文本传输...");
            } else {
                client.setFileType(FTP.BINARY_FILE_TYPE);
                log.info(">>采用二进制传输...");
            }

            // 设置流模式
            client.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);

            // 防止超时断开
            client.setDefaultTimeout(DEFAULT_TIMEOUT);

            log.info(">>连接FTP服务器成功!");

        } catch (IOException e) {
            log.error("连接FTP服务器失败!", e);
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }


    /**
     * 从本地上传文件到FTP服务器.
     *
     * @param serverDirc FTP服务器上传目录, 若为FTP根目录，则以"/"表示
     * @param file       本地的待上传文件
     * @return 上传成功返回true; 否则false
     */
    public boolean upload(String serverDirc, File file) throws IOException {
        try {
            this.connect();

            // 转到FTP服务器上传目录
            if (!this.client.changeWorkingDirectory(serverDirc)) {
                String err = "FTP服务器上没有该目录: " + serverDirc;
                log.warn(err);
            }

            // 上传文件
            return uploadFile(file);

        } finally {
            this.disConnect();
        }
    }


    /**
     * 从本地上传文件到FTP服务器.
     *
     * @param serverDirc FTP服务器上传目录, 若为FTP根目录，则以"/"表示
     * @param files      本地的待上传文件
     * @return 成功上传的文件个数
     */
    public int upload(String serverDirc, List<File> files) throws IOException {
        try {
            this.connect();

            // 转到FTP服务器上传目录
            if (!this.client.changeWorkingDirectory(serverDirc)) {
                String err = "FTP服务器上没有该目录: " + serverDirc;
                log.warn(err);
            }

            int cnt = 0;
            for (File file : files) {
                if (this.uploadFile(file)) {
                    cnt++;
                }
            }
            return cnt;
        } finally {
            this.disConnect();
        }
    }


    private boolean uploadFile(File file) throws IOException {
        String tmpServerFilename = file.getName() + EXTEND_NAME_TMP;

        boolean uploadSucc = false;
        InputStream is = null;
        try {
            log.info(">>正在上传 " + file.getCanonicalPath() + " ...");
            is = new FileInputStream(file);
            uploadSucc = client.storeFile(tmpServerFilename, is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        if (!uploadSucc) {
            log.info(">>文件上传失败: " + file.getCanonicalPath());
            return uploadSucc;
        }

        log.info(">>上传成功，正准备将临时文件更名为正式文件：{} -> {} ", tmpServerFilename, file.getName());

        final int MAX_TRY_TIMES = 2;   // 最大重试改名的次数

        int tryTimes = 0;// 尝试改名次数
        while (!client.rename(tmpServerFilename, file.getName())
                && tryTimes < MAX_TRY_TIMES) {
            tryTimes++;

            //将临时文件更名为正式文件失败，很可能是因为正式文件已经存在，所以先执行删除
            FTPFile[] lsFiles = client.listFiles(file.getName());
            if (lsFiles.length == 1) {
                client.deleteFile(file.getName());
            }
        }

        if (tryTimes == 2) {
            log.error(">>多次更名失败：{} -> {} ", tmpServerFilename, file.getName());
            throw new IOException("多次更名失败:" + tmpServerFilename);
        }
        return uploadSucc;
    }


    /**
     * 从FTP服务器下载文件
     *
     * @param serverDirc FTP服务器目录, 若为FTP根目录，则以"/"表示
     * @param filename   要下载的文件名
     * @param localDirc  下载文件的本地存放目录
     * @throws java.io.IOException
     */
    public File download(String serverDirc, String filename, File localDirc) throws
            IOException {
        try {
            this.connect();

            // 转到FTP服务器上传目录
            if (!this.client.changeWorkingDirectory(serverDirc)) {
                String err = "FTP服务器上没有该目录: " + serverDirc;
                log.warn(err);
            }

            // 下载文件
            return dnloadFile(filename, localDirc);

        } finally {
            this.disConnect();
        }
    }


    /**
     * 从FTP服务器下载文件
     *
     * @param serverDirc FTP服务器目录, 若为FTP根目录，则以"/"表示
     * @param files      要下载的文件名
     * @param localDirc  下载文件的本地存放目录
     * @throws java.io.IOException
     */
    public List<File> download(String serverDirc, List<String> files, File localDirc) throws
            IOException {
        try {
            this.connect();

            // 转到FTP服务器上传目录
            if (!this.client.changeWorkingDirectory(serverDirc)) {
                String err = "FTP服务器上没有该目录: " + serverDirc;
                log.warn(err);
            }

            List<File> fileList= new ArrayList<File>();
            for (String filename : files) {
                File f = this.dnloadFile(filename, localDirc);
                if (f!=null) {
                    fileList.add(f);
                }
            }
            return fileList;

        } finally {
            this.disConnect();
        }
    }


    private File dnloadFile(String filename, File localDirc) throws IOException {
        if (!localDirc.exists()) {
            localDirc.mkdirs();
        }

        String tmpLocalFilename = filename + EXTEND_NAME_TMP;
        File localTmpFile = new File(localDirc, tmpLocalFilename);
        File localFile = new File(localDirc, filename);

        boolean dnloadSucc = false;
        OutputStream os = null;
        try {
            log.info(">>正在下载 " + filename + " ...");
            os = new BufferedOutputStream(new FileOutputStream(localTmpFile));
            dnloadSucc = client.retrieveFile(filename, os);
        } finally {
            if (os != null) {
                os.close();
            }
        }

        if (!dnloadSucc) {
            return null;
        }

        log.info(">>文件下载成功，正准备将临时文件更名为正式文件：{} -> {} ", tmpLocalFilename, filename);
        final int MAX_TRY_TIMES = 2;   // 最大重试改名的次数

        int tryTimes = 0;// 尝试改名次数
        while (!localTmpFile.renameTo(localFile) && tryTimes < MAX_TRY_TIMES) {
            tryTimes++;

            //将临时文件更名为正式文件失败，很可能是因为正式文件已经存在，所以先执行删除
            if (localFile.exists()) {
                localFile.delete();
            }
        }

        if (tryTimes == 2) {
            log.error(">>多次更名失败：{} -> {} ", localTmpFile.getAbsolutePath(), localFile.getCanonicalPath());
            throw new IOException("多次更名失败:" + localTmpFile.getAbsolutePath());
        }

        return localFile;
    }


    public static void main(String[] args) {

        File file = new File("d:/a.txt");
        File file2 = new File("d:/b.txt");

        List<File> files = new ArrayList<File>();
        files.add(file);
        files.add(file2);

        try {
            FtpClientApp ftpApp = new FtpClientApp();
            log.info(">>" + ftpApp.upload("/a", files));

            List<String> a = new ArrayList<String>();
//            a.add("a.txt");
//            a.add("b.txt");
//            log.info(">>" + ftpApp.download("/", "test.xml", new File("d:/")));
//            log.info(">>" + ftpApp.download("/a", a, new File("d:/")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
