/**
 * 数据字典管理前端代码逻辑封装
 */
var dict = {};
(function() {
	var dictKey;//当前选择数据字典类型
	dict.initHeadDict = function() {//初始化字典头列表
		$('#table_head_dict').datagrid({
			columns: [[
	            {
					field: 'dictKey',
					align: 'center',
					width: 80,
					title: '字典头Key',
					hidden: true
	            }, 
	            {
					field: 'dictValue',
					align: 'left',
					width: 80,
					title: '字典类别名称'
	            }
            ]],
			idField: 'dictKey',
			url: globalWebAppURL + '/manager/dict/headList',
			border: false,
			fit: true,
			fitColumns: true,
			singleSelect: true,
			onSelect: function(rowIndex, rowData) {
				dictKey = rowData.dictKey;
				$("#list_data").datagrid("load", {
					dictKey: dictKey
				});
			},
			onLoadSuccess: function(data) {
				if (data.total > 0) {
					if (dictKey == -1 || dictKey == undefined) {
						$("#table_head_dict").datagrid("selectRow", 0);
					} else {
						$("#table_head_dict").datagrid("selectRecord",
								dictKey);
					}
				} else {//未选中字典头，按钮都不可选
					$("#dictAdd").linkbutton("disable");
					$("#dictDel").linkbutton("disable");
				}
			}
		});
	}

	dict.initList = function() {
		var options = {
			columns: [[
				{
					field: 'id',
					title: 'id',
					align: 'center',
					width: 100,
					hidden: true
				},
				{
					field: 'paramKey',
					title: '参数Key',
					align: 'center',
					width: 250,
					formatter: function(value, row, index) {
						return common.htmlLink(value, "dict.edit(" + index + ")", false);
					}
				}, 
				{
					field: 'paramValue',
					title: '参数值',
					align: 'center',
					width: 250
				}, 
				{
					field: 'description',
					title: '描述',
					align: 'center',
					width: 300
				} 
			]],
			url: globalWebAppURL + '/manager/dict/list'
		};
		$('#list_data').mygrid(options);
	}

	//新增数据字典
	dict.add = function() {
		$("#dict_edit").dialog("open").dialog('setTitle', '添加数据字典');
		$("input").tooltip('destroy');
		$("#dictId").val("");
		$("#dictKey").val(dictKey);
		$("#paramKey").removeAttr("readonly");
		$("#paramKey").removeClass('disableInput');
		$("#paramKey").val("");
		$("#paramValue").val("");
		$("#description").val("");
	}

	//删除数据字典
	dict.del = function() {
		var ids = common.concatIds('#list_data');
		if (ids != "") {
			$.messager.confirm("提示", "删除参数可能导致相关联的数据不可用，确认继续?", function(isContinue) {
				if (isContinue) {
					$.messager.confirm('提示', '确定要删除吗?', function(isDel) {
						if (isDel) {
							common.ajaxBatchSubmit(globalWebAppURL + "/manager/dict/del", ids, "list_data");
						}
					});
				}
			});
		} else {
			common.showWarning("请选择记录");
		}
	}

	//编辑数据字典
	dict.edit = function(index) {
		$("#dict_edit").dialog("open").dialog('setTitle', '编辑数据字典');
		$("#list_data").datagrid("unselectAll");
		$("#list_data").datagrid("selectRow", index);
		var row = $("#list_data").datagrid("getSelected");
		$("input").tooltip('destroy');
		$("#dictId").val(row.id);
		$("#dictKey").val(dictKey);
		$("#paramKey").val(row.paramKey);
		$("#paramKey").attr("readonly", "readonly");
		$("#paramValue").val(row.paramValue);
		$("#description").val(row.description);
		$("#paramKey").addClass('disableInput');
		$("#dictEditForm").form('validate');
	}

	//保存数据字典
	dict.save = function() {
		$('#dictEditForm').form('submit', {
			url: globalWebAppURL + '/manager/dict/save',
			onSubmit: function() {
				var isValid = $(this).form('validate');
				var checkKey = checkParamKey();
				var result = isValid && checkKey;
				if (!result) {
					$.messager.progress('close');
				}
				
				return result;
			},
			queryParams: {
				dictKey: dictKey
			},
			success: function(data) {
				$.messager.progress('close');
				var result = $.parseJSON(data);
				if (!result.success) {
					common.fadeMessagerNotClose(result.message, 3000);
				} else {//重载数据
					common.fadeMessager('list_data', result.message, 1000);
					$("#dict_edit").dialog("close");
				}
			}
		});
	}

	//检查数据字典Key是否重复
	function checkParamKey(index) {
		var check = false;
		$.ajax({
			type: "post",
			url: globalWebAppURL + "/manager/dict/checkParamKey",
			data: $('#dictEditForm').serialize(),
			async: false,
			success: function(data) {
				if (!data.paramKey) {
					common.showTip("#paramKey", "参数Key已存在!");
				} else {
					common.hideTip("#paramKey");
					check = true;
				}
			}
		});
		return check;
	}
})();