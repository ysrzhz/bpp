<%@ page language="java" pageEncoding="UTF-8"%>
<html>
<head>
<style type="text/css">
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
</style>
</head>
<body>
<div id="relate" class="easyui-dialog" title="权限管理" style="width:800px;height:420px;padding:5px" closed="true" modal="true">
	<div style="width:20%;height:100%;float:left;">
		<table id="app_list" url="app_list" class="easyui-datagrid" singleSelect="true" title="应用列表" fitColumns="true"
			fit="true" data-options="onSelect:getResources">
			<thead>
				<tr><th field="name" width="100">应用名称</th></tr>
			</thead>
		</table>
	</div>

	<div id="div_tree" style="width:79%;height:100%;float:right;">
		<div class="easyui-panel" fit=true title="资源树">
			<div id="toolbar" class="datagrid-toolbar">
			 <div  style="padding:1px 1px 2px 0px"  class="divborder"> 
				<%if("1".equals(request.getParameter("save"))){ %>
				<a href="#" iconCls="icon-save" plain="true"  class="easyui-linkbutton" onclick="getChecked()">保存</a>
				<div style='height:25;border-left:1px solid #ccc;border-right:1px solid #fff;display:inline;margin: 0px 6px'></div>
				<%} %>
       			<a href="#" iconCls='icon-collapse' plain="true" class="easyui-linkbutton" onclick="$('#resource_tree').tree('collapseAll')">折叠</a>
        		<a href="#" iconCls='icon-expand' plain="true" class="easyui-linkbutton" onclick="$('#resource_tree').tree('expandAll')">展开</a>
				<%if("1".equals(request.getParameter("save"))){ %>
				<div style='height:25;border-left:1px solid #ccc;border-right:1px solid #fff;display:inline;margin: 0px 6px'></div>
				全选<div style="display:inline;vertical-align:middle"><input title="全选/反选" type="checkbox" onClick="treeChecked(this)"/></div>  
				级联<div style="display:inline;vertical-align:middle"><input title="是否进行级联操作" id="cascadeOpt" type="checkbox" checked=true></div>  
				<div style='height:25;border-left:1px solid #ccc;border-right:1px solid #fff;display:inline;margin: 0px 6px'></div>
				<%} %>
			</div>	</div>
			<div id="resource_tree" class="easyui-tree" data-options="checkbox:true,cascadeCheck:false,onCheck:checkNode" style="margin:10px"></div>
		</div>
	</div>
	
	<form id="relate_form" action="relate_change" method="post">
		<input id="form_roleId" name="objectId" type="hidden"/>
		<input id="form_appId" name="typeId" type="hidden"/>
		<input id="form_resoucesId" name="relatedIds" type="hidden"/>
	</form>
</div>
<script type="text/javascript">
	var _appId;
	var _roleId;
	var _resourceUrl;
	var _checkEvent;
	var _loading;
	function getResources() {
		var row = $("#app_list").datagrid("getSelected");
		_appId = row.id;
		_loading = true;
		$('#resource_tree').tree({
			url : _resourceUrl + _appId,
			onLoadSuccess:function(){
				_loading = false;
			}
		});
	}
	
	function checkNode(node,checked){
		if(_checkEvent || !$('#cascadeOpt').get(0).checked || _loading){
			return;
		}
		_checkEvent = true;
		setTimeout('_checkEvent=false;	',100);
		doCheckNode(node);
	}
	
	function doCheckNode(node){
		doCheckParent(node);
		doCheckChildren(node);
	}
	
	function doCheckParent(node){
		var parent = $('#resource_tree').tree('getParent',node.target);
		if(parent&&!node.checked){
			$('#resource_tree').tree('check',parent.target);
			doCheckParent(parent);
		}
	}
	
	function doCheckChildren(node){
		try{				
			var children = $('#resource_tree').tree('getChildren',node.target);
			for (i = 0; i < children.length; i++) {  
				$('#resource_tree').tree(node.checked?'check':'uncheck',children[i].target);
			} 
		}catch(e){}
	}
	
	function initSelectRow(){
		$("#app_list").datagrid("selectRow", 0);
	}
	
	function getChecked() {
		var nodes = $('#resource_tree').tree('getChecked');
		var s = '';
		for ( var i = 0; i < nodes.length; i++) {
			if (s != ''){				
				s += ',';
			}
			s += nodes[i].id;
		}
		$("#relate_form").form("clear");
		$("#form_roleId").val(_roleId);
		$("#form_appId").val(_appId);
		$("#form_resoucesId").val(s);
		$("#relate_form").form("submit",{
			success: function(result){
				var result = eval('('+result+')');
		        $.messager.show({
					title: '提示',
					msg: result.retInfo
				});
			}
		});
		$("#relate_form").form("clear");
	}
	
	function treeChecked(selected) {
		var roots = $('#resource_tree').tree('getChildren');
		if (selected.checked) {
			for ( var i = 0; i < roots.length; i++) {
				var node = $('#resource_tree').tree('find', roots[i].id);
				$('#resource_tree').tree('check', node.target);
			}
		} else {
			for ( var i = 0; i < roots.length; i++) {
				var node = $('#resource_tree').tree('find', roots[i].id);
				$('#resource_tree').tree('uncheck', node.target);
			}
		}
	}
</script>
</body>
</html>