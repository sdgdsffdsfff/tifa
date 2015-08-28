package walker.basewf.common.jobcenter.jobs;

import java.util.Date;
import walker.basewf.common.jobcenter.BasicJob;



public class MeiziJob extends BasicJob {

    public void doJob() {
        log.info(">>Beautiful! - " + new Date());
    }

}
