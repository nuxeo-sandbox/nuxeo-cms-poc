/*
 * (C) Copyright 2019 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 *
 */

package org.nuxeo.ecm.cms.smartcrop;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.nuxeo.ecm.platform.commandline.executor.service.CommandLineDescriptor;
import org.nuxeo.ecm.platform.commandline.executor.service.cmdtesters.CommandTestResult;
import org.nuxeo.ecm.platform.commandline.executor.service.cmdtesters.CommandTester;

/**
 * Docker specific implementation of the {@link CommandTester} interface.
 * 
 * Does on check based on images name:
 * 
 * The testParameterString is expected to be
 * 
 * images -q <imagename:tag>
 *
 * @author tiry
 */
public class DockerCommandTester implements CommandTester {

	@Override
	public CommandTestResult test(CommandLineDescriptor cmdDescriptor) {
		String cmd = cmdDescriptor.getCommand();
		String params = cmdDescriptor.getTestParametersString();
		String[] cmdWithParams = (cmd + " " + params).split(" ");
		List<String> imgs;
		try {
			ProcessBuilder builder = new ProcessBuilder(cmdWithParams);
			// make sure we have only one InputStream to read to avoid parallelism/deadlock
			// issues
			builder.redirectErrorStream(true);
			Process process = builder.start();
			// close process input immediately
			process.getOutputStream().close();

			imgs = IOUtils.readLines(process.getInputStream(), "UTF-8");

			process.waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} catch (IOException e) {
			return new CommandTestResult(
					"command " + cmd + " not found in system path (descriptor " + cmdDescriptor + ")");
		}

		if (imgs == null || imgs.size() == 0) {
			return new CommandTestResult("Image is not registred");
		}
		return new CommandTestResult();
	}

}
