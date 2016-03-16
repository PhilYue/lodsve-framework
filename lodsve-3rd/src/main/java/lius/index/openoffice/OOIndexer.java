/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lius.index.openoffice;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lius.index.Indexer;
import lius.index.xml.XmlFileIndexer;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;


/**
 * @author Rida Benjelloun (ridabenjelloun@gmail.com) 
 */

public class OOIndexer

		extends Indexer {

	static Logger logger = Logger.getRootLogger();

	private XmlFileIndexer xfi = new XmlFileIndexer();

	private final Namespace NS_OO = Namespace.getNamespace("office",
			"http://openoffice.org/2000/office");

	public final Namespace NS_DC = Namespace.getNamespace("dc",
			"http://purl.org/dc/elements/1.1/");

	private final Namespace NS_OOMETA = Namespace.getNamespace("meta",
			"http://openoffice.org/2000/meta");

	public int getType() {
		return 1;
	}

	public boolean isConfigured() {
		boolean ef = false;
		if (getLiusConfig().getOOFields() != null)
			return ef = true;
		return ef;
	}

	public Collection getConfigurationFields() {
		return getLiusConfig().getOOFields();
	}

	public org.jdom.Document parse(InputStream is) {
		org.jdom.Document xmlDocContent = new org.jdom.Document();
		org.jdom.Document xmlMeta = new org.jdom.Document();
		try {
			List files = unzip(is);
			SAXBuilder builder = new SAXBuilder();
			builder.setEntityResolver(new OpenOfficeEntityResolver());
			builder.setValidation(false);
			File fileContent = File.createTempFile("tmp", "liusOOContent.xml");
			copyInputStream((InputStream) files.get(0),
					new BufferedOutputStream(new FileOutputStream(fileContent)));
			xmlDocContent = builder.build(fileContent);
			fileContent.delete();
			File fileMeta = File.createTempFile("tmp", "liusOOMeta.xml");
			copyInputStream((InputStream) files.get(1),
					new BufferedOutputStream(new FileOutputStream(fileMeta)));
			xmlMeta = builder.build(fileMeta);
			fileMeta.delete();
			xmlDocContent.getRootElement().addContent(
					xmlMeta.getRootElement().getChild("meta", NS_OO).detach());
			xmlDocContent.getRootElement().addNamespaceDeclaration(NS_DC);
			xmlDocContent.getRootElement().addNamespaceDeclaration(NS_OOMETA);
		} catch (JDOMException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());

		}
		return xmlDocContent;
	}

	public Collection getPopulatedLiusFields() {
		org.jdom.Document jdomDoc = (org.jdom.Document) this
				.parse(getStreamToIndex());
		return xfi.getPopulatedLiusFields(jdomDoc, getConfigurationFields());
	}

	public String getContent() {
		return xfi.concatOccurance(parse(getStreamToIndex()), "//*", "");
	}

	public List unzip(InputStream is) {
		List res = new ArrayList();
		try {
			ZipInputStream in = new ZipInputStream(is);
			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				if (entry.getName().equals("meta.xml")
						|| entry.getName().equals("content.xml")) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						stream.write(buf, 0, len);
					}
					InputStream isEntry = new ByteArrayInputStream(stream
							.toByteArray());
					res.add(isEntry);
				}
			}
			in.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return res;
	}

	protected void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

}