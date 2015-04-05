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

import java.util.ArrayList;

public class GeneratorManager {
	private ArrayList<GeneratorInterface> generators = new ArrayList<GeneratorInterface>();
	
	public GeneratorManager() {
		loadGenerators();
	}
	
	private void loadGenerators() {
		generators.clear();
		
		generators.add(new PlainTextGenerator());
		generators.add(new PhoneNumberGenerator());
		generators.add(new UrlGenerator());
		generators.add(new EmailGenerator());
		generators.add(new ContactGenerator());
		generators.add(new SmsGenerator());
	}
	
	public int generatorCount() {
		return generators.size();
	}
	
	public final ArrayList<GeneratorInterface> getGenerators() {
		return generators;
	}
	
	/**
	 * Retrieve the n-th generator in the list.
	 * @param index the index of the generator starting from zero
	 * @return the n-th generator in the list
	 */
	public final GeneratorInterface getGenerator(int index) {
		if (index >= 0 && index < generators.size())
			return generators.get(index);
		else
			return null;
	}
	
	/**
	 * Find the most appropriate generator of the text.
	 * @param str the text to be parsed
	 * @return index of the generator that recognizes the text with highest priority.
	 */
	public int findTextHandlerIndex(String str) {
		int priority = -1;
		int result = -1;
		for (int i=0; i<generators.size(); i++) {
			GeneratorInterface gen = generators.get(i);
			if (gen.getParsingPriority() > priority && gen.parseText(str, false)) {
				result = i;
				priority = gen.getParsingPriority();
			}				
		}
		assert result != -1 : "All unidentified text must fall back to plain text";
		return result;
	}
	
}
