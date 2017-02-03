<%//@page import="com.coship.urm.common.UrmConfig"%>
<%@ page pageEncoding="utf-8"%>
<html>
<jsp:include page="../resource/resource.jsp"></jsp:include>
<head>
<style>
.icon-connect {
	background: url(../resource/images/connect.png) no-repeat;
}
.icon-param {
	background: url(../resource/images/param.png) no-repeat;
}
.icon-role{ 
	background:url(../resource/images/group-small.png) no-repeat;
}
.divborder {
	border: 1px solid #DBDBDB;
		margin: 1px 0px 1px 0px;
}
</style>
</head>
<body>
<div class="frame-content">
	<table id="list_data" class="easyui-datagrid" title="组织管理" url="org_list" toolbar="#toolbar" singleSelect="false"
		fitColumns="true" rownumbers="true" striped="true" pageSize="20" fit=true>
		<thead>
		<tr>
			<th field="ck" checkbox="true"></th>
			<th field="name" width="100" formatter="nameFormatter">组织名称</th>
			<th field="code" width="100">组织编码</th>
			<!-- th field="manager" width="100" formatter="booleanFormatter">超级组织</th-->
			<th field="remark" width="200">组织描述</th>
		</tr>
		</thead>
	</table>
</div>

<div id="toolbar" >
  <div  style="padding:5px"  class="divborder"> 
	<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="javascript:createOrg();">新增</a> 
	<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="javascript:deleteOrg()">删除</a> 
	<!-- <a href="#" class="easyui-linkbutton" iconCls="icon-connect" plain="true" onclick="javascript:connect()">关联</a> -->
	<a href="#" class="easyui-linkbutton" iconCls="icon-role" plain="true" onclick="javascript:role()">角色</a>
	<!-- <a href="#" class="easyui-linkbutton" iconCls="icon-param" plain="true" onclick="javascript:param()">参数</a> -->
  </div>
</div>

<div id="dlg_create" class="easyui-dialog" style="width:500px;height:320px;padding:20px 10px" closed="true" modal="true" toolbar="#dlg-buttons">
	<form id="dlg_form" method="post" enctype="multipart/form-data">
			<table class="input">
				<tr>
					<th width="30%">组织名称</th>
					<td width="70%"><input type="text" name="name"
						class="easyui-validatebox" required='true' data-options="validType:'length[1,255]'">
						<span style="color:red;">1~255位</span></td>
						 
				</tr> 

				<tr>
					<th>组织编码</th>
					<td class="color"><input type="text" name="code"
						class="easyui-validatebox" required='true' data-options="validType:'length[1,255]'">
						<span style="color:red;">1~255位</span></td>
				</tr>
				<tr>
					<th>组织描述</th>
					<td class="color"><textarea name="remark" cols=45 rows=4
							class="easyui-validatebox" data-options="validType:'length[0,255]'"></textarea>
						</td>
				</tr>
			</table>
		</form>
</div>

<div id="dlg_connect" class="easyui-dialog" style="width:400px;height:500px;overflow:hidden" closed="true" modal="true">
	<div id="relateToolbar" class="datagrid-toolbar">
	 <div  style="padding:1px 1px 2px 0px"  class="divborder"> 
		<a href="#" iconCls="icon-save" plain="true" class="easyui-linkbutton" onclick="javasript:getOrgChecked()">保存</a>
	</div></div>
	<table id="org_data" class="easyui-datagrid" singleSelect="false" toolbar="#relateToolbar" fitColumns="true" rownumbers="true" striped="true" fit=true border=false>
	<thead><tr>
			<th data-options="field:'id',checkbox:true"></th>
			<th field="name" width="100px">组织名称</th>
	</table>
	<form id="relate_orgform" action="relate_change" method="post">
		<input id="relate_orgId" name="objectId" hidden="hidden" /> 
		<input id="relate_orgsId" name="relatedIds" hidden="hidden" />
	</form>
</div>

<div id="dlg_role" class="easyui-dialog" style="width:400px;height:500px;overflow:hidden" closed="true" modal="true">
	<div id="roleToolbar" class="datagrid-toolbar">
	  <div  style="padding:1px 1px 2px 0px"  class="divborder"> 
		<a href="#" iconCls="icon-save" plain="true" class="easyui-linkbutton" onclick="javasript:getRoleChecked()">保存</a>
	</div>
	</div>
	<table id="role_data" class="easyui-datagrid" singleSelect="false" toolbar="#roleToolbar" fitColumns="true" rownumbers="true" striped="true" fit=true border=false>
	<thead><tr>
			<th data-options="field:'id',checkbox:true"></th>
			<th field="name" width="100px">角色名称</th>
	</table>
	<form id="role_orgform" action="relate_change?typeId=orgRole" method="post">
		<input id="role_orgId" name="objectId" hidden="hidden" /> 
		<input id="role_roleId" name="relatedIds" hidden="hidden" />
	</form>
</div>

<div id="dlg-buttons">
 <div  style="padding:1px 1px 2px 0px"  class="divborder"> 
	<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="doSave()" plain="true">保存</a>
	</div>
</div>

<div id="dlg_param" class="easyui-dialog" style="width:800px;height:500px;overflow:hidden" closed="true" modal="true">
	<div id="paramToolbar" class="datagrid-toolbar">
	 <div  style="padding:5px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="javascript:createParam();">新增</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="javascript:deleteParam()">删除</a> 
	</div>
		</div>
	<table id="param_data" class="easyui-datagrid" singleSelect="false" toolbar="paramToolbar" fitColumns="true" rownumbers="true" 
			striped="true" fit=true border=false nowrap="false">
	<thead>
		<tr><th field="name" width="100px" formatter="paramNameFormatter">名称</th>
			<th field="value" width="200px">值</th>
			<th field="remark" width="200px">描述</th>

	</table>
</div>
	
<div id="dlg_paramForm" class="easyui-dialog" toolbar='#paramFormToolbar' style="width:550px;height:350px;overflow:hidden;padding:10px 20px" closed="true" modal="true">
	<form id="param_form" method="post">
		<input name="id" hidden="hidden" /> 
		<input id="orgId" name="orgId" hidden="hidden" /> 
		<table class="input">
			<tr><th width="80px">名称<td>
			<select name="name" class="easyui-combobox" required='true' style="width:200px"     data-options="panelHeight: 'auto'", editable=false>
			<%/*
			for(String name : UrmConfig.getParams()){
				out.print("<option value='"+name+"'>"+name+"</option>");	
			}*/ 
			%>
			</select> 
			<tr><th>值<td><textarea name="value" id="keyvalue" cols=54 rows=5 class="easyui-validatebox" required='true' data-options="validType:'length[1,255]'"></textarea>
			<tr><th>描述<td><textarea name="remark" cols=54 rows=5 class="easyui-validatebox" data-options="validType:'length[0,255]'"></textarea>
			<tr><th colspan="2"><font color="red">注意:参数的值请输入OMS中区域的区域编号且以英文下的逗号分隔</font></th></tr>
		</table>
	</form>
</div>	

<div id="paramFormToolbar">
 <div  style="padding:1px 1px 2px 0px"  class="divborder"> 
	<a href="#" class="easyui-linkbutton" iconCls="icon-save" plain="true" onclick="saveParam()" plain="true">保存</a>
</div></div>

<script type="text/javascript">
var _orgId;

function role() {
	var row = $('#list_data').datagrid('getSelected');
	if (row == null) {
		$.messager.show({
			title : '错误',
			msg : '请选择一个组织！',
			timeout : 1000
		});
		return;
	}
	
	_orgId = row.id;
	$('#dlg_role').dialog('open').dialog('setTitle', '组织角色');
	$('#role_data').datagrid({
		url : 'role_listByOrg',
		queryParams : {
			id : _orgId
		},
		onLoadSuccess : function(data) {
			if (data) {
				$.each(data.rows, function(index, item) {
					if (item.checked) {
						$('#role_data').datagrid('checkRow', index);
					}
				});
			}
		}
	});
}
		
function getRoleChecked() {
	var nodes = $('#role_data').datagrid('getChecked');
	var s = '';
	for ( var i = 0; i < nodes.length; i++) {
		if (s != '') {
			s += ',';
		}
		s += nodes[i].id;
	}
	$("#role_orgform").form("clear");
	$("#role_orgId").val(_orgId);
	$("#role_roleId").val(s);
	$("#role_orgform").form("submit", {
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.show({
				title : '提示',
				msg : result.retInfo
			});
		}
	});
	$('#dlg_role').dialog('close');
}

function param() {
	var row = $('#list_data').datagrid('getSelected');
	if (row == null) {
		$.messager.show({
			title : '错误',
			msg : '请选择一个组织',
			timeout : 1000
		});
		return;
	}
	$('#dlg_param').dialog('open').dialog('setTitle', '参数列表');
	$('#param_data').datagrid({'url':'org_listParam?orgId='+row.id});
}

function createParam() {
	$('#dlg_paramForm').dialog('open').dialog('setTitle', '新增参数');
	$('#param_form').form('clear');
	var row = $('#list_data').datagrid('getSelected');
	$('#orgId').val(row.id);
	_url = "org_createParam";
}

function deleteParam() {
	var row = $('#param_data').datagrid('getSelected');
	if(row == null){
		$.messager.show({title:'错误',msg:'请选择一个参数',timeout:1000});
		return;
	}
	$.messager.confirm('删除','确认删除参数',function(r){
		if (!r){
			return;
		}
		$.post('org_deleteParam',{id:row.id},function(result){
			if ("0" == result.ret){
				$('#param_data').datagrid('reload');
			} else {
				$.messager.show({title: 'Error',msg: result.retInfo});
			}
		},'json');
	});
}
		
function editParam(rowIndex) {
	$('#param_data').datagrid('selectRow', rowIndex);
	var row = $("#param_data").datagrid("getSelected");
	if (row) {
		$("#dlg_paramForm").dialog("open").dialog("setTitle", "修改参数");
		$('#param_form').form('clear');
		$("#param_form").form("load", row);
		_url = "org_updateParam";
	}
}

function saveParam(){
	var value = $('#keyvalue').val();
	//理论上数字和','的长度加起来应该和值的长度一样，和小于长度值，则代表有其它不合法字符
	var len = value.length;
	var count1 = 0;
	var count2 = 0;
	for(var i = 0;i<value.length;i++)
	{
		if (!isNaN(value[i]))
		{
			count1++;
		}
		if(value.charAt(i)==",")
		{
			count2++;
		}
	}
	if((count1+count2)<len)
	{
		$.messager.show({title:'错误',msg:'参数的值请以英文下的逗号分隔',timeout:3000});
		return;
	}else{
	    $('#param_form').form('submit',{
	        url: _url,
	        onSubmit: function(){
	            return $(this).form('validate');
	        },
	        success: function(result){
	            var result = eval('('+result+')');
	            if ("0" == result.ret){
	            	$('#dlg_paramForm').dialog('close');
	                $('#param_data').datagrid('reload');
	            } else {
	                $.messager.show({
	                    title: 'Error',
	                    msg: result.retInfo
	                });
	            }
	        }
	    });
	}
}

function paramNameFormatter(value, row, index) {
	return '<a href="#" onclick=editParam("' + index + '")>' + value + '</a>';
}

function booleanFormatter(value, row, index) {
	return value=="1"?'是':'否';
}

function createOrg() {
	$('#dlg_create').dialog('open').dialog('setTitle', '新增组织');
	$('#dlg_form').form('clear');
	_url = "org_create";
}

function deleteOrg() {
	doDelete('组织', 'org_delete');
}
		
function editOrg(rowIndex, name, url) {
	$('#list_data').datagrid('selectRow', rowIndex);
	var row = $("#list_data").datagrid("getSelected");
	if (row) {
		$("#dlg_create").dialog("open").dialog("setTitle", "修改" + name);
		$('#dlg_form').form('clear');
		$("#dlg_form").form("load", row);
		$("#manager").combobox('setValue', row.manager=="1"? "是" : "否");
		_url = url + "?id=" + row.id;
	}
}
		
function nameFormatter(value, row, index) {
	return '<a href="#" onclick=editOrg("' + index + '","组织","org_update")>' + value + '</a>';
}
		
function connect() {
	var row = $('#list_data').datagrid('getSelected');
	if (row == null) {
		$.messager.show({
			title : '错误',
			msg : '请选择一个组织进行关联！',
			timeout : 1000
		});
		return;
	}
	_orgId = row.id;
	$('#dlg_connect').dialog('open').dialog('setTitle', '关联组织');
	$('#org_data').datagrid({
		url : 'org_list',
		queryParams : {
			id : _orgId
		},
		onLoadSuccess : function(data) {
			if (data) {
				$.each(data.rows, function(index, item) {
					if (item.checked) {
						$('#org_data').datagrid('checkRow', index);
					}
				});
			}
		}
	});
}
		
function getOrgChecked() {
	var nodes = $('#org_data').datagrid('getChecked');
	var s = '';
	for ( var i = 0; i < nodes.length; i++) {
		if (s != '') {
			s += ',';
		}
		s += nodes[i].id;
	}
	$("#relate_orgform").form("clear");
	$("#relate_orgId").val(_orgId);
	$("#relate_orgsId").val(s);
	$("#relate_orgform").form("submit", {
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.show({
				title : '提示',
				msg : result.retInfo
			});
		}
	});
	$("#relate_orgform").form("clear");
}
</script>
</body>
</html>