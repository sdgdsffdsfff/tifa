package walker.basewf.demo.jobs;

import java.util.Date;
import walker.basewf.tifa.BasicJob;


public class HelloJob extends BasicJob {
    protected void doJob() {
        log.info(">>Hello World:) - " + new Date());
        try {
            Thread.sleep(2 * 1000);
        } catch (NumberFormatException e) {
            log.error("", e);
        } catch (InterruptedException e) {
            log.error("", e);
        }
        log.info(">>Game Over!! - " + new Date());
    }

}
