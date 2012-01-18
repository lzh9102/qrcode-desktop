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
