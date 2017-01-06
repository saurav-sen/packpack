package com.pack.pack.markup.gen;

/**
 * 
 * @author Saurav
 *
 */
public class PackAttachmentPageGenerator implements IMarkupGenerator {

	@Override
	public <T> void generateAndUpload(String entityId) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		String str = "https://www.youtube.com/watch?v=YjSUSPzJiAU";
		String[] split = str.split("v=");
		System.out.println("https://www.youtube.com/embed/" + split[1]);
	}
}