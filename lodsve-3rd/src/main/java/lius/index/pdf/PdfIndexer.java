package lius.index.pdf;

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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import lius.config.LiusField;
import lius.index.Indexer;

import org.apache.log4j.Logger;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;

/**
 *
 * Classe permettant d'indexer des fichiers PDF basée sur PDFBox et inspéré de
 * la classe LucenePDFDocument
 *
 * <br/><br/>
 *
 * Class for indexing PDF documents, based on PDFBox and inspired from
 * LucenePDFDocument.
 *
 * @author Rida Benjelloun (ridabenjelloun@gmail.com)
 *
 */

public class PdfIndexer extends Indexer {

	static Logger logger = Logger.getRootLogger();

	private PDDocument pdfDocument = null;

	public int getType() {
		return 1;
	}

	public boolean isConfigured() {
		boolean ef = false;
		if (getLiusConfig().getPdfFields() != null)
			return ef = true;
		return ef;
	}

	public Collection getConfigurationFields() {
		return getLiusConfig().getPdfFields();
	}

	public String getContent() {
		String contents = "";
		try {
			pdfDocument = PDDocument.load(getStreamToIndex());
			if (pdfDocument.isEncrypted()) {
				pdfDocument.decrypt("");
			}
			StringWriter writer = new StringWriter();
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.writeText(pdfDocument, writer);
			contents = writer.getBuffer().toString();
		} catch (CryptographyException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (InvalidPasswordException e) {
			logger.error(e.getMessage());
		} finally {
			if (pdfDocument != null) {
				try {
					pdfDocument.close();
				} catch (IOException ex) {
					logger.error(ex.getMessage());
				}
			}
		}
		return contents;
	}

	/**
	 * Méthode retournant un objet de type Lucene document à partir du fichier à
	 * indexer et d'une collection d'objets de type LiusField. Chaque objet
	 * LiusField contient de l'information sur le nom du champs Lucene, le type,
	 * etc. <br/><br/> Method that returns a Lucene object from the
	 * configuration file and a collection of LiusField objects. Each LiusField
	 * object contains information about the Lucene field, the type, etc.
	 */

	public Collection getPopulatedLiusFields() {
		Collection coll = new ArrayList();
		String contents = getContent();
		Iterator i = getLiusConfig().getPdfFields().iterator();
		while (i.hasNext()) {
			Object field = i.next();
			if (field instanceof LiusField) {
				LiusField lf = (LiusField) field;
				if (lf.getGet() != null) {
					if (lf.getGet().equalsIgnoreCase("content")) {
						lf.setValue(contents);
						coll.add(lf);
					} else {
						try {
							PDDocumentInformation metaData = pdfDocument
									.getDocumentInformation();
							if (lf.getGet().equalsIgnoreCase("title")) {
								if (metaData.getTitle() != null) {
									lf.setValue(metaData.getTitle());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("author")) {
								if (metaData.getAuthor() != null) {
									lf.setValue(metaData.getAuthor());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("creator")) {
								if (metaData.getCreator() != null) {
									lf.setValue(metaData.getCreator());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("keywords")) {
								if (metaData.getKeywords() != null) {
									lf.setValue(metaData.getKeywords());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("producer")) {
								if (metaData.getProducer() != null) {
									lf.setValue(metaData.getProducer());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("subject")) {
								if (metaData.getSubject() != null) {
									lf.setValue(metaData.getSubject());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("trapped")) {
								if (metaData.getTrapped() != null) {
									lf.setValue(metaData.getTrapped());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase(
									"creationDate")) {
								if (metaData.getCreationDate() != null) {
									lf.setDate(metaData.getCreationDate()
											.getTime());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase(
									"modificationDate")) {
								if (metaData.getModificationDate() != null) {
									lf.setDate(metaData.getModificationDate()
											.getTime());
									coll.add(lf);
								}
							} else if (lf.getGet().equalsIgnoreCase("summary")) {
								int summarySize = Math.min(contents.length(),
										500);
								String summary = contents.substring(0,
										summarySize);
								lf.setValue(summary);
								coll.add(lf);
							}
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
				}
			} else {
				coll.add(field);
			}
		}
		return coll;
	}

}