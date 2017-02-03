<%@ page pageEncoding="utf-8"%>
<% 
  String path =request.getContextPath();
  String entityChName="用户";
  String entity="user";
%>
<html>
<jsp:include page="../resource/resource.jsp"></jsp:include>
<link rel="stylesheet" type="text/css" href="<%=path %>/resources/css/dialog.css">
<script type="text/javascript" src="<%=path %>/resources/js/easyui/extends/easyui-dateformatter.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/common/common.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/mygrid.js"></script>
<head>
<style>
.icon-password {
	background: url(../resource/images/key.png) no-repeat;
}

.icon-user {
	background: url(../resource/images/group-small.png) no-repeat;
}

.icon-auth {
	background: url(../resource/images/auth.png) no-repeat;
}

.divborder {
	border: 1px solid #DBDBDB;
		margin: 1px 0px 1px 0px;
}

</style>
</head>
<body>
	<div class="frame-content">
		<div style="width:14%;height:100%;float:left">
			<table id="list_org" class="easyui-datagrid" title="组织列表" url="org_list" singleSelect="true" fitColumns="true"
				data-options="onSelect:selectOrg, onLoadSuccess:initSelectOrgRow" striped="true" fit=true>
				<thead>
					<tr>
						<th field="name" width="100">组织名称</th>
					</tr>
				</thead>
			</table>
		</div>

		<div style="width:85%;height:100%;float:right">
			<table id="list_data" class="easyui-datagrid" title="用户管理" toolbar="#toolbar" singleSelect="false" 
				fitColumns="true" rownumbers="true" striped="true" fit=true>
				<thead>
					<tr>
						<th field="ck" checkbox="true"></th>
						<th field="name" width="100" formatter="nameFormatter">用户名称</th>
						<th field="loginName" width="100">登录名称</th>
						<th field="orgName" width="100">组织名称</th>
<!-- 						<th field="areaName" width="100">区域</th> -->
						<th field="manager" width="100" formatter="booleanFormatter">管理员</th>
						<!-- 
						<th field="caozuo" width="100" formatter="caozuo">操作</th>
						 -->
					</tr>
				</thead>
			</table>
		</div>
	</div>

	<div id="toolbar" > 
	 <div  style="padding:5px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="javascript:createUser();">新增</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="javascript:deleteUser()">删除</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-password" plain="true" onclick="javascript:setPassword()">密码</a> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-user" plain="true" onclick="javascript:setRole()">角色</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-auth" plain="true" onclick="userAuth()">权限</a> 	
		</div> 
	
		 <div  style="padding:5px"  class="divborder"> 
			<span> 用户名称:</span><input id="name" type="text">
			<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="doSearch()" >查询</a>
			<a href="#" class="easyui-linkbutton" iconCls="icon-no"  plain="true" onclick="cleardoSearch()">清除</a>
		</div> 
	</div>

	<div id="dlg_create" class="easyui-dialog" style="width:450px;height:300px;padding:20px 10px" closed="true" modal="true" toolbar="#dlg-buttons">
		<form id="dlg_form" method="post">
			<input type="hidden" id="orgId" name="orgId">
			<table class="input">
				<tr>
					<th width="30%" height=30>用户组织</th>
					<td width="70%" id=orgName>
				<tr>
					<th>用户名称</th>
					<td class="color"><input type="text" name="name" class="easyui-validatebox" required='true' style="width:150px" data-options="validType:'length[1,255]'">
					<span style="color:red;">1~255位</span>
				<tr>
					<th>登录名称</th>
					<td><input id="loginName" type="text" name="loginName" class="easyui-validatebox" required='true' style="width:150px" data-options="validType:'length[1,255]'" onkeyup="checkBlank(this.value)">
					<span style="color:red;">1~255位</span>
				<tr>
					<th>管理员</th>
					<td class="color"><select id="manager" class="easyui-combobox" name="manager" style="width:150px" panelHeight="42px" 
					data-options="editable:false,valueField: 'code', textField: 'name'"><option value="1">是<option value="0">否</select>
			</table>
			<br>
			  <span style="color:red;margin:10px 10px">提示：默认情况下，用户的初始密码为111111</span>
		</form>
		<div id="dlg-buttons">
		 <div  style="padding:1px 1px 1px 0px"  class="divborder"> 
			<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="saveUser()" plain="true">保存</a>
		</div></div>
	</div>

	<div id="dlg_password" class="easyui-dialog" style="width:400px;height:150px;padding:20px 10px" closed="true" modal="true" toolbar="#password-buttons">
		<form id="form_password" method="post">
			<table class="input">
				<tr>
					<th width="20%">密码</th>
					<td>
						<input id="password" type="password" name="password" class="easyui-validatebox" required='true' onkeyup="checkBlank(this.value)" data-options="validType:'length[1,255]'"> 
						<span>1~255位且不含空格</span>
					</td>
				</tr>
				<tr>
					<th width="20%">确认密码</th>
					<td>
						<input id="password2" type="password" name="password2" class="easyui-validatebox" required='true' onkeyup="checkBlank(this.value)" data-options="validType:'length[1,255]'"> 
						<span>1~255位且不含空格</span>
					</td>
				</tr>
			</table>
		</form>
		<div id="password-buttons">
			<div style="padding: 1px 1px 1px 0px" class="divborder">
				<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="savePassword()" plain="true">保存</a>
			</div>
		</div>
	</div>
	<div id="dlg_role" class="easyui-dialog" style="width:400px;height:500px;overflow:hidden" closed="true" modal="true">
		<div id="roleToolbar" class="datagrid-toolbar">
				 <div  style="padding:1px 1px 1px 0px"  class="divborder"> 
			<a href="#" iconCls="icon-save" plain="true" class="easyui-linkbutton" onclick="javasript:getRoleChecked()">保存</a>
		</div>	</div>
		<table id="role_data" class="easyui-datagrid" singleSelect="false"
			toolbar="#roleToolbar" fitColumns="true" rownumbers="true" striped="true" fit=true border=false>
			<thead>
				<tr>
					<th data-options="field:'id',checkbox:true"></th>
					<th field="name" width="100px">角色名称</th>
				</tr>
			</thead>
		</table>
		<form id="relate_roleform" action="relate_change" method="post">
			<input id="relate_roleId" name="relatedIds" hidden="hidden" />
			<input id="relate_userId" name="objectId" hidden="hidden" /> 
			<input id="relate_orgId" name="typeId" hidden="hidden" />
		</form>
	</div>
	<div id="relate_site_dialog" class="easyui-dialog" style="padding:10px 20px" data-options="modal:true,closed:true,width:800,height:500"></div>
	<div id="relate_area_dialog" class="easyui-dialog" style="padding:10px 20px" data-options="modal:true,closed:true,width:800,height:500"></div>
	<jsp:include page="relate.jsp" />
	
	<script type="text/javascript">
		var globalWebAppURL = "<%=path %>";
		var _orgId;
		function createUser() {
			doCreate('用户', 'user_create', addUser);
		}

		function deleteUser() {
			doDelete('用户', 'user_delete');
		}
		function saveUser(){
		    $('#dlg_form').form('submit',{
		        url: _url,
		        onSubmit: function(){
		        	var loginName = $('#loginName').val();
		        	if(!checkBlank(loginName)){
						$.messager.show({
							title : '提示',
							msg : '登录名称不能包含空格',
							timeout : 1500
						});
		        		return false;
		        	}
		        	if($("#manager").combobox("getValue")==""){
		        		$.messager.show({
							title : '提示',
							msg : '请选择管理员',
							timeout : 1500
						});
		        		return false;
		        	}
		            return $(this).form('validate');
		        },
		        success: function(result){
		            var result = eval('('+result+')');
		            if ("0" == result.ret){
		            	$('#dlg_create').dialog('close');
		                $('#list_data').datagrid('reload');
		                $.messager.show({
		                    title: '成功',
		                    msg: '保存成功'
		                });
		            } else {
		                $.messager.show({
		                    title: '错误',
		                    msg: result.retInfo
		                });
		            }
		        }
		    });
		}
		
		function addUser() {
			var row = $("#list_org").datagrid("getSelected");
			$('#orgId').val(row.id);
			$('#orgName').html(row.name);
			var row_user = $("#list_data").datagrid("getSelected");
			
			$('#area').combobox({
				panelHeight	: 'auto',
				valueField	: 'code',
				textField	: 'name',
				editable    : false,
				url		    : globalWebAppURL+'/rsarea/listall',
				onLoadSuccess: function () { 
					 var data = $('#area').combobox('getData');
		             if (data.length > 0) {
		                 $('#area').combobox('select', data[0].code);
		             } 
					}
			});
		}
		
		function editUser() {
			var row = $("#list_org").datagrid("getSelected");
			$('#orgId').val(row.id);
			$('#orgName').html(row.name);
			var row_user = $("#list_data").datagrid("getSelected");
			$("#manager").combobox('setValue', row_user.manager == "1" ? "是" : "否");
			
			$('#area').combobox({
				panelHeight	: 'auto',
				valueField	: 'code',
				textField	: 'name',
				editable    : false,
				url		    : globalWebAppURL+'/rsarea/listall',
				onLoadSuccess: function () { 
					 var data = $('#area').combobox('getData');
		             if (data.length > 0) {
		                 $('#area').combobox('select', row_user.area);
		             } 
					}
			});

		}

		function nameFormatter(value, row, index) {
			return '<a href="#" onclick=doEdit("' + index
					+ '","用户","user_update",editUser)>' + value + '</a>';
		}
		
		
		
		function gl(url){
//			location.href=url;
			$("#relate_site_dialog").dialog({
				href:url,
				title : '关联站点',
				onClose : function(){
					$("#list_data").datagrid("reload");
				}
			}).dialog("open");
		} 
		function qy(url){
//			location.href=url;
			$("#relate_area_dialog").dialog({
				href:url,
				title : '所属区域',
				onClose : function(){
					$("#list_data").datagrid("reload");
				}
			}).dialog("open");
		}
		
	 function caozuo(value, row, index) {
			var actions='';
			var relateSite=globalWebAppURL+'/rsadplace/relateUsSiteList?userId='+row.id;
			return actions;
			
		} 
		
		function initSelectOrgRow() {
			$('#list_org').datagrid('selectRow', 0);
			var row = $('#list_org').datagrid('getSelected');
			_orgId = row.id;
		}

		function selectOrg(index, row) {
			_orgId = row.id;
			$("#name").val("");
			$('#loginName').val("");
			$('#list_data').datagrid({
				url : 'user_list',
				queryParams : {
					orgId : row.id
				}
			});
		}

		$(function(){
			$('#resource_tree').tree({
				checkbox:false,
				formatter:function(node){
					return node.checked?node.text:'<span style="color:red">'+node.text+'</span>';
				}
			});
		});
		function userAuth() {
			var row = $('#list_data').datagrid('getSelected');
			if (row == null) {
				$.messager.show({
					title : '错误',
					msg : '请选择一个用户',
					timeout : 1000
				});
				return;
			}
			_resourceUrl = 'resource_getUserResources?userId='+row.id+'&appId=';
			initSelectRow();
			$('#relate').dialog({
				title : "用户["+row.name+"]权限查看",
				height: 460
			});
			$('#relate').dialog("open");
		}
		
		function booleanFormatter(value, row, index) {
			return value=="1"?'是':'否';
		}
		
		function setPassword() {
			var row = $('#list_data').datagrid('getSelected');
			if (row == null) {
				$.messager.show({
					title : '错误',
					msg : '请选择一个用户',
					timeout : 1000
				});
				return;
			}
			$("#dlg_password").dialog("open").dialog("setTitle", "修改密码");
			$('#form_password').form('clear');
		}

		function savePassword() {
			var row = $('#list_data').datagrid('getSelected');
			$('#form_password').form('submit', {
				url : 'user_password?id=' + row.id,
				onSubmit : function() {
					var newPassword =  $('#password').val();
					var newPassword2 =  $('#password2').val();
					if(!checkBlank(newPassword)){
						$.messager.show({title : '提示', msg : '密码不能包含空格', timeout : 2000});
						return false;
					}
					
					if(!checkBlank(newPassword2)){
						$.messager.show({title : '提示', msg : '确认密码不能包含空格', timeout : 2000});
						return false;
					}
					
					if(newPassword!=newPassword2){
						$.messager.show({title : '提示', msg : '两次密码不一致', timeout : 2000});
						return false;
					}
					
					return $(this).form('validate');
				},
				success : function(result) {
					var result = eval('(' + result + ')');
					if(result.ret==0){
						
						$.messager.show({
							title : '提示',
							msg : '密码保存成功',
							timeout : 2000
						});
						
					}else{
						
						$.messager.show({
							title : '提示',
							msg : '密码保存失败',
							timeout :2000
						});
					}
					
					$('#dlg_password').dialog('close');
				}
			});
		}
		var _userId;
		function setRole() {
			var row = $('#list_data').datagrid('getSelected');
			if (row == null) {
				$.messager.show({
					title : '错误',
					msg : '请先选择一个用户',
					timeout : 1000
				});
				return;
			}
			_userId = row.id;
			_orgId = row.orgId;
			$('#dlg_role').dialog("open").dialog('setTitle', '设置角色');
			$('#role_data').datagrid({
				url : 'role_listByUserId',
				queryParams : {
					userId : _userId
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
			$("#relate_roleform").form("clear");
			$("#relate_roleId").val(s);
			$("#relate_userId").val(_userId);
			$("#relate_orgId").val(_orgId);
			$("#relate_roleform").form("submit", {
				success : function(result) {
					var result = eval('(' + result + ')');
					$.messager.show({
						title : '提示',
						msg : result.retInfo
					});
				}
			});
			$("#relate_roleform").form("clear");
			$('#dlg_role').dialog('close');
		}
		//查询
		function doSearch() {
			var params = {
				'orgId' : _orgId,
				'name' : $("#name").val()
			};
			$('#list_data').datagrid('load', params);
		}
		
		function cleardoSearch() {
			$("#name").val("");
		}
	
		
		(function($) {
			var myvalidate = {
				isNum : {
					validator : function(value) {
						return !isNaN(value);
					},
					message : "请输入数字！"
				}

			};

			$.extend($.fn.validatebox.defaults.rules, myvalidate);

		})(jQuery);
		
		function checkBlank(data){
			if(data==undefined){
				return false;
			}
			var   valid=/\s/;   
			if(valid.test(data)){
				return false;
			}
			return true;
		}
	</script>
</body>
</html>