/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 HyperHQ Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package sh.hyper.hyperbuildstep.drivers;

import hudson.Launcher;
import jenkins.model.Jenkins;
import sh.hyper.hyperbuildstep.HyperDriver;
import sh.hyper.hyperbuildstep.ContainerInstance;
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

        int status = launchHyperCLI(launcher, args)
                .stdout(launcher.getListener().getLogger())
                .stderr(launcher.getListener().getLogger())
                .join();

        return status;
    }

    @Override
    public void pullImage(Launcher launcher, String image) throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder()
                .add("pull")
                .add(image);

        int status = launchHyperCLI(launcher, args)
                .stdout(launcher.getListener().getLogger()).join();

        if (status != 0) {
            throw new IOException("Failed to pull image " + image);
        }
    }

    @Override
    public boolean checkImageExists(Launcher launcher, String image) throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder()
                .add("inspect")
                .add("-f", "'{{.Id}}'")
                .add(image);

        return launchHyperCLI(launcher, args)
                .stdout(launcher.getListener().getLogger()).join() == 0;
    }

    public void prependArgs(ArgumentListBuilder args) {
        String jenkinsHome = Jenkins.getInstance().getRootDir().getPath();
        String hyperCliPath = jenkinsHome + "/bin/hyper";
        String configPath = jenkinsHome + "/.hyper";
        args.prepend(configPath);
        args.prepend("--config");
        args.prepend(hyperCliPath);
    }

    private Launcher.ProcStarter launchHyperCLI(Launcher launcher, ArgumentListBuilder args) {
        prependArgs(args);

        return launcher.launch()
                .cmds(args);
    }
}
