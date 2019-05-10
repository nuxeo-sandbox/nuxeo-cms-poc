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

package org.nuxeo.ecm.cms.rendition.adapter;

import java.util.List;

import org.nuxeo.ecm.cms.rendition.DynamicRendition;
import org.nuxeo.ecm.core.api.Blob;

/**
 * 
 * Interface exposed by the DocumentModel adapter
 * 
 * @author tiry
 *
 */
public interface DynamicRenditionHolder {

	List<DynamicRendition> getRenditions();

	void add(DynamicRendition newRendition, boolean save, boolean preRender);		
	
	DynamicRendition getRendition(String name);
	
	void storeRenditionResult(String name, Blob blob, boolean save);
}
