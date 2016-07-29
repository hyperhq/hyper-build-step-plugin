package sh.hyper.hyperbuildstep;

import hudson.Launcher;

import java.io.IOException;

public interface HyperDriver {
	ContainerInstance createAndLaunchBuildContainer(Launcher launcher, String Image) throws IOException, InterruptedException;

	int execInContainer(Launcher launcher, String containerId, String commands) throws IOException, InterruptedException;

	int removeContainer(Launcher launcher, String containerId) throws IOException, InterruptedException;

	void pullImage(Launcher launcher, String image) throws IOException, InterruptedException;

	boolean checkImageExists(Launcher launcher, String image) throws IOException, InterruptedException;
}