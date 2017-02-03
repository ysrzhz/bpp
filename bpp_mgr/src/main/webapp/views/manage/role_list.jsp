<%@ page language="java"  pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<% 
  String path =request.getContextPath();
  String entityName = "manage";
  String entityNameCN="角色";
%>
<html>
<script type="text/javascript">
var entityName='<%=entityName%>';
var entityChinaName='<%=entityNameCN%>';

$(function(){
	//初始化datagrid
	entity.initPage = function(target, url){
		var operateFlag = $("#entityEdit").val();
		var gridOptions = {
			columns:[[
				{field:'id',title:entityChinaName+'ID',align:'center',width:80,hidden:true},
				{field:'name',align:'center',width:200,title:entityChinaName+'名称',hidden:false},
				{field:'code',align:'center',width:200,title:entityChinaName+'编码',hidden:false},
				{field:'operate',align:'center',width:200,title:'操作',hidden:false}
			]],
			url:url
		};
		$(target).mygrid(gridOptions);
		if ("edit" == operateFlag) {
			$(target).datagrid("showColumn", "operate");
		} else {
			$(target).datagrid("hideColumn", "operate");
		}
	}
			
	entity.initPage("#list_data", globalWebAppURL+'/<%=entityName%>/list.do');
});

</script>
<jsp:include page="/views/common.jsp"></jsp:include>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="<%=entityName%>管理">
    <meta name="description" content="<%=entityName%>管理页面">
	<title><%=entityName%>管理</title>
</head>
<body>
	<div id="tb" style="padding:5px;height:auto">
		<div style="margin-bottom:5px">
			<a href="#"  id="entity_Add" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="entity.add()">新增</a>
			<input type="hidden" id="<%=entityName %>Edit" value="edit" />
			<a href="#" id="entityDelete"  class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="entity.del()">删除</a>
		</div>
		<div>
			角色名称: <input type="text" id="<%=entityName %>NameS" name="name">
			角色编码: <input type="text" id="<%=entityName %>CodeS" name="code">
			
			<a href="#" id="query"class="easyui-linkbutton" iconCls="icon-search" onclick="doSearch()">查询</a>
			<a href="#" id="clear" class="easyui-linkbutton" iconCls="icon-delete" onclick="clearSearch()">清除</a>
		</div>
    </div>
	<table id="list_data"  title="列表" style="width:700px;height:250px" data-options="toolbar:'#tb'">
    </table>
    
    
	<div style="position:relative;" id="editEntity" class="easyui-dialog" data-options="modal:true,closed:true,resizable:false,buttons:'#edit_btn',width:800,
			height:280">
	</div>
	<div style="position:relative;" id="entityDetail" class="easyui-dialog" data-options="modal:true,closed:true,resizable:false,width:800,
			height:280">
	</div>
	<div id="edit_btn" style="padding:5px;height:auto">
		<div style="margin-bottom:5px">
			<a href="#" id="entitySave" class="easyui-linkbutton" iconCls="icon-save" onclick="entity.save()">保存</a>
			<a href="#" id="entityCancle" class="easyui-linkbutton" iconCls="icon-no"  onclick="entity.cancel()">取消</a>
		</div>
    </div>
    
    <div style="position:relative;" id="manageChannelDvb" class="easyui-dialog" data-options="modal:true,closed:true,resizable:false,width:800,height:600">
    </div>
</body>
<script type="text/javascript">
	var globalWebAppURL = "<%=path %>";

	//页面入口处
	$(function(){
		entity.initPage("#list_data", globalWebAppURL+'/views/<%=entityName%>/role_list');
		//channel.initChannelAssorts("assortsS");
		//channel.initChannelStatus("statusS");
		//channel.initrecordAreas("recordAreasS"); 
		//channel.initAreaCodeName("areaCodeS"); 
	});
	
	//查询方法
	function doSearch() {
		var condition = {
				name 	: $("#<%=entityName %>NameS").val(),
				code 	: $("#<%=entityName%>CodeS").val()
		};
		$("#list_data").datagrid('reload',condition);//根据URL= globalWebAppURL+'/manager/channel/list'二次加载数据，不用炫染框架
	}
	
	function clearSearch(){
		$("#<%=entityName%>NameS").val("");
		$("#<%=entityName%>CodeS").val("");
	} 
	
</script>
</html>