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

	public void launchBuildProcess(Launcher launcher, TaskListener listener, String image, String commands) {
		ContainerInstance targetContainer = launchBuildContainer(launcher, listener, image);

		driver.execInContainer(launcher, targetContainer.getId(), commands);

		driver.removeContainer(launcher, targetContainer.getId());
	}

	public ContainerInstance launchBuildContainer(Launcher launcher, TaskListener listener, String image) {
		return driver.createAndLaunchBuildContainer(launcher, image);

	}
}