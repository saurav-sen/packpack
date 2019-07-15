package com.squill.og.crawler.external.feed;

/**
 * 
 * @author Saurav
 *
 */
public class ExtFeedEntry {

    private String title;
    
    private String description;
    
    private String link;
    
    private String author;
    
    private String guid;
    
    private String pubDate;
    
    private String category;
    
    private long dateTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ExtFeedEntry) {
			return this.link.equals(((ExtFeedEntry)obj).link);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (this.getClass().getName() + "_" + this.link).hashCode();
	}

	@Override
    public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append("{");
		toString.append("\"title\": \"");
		toString.append(title);
		toString.append("\"");
		toString.append(", \"description\": \"");
		toString.append(description);
		toString.append("\"");
		toString.append(", \"link\": \"");
		toString.append(link);
		toString.append("\"");
		toString.append(", \"author\": \"");
		toString.append(author);
		toString.append("\"");
		toString.append(", \"guid\": \"");
		toString.append(guid);
		toString.append("\"");
		toString.append(", \"pubDate\": \"");
		toString.append(pubDate);
		toString.append("\"");
		toString.append(", \"category\": \"");
		toString.append(category);
		toString.append("\"");
		toString.append("}");
        return toString.toString();
    }

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}
}