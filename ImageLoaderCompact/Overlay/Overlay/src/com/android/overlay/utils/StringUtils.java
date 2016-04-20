package com.android.overlay.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.http.protocol.HTTP;

public class StringUtils {

	private static Random randGen = new Random();

	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
			+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	private static final String GOOD_IRI_CHAR = "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

	private static final String TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL = "(?:"
			+ "(?:aero|arpa|asia|a[cdefgilmnoqrstuwxz])"
			+ "|(?:biz|b[abdefghijmnorstvwyz])"
			+ "|(?:cat|com|coop|c[acdfghiklmnoruvxyz])"
			+ "|d[ejkmoz]"
			+ "|(?:edu|e[cegrstu])"
			+ "|f[ijkmor]"
			+ "|(?:gov|g[abdefghilmnpqrstuwy])"
			+ "|h[kmnrtu]"
			+ "|(?:info|int|i[delmnoqrst])"
			+ "|(?:jobs|j[emop])"
			+ "|k[eghimnprwyz]"
			+ "|l[abcikrstuvy]"
			+ "|(?:mil|mobi|museum|m[acdeghklmnopqrstuvwxyz])"
			+ "|(?:name|net|n[acefgilopruz])"
			+ "|(?:org|om)"
			+ "|(?:pro|p[aefghklmnrstwy])"
			+ "|qa"
			+ "|r[eosuw]"
			+ "|s[abcdeghijklmnortuvyz]"
			+ "|(?:tel|travel|t[cdfghjklmnoprtvwz])"
			+ "|u[agksyz]"
			+ "|v[aceginu]"
			+ "|w[fs]"
			+ "|(?:xn\\-\\-0zwm56d|xn\\-\\-11b5bs3a9aj6g|xn\\-\\-80akhbyknj4f|xn\\-\\-9t4b11yi5a|xn\\-\\-deba0ad|xn\\-\\-g6w251d|xn\\-\\-hgbk6aj7f53bba|xn\\-\\-hlcj6aya9esc7a|xn\\-\\-jxalpdlp|xn\\-\\-kgbechtv|xn\\-\\-zckzah)"
			+ "|y[etu]" + "|z[amw]))";

	public static String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}
	
	public static String encode(final String content, final String encoding) {
		try {
			return URLEncoder.encode(content, encoding != null ? encoding
					: HTTP.DEFAULT_CONTENT_CHARSET);
		} catch (UnsupportedEncodingException problem) {
			throw new IllegalArgumentException(problem);
		}
	}

	public static String encodeHex(byte[] bytes) {
		StringBuilder hex = new StringBuilder(bytes.length * 2);

		for (byte aByte : bytes) {
			if (((int) aByte & 0xff) < 0x10) {
				hex.append("0");
			}
			hex.append(Integer.toString((int) aByte & 0xff, 16));
		}

		return hex.toString();
	}

	public static boolean isWebUrl(CharSequence input) {
		if (input == null || input.length() == 0) {
			return false;
		}
		return Pattern
				.compile(
						"((?:(http|https|Http|Https|HTTP|HTTPS|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
								+ "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
								+ "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?"
								+ "((?:(?:["
								+ GOOD_IRI_CHAR
								+ "]["
								+ GOOD_IRI_CHAR
								+ "\\-]{0,64}\\.)+" // named host
								+ TOP_LEVEL_DOMAIN_STR_FOR_WEB_URL
								+ "|(?:(?:25[0-5]|2[0-4]" // or ip address
								+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(?:25[0-5]|2[0-4][0-9]"
								+ "|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1]"
								+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(?:25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
								+ "|[1-9][0-9]|[0-9])))"
								+ "(?:\\:\\d{1,5})?)" // plus option port number
								+ "(\\/(?:(?:["
								+ GOOD_IRI_CHAR
								+ "\\;\\/\\?\\:\\@\\&\\=\\#\\~" // plus option
																// query params
								+ "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?"
								+ "(?:\\b|$)").matcher(input).matches();
	}

	public static boolean isPhoneNumber(CharSequence input) {
		if (input == null || input.length() == 0) {
			return false;
		}
		return Pattern.compile( // sdd = space, dot, or dash
				"(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
						+ "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
						+ "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])")
				.matcher(input).matches();
	}

	public static boolean isEmailAddress(CharSequence input) {
		if (input == null || input.length() == 0) {
			return false;
		}
		return Pattern
				.compile(
						"[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
								+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "("
								+ "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}"
								+ ")+").matcher(input).matches();
	}

	public static boolean isAlphaNumeric(CharSequence input) {
		if (input == null || input.length() == 0) {
			return false;
		}
		return Pattern.compile("[a-zA-Z0-9 \\./-]*").matcher(input).matches();
	}
}
