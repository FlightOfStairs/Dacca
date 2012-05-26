package org.flightofstairs.honours.capture.agent

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.flightofstairs.honours.common.Call

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

public class Probes {

	private final ReadWriteLock probesLock = new ReentrantReadWriteLock();

	private final AtomicInteger lastProbeID = new AtomicInteger();

	private final BiMap<Call, Integer> probeMap = HashBiMap.create();

	public Integer getProbeIDAt(final Call call) {
		synchronized(probesLock.readLock()) {
			return probeMap[call]
		}
	}

	public int createProbeIDAt(final Call call) {
		synchronized(probesLock.writeLock()) {
			if(!probeMap.containsKey(call)) {
				probeMap[call] = lastProbeID.getAndIncrement()
			}
			return probeMap[call]
		}
	}

	public Call getCallFromID(final int probeID) {
		synchronized(probesLock.readLock()) {
			return probeMap.inverse()[probeID];
		}
	}

}
