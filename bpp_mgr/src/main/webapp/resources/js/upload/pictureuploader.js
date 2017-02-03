/**
 * 图片上传封装，提供以下公用方法,依赖jquery,ajaxfileupload.js
 * @author 907708
 * 1.图片上传
 * 2.返回图片预览地址
 * 3.返回图片长、宽、大小等信息
 */

(function(_win){
		var img = null;
		var pictureuploader = {};
		pictureuploader.settings = {extendsion:[]};
		//读取配置文件
		pictureuploader.config = function(config){
			for(var key in config){
				pictureuploader.settings[key] = config[key];
			}
		}
		/**
		 * 校验文件后缀是否可以上传
		 */
		pictureuploader.checkExtension = function(target){
			var extension = pictureuploader.settings.extendsion;
			for(var i in extension){
				//后缀文件名不区分大小写
				if(target.toLowerCase() == extension[i])
					return true;
			}
			return false;
		}
		
		/**
		 * 上传函数
		 * url为后台提交地址
		 * fileId 文件控件id
		 * success 为成功时回调函数 参数有 data,status
		 * error 为异常时回调函数 参数有 data,status
		 */
		pictureuploader.upload = function(url,fileId,success,error){
			ajaxLoading();
			$.ajaxFileUpload({
				url:url,
				secureuri:false,                       //是否启用安全提交,默认为false 
				fileElementId:fileId,           //文件选择框的id属性
				dataType:'json',                       //服务器返回的格式,可以是json或xml等
				success:success,
				error:error
			});
			ajaxLoadEnd();
		};
		/**
		 * 上传素材函数
		 * url为后台提交地址
		 * fileId 文件控件id
		 * sucai 素材名
		 * type 类型
		 * site 站点
		 * clickURL 点击链接
		 * success 为成功时回调函数 参数有 data,status
		 * error 为异常时回调函数 参数有 data,status
		 */
		pictureuploader.uploadPic = function(url,fileId,sucai,type,site,clickURL,duration,success,error){
			ajaxLoading();
			$.ajaxFileUpload({
				url:url,
				data:{ sucai: sucai, type: type, clickURL: clickURL, site: site, duration:duration},
				secureuri:false,                       //是否启用安全提交,默认为false
				fileElementId:fileId,           //文件选择框的id属性
				dataType:'json',                       //服务器返回的格式,可以是json或xml等
				success:success,
				error:error
			});
			ajaxLoadEnd();
		};

		/**
		 * 通过浏览器取得图片的宽和高,图片大小,返回格式为json,如 {src:'',height:20,width:20,size:1000}
		 * picFileId 页面文件控件的id
		 */
		pictureuploader.getPictureInfo = function(picFileId){
			var pictureInfo = {};
			img.style.position="absolute";//防止正常的内容变形 
			img.style.visibility='hidden';//藏起来 
			if(navigator.userAgent.indexOf("MSIE")>=1){
				pictureInfo.src = document.getElementById(picFileId).value;
			}else{
				pictureInfo.src = window.URL.createObjectURL(document.getElementById(picFileId).files.item(0));
			}
			pictureInfo.height = img.height;
			pictureInfo.width = img.width;
			pictureInfo.size = document.getElementById(picFileId).files[0].size;
			return pictureInfo;
			
		}
		/**
		 * 图片预览功能
		 * picFileId 页面文件控件的id
		 * picturePreviewId 页面用于预览的img的id
		 */
		pictureuploader.previewImage = function(picFileId,picturePreviewId){
		    img = new Image();
			document.getElementById(picturePreviewId).src='';
			if (navigator.userAgent.indexOf("MSIE")>=1) { //IE
				img.src = src;
			}else{
				var reader = new FileReader();  
				reader.onload = function(evt){
					ajaxLoading();
					img.src = this.result;
					document.getElementById(picturePreviewId).src=this.result;
					ajaxLoadEnd();
				}  
				reader.readAsDataURL(document.getElementById(picFileId).files[0]);
		   }
		 };
		 pictureuploader.open = function(){
		   $("#uploadfileDiv").dialog('open');
		   $("#myfiles").val('');
		   $("#previewImg").attr('src','');
		 };
		 
		 pictureuploader.closeupload = function(){
				$("#myfiles").val('');
				$("#previewImg").attr('src','');
				$('#uploadfileDiv').dialog('close');
		 };
		 /**
		  * 默认的上传事件
		  */
		 pictureuploader.defaultUpload = function(success,error){
			  if($("#myfiles").val()==''){
				  $.messager.alert('提示','请选择文件','info');
				  return false;
			  }
			  var fileType = $("#myfiles").val().substring($("#myfiles").val().lastIndexOf('.')+1);
			  if(!pictureuploader.checkExtension(fileType)){
					$.messager.alert('提示','只支持图片文件格式:'+pictureuploader.settings.extendsion.join(','),'error');
					return false; 
			  }
			  pictureuploader.upload(globalWebAppURL+"/fileupload/pic","myfiles",success,error);
			  return true;
		 };
		 /**
		  * 默认的获取图片信息的方法
		  */
		 pictureuploader.defaultGetPictureInfo = function(){
			 return pictureuploader.getPictureInfo('myfiles');
		 }
		 
		 function ajaxLoading(){  
			    $("<div class=\"datagrid-mask\"></div>").css({display:"block",width:"100%",height:$(document).height()+'px'}).appendTo("body");  
			    $("<div class=\"datagrid-mask-msg\"></div>").html("正在处理，请稍候。。。").appendTo("body").css({display:"block",left:($(document.body).outerWidth(true) - 190) / 2,top:($(window).height() - 45) / 2});  
			}  
         function ajaxLoadEnd(){  
			     $(".datagrid-mask").remove();  
			     $(".datagrid-mask-msg").remove();              
		 } 
		_win.pictureuploader = pictureuploader;
})(window);