package walker.basewf.common.jobcenter;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.dom4j.Document;
import org.dom4j.Element;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import walker.basewf.common.utils.XmlUtil;


/**
 * 作业调度管理与监控
 *
 * @author Huqingmiao
 */
public class JobManager extends Thread {
    private static Logger log = LoggerFactory.getLogger(JobManager.class);

    private final static String CONFIG_FILENAME = "quartz-jobs.xml";

    //Map<作业标识号, 作业描述类>
    private Map<String, JobDesc> jobDescMap = new HashMap<String, JobDesc>();

    private Scheduler scheduler = null;

    private static JobManager instance = new JobManager();

    public static JobManager getInstance() {
        return instance;
    }

    private JobManager() {
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    public void run() {
        try {
            this.startAllJobs();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }


    public void shutdown() {
        try {
            this.shutAllJobs();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }

    private void loadJobs() {
        log.info("Load {} begin... ", CONFIG_FILENAME);

        Document doc = null;
        try {
            File file = new File(CONFIG_FILENAME);
            if (!file.exists()) {
                URL url = JobListener.class.getClassLoader().getResource(CONFIG_FILENAME);
                file = new File(url.getPath());
            }
            doc = XmlUtil.read(file);

            // List list = doc.selectNodes("//tasks/task");
            Element root = doc.getRootElement();
            for (Iterator it = root.elementIterator(); it.hasNext(); ) {
                Element e = (Element) it.next();
                String desc = e.attribute("desc").getValue().trim();
                String className = e.attribute("className").getValue().trim();
                String cronExpression = e.attribute("cronExpression").getValue().trim();
                //String concurrent = e.attribute("concurrent").getValue().trim();//是否允许并发

                //创建作业描述类
                String identity = className.substring(className.lastIndexOf('.') + 1);
                JobDesc jobDesc = new JobDesc(identity, className, cronExpression, desc);

                jobDescMap.put(identity, jobDesc);
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

    /**
     * 启动所有作业
     *
     * @throws SchedulerException
     */
    private void startAllJobs() throws SchedulerException {
        if (jobDescMap.isEmpty()) {
            this.loadJobs();
        }
        for (Iterator<String> it = jobDescMap.keySet().iterator(); it.hasNext(); ) {
            String identity = it.next();
            JobDesc jobDesc = jobDescMap.get(identity);
            JobDetail jobDetail = jobDesc.getJobDetail();
            Trigger jobTrigger = jobDesc.getTrigger();
            scheduler.scheduleJob(jobDetail, jobTrigger);
        }
        scheduler.start();
    }

    /**
     * 终止所有作业
     *
     * @throws SchedulerException
     */
    private void shutAllJobs() throws SchedulerException {
        for (Iterator<String> it = jobDescMap.keySet().iterator(); it.hasNext(); ) {
            String identity = it.next();
            JobDesc jobDesc = jobDescMap.get(identity);
            Trigger jobTrigger = jobDesc.getTrigger();
            scheduler.pauseTrigger(jobTrigger.getKey());
        }
        scheduler.shutdown();
    }


    /**
     * 终止指定的作业
     *
     * @param identity
     * @throws SchedulerException
     */
    public void pauseJob(String identity) throws SchedulerException {
        if(!jobDescMap.containsKey(identity)){
            return;
        }
        JobDesc jobDesc = jobDescMap.get(identity);
        Trigger jobTrigger = jobDesc.getTrigger();
        scheduler.pauseTrigger(jobTrigger.getKey());
    }

    /**
     * 恢复指定的作业
     *
     * @param identity
     * @throws SchedulerException
     */
    public void resumeJob(String identity) throws SchedulerException {
        if(!jobDescMap.containsKey(identity)){
            return;
        }
        JobDesc jobDesc = jobDescMap.get(identity);
        Trigger jobTrigger = jobDesc.getTrigger();
        scheduler.resumeTrigger(jobTrigger.getKey());
    }


    public Map<String, JobDesc> getJobDescMap() {
        return this.jobDescMap;
    }


    public static void main(String[] agrs) {
        JobManager jobManager = JobManager.getInstance();
        jobManager.start();

        System.out.println(">>>Sleep 10 secs ");
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(">>>Show all jobs: ");
        Map<String, JobDesc> jobDescMap = jobManager.getJobDescMap();
        for (Iterator<String> it = jobDescMap.keySet().iterator(); it.hasNext(); ) {
            String identity = it.next();

            JobDesc jobDesc = jobDescMap.get(identity);
            System.out.println(jobDesc.getIdentity()+" "+jobDesc.getCronExpression()+" "+jobDesc.getClassName());
        }

        System.out.println(">>>Sleep 10 secs ");
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(">>>Pause MeiziJob ");
        try {
            jobManager.pauseJob("MeiziJob");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        System.out.println(">>>Sleep 10 secs ");
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(">>>Shutdown all jobs ");
        jobManager.shutdown();
    }
}
