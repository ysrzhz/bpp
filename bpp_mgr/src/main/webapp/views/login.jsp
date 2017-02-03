<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<% 
String path =request.getContextPath();
%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head id="login.jsp">
<title>华数消息管理平台</title>
<link rel="shortcut icon" href="<%=path%>/resources/images/coship.ico" /> 
<link href="<%=path%>/resources/css/login.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="<%=path%>/resources/js/easyui/jquery-1.8.3.min.js"></script>
<script type="text/javascript">
	   if(window!=top){
		   top.location.href = window.location.href;
	   }
	   $(document).keydown(function(e) {
			e = e || window.event;
			var code = e.keyCode ? e.keyCode : e.which;
			if (code == 13) {
				console.log("提交表单")
				$("#loginForm").submit();
			}
		}); 
</script>
</head>
<body>
<input type="hidden" name="showSuccessDialog" value="2" id="showSuccessDialog"/>
<div class="lo_tit"></div>
<div class="load">
<div class="side"><img src="<%=path%>/resources/images/le_line.jpg" width="10" height="301" /></div>
<div class="ct">
<div class="tit"><img src="<%=path%>/resources/images/load_091009_14.jpg" width="108" height="29" /></div>

<div class="lo_ct">
<form name="loginForm" id="loginForm" action="<%=path%>/login" method="post" onkeydown="if(event.keyCode==13){this.submit();}">
   <input type="hidden" name="coship_portalMS_login" value="true"/>
   <table width="338" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="25%" height="37" align="right">用户名：</td>
      <td width="47%" height="37"><div><input type="text" name="username" value=""/></div></td>
      <td width="28%" height="37" class="red" id="errorMessage"></td>
    </tr>
    <tr>
      <td height="37" align="right">密　码：</td>
      <td height="37"><div><input type="password" name="password" value=""/></div></td>
      <td height="37" class="red" id="passwordErrorMessage"></td>
    </tr>
    <tr>
    	<td height="37" align="right">&nbsp;</td>
	    <td height="37" colspan="2"><font size="2px" color="red"><%=(request.getAttribute("msg")==null)?"":request.getAttribute("msg")%></font></td>
    </tr>
    <tr>
      <td height="37">&nbsp;</td>
      <td height="37"  class="bot">
		<input type="submit" value="" class="loginBtn" src="<%=path%>/resources/images/bu_load1.jpg" />
      </td>
    </tr>
  </table>
  </form>
</div>
<div class="at" >注：忘记密码，请联系管理员。</div>
</div>
<div class="side"><img src="<%=path%>/resources/images/ri_line.jpg" width="10" height="301" /></div>
</div>
<div id="b_at">
 

 </div>
</body>
</html>
<!-- @IEMPMUpdateTime@ -->
