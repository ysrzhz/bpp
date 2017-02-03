package com.wasu.pub.cache.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.wasu.pub.cache.CacheService;

public class CacheServlet extends HttpServlet
{
  private static final long serialVersionUID = -8790083226326592397L;
  public static final Logger logger = Logger.getLogger(CacheServlet.class);

  public void init(ServletConfig config) throws ServletException
  {
    try {
      String configPath = config.getServletContext().getInitParameter("cacheConfig");

      CacheConfig.init(configPath);
      CacheServers.init();
    } catch (Exception ex) {
      logger.error("缓存启动错误", ex);
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    doPost(req, resp);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    response.setContentType("text/html; charset=utf-8");
    if (!CacheConfig.isCacheManage()) {
      response.getWriter().print("缓存管理不可??");
      return;
    }
    String page = request.getParameter("page");
    try {
      style(response);
      if ("value".equals(page))
        value(request, response);
      else
        index(request, response);
    }
    catch (Exception e) {
      logger.error("缓存管理异常", e);
    }
  }

  private void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    String uri = request.getRequestURI();
    if (!CacheServers.isActive()) {
      out.print("无可用缓存");
      return;
    }
    for (CacheServer server : CacheServers.getActiveServers()) {
      out.print("<form method=\"post\" action=\"" + uri + "?server=" + server + "\" method=post style=\"margin:0px\">");

      out.print(server);
      out.print("<input name=\"searchKey\">");
      out.print("<input type=\"submit\" value=\"搜索\">");
      out.print("<a href=\"" + uri + "?searchKey=query_&server=" + server + "\">query</a>&nbsp");
      out.print("<a href=\"" + uri + "?searchKey=time_&server=" + server + "\">time</a>");
      out.print("</form>");
    }

    String server = request.getParameter("server");
    String searchKey = request.getParameter("searchKey");
    String delKey = request.getParameter("key");
    String opt = request.getParameter("opt");
    if ("delete".equals(opt)) {
      CacheServers.getActiveServer(server).remove(delKey);
    }
    if ((server == null) || (searchKey == null)) {
      return;
    }
    searchKey = "*" + searchKey + "*";
    out.print("<table>");
    out.print("<tr><th width=\"800\" colspan=\"3\">KEY</th>");

    int index = 0;
    for (String key : CacheServers.getActiveServer(server).keys(searchKey)) {
      out.print("<tr><td>" + ++index + "<td><a href='" + uri + "?key=" + key + "&server=" + server + "&page=value'>" + key + "</a>");

      out.print("<td><a href='" + uri + "?key=" + key + "&server=" + server + "&opt=delete&searchKey=" + searchKey + "'>delete</a>");
    }

    out.print("</table>");
  }

  private void value(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    PrintWriter out = response.getWriter();
    String key = request.getParameter("key");
    String server = request.getParameter("server");
    CacheServer cacheServer = CacheServers.getActiveServer(server);
    Object object = cacheServer.get(key);
    out.print("<table>");
    if ((object instanceof CacheObject)) {
      CacheObject co = (CacheObject)object;
      out.print("<tr><th width=\"800\" colspan=2>key=" + key + ",time=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(co.getTime())));
      Object value = co.getValue();
      if (value == null) {
        out.print("<tr><td>null");
      }
      else
      {
        int index;
        Iterator i$;
        if ((value instanceof List)) {
          index = 0;
          for (i$ = ((List)value).iterator(); i$.hasNext(); ) { Object v = i$.next();
            out.print("<tr><td>" + ++index + "<td>" + CacheService.describe(v)); }
        }
        else {
          out.print("<tr><td>" + CacheService.describe(value));
        }
      }
    } else { out.print("<tr><th width=\"400\">");
      if (object.getClass() == Long.class) {
        String timtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(object.toString())));
        out.print(timtFormat);
      } else if ((object.getClass() == Integer.class) || (object.getClass() == String.class)) {
        out.print(object);
      } else {
        out.print(CacheService.describe(object));
      }
    }
    out.print("</table>");
  }

  private void style(HttpServletResponse response) throws Exception {
    PrintWriter out = response.getWriter();
    out.print("<style>");
    out.print("th{background-color:#EEE;padding:3px;}");
    out.print("td{padding:5px;}");
    out.print("td,th{border:1px solid #999;}table{border-collapse:collapse;}");
    out.print("a.linkButton{font-size:16px;border:1 solid #999;padding:3px;background-color:#EEE;}");
    out.print("a:hover.linkButton{background-color:#999;color:#fff;}");
    out.print("a.exeMenu{display:inline-block;width:60px;text-align:center;}");
    out.print("a,a:link,a:visited{color:blue;text-decoration:none;}");
    out.print("</style>");
  }
}
