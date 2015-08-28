package walker.basewf.tifa;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import walker.basewf.demo.jobs.HelloJob;

/**
 * Created by HuQingmiao on 2015-8-13.
 */
public abstract class BasicJob implements Job {

    protected static Logger log = LoggerFactory.getLogger(HelloJob.class);

    protected boolean concurrent = false; //是否允许并发，默认不允许

    public void execute(JobExecutionContext context) throws JobExecutionException {
        if (concurrent) {
            this.doJob();
            return;
        }

        //非并发，则采用静态同步锁
        synchronized (this.getClass().getName()) {
            this.doJob();
        }
    }

    abstract protected void doJob();
}
