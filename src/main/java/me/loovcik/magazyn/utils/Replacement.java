package me.loovcik.magazyn.utils;

import me.loovcik.core.TinyText;
import me.loovcik.magazyn.Magazyn;

import java.util.Map;

/** Klasa pomocnicza do zastępowania ciągów tekstowych */
public final class Replacement
{
	/** Podmienia określone placeholdery w tekście */
	public static String replace(String input, Map<String, String> replacements){
		for (Map.Entry<String, String> r : replacements.entrySet()){
			if (r.getValue() != null)
				input = input.replaceAll(r.getKey(), r.getValue());
		}
		return input;
	}

	/** Zamienia tekst na kapitalki */
	public static String capitals(String input){
		if (Magazyn.getInstance().configuration.useCapitals)
			return TinyText.parse(input);
		return input;
	}

	/** Konwertuje wartość boolean na tekstowy odpowiednik */
	public static String booleanToString(boolean value){
		return value ? Magazyn.getInstance().configuration.messages.yes : Magazyn.getInstance().configuration.messages.no;
	}

	private Replacement() {}
}