package walker.basewf.common.mq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import walker.basewf.common.utils.ConfigMgr;

public class MqConnectFactory {

    private static final Logger log = LoggerFactory.getLogger(MqConnectFactory.class);

    private static final ConnectionFactory factory = new ConnectionFactory();

    static {
        factory.setHost(ConfigMgr.getProperty("mqHost"));
        factory.setPort(Integer.parseInt(ConfigMgr.getProperty("mqPort")));
        factory.setUsername(ConfigMgr.getProperty("mqUsername"));
        factory.setPassword(ConfigMgr.getProperty("mqPassword"));
    }

    public static Connection getConnection() throws IOException {
        log.info("mq host: {}:{} ", ConfigMgr.getProperty("mqHost"), ConfigMgr.getProperty("mqPort"));
        return factory.newConnection();
    }
}