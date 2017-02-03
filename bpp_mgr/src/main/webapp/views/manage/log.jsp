<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ include file="../resource/resource.jsp"%>
<html>
<head>
</head>
<script type="text/javascript">

$.extend($.fn.datagrid.defaults, {

	loadFilter: function (data) {
		if (data.results) {
		    data['rows'] = data.results;
		    data['total'] = data.totalCount;
			return data;
	    } else {
			return data;
	    }
	 }
});

</script>
<body>
	<div class="frame-content">
			<table id="list_data" width="100%" title="日志列表" url="log_list" fit="true" toolbar="#toolbar" fitColumns="true" 
				class="easyui-datagrid"  striped="true"  rownumbers="true" pageList="[20,50,100]" pagination="true">
				<thead>
					<tr>
						<th field="u_account"  width="10%">登录账号</th>
						<th field="u_name"  width="10%">用户名</th>
						<th field="ip"  width="10%">ip地址</th>
						<th field="operationtime" formatter="formatTimebox" width="15%">操作时间</th>
						<th field="content" width="50%" formatter="formatContent">操作内容</th>
					</tr>
				</thead>
			</table>
	</div>
	<div id="dlg-buttons">
	 <div  style="padding:1px 1px 2px 0px"  class="divborder"> 
		<a href="#" class="easyui-linkbutton" iconCls="icon-save" onclick="doSave()" plain="true">保存</a>
		</div>
	</div>
	<div id="toolbar">
		<div  style="padding:5px"  class="divborder"> 
			<!-- 
			<a href="#" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="create_add();">新增</a> 
			<a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="delete_del();">删除</a>
			 -->
		</div>
		<div  style="padding:3px"  class="divborder"> 
		<span> 登录账号：</span><input id="u_account" class="easyui-textbox" type="text" style="width:140px;">&nbsp;&nbsp;
		<span> 用户名：</span><input id="u_name" class="easyui-textbox" type="text" style="width:140px;">&nbsp;&nbsp;
		<span> 操作内容：</span><input id="content" class="easyui-textbox" type="text" style="width:140px;">&nbsp;&nbsp;
		<span> 操作时间：</span><input id="startTime" class="easyui-datetimebox" type="text" data-options="showSeconds:false" style="width:140px;">至<input id="endTime" class="easyui-datetimebox" type="text" data-options="showSeconds:false" style="width:140px;">
		<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="doSearch()" >查询</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-delete"  plain="true" onclick="cleardoSearch()">清除</a>
		</div> 
	</div>
</body>

<script type="text/javascript">
	var _appId;
	//0未进行提交操作,1正在进行提交操作
	var _isSubmit = 0;
	
	//查询
	function doSearch() {
		var startTime = $("#startTime").datebox('getValue');
		var endTime = $("#endTime").datebox('getValue');
		
		if(endTime!=null && endTime!="" && startTime!=null &&  startTime!="" && endTime  < startTime) {
			$.messager.show({title: '警告',msg: "开始时间不能大于结束时间"});
			return;
		}
		
		var params = {
			'u_account':$("#u_account").val(),
			'u_name':$("#u_name").val(),
			'content':$("#content").val(),
			'startTime':startTime,
			'endTime':endTime
		};
		$('#list_data').datagrid('load',params);
	}
	
	function cleardoSearch() {
		$("#u_account").val("");
		$("#u_name").val("");
		$("#content").val("");
		$("#startTime").datebox('setValue',"");
		$("#endTime").datebox('setValue',"");
	}
	
	function formatContent (value, row, index){
		var show = value;
		if(value.length>=80){
			show = value.substring(0,80);
		}
		//return show;
		return "<a title='"+value+"'>"+show+"</a>";
		//return "<a onclick='showLog(1);' >"+show+"</a>";
		//return "<a onclick='showLog(encodeURIComponent("+ value + "))'>"+show+"</a>";
	}
	
	function showLog(value){
		alert(value);
	}
	
</script>
</html>