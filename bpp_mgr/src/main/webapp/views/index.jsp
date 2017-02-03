<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
  String path =request.getContextPath();
%>
<html>
<head>
	<meta charset="UTF-8">
	<title>华数消息系统管理平台</title>
	<link rel="shortcut icon" href="<c:url value='/resources/images/index_logo.ico' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/js/easyui/themes/default/easyui.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/js/easyui/themes/icon.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/index.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/dialog.css'/>"/>
	<style>
		#moduleDiv .List1 {
			float: left;
			text-align: center;
			list-style-type: none;
			height: 30px;
			position: relative;
			left: 0px;
			top: 17px;
			/* border: 1px solid #C9C9C9; */
		}

		#moduleDiv .List1 li {
			width: 140px;
			float: left;
			height: 30px;
			line-height: 20px;
			/* border-radius: 16px; */
			position: relative;
			top: -10px;
			vertical-align: middle;
			
		}
	
		.head a {
			color: rgb(4, 4, 4);
			text-decoration: underline;
		}		
		body {
		background: #FFFFFF !important;
		}
			
	</style>
	<script type="text/javascript" src="<c:url value='/resources/js/easyui/jquery-1.8.3.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/easyui/jquery.easyui.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/easyui/locale/easyui-lang-zh_CN.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/common/common.js' />"></script>
</head>
<body class="easyui-layout" style="overflow-y: hidden"  scroll="no">
	<div region="center" id="center" style="background: #eee; overflow-y:hidden">	
	</div>
	
	<div region="north" split="true" border="false" style="overflow: hidden; height: 65px; line-height: 60px; color: rgb(4, 4, 4);font-family: Verdana, 微软雅黑, 黑体; width: 1680px; border-bottom: 5px solid #4687c3;background-color: rgb(227, 238, 255);">
		<span style="float: right;padding-right: 20px;height: 60px;padding-top: 11px;" class="head">
		  用户：${sessionScope.loginUser.name}
	      <input type="hidden" id="userId" value="${sessionScope.loginUser.loginName}" />			  
		  │<a href="#" id="editpass" onclick="openPassword()">修改密码</a>  
		  │<a href="<c:url value='/logout' />" id="logout"><img src="<c:url value='/resources/images/bg_logout.gif' />">注销</a>
	  	</span>
		<strong style="font-size: 20px;float: left;height: 75px;">
		    <img src= "<c:url value='/resources/images/wasu_logo.png' />" style="position: relative;width:100px;height: 60px;top: 0px;">
		</strong>
		<div id="moduleDiv" style="margin: 5 10 5 150"></div>
	</div>
	<div region="south" data-options="border:true" style="height:50px;padding:10px;text-align:center">Copyright © 2016 wasu.com Inc. All Rights Reserved. 华数传媒网络有限公司  版权所有  &nbsp;&nbsp;&nbsp;产品版本：iMMSV100R001C01B01</div>
    <div id="editPassWordDiv" class="easyui-dialog" title="修改密码" data-options="modal:true,closed:true,iconCls:'icon-edit',tools:'#tt',buttons:'#editPasswodBtn'" style="width:400px;height:220px;">
    	<form id="editPassWordForm" method="post" >
	    	<table align="center" style="font-size: 12px;">
	    		<tr>
					<td align="right" style="font-weight: bold;">旧密码:</td>
					<td><input class="easyui-validatebox" type="password" id="oldPassword" name="oldPassword" required="required" /></td>
				</tr>
	    		<tr>
	    			<td align="right" style="font-weight: bold;">新密码:</td>
	    			<td>
	    				<input class="easyui-validatebox" type="password" id="newPassword" name="newPassword"  required="required" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td align="right" style="font-weight: bold;">确认新密码:</td>
	    			<td>
	    				<input class="easyui-validatebox" type="password" id="newPassword2" name="newPassword2"  required="required" />
	    			</td>
	    		</tr>
	    		<tr>
	    			<td colspan="2">
	    				<div id="checkPwdSpan" style="color:red;text-align: center;height:20px;"></div>
	    			</td>
	    		</tr>
	    	</table>
	    	<input type="hidden" id="flag" value="false">
	    </form>
    </div>
    <div id="editPasswodBtn" style="text-align:center;padding:5px;display:none;">
	    <a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="submitForm()">修改</a>
	    <a href="javascript:void(0)" class="easyui-linkbutton" iconcls="icon-delete" onclick="clearForm()">清除</a>
	</div>
    <script type="text/javascript">
    	$(function(){
    		var moduleJson;
    		var moduleContent="<ul class='List1'>";
    		$.ajax({
    			type:"post",
    			url:"<%=path%>/module",
    			data:null,
    			async:false,
    			success:function(data){
    				moduleJson = data;
    			}
    		});	
		
    		var obj = moduleJson;
    		var defaultModule = "${sessionScope.module}";
    		for ( var p in obj ){ //方法
    			if ( typeof ( obj[p]) != "function" ){
    				var hrefStr;
    				//p 为属性名称，obj[p]为对应属性的值

					$("#center").append('<iframe id=' + p + ' width="100%" height="100%" style="margin:0;border:0;" hidden="true"></iframe>');
    				
    				if (defaultModule == p) {					
    					hrefStr = "<li id='li_"+ p +"' class='select'><a href='#'  onclick='loadModule(\"" + p + "\")' style='top: 5px;position: relative;'>" + obj[p] + "</a></li>";
    				} else {					
    					hrefStr = "<li id='li_"+ p +"'><a href='#'  onclick='loadModule(\"" + p + "\")' style='top: 5px;position: relative;'>" + obj[p] + "</a></li>";
    				}
    				moduleContent += hrefStr + " "; 
    			} 
    		}			
			moduleContent+= "</ul>"
    		$("#moduleDiv").html(moduleContent);
    		loadModule(defaultModule);
    	});
    	
    	function loadModule(module) {
    		$("iframe").attr("hidden", true);
    		$("#"+module).attr("hidden", false);
    		var src = $("#"+module).attr("src");
    		if (src == undefined || src == "") {
    			src = "<%=path%>/content?module=" + module;
    			$("#"+module).attr("src", src);
    		}
    		
    		$("#moduleDiv ul li").removeClass('select');
    		$("#li_"+module).addClass('select');
			$.ajax({
				url:"<%=path%>/changeModule?module="+module,
				type:"post",
				success:function(data){
					if(!data.success) {
     					common.tipMessager('show', data.message, 3000);
					}
        		 }
        	 });
    	}
    	
        function openPassword(){
             $("#editPassWordDiv").window('open');
             $('#editPassWordForm').form('clear');
             $('#checkPwdSpan').text('');
        }
        
		function submitForm(){
		     var oldp = $("#oldPassword").val();
             var newp = $("#newPassword").val();
             var newp2 = $("#newPassword2").val();
             var userId = $("#userId").val();
             var flag=$('#flag').val();
             if(!checkPwd(oldp, "旧密码") || !checkPwd(newp, "新密码") || !checkPwd(newp2, "确认新密码")){
            	 return;
             }
             
             if(newp!=newp2){
             	$('#checkPwdSpan').text("两次新密码不一致")
            	 return;
             }
             
             if(flag=="false"){
            	 $('#checkPwdSpan').text("旧密码不正确")
            	 return;
             }
             
        	 $.ajax({
        		 url:"<%=path%>/editPassword",
        		 type:"post",
        		 data:{"oldPassword":oldp,"newPassword":newp},
        		 dataType:"json",
        		 success:function(data){
 					console.log(data);
        			if(data.success){
        			 $.messager.show({
 	                    title: '提示',
 	                    msg: '修改成功'
 	                });
					 $("#editPassWordDiv").window('close');
        			}else{
        			 $.messager.show({
 	                    title: '提示',
 	                    msg: data.message
 	                });
        			}
        		 },error:function(data){
        			 $.messager.alert('提示',msg,'error');
        		 }
        		 
        	 });
		}
		
		function clearForm(){
			$('#editPassWordForm').form('clear');
			$('#checkPwdSpan').text("");
		}
		
		function checkPwd(pwd, tip){
			if(pwd==undefined||pwd==''){
				$('#checkPwdSpan').text(tip+"不能为空");
				return false;
			}
			
			var valid=/\s/;
			if(valid.test(pwd)){
				$('#checkPwdSpan').text(tip+"不能包含空白字符");
				return false;
			}
			
			$('#checkPwdSpan').text("");
			return true;
		}
		
		//验证旧密码
		function validationPwd(oldPwd){
			if(!checkPwd(oldPwd, "旧密码")){
				return;
			}
			
			$.ajax({
				url:"<%=path%>/validationPassWord",
				type:"post",
				data:"password="+oldPwd,
				success:function(data){
					var result = JSON.stringify(data);
					if(data.success){
						$('#checkPwdSpan').text("");
						$('#flag').val("true");
					}else{
						$('#flag').val("false");
					}
				}
			});
		}
	</script>
</body>
</html>