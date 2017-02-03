<%@ page pageEncoding="utf-8"%>
<html>
<jsp:include page="../resource/resource.jsp"></jsp:include>
<head>
<style type="text/css">
.icon-auth {
	background: url(../resource/images/auth.png) no-repeat;
}

.icon-user {
	background: url(../resource/images/user.png) no-repeat;
}

.icon-org{ 
	background:url(../resource/images/org-small.png) no-repeat;
}
.divborder {
	border: 1px solid #DBDBDB;
	margin: 1px 0px 1px 0px;
}
</style>
</head>
<body>
	<div class="frame-content">
		<table id="list_data" class="easyui-datagrid" title="角色列表" url="role_list" toolbar="#toolbar" singleSelect="false"
			fitColumns="true" rownumbers="true" striped="true" pageSize="20" fit=true>
			<thead>
				<tr>
					<th field="ck" checkbox="true"></th>
					<th field="name" width="100" formatter="nameFormatter">角色名称</th>
					<th field="remark" width="100">角色描述</th>
				</tr>
			</thead>
		</table>
	</div>

	<div id="toolbar" >
	 <div  style="padding:5px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="createRole()">新增</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteRole()">删除</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-org" plain="true" onclick="org()">组织</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-user" plain="true" onclick="addUser()">用户</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-auth" plain="true" onclick="authRole()">权限</a> 
	</div>
		</div>

	<div id="dlg_create" class="easyui-dialog" style="width:420px;height:300px;padding:10px 20px" closed="true"
		modal="true" toolbar="#dlg-buttons">
		<form id="dlg_form" method="post" enctype="multipart/form-data">
			<table class="input">
				<tr>
					<th width="30%">角色名称</th>
					<td width="70%"><input type="text" name="name" class="easyui-validatebox" required='true' data-options="validType:'length[1,255]'">
				<span style="color:red;">0~255位</span>
				<tr>
					<th>角色描述</th>
					<td class="color"><textarea name="remark" cols=35 rows=4 class="easyui-validatebox" data-options="validType:'length[0,255]'"></textarea>
			</table>
		</form>
	</div>
	
	<div id="dlg-buttons">
	 <div  style="padding:1px 0px 1px 0px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="doSave()" plain="true">保存</a>
	</div></div>

	<div id="dlg_org" class="easyui-dialog" style="width:400px;height:500px;overflow:hidden" closed="true" modal="true">
		<div id="orgToolbar" class="datagrid-toolbar">
		 <div  style="padding:1px 0px 2px 0px"  class="divborder"> 
			<a href="#" iconCls="icon-save" plain="true" class="easyui-linkbutton" onclick="javasript:getOrgChecked()">保存</a>
		</div></div>
		<table id="org_data" class="easyui-datagrid" singleSelect="false" toolbar="#roleToolbar" fitColumns="true" rownumbers="true" striped="true" fit=true border=false>
		<thead><tr>
				<th data-options="field:'id',checkbox:true"></th>
				<th field="name" width="100px">角色名称</th>
		</table>
		<form id="org_orgform" action="relate_changeRelated?typeId=orgRole" method="post">
			<input id="org_orgId" name="objectId" hidden="hidden" /> 
			<input id="org_roleId" name="relatedIds" hidden="hidden" />
		</form>
	</div>

	<jsp:include page="relate.jsp">
		<jsp:param value="save" name="1"/>
	</jsp:include>
	<jsp:include page="relate_user.jsp" />

<script type="text/javascript">
	function org() {
		var row = $('#list_data').datagrid('getSelected');
		if (row == null) {
			$.messager.show({
				title : '错误',
				msg : '请选择一个角色！',
				timeout : 1000
			});
			return;
		}
		$('#dlg_org').dialog('open').dialog('setTitle', '角色组织');
		$('#org_data').datagrid({
			url : 'org_listAllByRole',
			queryParams : {
				id : row.id
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
		var row = $('#list_data').datagrid('getSelected');
		$("#org_orgform").form("clear");
		$("#org_orgId").val(s);
		$("#org_roleId").val(row.id);
		$("#org_orgform").form("submit", {
			success : function(result) {
				var result = eval('(' + result + ')');
				$.messager.show({
					title : '提示',
					msg : result.retInfo
				});
			}
		});
		$('#dlg_org').dialog('close');
	}
	
		var _roleId;
		var requestUrl = "<%=request.getContextPath()%>";
		//alert("requestUrl:"+requestUrl);
		function createRole() {
			doCreate('角色', requestUrl+'/views/manage/role_create');
		}

		function deleteRole() {
			doDelete('角色', 'role_delete');
		}

		function nameFormatter(value, row, index) {
			return '<a href="#" onclick=doEdit("' + index
					+ '","角色","role_update")>' + value + '</a>';
		}

		function addUser() {
			var row = $('#list_data').datagrid('getSelected');
			if (row == null) {
				$.messager.show({
					title : '错误',
					msg : '请选择一个角色进行授权',
					timeout : 1000
				});
				return;
			}
			_roleId = row.id;
			$('#user_dlg').dialog("open");
			$('#org_list').datagrid({url:'org_listByRole?id='+row.id,onLoadSuccess:function(){
				initSelectOrg();
			}});
		}

		function authRole() {
			var row = $('#list_data').datagrid('getSelected');
			if (row == null) {
				$.messager.show({
					title : '错误',
					msg : '请选择一个角色进行授权',
					timeout : 1000
				});
				return;
			}
			_roleId = row.id;
			_resourceUrl = 'relate_getRelate?objectId='+row.id+'&typeId=';
			initSelectRow();
			$('#relate').dialog("setTitle", "角色["+row.name+']权限设置');
			$('#relate').dialog("open");
		}
	</script>
</body>
</html>