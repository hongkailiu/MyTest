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
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class UploadArtifactActions extends Notifier {


    @DataBoundConstructor public UploadArtifactActions() {

    }


    @Override public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override public boolean perform(final AbstractBuild build, final Launcher launcher,
        final BuildListener listener) throws InterruptedException, IOException {
        String filePath = "test.txt";
        listener.getLogger().println("start uploading ... : " + filePath);

        InputStream is = getInputStream(build, filePath);

        String target = "result.data";
        File tempFile = new File(target);
        listener.getLogger().println("save to file: " + tempFile.getAbsolutePath());
        FileUtils.copyInputStreamToFile(is, tempFile);
        return true;
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
