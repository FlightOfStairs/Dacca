package org.flightofstairs.honours.capture.sources

public interface Source {
	public String getName();

	void startSource(int port);
}
