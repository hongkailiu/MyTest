package MyTest.MyTest.artifact;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by ehongka on 2/15/16.
 */
public class ArtifactHelper {

    public int uploadArtifact(StreamInfo pomStreamInfo, String pomFilename,
        StreamInfo jarStreamInfo, String jarFilename) throws IOException {
        // curl -v -F "r=thirdparty" -F "hasPom=true" -F "e=jar" -F "file=@mhweb-service-0.9.pom" -F "file=@mhweb-service-0.9.jar" -u admin:admin123 http://142.133.111.170:8081/service/local/artifact/maven/content
        CloseableHttpClient client = HttpClientBuilder.create().build();

        HttpPost post =
            new HttpPost("http://142.133.111.178:8081/service/local/artifact/maven/content");

        // basic authentication
        String auth = "admin" + ":" + "admin123";
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        post.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        StringBody stringBody1 = new StringBody("thirdparty", ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody2 = new StringBody("true", ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody3 = new StringBody("jar", ContentType.MULTIPART_FORM_DATA);

        InputStreamBodyWithLength inputStreamBodyWithLength1 =
            new InputStreamBodyWithLength(pomStreamInfo.getInputStream(), pomFilename,
                pomStreamInfo.getLength());

        //File file2 = new File(target);
        InputStreamBodyWithLength inputStreamBodyWithLength2 =
            new InputStreamBodyWithLength(jarStreamInfo.getInputStream(), jarFilename,
                jarStreamInfo.getLength());

        //
        System.out.println("aaa:" + pomFilename + "bbb:" + pomStreamInfo.getLength());
        System.out.println("111:" + jarFilename + "222:" + jarStreamInfo.getLength());

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("r", stringBody1);
        builder.addPart("hasPom", stringBody2);
        builder.addPart("e", stringBody3);
        builder.addPart("file", inputStreamBodyWithLength1);
        builder.addPart("file", inputStreamBodyWithLength2);

        HttpEntity entity = builder.build();
        post.setEntity(entity);
        HttpResponse response = client.execute(post);

        return response.getStatusLine().getStatusCode();
    }

    final String getContent(HttpResponse response) throws IOException {
        BufferedReader rd =
            new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String body = "";
        String content = "";
        while ((body = rd.readLine()) != null) {
            content += body + "\n";
        }
        return content.trim();
    }

    final String getContentTypeHeader(HttpPost post) throws IOException {
        return post.getEntity().getContentType().toString();
    }

    public StreamInfo getInputStreamInfo(AbstractBuild build, String filePath)
        throws IOException, InterruptedException {
        if (checkFileExists(filePath)) {
            File file = new File(filePath);
            return new StreamInfo(new FileInputStream(file), file.length());
        }

        FilePath filePathRelativeToWorkspace = new FilePath(build.getWorkspace(), filePath);
        return new StreamInfo(filePathRelativeToWorkspace.read(), filePathRelativeToWorkspace.length());
    }

    private boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
}
