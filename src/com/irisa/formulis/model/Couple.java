package com.irisa.formulis.model;

/**
 * Pure utility function to store a couple of elements
 * @author pmaillot
 *
 * @param <A>
 * @param <B>
 */
public class Couple<A, B> {
	private A _first;
	private B _second;
	
	public Couple()
	{
		this._first = null;
		this._second = null;
	}
	
	public Couple(A first, B second)
	{
		this._first = first;
		this._second = second;
	}
	
	public static <A, B> Couple<A, B> of(A first, B second) {
		return new Couple<A, B>(first, second);
	}
	
	public A getFirst()
	{
		return _first;
	}
	
	public B getSecond()
	{
		return _second;
	}
	
	public void setFirst(A obj)
	{
		this._first = obj;
	}
	
	public void setSecond(B obj)
	{
		this._second = obj;
	}
	
	@Override
	public int hashCode()
	{
		return this._first.toString().hashCode() + this._second.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o.getClass() == this.getClass())
		{
			@SuppressWarnings("unchecked")
			Couple<A, B> c = (Couple<A, B>) o;
			return (this.hashCode() == c.hashCode());	
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		return "( " + this._first + " ) ( " + this._second + ") ";
	}
}
