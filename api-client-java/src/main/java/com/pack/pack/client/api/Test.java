package com.pack.pack.client.api;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String ID = (String) APIConstants.Topic.class.getField("ID").get(null);
		System.out.println(ID);
	}

}
