package utils;

public class Pair<T, K> {

	private T first;
	private K second;
	
	public Pair() {
		this.first = null;
		this.second = null;
	}
	
	public Pair(T first, K second) {
		this.first = first;
		this.second = second;
	}
	
	public T getFirst() {
		return this.first;
	}
	
	public K getSecond() {
		return this.second;
	}
	
	public void setFirst(T first) {
		this.first = first;
	}
	
	public void setSecond(K second) {
		this.second = second;
	}
	
	public boolean isNull() {
		if (this.first == null || this.second == null) {
			return true;
		} else {
			return false;
		}
	}
	
}