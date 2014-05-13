package util;

/**
 * 2010, Pair class as found on   http://en.wikipedia.org/wiki/Generics_in_Java#Generic_class_definitions
 */

public class Pair<T, S>{
	
	private final T first;
	private final S second;
	
	public Pair(T f, S s){ 
		first = f;
		second = s;   
	}

	public T getFirst(){
		return first;
	}

	public S getSecond()   {
		return second;
	}

	public String toString()  {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append((null!=first)?first.toString():"");
		builder.append(", ");
		builder.append((null!=second)?second.toString():"");
		builder.append(")");
		return builder.toString(); 
	}
}
