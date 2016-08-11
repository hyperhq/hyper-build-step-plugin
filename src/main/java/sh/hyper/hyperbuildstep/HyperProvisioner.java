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