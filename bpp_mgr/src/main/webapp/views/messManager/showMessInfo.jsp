<%@ page language="java"  pageEncoding="utf-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.wasu.sid.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
  String path =request.getContextPath();
  String entityName = "messManager";
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="keywords" content="jquery,ui,easy,easyui,web">
  <meta name="description" content="">
</head>
<body>
<form id="entityForm" name="entityForm" method="post">
  <input type="hidden" name = "id" id="id" value="${id}"/>
  <table class="mytable" style="font-size: 12px">
    <tr>
      <th>消息标题：</th>
      <td>
        <input class="easyui-validatebox" style="width: 100%;" type="text" id="show_title" name="title" readonly="readonly" />
      </td>
    </tr>
    <tr>
      <th>消息内容：</th>
      <td>
        <textarea rows ="5"cols="20" style="height: auto;width: 100%" id="show_content" name= "content" readonly="readonly" />
      </td>
    </tr>
    <tr>
      <th>呈现方式：</th>
      <td>
        <select class="easyui-combobox" id="show_scope" name="scope" readonly="readonly" />
      </td>
    </tr>
    <tr>
      <th>展现类型：</th>
      <td>
        <select class="easyui-combobox" id="show_vtype" name="vtype" readonly="readonly"/>
      </td>
    </tr>
    <tr>
      <th>发送时间：</th>
      <td>
        <input id = "show_sendTime" name="sendTime" style="width: 150px" readonly="readonly"/>
      </td>
    </tr>
    <tr>
      <th>消息有效期：</th>
      <td>
        <input id="show_validTime" name="validTime" style="width: 150px" readonly="readonly" />
      </td>
    </tr>
    <tr>
      <th>机顶盒编号</th>
      <td>
        <textarea cols="22" style="height: auto;width: 100%" id="show_stbId" name= "stbId" readonly="readonly" />
      </td>
    </tr>
    <tr>
      <th>区域编号</th>
      <td>
        <textarea cols="22" style="height: auto;width: 100%" id="show_areaId" name= "areaId" readonly="readonly" />
      </td>
    </tr>
    <tr>
      <th>客户编号</th>
      <td>
        <textarea cols="22" style="height: auto;width: 100%" id="show_custId" name= "custId" readonly="readonly" />
      </td>
    </tr>
  </table>
</form>
<script type="text/javascript">
  var scope = [{"id":"0","text":"弹出框","selected":true},{"id":"1","text":"跑马灯"},{"id":"2","text":"悬浮框"},{"id":"3","text":"列表"}];
  var vtype = [{"id":"0","text":"广播类","selected":true},{"id":"1","text":"交互类"},{"id":"2","text":"收藏类"}];

  $('#show_scope').combobox({
    panelWidth  : 180,
    panelHeight	: 'auto',
    valueField	: 'id',
    textField	: 'text',
    editable   	:  false,
    data		:  scope
  });
  $('#show_vtype').combobox({
    panelWidth  : 180,
    panelHeight	: 'auto',
    valueField	: 'id',
    textField	: 'text',
    editable   	:  false,
    data		:  vtype
  });
  //获取编辑的消息信息
  var globalWebAppURL = "<%=path %>";
  var id = ${id};
  var url = globalWebAppURL+'/<%=entityName%>/get/'+id+'?ts='+new Date().getTime();
  $.get(url, function(data) {
    var entityData = data;
    $('#entityForm').form('load', entityData);
  });
</script>
</body>
</html>
