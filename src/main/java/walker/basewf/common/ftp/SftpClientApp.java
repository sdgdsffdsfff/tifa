package walker.basewf.common.ftp;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * SFTP客户端
 *
 * @author HuQingmiao
 */
public class SftpClientApp {

    private static Logger log = LoggerFactory.getLogger(walker.basewf.common.ftp.SftpConfig.class);

    private final static int DEFAULT_TIMEOUT = 30 * 1000;// 1分钟

    private String host;
    private int port;
    private String username;
    private String password;

    private String serverUploadDirc;
    private String serverDnloadDirc;
    private String localDnloadDirc;


    private JSch jsch = new JSch();
    private Session session;
    private ChannelSftp channel;

    public SftpClientApp() {
        this.host = walker.basewf.common.ftp.SftpConfig.getHost();
        this.port = SftpConfig.getPort();
        this.username = SftpConfig.getUsername();
        this.password = SftpConfig.getPassword();
        this.serverUploadDirc = SftpConfig.getServerUploadDirc();
        this.serverDnloadDirc = SftpConfig.getServerDnloadDirc();
        this.localDnloadDirc = SftpConfig.getLocalDnloadDirc();
    }

    public SftpClientApp(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 从本地上传文件到FTP服务器.
     *
     * @param serverDirc FTP服务器上传目录, 若为FTP根目录，则以"/"表示；
     *                   如果传入null, 则会取默认的上传目录：SftpConfig.getServerUploadDirc()。
     * @param file       本地的待上传文件
     */
    public void upload(String serverDirc, File file) throws IOException, SftpException {
        if (serverDirc == null) {
            serverDirc = this.serverUploadDirc;
        }
        try {
            this.connect();

            // 转到FTP服务器上传目录
            channel.cd(serverDirc);

            // 上传文件
            this.uploadFile(serverDirc, file);

        } finally {
            this.disConnect();
        }
    }


    /**
     * 从本地上传文件到FTP服务器.
     *
     * @param serverDirc FTP服务器上传目录, 若为FTP根目录，则以"/"表示；
     *                   如果传入null, 则会取默认的上传目录：SftpConfig.getServerUploadDirc()。
     * @param files      本地的待上传文件
     */
    public void upload(String serverDirc, List<File> files) throws IOException, SftpException {
        if (serverDirc == null) {
            serverDirc = this.serverUploadDirc;
        }
        try {
            this.connect();

            // 转到FTP服务器上传目录
            channel.cd(serverDirc);

            for (File file : files) {
                this.uploadFile(serverDirc, file);
            }

        } finally {
            this.disConnect();
        }
    }


    private void uploadFile(String serverDirc, File file) throws IOException, SftpException {
        InputStream is = null;
        try {
            log.info(">>正在上传" + file.getCanonicalPath() + " ...");

            is = new FileInputStream(file);
            channel.put(is, file.getName());

            log.info(">>文件{}已被上传至{}!", file.getName(), serverDirc);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    /**
     * 从FTP服务器下载文件
     *
     * @param serverDirc FTP服务器目录, 若为FTP根目录，则以"/"表示；
     *                   如果传入null, 则会取默认的下载目录：SftpConfig.getServerDnloadDirc()。
     * @param filename   要下载的文件名
     * @param localDirc  下载文件的本地存放目录
     * @throws java.io.IOException
     */
    public File download(String serverDirc, String filename, File localDirc)
            throws IOException, SftpException {
        if (serverDirc == null) {
            serverDirc = this.serverDnloadDirc;
        }
        if (localDirc == null) {
            localDirc = new File(this.localDnloadDirc);
        }
        try {
            this.connect();

            // 转到FTP服务器上传目录
            channel.cd(serverDirc);

            // 下载文件
            return dnloadFile(filename, localDirc);

        } finally {
            this.disConnect();
        }
    }


    /**
     * 从FTP服务器下载文件
     *
     * @param serverDirc FTP服务器目录, 若为FTP根目录，则以"/"表示；
     *                   如果传入null, 则会取默认的下载目录：SftpConfig.getServerDnloadDirc()。
     * @param files      要下载的文件名
     * @param localDirc  下载文件的本地存放目录, 如果传入null, 则会取默认的本地目录：SftpConfig.getLocalDnloadDirc()。
     * @throws java.io.IOException
     */
    public List<File> download(String serverDirc, List<String> files, File localDirc) throws
            IOException, SftpException {
        if (serverDirc == null) {
            serverDirc = this.serverDnloadDirc;
        }
        if (localDirc == null) {
            localDirc = new File(this.localDnloadDirc);
        }
        try {
            this.connect();

            // 转到FTP服务器上传目录
            channel.cd(serverDirc);

            List<File> fileList = new ArrayList<File>();
            for (String filename : files) {
                fileList.add(this.dnloadFile(filename, localDirc));
            }
            return fileList;

        } finally {
            this.disConnect();
        }
    }


    private File dnloadFile(String filename, File localDirc) throws IOException, SftpException {
        if (!localDirc.exists()) {
            localDirc.mkdirs();
        }
        File localFile = new File(localDirc, filename);
        OutputStream os = null;
        try {
            // 检查文件是否存在
            Vector<ChannelSftp.LsEntry> fileList = channel.ls(filename);
            if (fileList.isEmpty()) {
                return null;
            }

            log.info(">>正在下载" + filename + " ...");

            os = new BufferedOutputStream(new FileOutputStream(localFile));
            channel.get(filename, os);

            log.info(">>文件{}已被下载至{}!", filename, localDirc);
            return localFile;

        } finally {
            if (os != null) {
                os.close();
            }
        }
    }


    private void connect() throws IOException {
        final long RECONNECT_INTERVAL = 3 * 1000;  // 重连间隔时间
        final int MAX_RECONNECT_TIMES = 2;   // 最大重连次数

        int reConnectTimes = 0;
        try {
            //创建Session和Channel
            newChannel();

            // 如果不处于正常连接状态, 则重连
            while (channel == null && reConnectTimes < MAX_RECONNECT_TIMES) {
                Thread.sleep(RECONNECT_INTERVAL);// 等待几秒后重连
                newChannel();
                reConnectTimes++;
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        // 多次重连失败, 则抛出异常
        if (reConnectTimes >= MAX_RECONNECT_TIMES) {
            String err = ">>多次重连失败, 请检查SFtp服务器是否正常!";
            log.error(err);
            throw new IOException(err);
        }
    }

    private void disConnect() throws IOException {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
            channel = null;
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
            session = null;
        }
    }


    private void newSession() throws IOException {
        try {
            this.session = jsch.getSession(username, host, port);
            this.session.setPassword(password);

            //设置首次登陆的提示，可选值：(ask | yes | no)
            this.session.setConfig("StrictHostKeyChecking", "no");
            this.session.connect(DEFAULT_TIMEOUT);
            log.info(">>获取Sftp的Session成功! ");

        } catch (JSchException e) {
            log.error(">>获取Sftp的Session失败!", e);
            if (session != null) {
                session = null;
            }
        }
    }

    private void newChannel() throws IOException {
        try {
            if (session == null || !session.isConnected()) {
                newSession();
            }
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            log.info(">>建立Sftp的Channel成功!");

        } catch (JSchException e) {
            log.error(">>建立Sftp的Channel失败!", e);
            if (channel != null) {
                channel = null;
            }
        }
    }

    public static void main(String[] args) {

        File file = new File("d:/a.txt");
        File file2 = new File("d:/b.txt");

        List<File> files = new ArrayList<File>();
        files.add(file);
        files.add(file2);

        try {
            SftpClientApp ftpApp = new SftpClientApp();
            ftpApp.upload(null, new File("d:/chk_files/CHK_106000_CES_20150718.dat"));

            //log.info(">>" + ftpApp.download(null, "CHK_106000_CES_20150718.dat", new File("d:/chk_files")));
            //log.info(">>" + ftpApp.download("/a", a, new File("d:/")));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
