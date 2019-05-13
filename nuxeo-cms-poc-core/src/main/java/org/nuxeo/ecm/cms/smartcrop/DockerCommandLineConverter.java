package org.nuxeo.ecm.cms.smartcrop;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CloseableFile;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionException;
import org.nuxeo.ecm.platform.commandline.executor.api.CmdParameters;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandException;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandNotAvailable;
import org.nuxeo.ecm.platform.commandline.executor.api.ExecResult;
import org.nuxeo.ecm.platform.convert.plugins.CommandLineConverter;
import org.nuxeo.runtime.api.Framework;

import com.google.common.io.Files;

public class DockerCommandLineConverter extends CommandLineConverter {

	protected class CmdReturn {
		protected final CmdParameters params;

		protected final List<String> output;

		protected CmdReturn(CmdParameters params, List<String> output) {
			this.params = params;
			this.output = output;
		}
	}

	@Override
	public BlobHolder convert(BlobHolder blobHolder, Map<String, Serializable> parameters) throws ConversionException {
		String commandName = getCommandName(blobHolder, parameters);
		if (commandName == null) {
			throw new ConversionException("Unable to determine target CommandLine name");
		}

		Map<String, Blob> blobParams = getCmdBlobParameters(blobHolder, parameters);
		Map<String, String> strParams = getCmdStringParameters(blobHolder, parameters);

		CmdReturn result = execOnBlobWithDocker(commandName, blobParams, strParams);
		return buildResult(result.output, result.params);
	}

	protected CmdReturn execOnBlobWithDocker(String commandName, Map<String, Blob> blobParameters,
			Map<String, String> parameters) throws ConversionException {
		CommandLineExecutorService cles = Framework.getService(CommandLineExecutorService.class);
		CmdParameters params = cles.getDefaultCmdParameters();
		List<Closeable> toClose = new ArrayList<>();
		String extension = null;
		try {
			if (blobParameters != null) {
				for (String blobParamName : blobParameters.keySet()) {
					Blob blob = blobParameters.get(blobParamName);
					// closed in finally block
					CloseableFile closeable = blob
							.getCloseableFile("." + FilenameUtils.getExtension(blob.getFilename()));

					File closeableFile = closeable.getFile();
					params.addNamedParameter(blobParamName, closeableFile);

					extension = Files.getFileExtension(closeableFile.getName());

					String srcDir = closeableFile.getParentFile().getAbsolutePath();
					params.addNamedParameter("inDir", srcDir);

					// the provided file may be a symbolic link
					// since it does not work with Docker
					// we need a work around
					if (!closeableFile.getAbsolutePath().equals(closeableFile.getCanonicalPath())) {
						File newFile = new File(srcDir, UUID.randomUUID().toString() + extension);
						Files.copy(closeableFile, newFile);
						String srcFile = newFile.getName();
						params.addNamedParameter("inFile", srcFile);
						toClose.add(new CloseableFile(newFile, true));
					} else {
						params.addNamedParameter("inFile", closeableFile.getName());
					}
					toClose.add(closeable);
				}
			}

			if (parameters != null) {
				for (String paramName : parameters.keySet()) {
					params.addNamedParameter(paramName, parameters.get(paramName));
				}
			}

			// add the output file
			// need to include extension
			params.addNamedParameter("outFile", "smartCrop." + extension);

			ExecResult result = Framework.getService(CommandLineExecutorService.class).execCommand(commandName, params);
			if (!result.isSuccessful()) {
				throw result.getError();
			}

			return new CmdReturn(params, result.getOutput());
		} catch (CommandNotAvailable e) {
			throw new ConversionException("Unable to find targetCommand", e);
		} catch (IOException | CommandException e) {
			throw new ConversionException("Error while converting via CommandLineService", e);
		} finally {
			for (Closeable closeable : toClose) {
				IOUtils.closeQuietly(closeable);
			}
		}
	}

}
