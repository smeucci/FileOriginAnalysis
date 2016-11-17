package utils;

public class Pair<T> {

	private T key;
	private T value;
	
	public Pair(T key, T value) {
		this.key = key;
		this.value = value;	
	}
	
	public T getKey() {
		return this.key;
	}
	
	public T getValue() {
		return this.value;
	}
	
}