package com.pack.pack.markup.gen.model;

import java.util.ArrayList;
import java.util.List;

import com.pack.pack.model.web.JPack;

/**
 * 
 * @author Saurav
 *
 */
public class PackModel {

	private String id;

	private String name;

	private List<AttachmentModel> attachments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AttachmentModel> getAttachments() {
		if (attachments == null) {
			attachments = new ArrayList<AttachmentModel>(30);
		}
		return attachments;
	}

	public void setAttachments(List<AttachmentModel> attachments) {
		this.attachments = attachments;
	}

	public static PackModel build(JPack pack) {
		if (pack == null)
			return null;
		PackModel model = new PackModel();
		model.setId(pack.getId());
		model.setName(pack.getTitle());
		return model;
	}
}
