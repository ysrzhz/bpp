package com.wasu.bpp.util;

import com.google.gson.FieldNamingStrategy;
import java.lang.reflect.Field;

public class LowerCaseFieldNamingStrategy
  implements FieldNamingStrategy
{
  public String translateName(Field f)
  {
    return f.getName().toLowerCase();
  }
}
