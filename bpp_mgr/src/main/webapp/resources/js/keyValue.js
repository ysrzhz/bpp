(function($) {
	var div_html = '<div>&nbsp;键：&nbsp;<input type="text" style="width:100px" name="keyValue.key"/>'
		+ '&nbsp;值：&nbsp;<input type="text" style="width:100px" name="keyValue.value"/>'
		+ '<input type="button" style="margin-left:5px"/></div>';
	
	$.fn.extend({
		keyValue : function(_function, _param) {
			if (typeof _function == "string") {
				return kv.methods[_function](this, _param);
			}
			_function = _function || {};
			//不带参数则调setValue初始化,url=null
			var options = $.extend({}, kv.defaults, _function);
			this.data('options', options);
			return kv.methods["setValue"](this);
		},
	});
	
	$.fn.keyValue.defaults = {
		validLength : 100,
		separator : '&',
		split : '='
	};

	$.fn.keyValue.methods = {
		//获取option参数
		options : function(jq){
			var options = jq.data('options');
			if(!options){
				options = kv.defaults;
			}
			return options;
		},
		//初始化设置key value值
		setValue : function(jq, url) {
			var option = kv.methods['options'](jq);
			jq.children().remove();
			var ss;
			if(url){
				ss = url.split(option.separator);
			}
			if(ss && ss.length>0){
				for ( var i = 0; i < ss.length; i++) {
					var value = ss[i].split(option.split);
					if (i == 0) { //第一个为增加
						var div = kv.methods.addKeyValue(jq, {key:value[0],value:value[1],buttonType:'add'});
						div.append('<font>&nbsp;键值长度不超过'+option.validLength+'个字符</font>');
					} else { //后续为删除
						kv.methods.addKeyValue(jq, {key:value[0],value:value[1],buttonType:'remove'});
					}
				}
			}else{
				var div = kv.methods.addKeyValue(jq, {key:'',value:'',buttonType:'add'});
				div.append('<font>&nbsp;键值长度不超过'+option.validLength+'个字符</font>');
			}
			return jq;
		},
		//获取返回结果
		getValue : function(jq) {
			var option = kv.methods['options'](jq);
			//处理动态参数
			var keys = new Array();
			var values = new Array();
			jq.find('input[name="keyValue.key"]').each(function(i) {
				keys[i] = $(this).val().trim();
			});
			jq.find('input[name="keyValue.value"]').each(function(i) {
				values[i] = $(this).val().trim();
			});
			var param = "";
			var key = new Array();
			for ( var i = 0; i < keys.length; i++) {
				if (keys[i] != "" && values[i] != "") {
					if(key.length==0)
					{
						param += option.separator + keys[i] + option.split + values[i];
					}else{
						for(var k = 0;k<key.length;k++)
						{
							if(keys[i]==key[k])
							{
								param = "keyEQUAL";
							}else{
								param += option.separator + keys[i] + option.split + values[i];
							}
						}
					}
					key.push(keys[i]);
				}else if(keys[i] != "" && values[i] == ""){
					param = "keyNULL"
				}else if(keys[i] == "" && values[i] != ""){
					param = 'valueNULL';
				}
			}
			if (param.length > 0&&param!="keyNULL"&&param!="valueNULL"&&param!="keyEQUAL") {
				param = param.substr(option.separator.length);
			}
			return param;
		},
		//param={key:'',value:'',buttonType:'remove|add'}
		addKeyValue : function(jq, param) {
			var option = kv.methods['options'](jq);
			var len = jq.children('div').length;
			jq.append(div_html);
			var div = jq.children().last();
			div.attr('id',jq.attr('id')+'_keyvalue_'+len);
			div.children('input:eq(0)').val(param.key).attr('id',jq.attr('id')+'_key_'+len).validatebox({validType:'length[0,'+option.validLength+']'});
			div.children('input:eq(1)').val(param.value).attr('id',jq.attr('id')+'_value_'+len).validatebox({validType:'length[0,'+option.validLength+']'});
			var button = div.children('input:eq(2)');
			if (param.buttonType == 'add') { //给按钮赋text，并绑定增加事件
				button.val('增加').bind('click', function() {
					$('#'+jq.attr('id')).keyValue('addKeyValue',{key:'',value:'',buttonType:'remove'});
				});
			} else if (param.buttonType == 'remove') { //给按钮赋text，并绑定删除事件
				button.val('删除').bind('click', function() {
					$(this).parent().remove();
				});
			}
			return div;
		},
	}

	var kv = $.fn.keyValue;

})(jQuery);