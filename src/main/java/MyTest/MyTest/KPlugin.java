package MyTest.MyTest;

import hudson.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KPlugin extends Plugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(KPlugin.class.getName());
    public KPlugin(){
        LOGGER.info("==============iamhere");
        System.out.print("===============================");
    }
}
