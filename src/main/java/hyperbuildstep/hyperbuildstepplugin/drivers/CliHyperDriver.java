package hyperbuildstep.hyperbuildstepplugin.drivers;

import hudson.Launcher;
import hudson.Proc;
import hyperbuildstep.hyperbuildstepplugin.HyperDriver;
import hyperbuildstep.hyperbuildstepplugin.ContainerInstance;
import hudson.util.ArgumentListBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CliHyperDriver implements HyperDriver {

	@Override
	public ContainerInstance createAndLaunchBuildContainer(Launcher launcher, String image) throws IOException, InterruptedException {
		ArgumentListBuilder args = new ArgumentListBuilder()
			.add("create")
			.add(image);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int status = launchHyperCLI(launcher, args)
				.stdout(out).stderr(launcher.getListener().getLogger()).join();

			if (status != 0) {
				throw new IOException("Failed to create container");
			}

			String containerId = out.toString("UTF-8").trim();

			ContainerInstance buildContainer = new ContainerInstance(containerId);

			status = launchHyperCLI(launcher, new ArgumentListBuilder()
				.add("start", containerId))
				.stdout(launcher.getListener().getLogger())
				.stderr(launcher.getListener().getLogger())
				.join();

			if (status != 0) {
				throw new IOException("Failed to start container");
			}

			return buildContainer;
	}

	@Override
	public int execInContainer(Launcher launcher, String containerId, String commands) throws IOException, InterruptedException {
		ArgumentListBuilder args = new ArgumentListBuilder()
		.add("exec", containerId)
		.add("sh", "-c")
		.add(commands);

		int status = launchHyperCLI(launcher, args)
			.stdout(launcher.getListener().getLogger())
			.stderr(launcher.getListener().getLogger())
			.join();

		return status;
	}

	@Override
	public int removeContainer(Launcher launcher, String containerId) throws IOException, InterruptedException {
		ArgumentListBuilder args = new ArgumentListBuilder()
			.add("rm", "-f", containerId);

		int	status = launchHyperCLI(launcher, args)
				.stdout(launcher.getListener().getLogger())
				.stderr(launcher.getListener().getLogger())
				.join();

		return status;
	}

	public void prependArgs(ArgumentListBuilder args) {
		args.prepend("hyper");
	}

	private Launcher.ProcStarter launchHyperCLI(Launcher launcher, ArgumentListBuilder args) {
		prependArgs(args);

		return launcher.launch()
				.cmds(args);
	}
}