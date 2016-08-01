package sh.hyper.hyperbuildstep;

import sh.hyper.hyperbuildstep.drivers.CliHyperDriver;
import hudson.Launcher;
import hudson.model.TaskListener;

import java.io.IOException;

public class HyperProvisioner {

	protected final HyperDriver driver;

	public HyperProvisioner() {
		this.driver = new CliHyperDriver();
	}

	public void launchBuildProcess(Launcher launcher, TaskListener listener, String image, String commands) throws IOException, InterruptedException {
		ContainerInstance targetContainer = launchBuildContainer(launcher, listener, image);

		int status = driver.execInContainer(launcher, targetContainer.getId(), commands);

		if (status == 0) {
			driver.removeContainer(launcher, targetContainer.getId());
		}	
	}

	public ContainerInstance launchBuildContainer(Launcher launcher, TaskListener listener, String image) throws IOException, InterruptedException {
		if (!driver.checkImageExists(launcher, image)) {
			driver.pullImage(launcher, image);
		}

		return driver.createAndLaunchBuildContainer(launcher, image);
	}
}