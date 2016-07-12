package hyperbuildstep.hyperbuildstepplugin;

import hudson.Launcher;
import hyperbuildstep.hyperbuildstepplugin.ContainerInstance;

public interface HyperDriver {
	ContainerInstance createAndLaunchBuildContainer(Launcher launcher, String Image);

	void execInContainer(Launcher launcher, String containerId, String commands);
}