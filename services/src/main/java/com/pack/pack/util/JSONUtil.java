package com.pack.pack.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pack.pack.services.exception.PackPackException;

/**
 * 
 * @author Saurav
 *
 */
public class JSONUtil {

	private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

	private JSONUtil() {
	}

	public static String serialize(Object object) throws PackPackException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(DeserializationFeature.EAGER_DESERIALIZER_FETCH, true);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		String json = new String();
		try {
			json = mapper.writeValueAsString(object);

		} catch (JsonProcessingException e) {
			logger.debug("parsing error", e);
			logger.debug("Couldn't convert the given object to JSON String: "
					+ object);
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
			logger.debug("Couldn't parse the given JSON String: " + json);
			throw new PackPackException("TODO",
					"Couldn't parse the given JSON. Check payload.");
		}
		return jsonJavaObj;
	}
}