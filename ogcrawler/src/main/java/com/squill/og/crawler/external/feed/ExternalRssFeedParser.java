package com.squill.og.crawler.external.feed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Saurav
 *
 */
public class ExternalRssFeedParser {

	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	//private static final String CHANNEL = "channel";
	private static final String LANGUAGE = "language";
	private static final String COPYRIGHT = "copyright";
	private static final String LINK = "link";
	private static final String AUTHOR = "author";
	private static final String ITEM = "item";
	private static final String PUB_DATE = "pubDate";
	private static final String GUID = "guid";
	private static final String CATEGORY = "category";

	private URL url;

	private static final Logger $_LOG = LoggerFactory
			.getLogger(ExternalRssFeedParser.class);

	public ExternalRssFeedParser(String feedUrl) {
		try {
			this.url = new URL(feedUrl);
		} catch (MalformedURLException e) {
			$_LOG.error(e.getMessage(), e);
		}
	}

	public ExtFeed parse() {
		ExtFeed feed = null;
		try {
			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";
			String category = "";

			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = read();
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// read the XML document
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					String localPart = event.asStartElement().getName()
							.getLocalPart();
					switch (localPart) {
					case ITEM:
						if (isFeedHeader) {
							isFeedHeader = false;
							feed = new ExtFeed(title, link, description,
									language, copyright, pubdate);
							description = "";
							title = "";
							link = "";
							language = "";
							copyright = "";
							author = "";
							pubdate = "";
							guid = "";
							category = "";
						}
						event = eventReader.nextEvent();
						break;
					case TITLE:
						title = getCharacterData(event, eventReader);
						break;
					case DESCRIPTION:
						description = getCharacterData(event, eventReader);
						break;
					case LINK:
						link = getCharacterData(event, eventReader);
						break;
					case GUID:
						guid = getCharacterData(event, eventReader);
						break;
					case LANGUAGE:
						language = getCharacterData(event, eventReader);
						break;
					case AUTHOR:
						author = getCharacterData(event, eventReader);
						break;
					case PUB_DATE:
						pubdate = getCharacterData(event, eventReader);
						break;
					case COPYRIGHT:
						copyright = getCharacterData(event, eventReader);
						break;
					case CATEGORY:
						category = getCharacterData(event, eventReader);
						break;
					}
				} else if (event.isEndElement()) {
					if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
						ExtFeedEntry entry = new ExtFeedEntry();
						entry.setAuthor(author);
						entry.setDescription(description);
						entry.setGuid(guid);
						entry.setLink(link);
						entry.setTitle(title);
						entry.setPubDate(pubdate);
						entry.setCategory(category);
						feed.getEntries().add(entry);
						
						description = "";
						title = "";
						link = "";
						language = "";
						copyright = "";
						author = "";
						pubdate = "";
						guid = "";
						category = "";
						
						event = eventReader.nextEvent();
						continue;
					}
				}
			}
		} catch (XMLStreamException e) {
			$_LOG.error(url.toString());
			throw new RuntimeException(e);
		}
		return feed;
	}

	private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
			throws XMLStreamException {
		String result = "";
		event = eventReader.nextEvent();
		if (event instanceof Characters) {
			result = event.asCharacters().getData();
		}
		return result;
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}