package org.flightofstairs.honours.common

import org.gcontracts.annotations.*

@Invariant({ count > 0 })
public class CallCount implements Serializable {
	final Call call;

	Integer count = 0;
	protected void setCount(Integer count) { this.count = count }

	@Requires({ call != null })
	@Ensures({ call != null })
	public CallCount(call, int initial = 1) {
		this.call = call;
		this.count = initial;
	}

	public void increment(int amount = 1) { count += amount; }
	
	@Override
	public String toString() { return "$call: $count"; }
}