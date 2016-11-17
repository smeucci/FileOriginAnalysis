package utils;

public class Pair<T, K> {

	private T key;
	private K value;
	
	public Pair(T key, K value) {
		this.key = key;
		this.value = value;	
	}
	
	public T getKey() {
		return this.key;
	}
	
	public K getValue() {
		return this.value;
	}
	
}