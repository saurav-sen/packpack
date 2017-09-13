package com.pack.pack.common.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pack.pack.model.web.notification.FeedMsg;
import com.pack.pack.services.exception.PackPackException;
/*import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;*/
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author Saurav
 *
 */
public class JSONUtil {

	private JSONUtil() {
	}
	
	/*public static void main(String[] args) throws Exception {
		DateTime t = new DateTime(DateTimeZone.getDefault());
		System.out.println(t.toString());
		long millis = t.getMillis();
		System.out.println(millis);
		t = new DateTime(millis, DateTimeZone.getDefault());
		System.out.println(t);
		long millis1 = t.getMillis();
		System.out.println(millis1);
		assert(millis - millis1 == 0);
		t = new DateTime(millis, DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Los_Angeles")));
		System.out.println(t);
		long millis2 = t.getMillis();
		System.out.println(millis2);
		assert(millis - millis2 == 0);
		assert(millis1 - millis2 == 0);
	}*/
	
	public static void main(String[] args) throws Exception {
		FeedMsg msg = new FeedMsg();
		msg.setKey("key1");
		msg.setTitle("title1");
		System.out.println(serialize(msg, false));
		msg = deserialize(serialize(msg), FeedMsg.class, true);
		System.out.println(msg.getKey());
		System.out.println(msg.getTitle());
	}
	
	public static String serialize(Object object) throws PackPackException {
		return serialize(object, true);
	}

	public static String serialize(Object object, boolean wrapRoot) throws PackPackException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH, true);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot);
		String json = new String();
		try {
			json = mapper.writeValueAsString(object);

		} catch (JsonProcessingException e) {
			throw new PackPackException("TODO",
					"Error writing JSON to response");
		}
		return json;
	}

	public static <T> T deserialize(String json, Class<T> type)
			throws PackPackException {
		return deserialize(json, type, false);
	}

	public static <T> T deserialize(String json, Class<T> type,
			boolean unWrapRootValue) throws PackPackException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		mapper.configure(
				DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,
				unWrapRootValue);
		T jsonJavaObj = null;
		try {
			jsonJavaObj = mapper.readValue(json.getBytes("UTF-8"), type);
		} catch (Exception e) {
			throw new PackPackException("TODO",
					"Couldn't parse the given JSON. Check payload.");
		}
		return jsonJavaObj;
	}
}