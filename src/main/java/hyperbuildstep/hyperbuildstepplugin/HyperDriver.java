package hyperbuildstep.hyperbuildstepplugin;

import hudson.Launcher;

public interface HyperDriver {
	void createAndLaunchBuildContainer(Launcher launcher, String Image);
}