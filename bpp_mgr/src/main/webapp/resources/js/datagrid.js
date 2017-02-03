var validate = {};

(function() {
	//新增属性数组
	var propArr = [];
	//新增属性索引前缀
	var defaultPrefix = "index_";
	
	//去除新增prop
	validate.del = function(index, prefix){
		for(var i=0; i<propArr.length; i++){
			if (propArr[i].index == prefix+index){
				propArr.splice(i, 1);
				return;
			}
		}
	};

	//添加新增属性
	validate.add = function(index, prop, prefix){
		propArr.push({index:prefix+index, prop:prop});
	};

	//验证新增属性
	validate.validate = function(index, prop, prefix){
		if (prefix == undefined) {
			prefix = defaultPrefix;
		}
		var result = true;
		for(var i=0; i<propArr.length; i++){
			var indexValue = propArr[i].index;
			if (indexValue.indexOf(prefix) != -1 && indexValue != prefix+index){
				if(propArr[i].prop == prop){
					result = false;
					break;
				}
			}
		}
		//删除旧prop
		validate.del(index, prefix);
		//增加新prop
		validate.add(index, prop, prefix);
		//console.log("prefix:" + prefix + ', prop:'+prop +', propArr:'+propArr);
		return result;
	}
	
	validate.clear = function() {
		propArr = [];
	}
})();

//退出所有编辑
function endEditRows(target){
	var rows = $(target).datagrid('getRows');
	for(var i = 0;i < rows.length; i ++){
		$(target).datagrid("endEdit", i);
	}
}

//根据index获取行
function getRow(target, index){
	var rows = $(target).datagrid('getRows');
	for(var i = 0;i < rows.length; i ++){
		if (index == i) {
			return rows[i];
		}
	}
	return null;
}

//获取指定编辑行正在编辑的input
function getEditTarget(target, index, editorNo) {
	var editors = $(target).datagrid('getEditors', index); 
	var editor = editors[editorNo];  
	return editor.target;
}

//创建table表头的右键菜单
function createHeadContextMenu(tableId,onClickFunc){
	var contextMenu = $('<div/>').appendTo('body');
	var onclick = onClickFunc;
	if(!onclick){
		onclick = function(item){
			console.log("item="+item);
			console.log("this="+$(this));
			if (item.iconCls == 'icon-ok'){
				$('#'+tableId).datagrid('hideColumn', item.name);
				contextMenu.menu('setIcon', {
					target: item.target,
					iconCls: 'icon-empty'
				});
			} else {
				$('#'+tableId).datagrid('showColumn', item.name);
				contextMenu.menu('setIcon', {
					target: item.target,
					iconCls: 'icon-ok'
				});
			}
		}
	}
	contextMenu.menu({
		onClick: onclick
	});
	var fields = $('#'+tableId).datagrid('getColumnFields');
	for(var i=0; i<fields.length; i++){
		var field = fields[i];
		var col = $('#'+tableId).datagrid('getColumnOption', field);
		var icon = col.hidden?'icon-empty':'icon-ok';
		contextMenu.menu('appendItem', {
			text: col.title,
			name: field,
			iconCls: icon
		});
	}
	return contextMenu;
}
