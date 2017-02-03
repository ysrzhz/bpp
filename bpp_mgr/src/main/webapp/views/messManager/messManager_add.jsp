<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<%
  String path =request.getContextPath();
  String entityName = "messManager";
%>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta name="keywords" content="jquery,ui,easy,easyui,web">
  <meta name="description" content="">
</head>
<body>
<form id="entityForm" name="entityForm" method="post">
  <table class="mytable" style="font-size: 12px">
    <tr>
      <th>消息标题：</th>
      <td>
        <input class="easyui-validatebox" style="width: 100%;" type="text" id="add_title" name="title"   />
      </td>
    </tr>
      <tr>
          <th>消息内容：</th>
          <td>
              <textarea rows ="5"cols="20" style="height: auto;width: 100%" id="add_content" name= "content" class="easyui-validatebox" data-options="required:true"/>
          </td>
      </tr>
      <tr>
          <th>呈现方式：</th>
          <td>
              <select class="easyui-combobox" id="add_scope" name="scope" ></select>
          </td>
      </tr>
      <tr>
          <th>展现类型：</th>
          <td>
              <select class="easyui-combobox" id="add_vtype" name="vtype" ></select>
          </td>
      </tr>
      <tr>
          <th>发送时间：</th>
          <td>
              <input class="easyui-datetimebox" id = "add_sendTime" name="sendTime"
                     data-options="validType:'dateAfterNow'" style="width: 150px;"></td>
      </tr>
      <tr>
          <th>消息有效期：</th>
          <td>
              <input class="easyui-datetimebox" id="add_validTime" name="validTime"
                     data-options="validType:'dateAfterTomor'" style="width: 150px" />
          </td>
      </tr>
      <tr>
          <th>机顶盒编号</th>
          <td>
              <textarea cols="22" style="height: auto;width: 220px" id="add_stbId" name= "stbId"/>
              <input type="radio" name="s_bosstype" value="H" checked/>杭网
              <input type="radio" name="s_bosstype" value="S"/>省网
              <input type="button" name="choose" onclick="getTree('stbId')" value="选择" />
          </td>
      </tr>
      <tr>
          <th>区域编号</th>
          <td>
              <textarea cols="22" style="height: auto;width: 220px" id="add_areaId" name= "areaId"/>
              <input type="radio" name="a_bosstype" value="H" checked/>杭网
              <input type="radio" name="a_bosstype" value="S"/>省网
              <input type="button" name="choose" onclick="getTree('areaId')" value="选择" />
          </td>
      </tr>
      <tr>
          <th>客户编号</th>
          <td>
              <textarea cols="22" style="height: auto;width: 220px" id="add_custId" name= "custId" />
              <input type="radio" name="c_bosstype" value="H" checked/>杭网
              <input type="radio" name="c_bosstype" value="S"/>省网
              <input type="button" name="choose" onclick="getTree('custId')" value="选择" />
          </td>
      </tr>
  </table>
</form>

<div id="createTree" class="easyui-dialog"
     style="width: 420px; height: 420px;" closed="true"
     modal="true" buttons="#toolbar_region">
        <div class="easyui-tree" id="showTree">
            <ul id="area_tree"></ul>
    </div>
</div>
<div id="toolbar_region" style="padding: 5px; height: auto;display:none">
    <a href="#" class="easyui-linkbutton" iconCls="icon-save"
       id="regionSave" onclick="paramSave()" plain="false">确定</a>
    <a href="#" class="easyui-linkbutton" iconCls="icon-no"
        onclick="javascript:$('#createTree').dialog('close')" plain="false">关闭</a>
</div>
<script type="text/javascript">
    $(function(){
        /*加载下拉框数据*/
        var scope = [{"id":"0","text":"弹出框","selected":true},{"id":"1","text":"跑马灯"},{"id":"2","text":"悬浮框"},{"id":"3","text":"列表"}];
        var vtype = [{"id":"0","text":"广播类","selected":true},{"id":"1","text":"交互类"},{"id":"2","text":"收藏类"}];

        //加载下拉框数据
        $('#add_scope').combobox({
            panelWidth  : 180,
            panelHeight	: 'auto',
            valueField	: 'id',
            textField	: 'text',
            editable   	:  false,
            data		:  scope
        });
        $('#add_vtype').combobox({
            panelWidth  : 180,
            panelHeight	: 'auto',
            valueField	: 'id',
            textField	: 'text',
            editable   	:  false,
            data		:  vtype
        });

        /*日期格式*/
        $.fn.datebox.defaults.formatter = function(date){
            var y = date.getFullYear();
            var m = date.getMonth()+1;
            var d = date.getDate();
            var h = date.getHours();
            var M = date.getMinutes();
            var s = date.getSeconds();
            if(m < 10){
                m = "0"+m;
            }
            if(d <10){
                d = "0"+d;
            }
            if(h <10){
                h = "0"+h;
            }
            if(M <10){
                M = "0"+M;
            }
            if(s <10){
                s = "0"+s;
            }
            return y +'-'+ m +'-'+ d;
        }

        $.extend($.fn.validatebox.defaults.rules, {
            dateAfterNow: {
                validator: function(value){
                    var now = new Date();
                    var strDate = now.getFullYear()+"-";
                    strDate += now.getMonth()+1+"-";
                    strDate += now.getDate()+"-";
                    var d1 = $.fn.datebox.defaults.parser(strDate);
                    var d2 = $.fn.datebox.defaults.parser(value);
                    return d2>=d1;
                },
                message: '发送日期必须大于当前日期'
            },
            dateAfterTomor: {
                validator: function(value){
                    var now = new Date();
                    var strDate = now.getFullYear()+"-";
                    strDate += now.getMonth()+1+"-";
                    strDate += now.getDate()+1;
                    var d1 = $.fn.datebox.defaults.parser(strDate);
                    var d2 = $.fn.datebox.defaults.parser(value);
                    return d2>=d1;
                },
                message: '有效日期必须大于次日日期'
            }
        })
        /*初始化发送时间和有效期*/
        var curr_time = new Date();
        var strDate = curr_time.getFullYear()+"-";
        strDate += curr_time.getMonth()+1+"-";
        strDate += curr_time.getDate()+"-";
        strDate +=" ";
        strDate += curr_time.getHours()+":";
        strDate += curr_time.getMinutes()+":";
        strDate += curr_time.getSeconds();
        var validDate = curr_time.getFullYear()+"-";
        validDate += curr_time.getMonth()+1+"-";
        validDate += curr_time.getDate()+1+"-";
        validDate +=" ";
        validDate += curr_time.getHours()+":";
        validDate += curr_time.getMinutes()+":";
        validDate += curr_time.getSeconds();
        $("#add_sendTime").val(strDate);
        $("#add_validTime").val(validDate);
    });

    var globalWebAppURL = "<%=path %>";
    function getTree(name) {
        var title = "机顶盒列表";
        if(name == "areaId") title = "区域编号列表";
        if(name == "custId") title = "客户编号列表";
        $('#createTree').dialog({
            left : "750px",
            top : "20px"
        });
        $("#toolbar_region").attr("display","block");
        $("#createTree").dialog("open").dialog("setTitle", title);
        loadTree(name);
    }

    var level;
    var sendName = null;
    var type = null;
    function loadTree(name) {
        var bosstype = $("input[name='s_bosstype']:checked").val();
        if(name == 'areaId') bosstype = $("input[name='a_bosstype']:checked").val();
        if(name == 'custId') bosstype = $("input[name='c_bosstype']:checked").val();
        if(name != sendName ||(name == sendName && bosstype != type)){   //接收类型不同且数据源发生改变均重新加载树结构
            sendName = name;
            type = bosstype;
            stbids = "";
            $('#area_tree').tree({
                checkbox: true,
                animate:true,
                url: globalWebAppURL+"/<%=entityName%>/getTree?bosstype="+bosstype,
                lines:true,
                loadFilter: function(data){
                    if (data.d){
                        return data.d;
                    } else {
                        return data;
                    }
                },
                onBeforeExpand:function(node,param){
                    if(node.attributes.isParent){
                        $('#area_tree').tree('options').url = globalWebAppURL+"/<%=entityName%>/getTree?parentId="+node.attributes.ids+"&name="+name;
                    }else if(name=='stbId'&& !node.attributes.isParent){
                        $('#area_tree').tree('options').url = globalWebAppURL+"/<%=entityName%>/getStbids?regionId="+node.id;
                    }else{
                        return false;
                    }
                } ,
                onLoadSuccess:function(node,data){

                }
            })
        }
    }

    //获得tree的层数
    var easyui_tree_options = {
        length : 0,  //层数
        getLevel : function(treeObj, node){ //treeObj为tree的dom对象，node为选中的节点
            while(node != null){
                node = $(treeObj).tree('getParent', node.target)
                easyui_tree_options.length++;
            }
            var length1 = easyui_tree_options.length;
            easyui_tree_options.length = 0;     //重置层数
            return length1;
        }
    }
    var stbids="";
    var areaids = "";
    var custids = "";
    function paramSave(){
        var count = 0;
        var nodes = $("#area_tree").tree('getChecked');
        for(var i = 0;i<nodes.length;i++){
            if($('#area_tree').tree('isLeaf',nodes[i].target)){
                if('stbId' == sendName){
                    stbids += nodes[i].text+"\n";
                    count++;
                }else{
                    custids += nodes[i].text + "\n";
                    count++;
                }
            }else{
                if(isChildCheck(nodes[i])){
                    areaids +=nodes[i].id+"\n";
                    if('areaId' == sendName)count++;
                }else{
                    continue;
                }
            }
        }
        if(count>500){
            $.messager.alert('提示','选择的消息接受者最多为500','info');
            stbids="";
            areaids = "";
            custids = "";
            return;
        }
        if(count==0){
            $.messager.alert('提示','没有选中消息接受者','info');
            return;
        }
        stbids = stbids.substring(0,stbids.length-1);
        areaids = areaids.substring(0,areaids.length-1);
        custids = custids.substring(0,custids.length-1);
        if('stbId'== sendName)$("#add_stbId").val(stbids);
        if('areaId' == sendName) $("#add_areaId").val(areaids);
        if('custId' == sendName) $("#add_custId").val(custids);
        stbids = "";
        areaids = "";
        custids = "";
        $("#toolbar_region").attr("display","none");
        $('#createTree').dialog('close');
        count = 0;
    }

    function isChildCheck(node){
        var childs = $("#area_tree").tree('getChildren',node.target);
        for(var i = 0;i< childs.length;i++){
            if(childs[i].checked){
                return false;
            }else{
                continue;
            }
        }
        return true;
    }
    function isHavChild(node){
        var childs = $("#area_tree").tree('getChildren',node.target);
        return childs.length > 0;
    }
</script>
</body>
</html>
