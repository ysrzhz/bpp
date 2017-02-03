<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% 
  String path =request.getContextPath();
%>
<html>
<head>
	<meta charset="UTF-8">
	<title>华数消息系统管理平台</title>
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/js/easyui/themes/default/easyui.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/js/easyui/themes/icon.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/index.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/resources/css/contentstyle.css' />" />
	<script type="text/javascript">
		var module = "${module}";
		var globalWebAppURL= "<%=path%>";
	</script>
	<script type="text/javascript" src="<c:url value='/resources/js/easyui/jquery-1.8.3.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/easyui/jquery.easyui.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/easyui/locale/easyui-lang-zh_CN.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/common/common.js' />"></script>
	<script type="text/javascript" src="<c:url value='/resources/js/outlook.js' />"></script>
    <script type="text/javascript">  
        var createFlow = function(){  
            $("#spanBefore").flow({hover:function(){  
                $(this).addClass("hover");  
            },remove:function(){  
                $(this).removeClass("hover");  
            },click:function(){  
                alert($(this).attr("id") + "->" + $(this).attr("next") + " Click");  
            }});  
        }  
    </script>  
</head>
<body>
	<div class="easyui-layout" fit="true">
		<div region="west" id="west" hide="true" split="true" title="导航菜单" style="width:180px;">
			<div id='wnav' class="easyui-accordion" fit="false" border="false" ></div>
			<ul id="css3menu" style="padding:0px; margin:0px;list-type:none; float:left; margin-left:40px;">
				<li><a class="active" name="basic" href="javascript:;" title=""></a></li>
			</ul>
		</div>
		<div region="center" id="mainPanel" style="background: #eee; overflow-y:hidden">
	        <div id="tabs" class="easyui-tabs"  fit="true" border="false" >
				<div title="导航页面" style="padding:3px;overflow:hidden;" id="home">
					 <!-- addTab名字必须与菜单一致，否则会出现第二个窗口 -->
					 <div id="x_contant" style="margin-top: 0px;">
						 <a class="xzt1" href="javascript:void(0)" title="消息管理" onclick="addTab('adv','消息管理','<%=path%>/views/messManager/messManager_list.jsp','icon icon-nav')">
							 <img src="<%=path %>/resources/images/mms_xztz_1.png" />
						 </a>
					 </div>
					 <div style="text-align:center;margin:50px 0; font:normal 14px/24px 'MicroSoft YaHei';"/></div>
				</div>
	    	</div>
		</div>
		<div id="mm" class="easyui-menu" style="width: 150px;">
			<div id="mm-tabclose">关闭</div>
			<div id="mm-tabcloseother">关闭其他</div>
			<div id="mm-tabcloseleft">关闭左侧所有</div>
			<div id="mm-tabcloseright">关闭右侧所有</div>
			<div class="menu-sep"></div>
			<div id="mm-tabcloseall">关闭全部</div>
		</div>
	</div>
</body>
</html>