package MyTest.MyTest.artifact;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;



public class UploadArtifactActions extends Notifier {

    private ArtifactHelper artifactHelper = new ArtifactHelper();

    @DataBoundConstructor public UploadArtifactActions() {

    }


    @Override public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override public boolean perform(final AbstractBuild build, final Launcher launcher,
        final BuildListener listener) throws InterruptedException, IOException {
        String filename1 = "mhweb-service-0.9.pom";
        StreamInfo info1 = artifactHelper.getInputStreamInfo(build, filename1);

        String filename2 = "mhweb-service-0.9.jar";
        StreamInfo info2 = artifactHelper.getInputStreamInfo(build, filename2);

        listener.getLogger().println("uploading to nexus ... ");
        int status = artifactHelper.uploadArtifact(info1, filename1, info2, filename2);
        listener.getLogger().println("uploading to nexus with status: " + status);
        return true;
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
