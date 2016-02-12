package MyTest.MyTest;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class UploadArtifactActions extends Notifier {


    @DataBoundConstructor public UploadArtifactActions() {

    }


    @Override public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override public boolean perform(final AbstractBuild build, final Launcher launcher,
        final BuildListener listener) throws InterruptedException, IOException {
        String filePath = "test.txt";
        String target = "mhweb-service-0.9.jar";
        //long l1 = copyFileFromSlaveToMaster(build, listener, filePath, target);

        String filePath1 = "pom.xml";
        String pom = "mhweb-service-0.9.pom";
        //long l2 = copyFileFromSlaveToMaster(build, listener, filePath1, pom);

        listener.getLogger().println("uploading to nexus ... ");
        File pomFile = new File(pom);
        InputStream pomIS = new FileInputStream(pomFile);
        File targetFile = new File(target);
        InputStream artifactIS = new FileInputStream(target);
        uploadArtifact(pomIS, pomFile.length(), artifactIS, targetFile.length());
        return true;
    }

    private long copyFileFromSlaveToMaster(AbstractBuild build, BuildListener listener, String filePath, String target)
        throws IOException, InterruptedException {

        listener.getLogger().println("start uploading ... : " + filePath);

        InputStream is = getInputStream(build, filePath);

        File tempFile = new File(target);
        listener.getLogger().println("save to file: " + tempFile.getAbsolutePath());
        FileUtils.copyInputStreamToFile(is, tempFile);
        listener.getLogger().println("file length: : " + tempFile.length());
        return tempFile.length();
    }

    // curl -v -F "r=thirdparty" -F "hasPom=true" -F "e=jar" -F "file=@mhweb-service-0.9.pom" -F "file=@mhweb-service-0.9.jar" -u admin:admin123 http://142.133.111.170:8081/service/local/artifact/maven/content
    private void uploadArtifact(InputStream pom, long l1, InputStream target, long l2) throws IOException {
        //CloseableHttpClient client = HttpClientBuilder.create().build();

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin123");
        provider.setCredentials(AuthScope.ANY, credentials);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

        HttpPost post = new HttpPost("http://142.133.111.170:8081/service/local/artifact/maven/content");

        StringBody stringBody1 = new StringBody("thirdparty", ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody2 = new StringBody("true", ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody3 = new StringBody("jar", ContentType.MULTIPART_FORM_DATA);

        ContentBody cb1 = new InputStreamKnownSizeBody(pom, l1, ContentType.APPLICATION_OCTET_STREAM, "mhweb-service-0.9.pom");
        ContentBody cb2 = new InputStreamKnownSizeBody(target, l2, ContentType.APPLICATION_OCTET_STREAM, "mhweb-service-0.9.jar");

        //FileBody
        //File file = new File(pom);
        /*File file1 = new File(pom);
        FileBody fileBody1 = new FileBody(file1, ContentType.DEFAULT_BINARY);

        File file2 = new File(target);
        FileBody fileBody2 = new FileBody(file2, ContentType.DEFAULT_BINARY);*/

        //
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("r", stringBody1);
        builder.addPart("hasPom", stringBody2);
        builder.addPart("e", stringBody3);
        //builder.addBinaryBody("file1", pom, ContentType.DEFAULT_BINARY, "mhweb-service-0.9.pom");
        //builder.addBinaryBody("file2", target, ContentType.DEFAULT_BINARY, "mhweb-service-0.9.jar");
        builder.addPart("file", cb1);
        builder.addPart("file", cb2);

        HttpEntity entity = builder.build();
        //
        post.setEntity(entity);
        HttpResponse response = client.execute(post);

        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseString = getContent(response);
        final String contentTypeInHeader = getContentTypeHeader(post);
        System.out.println("statusCode: " + statusCode);
        System.out.println("111: " + responseString.contains("Content-Type: multipart/form-data;"));
        System.out.println("222: " + contentTypeInHeader.contains("Content-Type: multipart/form-data;"));
        System.out.println(responseString);
        System.out.println("POST Content Type: " + contentTypeInHeader);
    }

    final String getContent(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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

    private InputStream getInputStream(AbstractBuild build, String filePath)
        throws IOException, InterruptedException {
        if (checkFileExists(filePath)) {
            new FileInputStream(filePath);
        }

        FilePath filePathRelativeToWorkspace = new FilePath(build.getWorkspace(), filePath);
        return filePathRelativeToWorkspace.read();
    }

    private boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    @Extension public static final class UploadArtifactActionsDescriptor
        extends BuildStepDescriptor<Publisher> {

        @Override public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override public String getDisplayName() {
            return "Upload Artifacts";
        }

    }
}
