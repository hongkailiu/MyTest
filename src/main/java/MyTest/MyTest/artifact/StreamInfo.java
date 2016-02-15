package MyTest.MyTest.artifact;

import java.io.InputStream;

/**
 * Created by ehongka on 2/15/16.
 */
public class StreamInfo {
    private InputStream inputStream;
    private long length;

    public StreamInfo(InputStream inputStream, long length) {
        this.inputStream = inputStream;
        this.length = length;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public long getLength() {
        return length;
    }
}
