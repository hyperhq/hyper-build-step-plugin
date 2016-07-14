package hyperbuildstep.hyperbuildstepplugin;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.util.ArgumentListBuilder;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link HyperBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #image})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 */
public class HyperBuilder extends Builder implements SimpleBuildStep {

    private final String image;
    private final String commands;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public HyperBuilder(String image, String commands) {
        this.image = image;
        this.commands = commands;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getImage() {
        return image;
    }

    public String getCommands() {
        return commands;
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        HyperProvisioner provisioner = new HyperProvisioner();
        try {
            provisioner.launchBuildProcess(launcher, listener, image, commands);
        } catch (Exception e) {

        }

    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link HyperBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hyper_world/HyperBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user. 
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a name");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the name too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Execute shell in Hyper_";
        }

        //save credential
        public FormValidation doSaveCredential(@QueryParameter("accessId") final String accessId,
                                               @QueryParameter("secretKey") final String secretKey) throws IOException, ServletException {
            try {
                String jsonStr = "{\"clouds\": {" +
                        "\"tcp://us-west-1.hyper.sh:443\": {" +
                        "\"accesskey\": " + "\"" + accessId + "\"," +
                        "\"secretkey\": " + "\"" + secretKey + "\"" +
                        "}" +
                        "}" +
                        "}";
                BufferedWriter writer = null;
                String configPath;
                String jenkinsHome = System.getenv("HUDSON_HOME");

                if (jenkinsHome == null) {
                    configPath = "./hyper/config.json";
                } else {
                    File hyperPath = new File(jenkinsHome +"/.hyper");
                    if (!hyperPath.exists()) {
                        hyperPath.mkdir();
                    }
                    configPath = jenkinsHome + "/.hyper/config.json";
                }


                File config = new File(configPath);
                if(!config.exists()){
                    try {
                        config.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    writer = new BufferedWriter(new FileWriter(config));
                    writer.write(jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if(writer != null){
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return FormValidation.ok("Credentials saved!");
            } catch (Exception e) {
                return FormValidation.error("Saving credentials error : "+e.getMessage());
            }
        }

        //download Hypercli
        public FormValidation doDownloadHypercli() throws IOException, ServletException {
            try {
                String urlPath = "https://hyper-install.s3.amazonaws.com/hyper";
                String hyperCliPath;
                URL url = new URL(urlPath);
                URLConnection connection = url.openConnection();
                InputStream in = connection.getInputStream();

                String jenkinsHome = System.getenv("HUDSON_HOME");

                if (jenkinsHome == null) {
                    hyperCliPath = "./hyper";
                } else {
                    File hyperPath = new File(jenkinsHome +"/bin");
                    if (!hyperPath.exists()) {
                        hyperPath.mkdir();
                    }
                    hyperCliPath = jenkinsHome + "/bin/hyper";
                }

                FileOutputStream os = new FileOutputStream(hyperCliPath);
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    os.write(buffer, 0, read);
                }
                os.close();
                in.close();

                try {
                    String command = "chmod +x " + hyperCliPath;
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec(command);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return FormValidation.ok("Hypercli downloaded!");
            } catch (Exception e) {
                return FormValidation.error("Downloading Hypercli error : "+e.getMessage());
            }
        }
    }
}

