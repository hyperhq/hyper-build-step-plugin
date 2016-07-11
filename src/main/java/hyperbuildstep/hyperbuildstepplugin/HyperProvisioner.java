package hyperbuildstep.hyperbuildstepplugin;

import hyperbuildstep.hyperbuildstepplugin.drivers.CliHyperDriver;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.TaskListener;

public class HyperProvisioner {

	protected final HyperDriver driver;

	public HyperProvisioner() {
		this.driver = new CliHyperDriver();
	}

	public void launchBuildProcess(Launcher launcher, TaskListener listener) {
		launchBuildContainer(launcher, listener);
	}

	public void launchBuildContainer(Launcher launcher, TaskListener listener) {
		driver.createAndLaunchBuildContainer(launcher, "busybox");
	}
}