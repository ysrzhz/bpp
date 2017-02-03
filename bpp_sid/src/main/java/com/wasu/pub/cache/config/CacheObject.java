package com.wasu.pub.cache.config;

import java.io.Serializable;

public class CacheObject implements Serializable {
	private static final long serialVersionUID = 4451974277683175393L;
	private Object value;
	private long time;

	public CacheObject(Object value) {
		this.value = value;
		this.time = System.currentTimeMillis();
	}

	public Object getValue() {
		return this.value;
	}

	public long getTime() {
		return this.time;
	}

	@Override
	public String toString() {
		return "CacheObject [value=" + value + ", time=" + time + "]";
	}
}