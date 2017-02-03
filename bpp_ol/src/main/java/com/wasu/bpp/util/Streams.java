package com.wasu.bpp.util;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.wasu.bpp.exception.MmsRuntimeException;
 
public final class Streams
{
  private static final int DEFAULT_BUFFER_SIZE = 8192;

  public static long copy(InputStream pInputStream, OutputStream pOutputStream, boolean pClose)
    throws IOException
  {
    return copy(pInputStream, pOutputStream, pClose, true, 
      new byte[8192]);
  }

  public static long copy(InputStream pIn, OutputStream pOut, boolean pClose, boolean filterBOM, byte[] pBuffer)
    throws IOException
  {
    OutputStream out = pOut;
    InputStream in = pIn;

    if ((out == null) || (in == null))
    {
      if (in != null)
      {
        in.close();
      }

      if ((out != null) && (pClose))
      {
        out.close();
      }

      return 0L;
    }
    try
    {
      long total = 0L;
      int res = 0;
      if (filterBOM)
      {
        res = in.read(pBuffer, 0, 3);
        if (res == 3)
        {
          if (((pBuffer[0] & 0xFF) == 239) && ((pBuffer[1] & 0xFF) == 187) && ((pBuffer[2] & 0xFF) == 191))
          {
            res = 0;
          }
        }

        if (res > 0)
        {
          total += res;
          out.write(pBuffer, 0, res);
        }
      }

      res = in.read(pBuffer);
      while (res != -1) {
        if (res > 0) {
          total += res;
          out.write(pBuffer, 0, res);
        }
        res = in.read(pBuffer);
      }

      if (out != null) {
        if (pClose)
          out.close();
        else {
          out.flush();
        }
        out = null;
      }
      in.close();
      in = null;
      return total;
    } finally {
      if (in != null)
        try {
          in.close();
        }
        catch (Throwable localThrowable2)
        {
        }
      if ((pClose) && (out != null))
        try {
          out.close();
        }
        catch (Throwable localThrowable3)
        {
        }
    }
  }

  public static String asString(InputStream pStream)
    throws IOException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    copy(pStream, baos, true);
    return baos.toString();
  }

  public static String asString(InputStream pStream, String pEncoding)
    throws IOException,MmsRuntimeException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    copy(pStream, baos, true);
    return baos.toString(pEncoding);
  }
}