package walker.basewf.tifa;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Huqingmiao
 */

public class JobListener implements ServletContextListener {

    private JobManager jobMgr = JobManager.getInstance();


    public void contextInitialized(ServletContextEvent schedual) {
        jobMgr.start();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        jobMgr.shutdown();
    }

}
