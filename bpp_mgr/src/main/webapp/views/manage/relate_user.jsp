<%@ page language="java" pageEncoding="UTF-8"%>
<html>
<head>
</head>
<body>
	<div id="user_dlg" class="easyui-dialog" title="用户角色"
		style="width:800px;height:460px;padding:5px" closed="true"
		modal="true">
		<div style="width:20%;height:100%;float:left;">
			<table id="org_list" url="org_list" class="easyui-datagrid"
				singleSelect="true" title="组织列表" fitColumns="true" fit="true"
				data-options="onSelect:getUserByOrgId">
				<thead>
					<tr>
						<th field="name" width="100">组织名称</th>
					</tr>
				</thead>
			</table>
		</div>

		<div id="div_user" style="width:79%;height:100%;float:right">
			<div id="userToolbar" class="datagrid-toolbar">
				<a href="#" iconCls="icon-save" plain="true"
					class="easyui-linkbutton" onclick="getUserChecked()">保存</a>
			</div>
			<table id="user_data" class="easyui-datagrid" title="用户列表"
				singleSelect="false" fitColumns="true" rownumbers="true"
				striped="true" fit=true toolbar="#userToolbar">
				<thead>
					<tr>
						<th data-options="field:'id',checkbox:true"></th>

						<th field="name" width="100">用户名称</th>
					</tr>
				</thead>
			</table>
		</div>
		<form id="relate_userform" action="relate_changeRelated" method="post">
			<input id="relate_objectId" name="objectId" hidden="hidden" /> 
			<input id="relate_typeId" name="typeId" hidden="hidden" /> 
			<input id="relate_relatedIds" name="relatedIds" hidden="hidden" />
		</form>
	</div>
</body>
<script type="text/javascript">
	var _orgId;

	function initSelectOrg() {
		$("#org_list").datagrid("selectRow", 0);
	}
	
	function getUserByOrgId() {
		var row = $('#org_list').datagrid("getSelected");
		if(!row){
			return;
		}
		_orgId = row.id;
		$('#user_data').datagrid(
				{
					url : 'relate_getUserRate?typeId=' + row.id + '&objectId='
							+ _roleId,
					onLoadSuccess : function(data) {
						if (data) {
							$.each(data.rows,
									function(index, item) {
										if (item.checked) {
											$('#user_data').datagrid(
													'checkRow', index);
										}
									});
						}
					}
				});
		$('#div_user').show();
	}

	function getUserChecked() {
		var nodes = $('#user_data').datagrid('getChecked');
		var s = '';
		for ( var i = 0; i < nodes.length; i++) {
			if (s != '') {
				s += ',';
			}
			s += nodes[i].id;
		}
		$("#relate_userform").form("clear");
		$("#relate_relatedIds").val(_roleId);
		$("#relate_typeId").val(_orgId);
		$("#relate_objectId").val(s);
		$("#relate_userform").form("submit",{
			success: function(result){
				var result = eval('('+result+')');
		        $.messager.show({
					title: '提示',
					msg: result.retInfo
				});
			}
		});
	}
</script>
</html>