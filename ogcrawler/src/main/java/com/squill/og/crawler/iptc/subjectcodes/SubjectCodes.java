package com.squill.og.crawler.iptc.subjectcodes;

import java.util.ArrayList;
import java.util.List;

public class SubjectCodes {

	private List<SubjectCode> codes;

	public List<SubjectCode> getCodes() {
		if(codes == null) {
			codes = new ArrayList<SubjectCode>();
		}
		return codes;
	}

	public void setCodes(List<SubjectCode> codes) {
		this.codes = codes;
	}
}
