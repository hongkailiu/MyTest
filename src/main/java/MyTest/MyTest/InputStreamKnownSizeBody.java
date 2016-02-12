package MyTest.MyTest;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.InputStream;

/**
 * Created by ehongka on 2/12/16.
 * ref. http://www.radomirml.com/blog/2009/02/13/file-upload-with-httpcomponents-successor-of-commons-httpclient/
 */
public class InputStreamKnownSizeBody extends InputStreamBody {
    private long length;

    public InputStreamKnownSizeBody(
        final InputStream in, final long length,
        ContentType contentType, final String filename) {
        super(in, contentType, filename);
        this.length = length;
    }

    @Override
    public long getContentLength() {
        return this.length;
    }
}
