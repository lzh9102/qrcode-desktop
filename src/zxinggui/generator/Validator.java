package zxinggui.generator;

public class Validator {
	
	private Validator() {}
	
	private static final String EMAIL_PATTERN =
		"^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";
	private static final String URI_PATTERN =
		"(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
	
	public static boolean isValidPhoneNumber(String number) {
		return number.matches("^\\+?[0-9]+$");
	}
	
	public static boolean isValidNumber(String number) {
		return number.matches("^[0-9]+$");
	}
	
	public static boolean isValidURI(String uri) {		
		return uri.matches(URI_PATTERN);
	}

	public static boolean isValidEmail(String email) {
		return email.matches(EMAIL_PATTERN);
	}
}
