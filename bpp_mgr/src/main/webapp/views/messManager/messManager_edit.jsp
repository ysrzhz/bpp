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
        <input class="easyui-validatebox" style="width: 100%;" type="text" id="edit_title" name="title"  />
      </td>
    </tr>
    <tr>
      <th>消息内容：</th>
      <td>
        <textarea rows ="5"cols="20" style="height: auto;width: 100%" id="edit_content" name= "content" class="easyui-validatebox" data-options="required:true" />
      </td>
    </tr>
    <tr>
      <th>呈现方式：</th>
      <td>
        <select class="easyui-combobox" id="edit_scope" name="scope" ></select>
      </td>
    </tr>
    <tr>
      <th>展现类型：</th>
      <td>
        <select class="easyui-combobox" id="edit_vtype" name="vtype" ></select>
      </td>
    </tr>
    <tr>
      <th>发送时间：</th>
      <td>
        <input class="easyui-datetimebox" id = "edit_sendTime" name="sendTime"
               data-options="validType:'dateAfterNow'" style="width: 150px">
      </td>
    </tr>
    <tr>
      <th>消息有效期：</th>
      <td>
        <input class="easyui-datetimebox" id="edit_validTime" name="validTime"
               data-options="validType:'dateAfterNow'" style="width: 150px" />
      </td>
    </tr>
    <tr>
      <th>机顶盒编号</th>
      <td>
        <textarea cols="22" style="height: auto;width: 220px" id="edit_stbId" name= "stbId" />
        <input type="radio" name="s_bosstype" value="H" checked/>杭网
        <input type="radio" name="s_bosstype" value="S"/>省网
        <input type="button" name="choose" onclick="getTree('stbId')" value="选择" />
      </td>
    </tr>
    <tr>
      <th>区域编号</th>
      <td>
        <textarea cols="22" style="height: auto;width: 220px" id="edit_areaId" name= "areaId"/>
        <input type="radio" name="a_bosstype" value="H" checked/>杭网
        <input type="radio" name="a_bosstype" value="S"/>省网
        <input type="button" name="choose" onclick="getTree('areaId')" value="选择" />
      </td>
    </tr>
    <tr>
      <th>客户编号</th>
      <td>
        <textarea cols="22" style="height: auto;width: 220px" id="edit_custId" name= "custId" />
        <input type="radio" name="c_bosstype" value="H" checked/>杭网
        <input type="radio" name="c_bosstype" value="S"/>省网
        <input type="button" name="choose" onclick="getTree('custId')" value="选择" />
      </td>
    </tr>
  </table>
</form>

<div id="createTree" class="easyui-dialog"
     style="width: 420px; height: 420px;" closed="true"
     modal="true" buttons="#toolbar_region">
  <div class="easyui-tree" id="showTree">
    <ul id="area_tree"></ul>
  </div>
</div>
<div id="toolbar_region" style="padding: 5px; height: auto;display: none;">
  <a href="#" class="easyui-linkbutton" iconCls="icon-save"
     id="regionSave" onclick="paramSave()" plain="false">确定</a>
  <a href="#" class="easyui-linkbutton" iconCls="icon-no"
     onclick="javascript:$('#createTree').dialog('close')" plain="false">关闭</a>
</div>
<script type="text/javascript">
  var scope = [{"id":"0","text":"弹出框","selected":true},{"id":"1","text":"跑马灯"},{"id":"2","text":"悬浮框"},{"id":"3","text":"列表"}];
  var vtype = [{"id":"0","text":"广播类","selected":true},{"id":"1","text":"交互类"},{"id":"2","text":"收藏类"}];

  $('#edit_scope').combobox({
    panelWidth  : 180,
    panelHeight	: 'auto',
    valueField	: 'id',
    textField	: 'text',
    editable   	:  false,
    data		:  scope
  });
  $('#edit_vtype').combobox({
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
  //日期验证
  $.extend($.fn.validatebox.defaults.rules, {
    dateAfterNow: {
      validator: function(value){
        var now = new Date();
        var strDate = now.getFullYear()+"-";
        strDate += now.getMonth()+1+"-";
        strDate += now.getDate()+"-";
        var d1 = $.fn.datebox.defaults.parser(strDate);
        var d2 = $.fn.datebox.defaults.parser(value);
        return d2>=d1;
      },
      message: '日期必须大于当前日期'
    },
    dateAfterTomor: {
      validator: function(value){
        var now = new Date();
        var strDate = now.getFullYear()+"-";
        strDate += now.getMonth()+1+"-";
        strDate += now.getDate()+1;
        var d1 = $.fn.datebox.defaults.parser(strDate);
        var d2 = $.fn.datebox.defaults.parser(value);
        return d2>=d1;
      },
      message: '有效日期必须大于次日有效日期'
    }
  })
  //加载树结构
  function getTree(name) {
    var title = "机顶盒列表";
    if(name == "areaId") title = "区域编号列表";
    if(name == "custId") title = "客户编号列表";
    $('#createTree').dialog({
      left : "750px",
      top : "20px"
    });
    $("#toolbar_region").attr("display","block");
    $("#createTree").dialog("open").dialog("setTitle", title);
    loadTree(name);
  }
  var level;
  var sendName = null;
  var type = null;
  function loadTree(name) {
    var bosstype = $("input[name='s_bosstype']:checked").val();
    if(name == 'areaId') bosstype = $("input[name='a_bosstype']:checked").val();
    if(name == 'custId') bosstype = $("input[name='c_bosstype']:checked").val();
    if(name != sendName ||(name == sendName && bosstype != type)){   //接收类型不同且数据源发生改变均重新加载树结构
      sendName = name;
      type = bosstype;
      stbids = "";
      $('#area_tree').tree({
        checkbox: true,
        animate:true,
        url: globalWebAppURL+"/<%=entityName%>/getTree?bosstype="+bosstype,
        lines:true,
        loadFilter: function(data){
          if (data.d){
            return data.d;
          } else {
            return data;
          }
        },
        onBeforeExpand:function(node,param){
          if(node.attributes.isParent){
            $('#area_tree').tree('options').url = globalWebAppURL+"/<%=entityName%>/getTree?parentId="+node.attributes.ids+"&name="+name;
          }else if(name=='stbId'&& !node.attributes.isParent){
            $('#area_tree').tree('options').url = globalWebAppURL+"/<%=entityName%>/getStbids?regionId="+node.id;
          }else{
            return false;
          }
        } ,
        onLoadSuccess:function(node,data){

        }
      })
    }
  }

  //获得tree的层数
  var easyui_tree_options = {
    length : 0,  //层数
    getLevel : function(treeObj, node){ //treeObj为tree的dom对象，node为选中的节点
      while(node != null){
        node = $(treeObj).tree('getParent', node.target)
        easyui_tree_options.length++;
      }
      var length1 = easyui_tree_options.length;
      easyui_tree_options.length = 0;     //重置层数
      return length1;
    }
  }
  var stbids="";
  var areaids = "";
  var custids = "";
  function paramSave(){
    var count = 0;
    stbids = "";
    areaids = "";
    custids = "";
    var nodes = $("#area_tree").tree('getChecked');
    for(var i = 0;i<nodes.length;i++){
      if($('#area_tree').tree('isLeaf',nodes[i].target)){
        if('stbId' == sendName){
          stbids += nodes[i].text+"\n";
          count++;
        }else{
          custids += nodes[i].text + "\n";
          count++;
        }
      }else{
        if(isChildCheck(nodes[i])){
          areaids +=nodes[i].id+"\n";
          if('areaId' == sendName)count++;
        }else{
          continue;
        }
      }
    }
    if(count>500){
      $.messager.alert('提示','选择的消息接受者最多为500','info');
      return;
    }
    if(count==0){
      $.messager.alert('提示','没有选中消息接受者','info');
      return;
    }
    stbids = stbids.substring(0,stbids.length-1);
    areaids = areaids.substring(0,areaids.length-1);
    custids = custids.substring(0,custids.length-1);
    if('stbId'== sendName)$("#edit_stbId").val(stbids);
    if('areaId' == sendName) $("#edit_areaId").val(areaids);
    if('custId' == sendName) $("#edit_custId").val(custids);
    stbids = "";
    areaids = "";
    custids = "";
    $("#toolbar_region").attr("display","none");
    $('#createTree').dialog('close');
    count = 0;
  }

  function isChildCheck(node){
    var childs = $("#area_tree").tree('getChildren',node.target);
    for(var i = 0;i< childs.length;i++){
      if(childs[i].checked){
        return false;
      }else{
        continue;
      }
    }
    return true;
  }
</script>
</body>
</html>
