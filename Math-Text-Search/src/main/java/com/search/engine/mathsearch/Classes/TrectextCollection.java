package com.search.engine.mathsearch.Classes;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is for INFSCI 2140 in 2019
 * This Class need to be modified
 */
public class TrectextCollection extends TrecwebCollection implements DocumentCollection {
	// Essential private methods or variables can be added.
	String document = null;
	String line = null;
	BufferedReader br;
	Map<String, Object> trectextMap;
	Iterator<Entry<String, Object>> iterator;

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public TrectextCollection() throws IOException {
		// 1. Open the file in Classes.Path.DataTextDir.
		// 2. Make preparation for function nextDocument().
		// NT: you cannot load the whole corpus into memory!!
		try {

			File inputFile = new File(Path.DataTextDir);
			br = new BufferedReader(new FileReader(inputFile));
			trectextMap = new HashMap<String, Object>();
			// load from<DOC>to</DOC> as one document into the memory
			while ((line = br.readLine()) != null) {
				if (line.equals(new String("<DOC>"))) {
					document = line;

					while (!line.equals(new String("</DOC>"))) {
						line = br.readLine();
						document += line;
					}
					document = document.replaceAll("(&.*?;)", " ");

					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxParser = factory.newSAXParser();
					// Classes.TextHandler handle the content processing
					TextHandler texthandler = new TextHandler();
					// SAXParser is a event based XML Parser
					saxParser.parse(new ByteArrayInputStream(document.getBytes()), texthandler);
					trectextMap.put(texthandler.getMapKey(), texthandler.getMapValue());
					document = null;
					line = null;
				}
			}
			iterator = trectextMap.entrySet().iterator();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public Map<String, Object> nextDocument() throws IOException {
		// 1. When called, this API processes one document from corpus, and returns its
		// doc number and content.
		// 2. When no document left, return null, and close the file.
		if (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			if (entry.getKey() != null && entry.getValue() != null) {
				HashMap<String, Object> r = new HashMap<String, Object>();
				r.put(entry.getKey(), entry.getValue().toString().toCharArray());
				return r;
			}
		}

		return null;
	}

}

class TextHandler extends DefaultHandler {



	boolean bDOC = false;
	boolean bDOCNO = false;
	boolean bTEXT = false;
	StringBuffer textContentBuffer;
	String DocNoBuffer;
	String Key;
	String Value;

	public String getMapKey() {
		if (Key == null || Value == null) {
			return null;
		} else {
			return Key;
		}
	}

	public Object getMapValue() {
		if (Key == null || Value == null) {
			return null;
		} else {
			return Value;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		if (qName.equalsIgnoreCase("DOC")) {
			bDOC = true;
		} else if (qName.equalsIgnoreCase("DOCNO")) {
			DocNoBuffer = new String();
			bDOCNO = true;
		} else if (qName.equalsIgnoreCase("TEXT")) {
			bTEXT = true;
			textContentBuffer = new StringBuffer();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equalsIgnoreCase("DOC")) {

		} else if (qName.equalsIgnoreCase("DOCNO")) {
//			System.out.println("DOCNO:" + DocNoBuffer);

		} else if (qName.equalsIgnoreCase("TEXT")) {

			this.Key = DocNoBuffer;
			this.Value = textContentBuffer.toString();
			bTEXT = false;
			textContentBuffer = null;
			DocNoBuffer = null;

		}
	}

	@Override
	public void characters(char ch[], int start, int length) {

		if (bDOCNO) {
			// do
			DocNoBuffer = new String(ch, start, length).trim();
			bDOCNO = false;
		} else if (bTEXT) {
			textContentBuffer.append(new String(ch, start, length));

		}
	}

}
