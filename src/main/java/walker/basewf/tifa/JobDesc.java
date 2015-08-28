package walker.basewf.tifa;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import walker.basewf.common.BasicVo;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 *
 * 作业描述类
 *
 * Created by HuQingmiao on 2015-8-17.
 */
public class JobDesc extends BasicVo{

    private String identity;      //作业标识

    private String desc;          //作业描述

    private String className;     //作业类名

    private String cronExpression;//时间


    private JobDetail jobDetail; //作业细节

    private Trigger trigger;     //触发器


    public JobDesc(String identity, String className, String cronExpression, String desc) throws ClassNotFoundException {
        this.identity = identity;
        this.className = className;
        this.cronExpression = cronExpression;
        this.desc = desc;

        // 创建作业实例
        this.jobDetail = newJob((Class<? extends Job>) Class.forName(className))
                .withIdentity(identity, "walker-group")
                .build();

        // 创建触发器
        this.trigger = newTrigger()
                .withIdentity(identity + "Trig", "walker-group")
                .withSchedule(cronSchedule(cronExpression))
                .build();
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public void setJobDetail(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
