<%@ page pageEncoding="utf-8"%>
<%@ include file="../resource/resource.jsp" %> 

<style>
.divborder {
	border: 1px solid #DBDBDB;
		margin: 1px 0px 1px 0px;
}
</style>
<html>
<head>
</head>
<body>
	<div class="frame-content">
		<table id="list_data" class="easyui-datagrid" title="应用列表" url="app_list" toolbar="#toolbar" singleSelect="false"
			fitColumns="true" rownumbers="true" striped="true" pageSize="20" fit=true>
			<thead>
				<tr>
					<th field="ck" checkbox="true"></th>
					<th field="name" width="100" formatter="nameFormatter">应用名称</th>
					<th field="code" width="100">应用编码</th>
				</tr>
			</thead>
		</table>
	</div>

	<div id="toolbar" >
	 <div  style="padding:5px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true"onclick="createApp()">新增</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="deleteApp()">删除</a>
	</div>
	</div>

	<div id="dlg_create" class="easyui-dialog" style="width:400px;height:180px;padding:10px 20px" closed="true"
		modal="true" toolbar="#dlg-buttons">
		<form id="dlg_form" method="post" enctype="multipart/form-data">
			<table class="input">
				<tr>
					<th width="30%">应用名称</th>
					<td width="70%"><input type="text" name="name" class="easyui-validatebox" required='true' data-options="validType:'length[1,255]'">
				<span style="color:red;">1~255位</span>
				<tr>
					<th>应用编码</th>
					<td class="color"><input type="text" name="code" class="easyui-validatebox" data-options="validType:'length[1,255]'"/>
			<span style="color:red;">0~255位</span>
			</table>
		</form>
	</div>

	<div id="dlg-buttons">
	 <div  style="padding:1px 0px 1px 0px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-save"
			onclick="doSave()" plain="true">保存</a>
	</div>	</div>
	<script type="text/javascript">
		function createApp() {
			doCreate('应用', 'app_create');
		}

		function deleteApp() {
			doDelete('应用', 'app_delete');
		}

		function nameFormatter(value, row, index) {
			return '<a href="#" onclick=doEdit("' + index + '","应用","app_update")>' + value + '</a>';
		}
	</script>
</body>
</html>