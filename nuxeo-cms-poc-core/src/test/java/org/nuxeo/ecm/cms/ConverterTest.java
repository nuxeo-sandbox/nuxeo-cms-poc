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
 *     Tiry
 */

package org.nuxeo.ecm.cms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.ecm.restapi.test.BaseTest;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class })
@Deploy({ "org.nuxeo.ecm.cms.nuxeo-cms-poc-core" })
@Deploy({ "org.nuxeo.ecm.cms.nuxeo-cms-poc-core:OSGI-INF/smartcrop-contrib.xml" })
public class ConverterTest extends BaseTest {

	@Inject
	protected CoreSession session;

	@Inject
	protected ConversionService cs;
	
	@Inject
	protected CommandLineExecutorService cls;

	protected static final String TEST_IMG = "mueller.jpg";

	protected DocumentModel createDummyDoc(File img) {
		DocumentModel doc = session.createDocumentModel("/", "adoc", "File");		
		Blob blob = new FileBlob(img);
		blob.setMimeType("image/png");
		blob.setFilename(TEST_IMG);		
		doc.setPropertyValue("file:content", (Serializable) blob);		
		return session.createDocument(doc);
	}
	
	@Test
	public void checkConverter() throws Exception {

		// no need to run the test if Docker is not there
		// or image is not registered
		assumeTrue(cls.getAvailableCommands().contains("smartCrop"));
		
		assertTrue(cls.getAvailableCommands().contains("smartCrop"));
		assertTrue(cs.getRegistredConverters().contains("smartCrop"));
		
		URL url = this.getClass().getClassLoader().getResource(TEST_IMG);
		File srcImgFile = new File(url.toURI());
		BufferedImage sourceImg = ImageIO.read(srcImgFile);
		
		DocumentModel doc = createDummyDoc(srcImgFile);
		
		Map<String, Serializable> params = new HashMap<>();		
		params.put("W", "50");
		params.put("H", "100");
		
		BlobHolder bh = cs.convert("smartCrop", doc.getAdapter(BlobHolder.class), params);
		
		BufferedImage resultImg = ImageIO.read(bh.getBlob().getStream());
		
		assertEquals(100,  resultImg.getHeight());
		assertEquals(50,  resultImg.getWidth());		
	}

}
