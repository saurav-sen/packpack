package com.pack.pack.services.redis;

import java.util.UUID;

public class Base62 {

	private static final char[] CHAR_SET = new char[] { 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
			'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
			'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9' };

	public static Encoder getEncoder() {
		return new Encoder();
	}

	public static Decoder getDecoder() {
		return new Decoder();
	}

	public static final class Encoder {

		public String encode(int number) {
			StringBuilder text = new StringBuilder();
			number = adjust(number);
			while (number > 0) {
				text.append(CHAR_SET[number % 62]);
				number = number / 62;
			}

			text = text.reverse();
			return text.toString();
		}
		
		private int adjust(int number) {
			while(number <= 0) {
				number = UUID.randomUUID().hashCode();
				number = (int)(System.currentTimeMillis() - number);
			}
			return number;
		}
	}

	public static final class Decoder {

		public int decode(String text) {
			int number = 0;
			for (int i = 0; i < text.length(); i++) {
				if ('a' <= text.charAt(i) && text.charAt(i) <= 'z')
					number = number * 62 + text.charAt(i) - 'a';
				if ('A' <= text.charAt(i) && text.charAt(i) <= 'Z')
					number = number * 62 + text.charAt(i) - 'A' + 26;
				if ('0' <= text.charAt(i) && text.charAt(i) <= '9')
					number = number * 62 + text.charAt(i) - '0' + 52;
			}
			return number;
		}
	}
	
	public static void main(String[] args) {
		String text = "https://timesofindia.indiatimes.com/india/exclusion-from-nrc-wont-mean-automatic-removal-from-voter-list-cec/articleshow/65229203.cms";
		int hash = text.hashCode();
		System.out.println(hash);
		String encodedText = Base62.getEncoder().encode(hash);
		System.out.println(encodedText);
		int decodedHash = Base62.getDecoder().decode(encodedText);
		System.out.println(decodedHash);
		System.out.println(hash == decodedHash);
	}
}
