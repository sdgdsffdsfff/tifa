package walker.basewf.demo.jobs;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzTest {

    private static Logger log = LoggerFactory.getLogger(QuartzTest.class);

    /**
     * @throws Exception
     */
    public void run() throws Exception {
        log.info(">>BEGIN......");
        // compute a time that is on the next round minute
        // Date runTime = evenMinuteDate(new Date());

        Scheduler sched = new StdSchedulerFactory().getScheduler();

        // 创建HelloJob
        JobDetail helloJob = newJob(HelloJob.class)
                .withIdentity("helloJob", "group1")
                .build();

        // 创建触发器
        Trigger helloTrig = newTrigger()
                .withIdentity("helloTrig", "group1")
                .withSchedule(cronSchedule("1/1 * * * * ?"))
                        //.startAt(runTime)
                .build();

        // 放入调度计划
        sched.scheduleJob(helloJob, helloTrig);


        // 创建MeiziJob
        JobDetail meiziJob = newJob(MeiziJob.class)
                .withIdentity("meiziJob", "group1")
                .build();

        // 创建触发器
        Trigger meiziTrig = newTrigger()
                .withIdentity("meiziTrig", "group1")
                .withSchedule(cronSchedule("1/1 * * * * ?"))
                        //.startAt(runTime)
                .build();

        // 注册触发器
        sched.scheduleJob(meiziJob, meiziTrig);

        //启动调度任务
        log.info(">>启动调度器...");
        sched.start();


//===============  测试表明，虽然把job暂停, 但在恢复后仍然会追加执行暂停后60秒内触发的任务; 这意味着1分钟后才真正停止这个job ===========//
        Thread.sleep(5 * 1000);

        log.info(">>暂停HelloJob...");
        //sched.pauseJob(helloJob.getKey());
        sched.pauseTrigger(helloTrig.getKey());

        Thread.sleep(61 * 1000);

        log.info(">>恢复HelloJob...");
        //sched.resumeJob(helloJob.getKey());
        sched.resumeTrigger(helloTrig.getKey());

        log.info(">>END<<");
    }

    public static void main(String[] args) throws Exception {
        QuartzTest example = new QuartzTest();
        example.run();
    }

}
