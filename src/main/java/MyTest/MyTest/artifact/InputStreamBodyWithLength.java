package MyTest.MyTest.artifact;

import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.InputStream;

/**
 * Created by ehongka on 2/15/16.
 */
public class InputStreamBodyWithLength extends InputStreamBody {
    private long length;

    public InputStreamBodyWithLength(InputStream is, String filename, long length) {
        super(is, filename);
        this.length = length;
    }

    @Override public long getContentLength() {
        return length;
    }
}
