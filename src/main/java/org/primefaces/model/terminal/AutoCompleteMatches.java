package org.primefaces.model.terminal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

public class AutoCompleteMatches extends JSONObject {

	private static final String BASECOMMAND = "baseCommand";
	private static final String MATCHES = "matches";

	public AutoCompleteMatches() {
		this("");
	}

	public AutoCompleteMatches(String baseCommand) {
		super();
		setBaseCommand(baseCommand);
		put(MATCHES, new JSONArray());
	}

	public String getBaseCommand() {
		return (String) get(BASECOMMAND);
	}

	public void setBaseCommand(String baseCommand) {
		put(BASECOMMAND, baseCommand);
	}

	public Collection<String> getMatches() {
		JSONArray arr = (JSONArray) get(MATCHES);

		LinkedList<String> matches = new LinkedList<String>();
		Iterator<Object> i = arr.iterator();

		while (i.hasNext()) {
			String match = (String) i.next();
			matches.add(match);
		}

		return matches;
	}

	public void setMatches(Collection<String> matches) {
		JSONArray arr = (JSONArray) get(MATCHES);

		if (matches != null) {
			for (String match : matches) {
				arr.put(match);
			}
		}

		put(MATCHES, arr);
	}

	public void addMatch(String match) {
		JSONArray arr = (JSONArray) get(MATCHES);

		arr.put(match);

		put(MATCHES, arr);
	}
}
