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
import javax.swing.JPanel;
import java.lang.String;

public interface GeneratorInterface {
	/**
	 * @return a JPanel object containing the GUI
	 */
	JPanel getPanel();
	
	/**
	 * @return the name of the generator
	 */
	String getName();

	/**
	 * @return the generated text to be encoded
	 */
	String getText() throws GeneratorException;
	
	/**
	 * Try to parse the text to fit in the generator's fields.
	 * @param text the text to be parsed
	 * @param write write the parsed data into the generator's fields
	 * @return If the text is recognized as the generator's format, it should
	 * 			return true; false otherwise.
	 */
	boolean parseText(String text, boolean write);
	
	/**
	 * This function should return a non-negative integer indicating the
	 * priority to accept parsed data. For example, when the input text
	 * fits in both the contact format and the plain text format, the
	 * contact format should be chosen, thus the priority of contact is
	 * higher than that of plain text. Plain text is the fallback format, 
	 * so its priority is always 0.
	 */
	int getParsingPriority();

	/**
	 * Called when the generator interface is activated.
	 * The generator should set the focus to the first widget it defines.
	 */
	void setFocus();
	
}
