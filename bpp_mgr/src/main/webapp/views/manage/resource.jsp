<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ include file="../resource/resource.jsp"%>
<html>
<head>
<style>
.treenode-top{ 
	background:url(../resource/images/menu_blue.png) no-repeat;
}
.treenode-sub{ 
	background:url(../resource/images/menu_gray.png) no-repeat;
}
.icon-collapse{ 
	background:url(../resource/images/collapse.png) no-repeat;
}
.icon-expand{ 
	background:url(../resource/images/expand.png) no-repeat;
}
.icon-import{ 
	background:url(../resource/images/import.png) no-repeat;
}
.icon-export{ 
	background:url(../resource/images/export.png) no-repeat;
}
.divborder {
	border: 1px solid #DBDBDB;
	margin: 1px 0px 1px 0px;
}
</style>
</head>
<body>
	<div class="frame-content">
		<div style="width:14%;height:100%;float:left;">
			<table id="list_data" class="easyui-datagrid" singleSelect="true" title="应用列表" fitColumns="true" border="true"
					fit="true" data-options="url:'app_list.do',onSelect:getResourceByAppId,onLoadSuccess:initSelectRow">
				<thead>
					<tr><th field="name" width="100">应用名称</th></tr>
				</thead>
			</table>
		</div>

		<div style="width:84%; height:100%;float:right;">
			<table id="resource_tree" title="资源树列表" fit="true" toolbar="#toolbar" fitColumns="true" 
				class="easyui-treegrid" rownumbers="true" animate="false" data-options="idField:'id',treeField:'text'">
				<thead>
					<tr>
						<th field="text" formatter="resourceFormatter" width="120">资源名称</th>
						<th field="action" width="200">动作</th>
						<th field="code" width="100">资源编码</th>
						<th field="rank" width="30">排序</th>
				</thead>
			</table>
			<div id="toolbar">
			 <div  style="padding:5px"  class="divborder"> 
				<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="createResource();">新增</a> 
				<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteResource()">删除</a>
				<div style='height:25;border-left:1px solid #ccc;border-right:1px solid #fff;display:inline;margin: 0px 6px'></div>
				<!-- <a href="#" class="easyui-linkbutton" iconCls="icon-import" plain="true" onclick="importResource()">导入</a>
				<a href="#" class="easyui-linkbutton" iconCls="icon-export" plain="true" onclick="exportResource()">导出</a>
				<div style='height:25;border-left:1px solid #ccc;border-right:1px solid #fff;display:inline;margin: 0px 6px'></div> -->
				<a href="#" class="easyui-linkbutton" iconCls="icon-expand" plain="true" onclick="$('#resource_tree').treegrid('expandAll')">展开</a>
				<a href="#" class="easyui-linkbutton" iconCls="icon-collapse" plain="true" onclick="$('#resource_tree').treegrid('collapseAll')">折叠</a>
			</div>
				</div>
		</div>
	</div>
	
	
	<div id="resource_create" class="easyui-dialog" style="width: 500px; height:260px; 
		padding: 10px 20px" closed="true" modal="true" toolbar="#edit-toolbar">
		<form id="resource_form" method="post" enctype="multipart/form-data">
			<table class="input">
				<tr><th>资源名称</th>
					<td><input id="create_name" name="name" type="text" class="easyui-validatebox" data-options="required:true ,validType:'length[1,255]'" size=40/>
					<span style="color:red;">1~255位</span></td>
				
				<tr><th>动作</th>
					<td class="color"><input name="action" type="text" class="easyui-validatebox" size=40 data-options="validType:'length[0,255]'"/>
					<span style="color:red;">0~255位</span></td>
				<tr><th>编码</th>
					<td><input name="code" type="text" class="easyui-validatebox" size=40 data-options="required:true ,validType:'length[1,255]'"/>
					<span style="color:red;">1~255位</span></td>
			
				<tr><th>排序</th>
					<td class="color"><input name="rank" type="text" class="easyui-numberbox" data-options="required:true,validType:['Number','length[1,255]']" size=40/>
					<span style="color:red;">非负数</span></td>
				
				<tr><th>父节点</th>
					<td><input type="text" name="parentName" id="resource_parentName" onclick="javascript:openResourceParent()" size=40 style="cursor:hand" readonly /><button  type="button" onclick="javascript:toTop()">设为空</button></td>
						<input type="hidden" name="parentId" id="resource_parentId">
			</table>
		</form>
		<div id="edit-toolbar">
		 <div  style="padding:1px 0px 1px 0px"  class="divborder"> 
			<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="doSave()" plain="true">保存</a> 
		</div></div>
		<div id="dlg-tree" class="easyui-dialog" title="选择父节点(双击选择)"
			closed=true style="width:300px;height:500px;padding:10px 20px" modal="true">
			<div id="tree" class="easyui-tree" lines="true" data-options="onDblClick:typeSelect"></div>
		</div>
	</div>
	
	<div id="importDlg" class="easyui-dialog" toolbar="#import-toolbar" title="导入资源" closed=true style="width:450px;height:200px;padding:30px 10px" modal="true">
		<form action="resource_importResource" id="importForm" method="post" enctype="multipart/form-data">
			<input type=hidden id="importApp" name="appId">
			<input id="importResource" name="importResource" type="file" class="easyui-validatebox" data-options="required:false" style="width:165px;" accept=".json"><br><br>
			<span style="color:red;">（请选择json数据格式的文件,如resource.json）</span>
		</form>
		<div id="import-toolbar">
		 <div  style="padding:1px 0px 1px 0px"  class="divborder"> 
			<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="doImport()" plain="true">保存</a> 
			
		</div></div>
	</div>
</body>
<script type="text/javascript">
	var _appId;
	//0未进行提交操作,1正在进行提交操作
	var _isSubmit = 0;
	
	//扩展easyui表单的验证
	$.extend($.fn.validatebox.defaults.rules, {
	    Number: {
	        validator: function (value) {
	            var reg =/^[0-9]*$/;
	            return reg.test(value);
	        },
	        message: '请输入非负数.'
	    },
	});
	
	function exportResource(){
		window.open("resource_export?appId="+_appId);
	}
	
	function importResource(){
		$("#importDlg").dialog("open");
	}
	
	function doImport(){
		var row = $("#list_data").datagrid("getSelected");
		if(row==null||null=="")
		{
			$.messager.show({
				title : "错误",
				msg : "请选择一个应用，没有请到应用管理中新增",
				showType : 'slide',
				timeout : 1500
			});
			return false;
		}
		var importResource = $('#importResource').val();
		if(importResource==null||importResource.trim()==""){
			$.messager.show({
				title : "错误",
				msg : "请选择一个文件",
				showType : 'slide',
				timeout : 1500
			});
			return false;
		}
		if(importResource.indexOf(".json")<0){
			$.messager.show({
				title : "错误",
				msg : "文件格式错误，请选择json格式的文件",
				showType : 'slide',
				timeout : 1500
			});
			return false;
		}
		if($('#importForm').form('validate')){
			$('#importApp').val(_appId);
			$('#importForm').form('submit', {
			    success: function(result){
			    	var result = eval('(' + result + ')');
					if ("0" == result.ret) {
						$('#importDlg').dialog('close');
						$("#resource_tree").treegrid('reload');
						$('#importResource').val("");
					} else {
						$.messager.show({
							title : "错误",
							msg : "文件导入有误，主键冲突！",
							showType : 'slide',
							timeout : 1500
						});
					}
			    }
			});
		}
	}
	
	function resourceFormatter(value, row, index) {
		return '<a href="#" onclick=doEdit("' + row.id + '") >' + value + '</a>';
	}

	function getResourceByAppId() {
		var row = $("#list_data").datagrid("getSelected");
		$('#resource_tree').treegrid({
			url : 'resource_getResourceTree',
			queryParams : {
				appId : row.id
			}
		});
		_appId = row.id;

	}

	function initSelectRow(){
		$('#list_data').datagrid('selectRow', 0);
	}
	
	function openResourceParent() {
		$("#tree").tree({
			url : 'resource_getResourceTree?appId=' + _appId
		});
		$('#dlg-tree').dialog('open');
	}
	
	function typeSelect(node) {
		$('#resource_parentId').val(node.id);
		$('#resource_parentName').val(node.text);
		$('#dlg-tree').dialog('close');
	}

	function createResource() {
		var row = $("#resource_tree").treegrid("getSelected");
		$("#resource_create").dialog("close");
		$("#resource_create").dialog("open").dialog("setTitle", "新增资源信息");
		$("#resource_form").form("clear");
		if(row){			
			$("#resource_parentId").val(row.id);
			$("#resource_parentName").val(row.text);
		}
		_appId = $("#list_data").datagrid("getSelected").id;
		url = "resource_create?appId=" + _appId;
	}

	function doSave() {
		$("#resource_form").form('submit', {
			url : url,
			onSubmit : function() {
				if(_isSubmit==0){
					_isSubmit=1;
					var val = $(this).form('validate');
					if(!val){
						$.messager.alert('提示','输入值无效！','info'); 
					}
					return val;
				}
			},
			success : function(result) {
				var result = eval('(' + result + ')');
				if ("0" == result.ret) {
					$('#resource_create').dialog('close');
					$("#resource_tree").treegrid('reload');
				} else {
					$.messager.show({
						title : "错误",
						msg : result.retInfo,
						showType : 'slide',
						timeout : 1500
					});
				}
				_isSubmit=0;
			}
		});
	}

	function doEdit(rowIndex) {
		$("#resource_tree").treegrid('select', rowIndex);
		var row = $("#resource_tree").treegrid("getSelected");
		if (row) {
			$("#resource_create").dialog("close");
			$("#resource_create").dialog("open").dialog("setTitle", "修改资源信息");
			$("#resource_form").form("clear");
			$("#resource_form").form("load", row);
			$("#create_name").val(row.text);
			url = "resource_update?id=" + rowIndex + "&appId=" + _appId;
		}
	}

	function deleteResource() {
		var node = $("#resource_tree").treegrid("getSelected");
		if (node == null) {
			$.messager.show({
				title : '错误',
				msg : '请选择一条记录',
				timeout : 1000
			});
			return;
		}
		if (node) {
			$.messager.confirm("删除", "确认删除？", function(r) {
				if (r) {
					$.post("resource_delete", {
						id : node.id
					}, function(result) {
						if ("0" == result.ret) {
							$("#resource_tree").treegrid('reload');
						} else {
							$.messager.show({
								title : '错误',
								msg : result.retInfo
							});
						}
					}, "json");
				}
			});
		}
	}
	function toTop(){
		$('#resource_parentId').val("");
		$('#resource_parentName').val("");
	}
</script>
</html>