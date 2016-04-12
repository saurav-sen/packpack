package com.pack.pack.oauth1.client.internal;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UriComponent {

	public enum Type {

		/**
		 * ALPHA / DIGIT / "-" / "." / "_" / "~" characters.
		 */
		UNRESERVED,
		/**
		 * The URI scheme component type.
		 */
		SCHEME,
		/**
		 * The URI authority component type.
		 */
		AUTHORITY,
		/**
		 * The URI user info component type.
		 */
		USER_INFO,
		/**
		 * The URI host component type.
		 */
		HOST,
		/**
		 * The URI port component type.
		 */
		PORT,
		/**
		 * The URI path component type.
		 */
		PATH,
		/**
		 * The URI path component type that is a path segment.
		 */
		PATH_SEGMENT,
		/**
		 * The URI path component type that is a matrix parameter.
		 */
		MATRIX_PARAM,
		/**
		 * The URI query component type.
		 */
		QUERY,
		/**
		 * The URI query component type that is a query parameter, space
		 * character is encoded as {@code +}.
		 */
		QUERY_PARAM,
		/**
		 * The URI query component type that is a query parameter, space
		 * character is encoded as {@code %20}.
		 */
		QUERY_PARAM_SPACE_ENCODED,
		/**
		 * The URI fragment component type.
		 */
		FRAGMENT,
	}

	private UriComponent() {
	}

	public static void validate(final String s, final Type t) {
		validate(s, t, false);
	}

	public static void validate(final String s, final Type t,
			final boolean template) {
		final int i = _valid(s, t, template);
		if (i > -1) {
			throw new IllegalArgumentException("URI compnent not valid");
		}
	}

	public static boolean valid(final String s, final Type t) {
		return valid(s, t, false);
	}

	public static boolean valid(final String s, final Type t,
			final boolean template) {
		return _valid(s, t, template) == -1;
	}

	private static int _valid(final String s, final Type t,
			final boolean template) {
		final boolean[] table = ENCODING_TABLES[t.ordinal()];

		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if ((c < 0x80 && c != '%' && !table[c]) || c >= 0x80) {
				if (!template || (c != '{' && c != '}')) {
					return i;
				}
			}
		}
		return -1;
	}

	public static String contextualEncode(final String s, final Type t) {
		return _encode(s, t, false, true);
	}

	public static String contextualEncode(final String s, final Type t,
			final boolean template) {
		return _encode(s, t, template, true);
	}

	public static String encode(final String s, final Type t) {
		return _encode(s, t, false, false);
	}

	public static String encode(final String s, final Type t,
			final boolean template) {
		return _encode(s, t, template, false);
	}

	public static String encodeTemplateNames(String s) {
		int i = s.indexOf('{');
		if (i != -1) {
			s = s.replace("{", "%7B");
		}
		i = s.indexOf('}');
		if (i != -1) {
			s = s.replace("}", "%7D");
		}

		return s;
	}

	private static String _encode(final String s, final Type t,
			final boolean template, final boolean contextualEncode) {
		final boolean[] table = ENCODING_TABLES[t.ordinal()];
		boolean insideTemplateParam = false;

		StringBuilder sb = null;
		for (int offset = 0, codePoint; offset < s.length(); offset += Character
				.charCount(codePoint)) {
			codePoint = s.codePointAt(offset);

			if (codePoint < 0x80 && table[codePoint]) {
				if (sb != null) {
					sb.append((char) codePoint);
				}
			} else {
				if (template) {
					boolean leavingTemplateParam = false;
					if (codePoint == '{') {
						insideTemplateParam = true;
					} else if (codePoint == '}') {
						insideTemplateParam = false;
						leavingTemplateParam = true;
					}
					if (insideTemplateParam || leavingTemplateParam) {
						if (sb != null) {
							sb.append(Character.toChars(codePoint));
						}
						continue;
					}
				}

				if (contextualEncode && codePoint == '%'
						&& offset + 2 < s.length()
						&& isHexCharacter(s.charAt(offset + 1))
						&& isHexCharacter(s.charAt(offset + 2))) {
					if (sb != null) {
						sb.append('%').append(s.charAt(offset + 1))
								.append(s.charAt(offset + 2));
					}
					offset += 2;
					continue;
				}

				if (sb == null) {
					sb = new StringBuilder();
					sb.append(s.substring(0, offset));
				}

				if (codePoint < 0x80) {
					if (codePoint == ' ' && (t == Type.QUERY_PARAM)) {
						sb.append('+');
					} else {
						appendPercentEncodedOctet(sb, (char) codePoint);
					}
				} else {
					appendUTF8EncodedCharacter(sb, codePoint);
				}
			}
		}

		return (sb == null) ? s : sb.toString();
	}

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static void appendPercentEncodedOctet(final StringBuilder sb,
			final int b) {
		sb.append('%');
		sb.append(HEX_DIGITS[b >> 4]);
		sb.append(HEX_DIGITS[b & 0x0F]);
	}

	private static void appendUTF8EncodedCharacter(final StringBuilder sb,
			final int codePoint) {
		final CharBuffer chars = CharBuffer.wrap(Character.toChars(codePoint));
		final ByteBuffer bytes = UTF_8_CHARSET.encode(chars);

		while (bytes.hasRemaining()) {
			appendPercentEncodedOctet(sb, bytes.get() & 0xFF);
		}
	}

	private static final String[] SCHEME = { "0-9", "A-Z", "a-z", "+", "-", "." };
	private static final String[] UNRESERVED = { "0-9", "A-Z", "a-z", "-", ".",
			"_", "~" };
	private static final String[] SUB_DELIMS = { "!", "$", "&", "'", "(", ")",
			"*", "+", ",", ";", "=" };
	private static final boolean[][] ENCODING_TABLES = initEncodingTables();

	private static boolean[][] initEncodingTables() {
		final boolean[][] tables = new boolean[Type.values().length][];

		final List<String> l = new ArrayList<String>();
		l.addAll(Arrays.asList(SCHEME));
		tables[Type.SCHEME.ordinal()] = initEncodingTable(l);

		l.clear();

		l.addAll(Arrays.asList(UNRESERVED));
		tables[Type.UNRESERVED.ordinal()] = initEncodingTable(l);

		l.addAll(Arrays.asList(SUB_DELIMS));

		tables[Type.HOST.ordinal()] = initEncodingTable(l);

		tables[Type.PORT.ordinal()] = initEncodingTable(Arrays.asList("0-9"));

		l.add(":");

		tables[Type.USER_INFO.ordinal()] = initEncodingTable(l);

		l.add("@");

		tables[Type.AUTHORITY.ordinal()] = initEncodingTable(l);

		tables[Type.PATH_SEGMENT.ordinal()] = initEncodingTable(l);
		tables[Type.PATH_SEGMENT.ordinal()][';'] = false;

		tables[Type.MATRIX_PARAM.ordinal()] = tables[Type.PATH_SEGMENT
				.ordinal()].clone();
		tables[Type.MATRIX_PARAM.ordinal()]['='] = false;

		l.add("/");

		tables[Type.PATH.ordinal()] = initEncodingTable(l);

		l.add("?");

		tables[Type.QUERY.ordinal()] = initEncodingTable(l);

		tables[Type.QUERY_PARAM.ordinal()] = initEncodingTable(l);
		tables[Type.QUERY_PARAM.ordinal()]['='] = false;
		tables[Type.QUERY_PARAM.ordinal()]['+'] = false;
		tables[Type.QUERY_PARAM.ordinal()]['&'] = false;

		tables[Type.QUERY_PARAM_SPACE_ENCODED.ordinal()] = tables[Type.QUERY_PARAM
				.ordinal()];

		tables[Type.FRAGMENT.ordinal()] = tables[Type.QUERY.ordinal()];

		return tables;
	}

	private static boolean[] initEncodingTable(final List<String> allowed) {
		final boolean[] table = new boolean[0x80];
		for (final String range : allowed) {
			if (range.length() == 1) {
				table[range.charAt(0)] = true;
			} else if (range.length() == 3 && range.charAt(1) == '-') {
				for (int i = range.charAt(0); i <= range.charAt(2); i++) {
					table[i] = true;
				}
			}
		}

		return table;
	}

	private static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

	public static String decode(final String s, final Type t) {
		if (s == null) {
			throw new IllegalArgumentException();
		}

		final int n = s.length();
		if (n == 0) {
			return s;
		}

		// If there are no percent-escaped octets
		if (s.indexOf('%') < 0) {
			// If there are no '+' characters for query param
			if (t == Type.QUERY_PARAM) {
				if (s.indexOf('+') < 0) {
					return s;
				}
			} else {
				return s;
			}
		} else {
			// Malformed percent-escaped octet at the end
			if (n < 2) {
				throw new IllegalArgumentException(
						"Malformed percent-escaped octet at the end");
			}

			// Malformed percent-escaped octet at the end
			if (s.charAt(n - 2) == '%') {
				throw new IllegalArgumentException(
						"Malformed percent-escaped octet at the end");
			}
		}

		if (t == null) {
			return decode(s, n);
		}

		switch (t) {
		case HOST:
			return decodeHost(s, n);
		case QUERY_PARAM:
			return decodeQueryParam(s, n);
		default:
			return decode(s, n);
		}
	}

	private static String decode(final String s, final int n) {
		final StringBuilder sb = new StringBuilder(n);
		ByteBuffer bb = null;

		for (int i = 0; i < n;) {
			final char c = s.charAt(i++);
			if (c != '%') {
				sb.append(c);
			} else {
				bb = decodePercentEncodedOctets(s, i, bb);
				i = decodeOctets(i, bb, sb);
			}
		}

		return sb.toString();
	}

	private static String decodeQueryParam(final String s, final int n) {
		final StringBuilder sb = new StringBuilder(n);
		ByteBuffer bb = null;

		for (int i = 0; i < n;) {
			final char c = s.charAt(i++);
			if (c != '%') {
				if (c != '+') {
					sb.append(c);
				} else {
					sb.append(' ');
				}
			} else {
				bb = decodePercentEncodedOctets(s, i, bb);
				i = decodeOctets(i, bb, sb);
			}
		}

		return sb.toString();
	}

	private static String decodeHost(final String s, final int n) {
		final StringBuilder sb = new StringBuilder(n);
		ByteBuffer bb = null;

		boolean betweenBrackets = false;
		for (int i = 0; i < n;) {
			final char c = s.charAt(i++);
			if (c == '[') {
				betweenBrackets = true;
			} else if (betweenBrackets && c == ']') {
				betweenBrackets = false;
			}

			if (c != '%' || betweenBrackets) {
				sb.append(c);
			} else {
				bb = decodePercentEncodedOctets(s, i, bb);
				i = decodeOctets(i, bb, sb);
			}
		}

		return sb.toString();
	}

	private static ByteBuffer decodePercentEncodedOctets(final String s, int i,
			ByteBuffer bb) {
		if (bb == null) {
			bb = ByteBuffer.allocate(1);
		} else {
			bb.clear();
		}

		while (true) {
			// Decode the hex digits
			bb.put((byte) (decodeHex(s, i++) << 4 | decodeHex(s, i++)));

			// Finish if at the end of the string
			if (i == s.length()) {
				break;
			}

			// Finish if no more percent-encoded octets follow
			if (s.charAt(i++) != '%') {
				break;
			}

			// Check if the byte buffer needs to be increased in size
			if (bb.position() == bb.capacity()) {
				bb.flip();
				// Create a new byte buffer with the maximum number of possible
				// octets, hence resize should only occur once
				final ByteBuffer bb_new = ByteBuffer.allocate(s.length() / 3);
				bb_new.put(bb);
				bb = bb_new;
			}
		}

		bb.flip();
		return bb;
	}

	private static int decodeOctets(final int i, final ByteBuffer bb,
			final StringBuilder sb) {
		// If there is only one octet and is an ASCII character
		if (bb.limit() == 1 && (bb.get(0) & 0xFF) < 0x80) {
			// Octet can be appended directly
			sb.append((char) bb.get(0));
			return i + 2;
		} else {
			//
			final CharBuffer cb = UTF_8_CHARSET.decode(bb);
			sb.append(cb.toString());
			return i + bb.limit() * 3 - 1;
		}
	}

	private static int decodeHex(final String s, final int i) {
		final int v = decodeHex(s.charAt(i));
		if (v == -1) {
			throw new IllegalArgumentException(
					"URI compnent encoded octet invalid");
		}
		return v;
	}

	private static final int[] HEX_TABLE = initHexTable();

	private static int[] initHexTable() {
		final int[] table = new int[0x80];
		Arrays.fill(table, -1);

		for (char c = '0'; c <= '9'; c++) {
			table[c] = c - '0';
		}
		for (char c = 'A'; c <= 'F'; c++) {
			table[c] = c - 'A' + 10;
		}
		for (char c = 'a'; c <= 'f'; c++) {
			table[c] = c - 'a' + 10;
		}
		return table;
	}

	private static int decodeHex(final char c) {
		return (c < 128) ? HEX_TABLE[c] : -1;
	}

	public static boolean isHexCharacter(final char c) {
		return c < 128 && HEX_TABLE[c] != -1;
	}

	public static String fullRelativeUri(final URI uri) {
		if (uri == null) {
			return null;
		}

		final String query = uri.getRawQuery();

		return uri.getRawPath()
				+ (query != null && query.length() > 0 ? "?" + query : "");
	}
}