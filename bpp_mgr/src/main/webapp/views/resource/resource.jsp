<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<% 
  String path =request.getContextPath();
%>
<link rel="stylesheet" type="text/css" href="<%=path %>/resources/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=path %>/resources/js/easyui/themes/icon.css">
<script type="text/javascript" src="<%=path %>/resources/js/easyui/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/easyui/extends/easyui-extends-dateformatter.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/com.js"></script>
<script type="text/javascript" src="<%=path %>/resources/js/json2.js"></script>
<style>
body{
	overflow:hidden;
}
table.input{
	border-collapse:collapse;width:100%;font-size:12px;
}
table.input input{
	border:1px solid #AAA;
}
table.input td,table.input th{
	border:1px solid #96b7e0;padding:3px;
}
table.input th{
	background-color:#E6F0F6;white-space:nowrap;
}
table.input td.color{
	background-color:#F7FAFF;
}
div.frame-content{
	margin:10px 0px;height:100%;
}
a{
	text-decoration:none;color:blue;
}
</style>
<script>
$(document).keydown(function(e) {
	var code = e.keyCode ? e.keyCode : e.which;
	if (code == 27) {
		var dialog = $('.easyui-dialog:visible:last');
		if(dialog.length > 0){			
			dialog.dialog('close');
		}
	}
});

var _url;
function doCreate(name,url,createFunc) {
	$('#dlg_create').dialog('open').dialog('setTitle','新增'+name);
    $('#dlg_form').form('clear');
    _url = url;
    if(createFunc){
    	createFunc();
    }
}

function doEdit(rowIndex, name, url, editFunc){
	$('#list_data').datagrid('selectRow', rowIndex);
    var row = $("#list_data").datagrid("getSelected");
    if(row){
        $("#dlg_create").dialog("open").dialog("setTitle","修改"+name);
        $('#dlg_form').form('clear');
        $("#dlg_form").form("load",row);
		_url = url+"?id="+row.id;
		if(editFunc){
			editFunc(row);
		}
    }
}

function doSave(){
    $('#dlg_form').form('submit',{
        url: _url,
        onSubmit: function(){
            return $(this).form('validate');
        },
        success: function(result){
            var result = eval('('+result+')');
            if ("0" == result.ret){
            	$('#dlg_create').dialog('close');
                $('#list_data').datagrid('reload');
                $.messager.show({
                    title: '提示',
                    msg: '保存成功'
                });
            } else {
                $.messager.show({
                    title: 'Error',
                    msg: result.retInfo
                });
            }
        }
    });
}

function doDelete(name, url){
    var rows = $('#list_data').datagrid('getSelections');
	if(rows == null || rows.length==0){
		$.messager.show({title:'错误',msg:'请选择一个'+name,timeout:1000});
		return;
	}
    if (rows){
		$.messager.confirm('删除','该操作不可恢复，是否删除？',function(r){
            if (r){
            	for (var i=0 ; i < rows.length; i++){
            		var row = rows[i];
	                $.post(url,{id:row.id},function(result){
	                    if ("0" == result.ret){
	                        $('#list_data').datagrid('reload');
	                        $('#dg_program').datagrid('unselectAll');
	                        $.messager.show({
	                            title: '提示',
	                            msg: '删除成功'
	                        });
	                    } else {
	                        $.messager.show({
	                            title: 'Error',
	                            msg: result.retInfo
	                        });
	                    }
	                },'json');
            	}
            }
        });
    }
}
</script>