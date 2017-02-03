<%@ page language="java"  pageEncoding="utf-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
  String path =request.getContextPath();
  String entityName = "messManager";
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="keywords" content="消息管理">
  <meta name="description" content="消息管理页面">
  <title>消息管理</title>
  <jsp:include page="/views/common.jsp"></jsp:include>
  <link rel="stylesheet" type="text/css" href="<%=path %>/resources/css/dialog.css">
  <script type="text/javascript" src="<%=path %>/resources/js/easyui/extends/easyui-dateformatter.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/easyui/extends/easyui-pans-formatter.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/easyui/datagrid-filter.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/common/common.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/mygrid.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/fileuploader/pictureuploader.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/fileuploader/ajaxfileupload.js"></script>
  <script type="text/javascript" src="<%=path %>/resources/js/fileuploader/ajaxloading.js"></script>
  <link rel="stylesheet" type="text/css" href="<%=path %>/resources/css/uploadify.css">
  <script type="text/javascript" src="<%=path %>/resources/js/upload/jquery.uploadify.js"></script>
</head>
<body>

<style type="text/css">
  #createTree div.panel{
    overflow:auto;
  }
  .panel-body.panel-body-noheader.panel-body-noborder.dialog-content{
    padding : 4px !important;
  }
</style>
<div id="tb" style="padding:5px;height:auto">
  <div>
    <table>
      <tr>
        <td style="text-align:right;font-size:12px;">消息内容:</td><td><input type = "text" id = "show_content" name = "content" /></td>
        <td style="text-align:right;font-size:12px;">发送状态:</td><td><select class="easyui-combobox" id="send_status" style="width: 173px;" mame="status"></select></td>
        <td style="text-align:right;font-size:12px;">数据来源:</td><td><select class="easyui-combobox" id="data_src" name="dataSrc" style="width: 173px;"></select></td>
        <td rowspan="2">
          <a href="#" id="query"class="easyui-linkbutton" iconCls="icon-search" onclick="doSearch()">查询</a>&nbsp;
          <a href="#" id="clear" class="easyui-linkbutton" iconCls="icon-delete" onclick="clearSearch()">清除</a>
        </td>
      </tr>
      <tr>
        <td style="text-align:right;font-size:12px;">机顶盒编号:</td><td><input type = "text" id = "show_stbId" name = "stbId"/></td>
        <td style="text-align:right;font-size:12px;">区域编号:</td><td><input type = "text" id = "show_areaId" name = "areaId"/></td>
        <td style="text-align:right;font-size:12px;">客户编号:</td><td><input type = "text" id = "show_custId" name = "custId"/></td>
      </tr>
    </table>
    <%--消息接受者:<input type="text" id="msg_param" name = "param">--%>
    <%--发送类型:<select class="easyui-combobox" id="send_type" style="width: 100px;" name="sendType" ></select>--%>
  </div>
  <div style="margin-bottom:5px">
    <a href="#"  id="entity_Add" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="entity.add()">新增</a>
    <a href="#" id="entityDelete"  class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="entity.del()">删除</a>
  </div>
</div>
<table id="list_data"  title="消息管理列表" style="width:700px;height:250px" data-options="toolbar:'#tb'">
</table>
<div style="position:relative;" id="editEntity" class="easyui-dialog" data-options="modal:true,closed:true,resizable:false,buttons:'#edit_btn',width:800,
			height:280">
</div>
<div style="position:relative;" id="entityDetail" class="easyui-dialog" data-options="modal:true,closed:true,resizable:false,width:800,
			height:280">
</div>
<div id="edit_btn" style="padding:5px;height:auto">
  <div style="margin-bottom:5px">
    <a href="#" id="entitySave" class="easyui-linkbutton" iconCls="icon-save" onclick="">保存</a>
    <a href="#" id="entityCancle" class="easyui-linkbutton" iconCls="icon-no"  onclick="">取消</a>
  </div>
</div>

<div style="position:relative;" id="manageChannelDvb" class="easyui-dialog" data-options="modal:true,closed:true,resizable:false,width:800,height:600">
</div>

<script type="text/javascript">

  var globalWebAppURL = "<%=path %>";
  var entityName ='<%=entityName%>';
  var entity = {};
  var scopeObj = {"0":"弹出框","1":"跑马灯","2":"悬浮框","3":"列表"};
  var vtypeObj = {"0":"广播类","1":"交互类","2":"收藏类"};
  var sendStatusObj = {"0":"待发送","1":"发送成功","2":"发送失败","3":"已达","4":"已读"};
  var dataSrcObj = {"0":"MMS","1":"用户中心","2":"智能推荐系统","3":"酒店管理系统","4":"终端升级"};
  var scopeJson = common.queryList(scopeObj,true);
  var vtypeJson = common.queryList(vtypeObj,true);
  var statusJson = common.queryList(sendStatusObj,true);
  var dataSrcJson = common.queryList(dataSrcObj,true);
  //页面入口处
  $(function(){
    //初始化datagrid
    entity.initPage = function(target, url){
      var gridOptions = {
        fitColumns:true,
        columns:[[
          {field:'id',title:'消息编号',align:'center',width:40,hidden:false},
          {field:'title',align:'center',width:80,title:'消息标题',hidden:false,formatter:function(value,row,index){
            return value?value:"";
          }},
          {field:'content',align:'center',width:80,title:'消息内容',hidden:false,formatter:function(value,row,index){
              return common.htmlLink(value, "entity.edit("+index+","+row.id+")", false);
          }},
          {field:'stbId',align:'center',width:80,title:'机顶盒编号',hidden:false},
          {field:'areaId',align:'center',width:80,title:'区域编号',hidden:false},
          {field:'custId',align:'center',width:80,title:'客户编号',hidden:false},
          {field:'sendTime',align:'center',width:80,title:'发送时间',hidden:false,formatter:function(value,row,index){
            return value?toDate(value):"";
          }},
          {field:'validTime',align:'center',width:80,title:'有效期',hidden:false,formatter:function(value,row,index){
            return value?toDate(value):"";
          }},
          /*{field:'scope',align:'center',width:80,title:'呈现方式',hidden:false,formatter:function(value,row,index){
            return scopeObj[value];
          }},
          {field:'vtype',align:'center',width:80,title:'展现类型',hidden:false,formatter:function(value,row,index){
            return vtypeObj[value];
          }},*/
          {field:'status',align:'center',width:80,title:'发送状态',hidden:false,formatter:function(value,row,index){
            if(value =='2'){
              //return common.htmlLink(sendStatusObj[value], "entity.resend("+index+","+row.id+")", false);
              return "<a  href='#' onMouseOver='over(this)' onMouseOut='out(this)'  onclick='entity.resend("+index+","+row.id+")'>"+sendStatusObj[value]+"</a>"
            }
            return sendStatusObj[value];
          }},
          {field:'dataSrc',align:'center',width:80,title:'数据来源',hidden:false,formatter:function(value,row,index){
            return dataSrcObj[value];
          }},
        ]],
        url:url,
        pagination: true,
        autoRowHeight:false,
        nowrap:false,
        onLoadSuccess:function(data){
          $("input[type='checkbox']").attr("checked",false);
        }
      };
      $(target).mygrid(gridOptions);
    }
    entity.initPage("#list_data", globalWebAppURL+"/"+entityName+"/list.do");


    entity.resend = function(index,id){
      $.messager.confirm('提示','此操作不可恢复，确定要重新发送?',function(r){
        if (r){
          common.ajaxBatchSubmit(globalWebAppURL+"/"+entityName+"/resend", id, "list_data");
        }
      });
    }
    //新增消息
    entity.add = function () {
      var p = $('<div/>').dialog({
        title : '新增消息',
        href : globalWebAppURL+'/views/messManager/messManager_add.jsp',
        width : 500,
        left:250,
        top:8,
        iconCls:'icon-add',
        modal : true,
        buttons : [ {
          text : '确定',
          iconCls : 'icon-ok',
          handler : function() {
            var f = p.find('form');
            var texts = f.find('textarea');
            var b = false;
            for(var i = 0; i < texts.length; i++){
              var value = $(texts[i]).val();
              if(value!=null && value!=""){
                b = true;
                break;
              }
            }
            if(b){
              f.form('submit', {
                url : globalWebAppURL+"/"+entityName+"/save.do",
                success : function(d) {
                  var json = $.parseJSON(d);
                  if (json.success) {
                    $("#list_data").datagrid('unselectAll');
                    $("#list_data").datagrid('reload');
                    p.dialog('close');
                  }
                  $.messager.show({
                    msg : json.message,
                    title : '提示'
                  });
                }
              });
            }else{
              $.messager.alert('提示','机顶盒编号、区域编号和客户编号至少选择一项','info');
            }
          }
        },{
          text : '取消',
          iconCls : 'icon-cancel',
          handler : function() {
            p.dialog('destroy');
          }
        } ],
        onClose : function() {
          $(this).dialog('destroy');
        }
      });
    }

    //编辑
    entity.edit = function (index,id) {
      var rows = $('#list_data').datagrid('getRows');
      var o = rows[index];
      var titl = (o.status == '0' && o.dataSrc == '0')?"编辑消息":"消息信息";
      var isEdit = o.status == '0'?true:false;
      var isMMS = o.dataSrc == '0'?true:false;
      var p = $('<div/>').dialog({
        title : titl,
        href : globalWebAppURL+"/"+entityName+"/edit?id="+id+"&isEdit="+isEdit+"&isMMS="+isMMS,
        width : 500,
        left:250,
        top:8,
        iconCls:'icon-edit',
        modal : true,
        onClose : function() {
          $(this).dialog('destroy');
          $("#list_data").datagrid('unselectAll');
        }
      });
      if(o.status =='0'&& o.dataSrc == '0'){     //如果消息状态为待发送且为消息系统发出，则加载编辑界面的确认和取消按钮
        $(p).dialog({
          buttons : [ {
            text : '确定',
            iconCls : 'icon-ok',
            handler : function() {
              var f = p.find('form');
              var texts = f.find('textarea');
              var b = false;
              for(var i = 0; i < texts.length; i++){
                var value = $(texts[i]).val();
                if(value!=null && value!=""){
                  b = true;
                  break;
                }
              }
              if(b) {
                f.form('submit', {
                  url: globalWebAppURL + "/" + entityName + "/updat?_t=" + new Date().getTime(),
                  success: function (d) {
                    var json = $.parseJSON(d);
                    if (json.success) {
                      $("#list_data").datagrid('unselectAll');
                      p.dialog('close');
                    }
                    $.messager.show({
                      msg: json.message,
                      title: '提示'
                    });
                    $("#list_data").datagrid('reload');
                  }
                });
              }else{
                $.messager.alert('提示','机顶盒编号、区域编号和客户编号至少选择一项','info');
              }
            }
          },{
            text : '取消',
            iconCls : 'icon-cancel',
            handler : function() {
              p.dialog('destroy');
            }
          } ]
        });
      }
    }

    //删除
    entity.del = function() {
      var ids = common.concatIds('#list_data');
      if (ids != "") {
        $.messager.confirm('提示','此操作不可恢复，确定要执行删除?',function(r){
          if (r){
            common.ajaxBatchSubmit(globalWebAppURL+"/"+entityName+"/del", ids, "list_data");
          }
        });
      } else {
        common.showWarning("请选择记录");
      }

    }

    entity.showTip = function(target, msg){
      $(target).tooltip('destroy');
      $(target).tooltip({
        position: 'right',
        content: '<span style="color:#000000">'+msg+'</span>',
        showEvent:'mouseover',
        onShow: function(){
          $(this).tooltip('tip').css({
            backgroundColor: '#FFFFCC',
            borderColor: '#CC9933',
            color:"#000"
          });
        }
      }).tooltip('show');
    }

    //隐藏提示
    entity.hideTip = function(target) {
      $(target).tooltip('destroy');
    }

    //加载下拉框数据
   /* $('#show_scope').combobox({
      panelWidth  : 180,
      panelHeight	: 'auto',
      valueField	: 'id',
      textField	: 'text',
      editable   	:  false,
      data		:  scopeJson
    });
    $('#v_type').combobox({
      panelWidth  : 180,
      panelHeight	: 'auto',
      valueField	: 'id',
      textField	: 'text',
      editable   	:  false,
      data		:  vtypeJson
    });*/
    $('#send_status').combobox({
      panelWidth  : 173,
      panelHeight	: 'auto',
      valueField	: 'id',
      textField	: 'text',
      editable   	:  false,
      data		:  statusJson
    });
    $('#data_src').combobox({
      panelWidth  : 173,
      panelHeight	: 'auto',
      valueField	: 'id',
      textField	: 'text',
      editable   	:  false,
      data		:  dataSrcJson
    });

  });

  //鼠标移入
  function over(t){
    entity.showTip(t,"点此重新发送消息");
  }

  //鼠标移出
  function out(t){
    entity.hideTip(t);
  }

  //格式化时间
  function toDate(date){
    var myDate = new Date(date);
    var year =myDate.getFullYear();//获取完整的年份(4位,1970-????)
    var month = myDate.getMonth()+1;       //获取当前月份(0-11,0代表1月)
    if(month < 10){
      month = "0"+month;
    }
    var date = myDate.getDate();        //获取当前日(1-31)
    if(date <10){
      date = "0"+date;
    }
    var hours = myDate.getHours();       //获取当前小时数(0-23)
    if(hours <10){
      hours = "0"+hours;
    }
    var min = myDate.getMinutes(); //获取当前分钟数(0-59)
    if(min <10){
      min = "0"+min;
    }
    var seconds = myDate.getSeconds();     //获取当前秒数(0-59)
    if(seconds <10){
      seconds = "0"+seconds;
    }
    return year +"-"+month+"-"+date+" "+hours+":"+min+":"+seconds;
  }

  //查询方法
  function doSearch() {
    var condition = {
      /*vtype:$("#v_type").combo("getValue"),
      scope:$("#show_scope").combo("getValue"),*/
      status:$("#send_status").combo("getValue"),
      dataSrc:$("#data_src").combo("getValue"),
      content:$("#show_content").val(),
      stbId:$("#show_stbId").val(),
      areaId:$("#show_areaId").val(),
      custId:$("#show_custId").val()
    };
    $("#list_data").datagrid('load',condition);//根据URL= globalWebAppURL+'/manager/channel/list'二次加载数据，不用炫染框架
  }

  function clearSearch(){
   /* $("#show_scope").combobox('setValue','');
    $("#v_type").combobox('setValue','');*/
    $("#send_status").combobox('setValue','');
    $("#data_src").combobox('setValue','');
    $("#show_content").val('');
    $("#show_stbId").val('');
    $("#show_areaId").val('');
    $("#show_custId").val('')
  }

</script>
</body>
</html>
