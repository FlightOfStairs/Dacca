package org.flightofstairs.honours.display.components

import org.apache.commons.collections15.Predicate;

class PackageFilter<String> implements Predicate {
	private final Collection<String> packageList = [] as Set;
	
	boolean evaluate(String vertex) {
		return packageList.size() == 0 || packageList.any { vertex.startsWith(it + ".") }
	}
	
	public void setPackages(final Collection<String> packageList) {
		this.packageList.clear();
		this.packageList.addAll(packageList);
	}
}

