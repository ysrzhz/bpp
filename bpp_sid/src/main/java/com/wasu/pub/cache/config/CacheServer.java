package com.wasu.pub.cache.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CacheServer
{
  private Map<String, String> params = new HashMap();
  private boolean active;

  public boolean isActive()
  {
    return this.active;
  }

  public boolean checkActive() {
    this.active = test();
    return this.active;
  }

  public void addParam(String name, String value) {
    this.params.put(name, value);
  }

  public String param(String name) {
    return param(name, null);
  }

  public String param(String name, String defaultValue) {
    String param = (String)this.params.get(name);
    return param == null ? defaultValue : param;
  }

  public int paramInt(String name, int defaultValue) {
    String param = param(name, defaultValue + "");
    return Integer.parseInt(param);
  }

  protected abstract boolean test();

  public abstract void init();

  public abstract void set(String paramString, Object paramObject)
    throws Exception;

  public abstract void mset(List<Object> paramList)
    throws Exception;

  public abstract Object get(String paramString)
    throws Exception;

  public abstract List<Object> mget(String[] paramArrayOfString)
    throws Exception;

  public abstract void remove(String paramString)
    throws Exception;
  
  public abstract Long push(String key, String... values)  throws Exception;
  
  public abstract void expire(String key, int delayTime) throws Exception;

  public abstract void clear()
    throws Exception;

  public abstract Set<String> keys(String paramString)
    throws Exception;
}
