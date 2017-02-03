<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
  String path =request.getContextPath();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>暂无权限</title>
<style type="text/css">
<!--
.t {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        color: #CC0000;
}
.c {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        font-size: 16px;
        font-weight: normal;
        color: #000000;
        line-height: 20px;
        text-align: center;
        border: 1px solid #CCCCCC;
        background-color: #FFFFEC;
}
body {
        background-color: #FFFFFF;
        margin-top: 100px;
}
-->
</style>
</head>
<body>

<div align="center">
  <h2><span class="t">对不起，您没有进入该页面的权限！</span></h2>
  <table border="0" cellpadding="8" cellspacing="0" width="460">
    <tbody>
      <tr>
        <td class="c"><a href="<%=path%>/views/index.jsp">回到主页</a></td>
      </tr>
    </tbody>
  </table>
</div>

</body>
</html>
