/*
 * Copyright 2012 Timothy Lin <lzh9102@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zxinggui.generator;

public class Validator {
	
	private Validator() {}
	
	private static final String EMAIL_PATTERN =
		"^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";
	private static final String URI_PATTERN =
		"^((http|ftp|https):\\/\\/)?[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?$";
	
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
