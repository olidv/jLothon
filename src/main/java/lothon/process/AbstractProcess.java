package lothon.process;

import java.io.File;

public abstract class AbstractProcess extends Thread {

    protected final File dataDir;

    public AbstractProcess(File dataDir) {
        this.dataDir = dataDir;
    }

}
