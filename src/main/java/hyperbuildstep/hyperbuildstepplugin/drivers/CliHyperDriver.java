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
	public ContainerInstance createAndLaunchBuildContainer(Launcher launcher, String image) {
		ArgumentListBuilder args = new ArgumentListBuilder()
			.add("create")
			.add(image);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
			launchHyperCLI(launcher, args)
				.stdout(out).stderr(launcher.getListener().getLogger()).join();
			} catch (Exception e) {

			}

			String containerId = "";
			try {
			containerId = out.toString("UTF-8").trim();
			} catch (Exception e) {

			}

			ContainerInstance buildContainer = new ContainerInstance(containerId);

			try {
			launchHyperCLI(launcher, new ArgumentListBuilder()
				.add("start", containerId))
				.stdout(launcher.getListener().getLogger())
				.stderr(launcher.getListener().getLogger())
				.join();
			} catch (Exception e) {

			}

			return buildContainer;
	}

	@Override
	public void execInContainer(Launcher launcher, String containerId, String commands) {
		ArgumentListBuilder args = new ArgumentListBuilder()
		.add("exec", containerId)
		.add("sh", "-c")
		.add(commands);

		try{
			launchHyperCLI(launcher, args)
			.stdout(launcher.getListener().getLogger())
			.stderr(launcher.getListener().getLogger())
			.join();
		} catch (Exception e) {
			
		}
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