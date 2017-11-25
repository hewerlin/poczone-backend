package net.poczone.framework.tools;

import java.util.HashMap;

public class Sleepers<T> {
	public HashMap<T, Object> gates = new HashMap<>();

	public void waitFor(T key, long timeout) throws InterruptedException {
		Object gate = getGate(key);
		synchronized (gate) {
			gate.wait(timeout);
		}
	}

	public void notifyAll(T key) {
		Object gate = getGate(key);
		synchronized (gate) {
			gate.notifyAll();
		}
	}

	private synchronized Object getGate(T key) {
		Object gate = gates.get(key);
		if (gate == null) {
			gate = new Object();
			gates.put(key, gate);
		}
		return gate;
	}
}
