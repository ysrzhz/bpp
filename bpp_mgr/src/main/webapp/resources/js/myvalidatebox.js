/**
 * 扩张validatebox校验
 * isNum：判断是否为数字
 * betweenNum:判断一个数字在一个范围内
 * easyui 在version 1.3.2开始支持联合校验
 * 使用方式: validType:['isNum','betweenNum[0,20]']
 * @author 907708
 */
(function($){
	var myvalidate = {
		isNum :
		{
			validator : function(value)
			{
				return !isNaN(value);
			},
			message : "请输入数字！"
		},
		isNum2 :
		{
			validator : function(value)
			{
				
				var flag=!isNaN(value);
				alert(flag);
				
				return !isNaN(value);
			},
			message : "哈哈哈！"
		},
	    
		betweenNum:{
            validator: function(value,args) {
                return value>args[0]&& value<=args[1];
                	
            },
            message: "请输入的数字在 {0} 和 {1}之间！"
        },
        isIp:{
        	validator:function(value){
        		var exp=/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
        		return exp.test(value);
        	},
        	message:"请输入IP地址"
        },
        afterDate:{
        	validator:function(value,args){
        		var target = $("input[id="+args[0]+"]").val();
        		return value>target;
        	},
        	message:'请输选择大于{0}的日期'
        },
        
        
        
        
        beforeDate:{
        	validator:function(value,args){
        		var target = $("input[id="+args[0]+"]").val();
        		return value<target;
        	},
            message:'请选择小于{0}的日期'
        	
        },
        selectNotNull: {
    	    validator: function(value){
    	    	if(value==undefined || value.trim().length==0 || value.trim()=="请选择"){
    	    		return false;
    	    	}
    	    	return true;
    	    },
            message:'请选择'
        },
        regexMatch:{
        	validator:function(value,args){
        		var rs = new RegExp(args[0]);
        		if(args[1]){
        			this.message = args[1];
        		}else{
        			this.message = '请输入符合格式的文本';
        		}
        		return rs.test(value);
        	}   	
        }
	};
	
	
	
	$.extend($.fn.validatebox.defaults.rules,myvalidate);
	
	
	

	/**
	 * 动态校验
	 */
	$.extend($.fn.validatebox.methods, {    
	    remove: function(jq, newposition){    
	        return jq.each(function(){
	        	if($(this).hasClass('easyui-datetimebox')){
	        		var _datebox = $(this).next().children().first();
	        		_datebox.removeClass("validatebox-text validatebox-invalid").unbind('focus').unbind('blur');
	        	}else{
	               $(this).removeClass("validatebox-text validatebox-invalid").unbind('focus').unbind('blur');  
	            }
	        });    
	    },  
	    reuse: function(jq, newposition){    
	        return jq.each(function(){ 
	           var opt;
	           if($(this).hasClass('easyui-datetimebox')){
	        	   //opt = $(this).data().datetimebox.options;
	        	   var _datebox = $(this).next().children().first();
	        	   _datebox.addClass("validatebox-invalid");
	           }else{
	        	   opt = $(this).data().validatebox.options; 
	        	   $(this).addClass("validatebox-text").validatebox(opt);  
	           }
	        });    
	    }     
	});  
})(jQuery);