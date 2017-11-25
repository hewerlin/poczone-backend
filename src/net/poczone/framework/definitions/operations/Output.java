package net.poczone.framework.definitions.operations;

public interface Output<T> {
	String getName();
	
	Class<T> getType();
}
