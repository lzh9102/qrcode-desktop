package zxinggui.generator;

import java.util.ArrayList;

public class GeneratorManager {
	private ArrayList<GeneratorInterface> generators = new ArrayList<GeneratorInterface>();
	
	public GeneratorManager() {
		loadGenerators();
	}
	
	private void loadGenerators() {
		generators.clear();
		
		generators.add(new ContactGenerator());
		generators.add(new PlainTextGenerator());
		generators.add(new PhoneNumberGenerator());
		generators.add(new UrlGenerator());
		generators.add(new EmailGenerator());
		generators.add(new SmsGenerator());
	}
	
	public int generatorCount() {
		return generators.size();
	}
	
	public final ArrayList<GeneratorInterface> getGenerators() {
		return generators;
	}
	
	public final GeneratorInterface getGenerator(int index) {
		if (index >= 0 && index < generators.size())
			return generators.get(index);
		else
			return null;
	}
	
}
