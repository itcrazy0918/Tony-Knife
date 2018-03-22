package com.tly.bigdata.component;

import java.io.Serializable;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013年11月21日 上午9:49:41
 * @Description 
 */
public class Pair<T1, T2> implements Serializable {
	private static final long serialVersionUID = 5460280867709910635L;

	public T1 first;
	
	public T2 second;
	
	public Pair () {
		
	}
	
	public Pair (T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	public static <T1, T2> Pair<T1, T2> makePair (T1 first, T2 second) {
		return new Pair<T1, T2>(first, second);
	}

	public T1 getFirst() {
		return first;
	}

	public void setFirst(T1 first) {
		this.first = first;
	}

	public T2 getSecond() {
		return second;
	}

	public void setSecond(T2 second) {
		this.second = second;
	}
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Pair<?, ?> other = (Pair<?, ?>) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        }
        else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        }
        else if (!second.equals(other.second))
            return false;
        return true;
    }
	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
}
