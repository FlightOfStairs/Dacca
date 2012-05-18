package org.flightofstairs.honours.common

import org.gcontracts.annotations.*

public class ClassRelation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Map<Call, Integer> calls = [:];
	
	@Requires({ call != null })
	public void addCall(Call call, int count = 1) {
		if(calls.containsKey(call)) calls[call] = calls[call] + count;
		else calls[call] = count;
	}

	@Ensures({ result >= 0 })
	public int countAll() { return calls.values().sum(); }

	@Ensures({ result >= 0 })
	public int callVariety() { return calls.size(); }
	
	@Requires({ call != null })
	@Ensures({ result >= 0 })
	public int countCall(Call call) { return calls.containsKey(call) ? calls[call] : 0; }
	
	@Ensures({ result != null && result.size() == (onlyUnique ? callVariety() : countAll())	})
	public List<Call> getCalls(boolean onlyUnique = true) {
		def results = [];
		
		for(Call call in calls.keySet())
			(onlyUnique ? 1 : calls[call]).times { results << call }

		return results
	}
}

