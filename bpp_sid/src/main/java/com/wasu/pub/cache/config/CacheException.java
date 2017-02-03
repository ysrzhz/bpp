package com.wasu.pub.cache.config;

public class CacheException extends Exception
{
  private static final long serialVersionUID = -3879876394070750036L;

  public CacheException(String message)
  {
    super(message);
  }

  public static void notNull(Object value, String message) throws CacheException
  {
    assertFor((value != null) && (value.toString().trim().length() > 0), message);
  }

  public static void assertFor(boolean condition, String message) throws CacheException
  {
    if (!condition)
      throw new CacheException(message);
  }
}
