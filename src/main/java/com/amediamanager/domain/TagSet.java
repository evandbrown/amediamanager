package com.amediamanager.domain;

public class TagSet<E> extends java.util.HashSet<E> {
	@Override
	public String toString() {
		if(! super.isEmpty()) {
			String s = super.toString();
			return s.substring(1, s.length()-1);
		} else {
			return super.toString();
		}
	}
}
