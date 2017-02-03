<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<% 
  String path =request.getContextPath();
  String entityChName="角色管理";
  String entity="manage";
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="jquery,ui,easy,easyui,web">
    <meta name="description" content="">
	<title><%=entityChName %>编辑页面</title>

</head>
<body>
	<form id="entityForm" name="entityForm" method="post">
		<table class="mytable">
			<tr>
				<th width="15%"><%=entityChName %>角色名称:</th>
				<td colspan="3">
					<input type="hidden" id="id" name="id" value="${id}"/>
					<input class="easyui-validatebox" style="width:300px;" type="text" id="entityName" name="name" data-options="required:true,validType:'length[1,255]'" />
					<span class="span_require_tip">*</span>
				</td>
			</tr>
			<tr>
				<th><%=entityChName %>角色描述：</th>
				<td colspan="3">
					<textarea name="remark" cols=35 rows=4 class="easyui-validatebox" data-options="validType:'length[0,255]'">
				</td>
			</tr>
			
		</table>
		
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
	
	<script type="text/javascript">
		//全局URL
		var globalWebAppURL = "<%=path %>";
		
		$(function(){
			var id = "${id}";
			if (id == undefined || id.trim() == "") {
				entity.initAddForm();
			} else {
				entity.initEditForm();
			}
		});
	
		$("#customType").bind("click", function()
		{

			var check = $(this).attr("checked");

			if (check)
			{
				$('#networkTypeott').attr("checked", true);
				$('#networkTypedvb').attr("checked", false);
				$('#networkTypeott').attr("disabled", true);
				$('#networkTypedvb').attr("disabled", true);

				$('#sourceUrlTr').css("display", "");
				$('#videoQualityTr').css("display", "none");
				$('#NetworkIdTr').css("display", "none");
				$('#recordAreaTr').css("display", "none");
				$('#tsIdTr').css("display", "none");
			} else
			{
				$('#networkTypeott').removeAttr("checked");
				$('#networkTypedvb').removeAttr("checked");
				$('#networkTypeott').attr("disabled", false);
				$('#networkTypedvb').attr("disabled", false);

				$('#sourceUrlTr').css("display", "none");
				$('#videoQualityTr').css("display", "");
				$('#NetworkIdTr').css("display", "");
				$('#recordAreaTr').css("display", "");
				$('#tsIdTr').css("display", "");
			}

		});
	</script>
</body>
</html>