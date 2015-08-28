package walker.basewf.demo.jobs;

import java.util.Date;
import walker.basewf.tifa.BasicJob;



public class MeiziJob extends BasicJob {

    public void doJob() {
        log.info(">>Beautiful! - " + new Date());
    }

}
