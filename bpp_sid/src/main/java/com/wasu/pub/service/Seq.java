package com.wasu.pub.service;

public interface Seq {
	public Long getSeq(String name);
	public Long getSeq(String name, int size);
}