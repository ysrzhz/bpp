
//属性框
function showModuleOpt(data) {
    if(data.resourceType == "17"){
        jQuery("#childLayout").show();
    }else{
        jQuery("#childLayout").hide();
    }
	actionOpt = data;
	jQuery("#colorValue").val(data.cellColor);
	jQuery("#color_check").val(data.cellColor);
	jQuery("#color_op").val(data.cellAlpha);
	if(!!data.cellColor) {
		jQuery("#colorType").removeAttr("disabled")[0].checked = false;
	} else {
		jQuery("#colorType").attr("disable","disable")[0].checked = true;
	}
	jQuery("#defaultColor").val(cellDefaultColor);
    getNum(parseInt(data.cellState != "" ? data.cellState : 0));
	jQuery("input[name='isAuth'][value='"+data.isAuth+"']")[0].checked = true;

	jQuery("#minSoftVer_op").val(data.minSoftVer == null ? "" : data.minSoftVer);
    jQuery("#productCode").val(data.productCode == null ? "" : data.productCode);
    jQuery("#offsetPos").val(data.margin == null ? "" : data.margin);
	var pictures = data.posterList;
	jQuery("#imageView").empty();

	var img_url = globalWebAppURL + "/manager/titleLayout/findPosterList?sourceId=" + data.id;
	jQuery.get(img_url, {}, function(imgs) {
		for(var i = 0; i < imgs.length; i++) {
			var p = imgs[i];
			var url = p.posterServerHttp + p.localPath;
			var _w = Math.round((p.width * 1 + 10 * pNum) / (75 * pNum));
			var _h = Math.round((p.height * 1 + 10 * pNum) / (75 * pNum));

			var w = _w * 40 + 5;
			var h = _h * 40 + 5;
			var bgColor = data.cellColor || cellDefaultColor;
			bgColor = getRgbaColor(bgColor, data.cellAlpha);
			var spanHtml = "<span style='text-align:center;display:block;width:" + w + "px'>" + p.strTerminalType + "-" + p.strResolution + "-" + p.width + "*" + p.height + "</span>";
			jQuery("#imageView").append("<div><div style='background-color:" + bgColor + "'><img onclick='openUrl(\"" + url + "\")' width='" + w + "px' height='" + h + "px' src='" + url + "'></img></div>" + spanHtml + "</div>");
		}
	});
	jQuery("#bg").show();
}

function getNum(_num){
    jQuery("input[name='scale'][value='"+ (_num & 1) +"']")[0].checked = true;
    jQuery("input[name='drage'][value='"+ (_num & 2)/2 +"']")[0].checked = true;
    jQuery("input[name='delete'][value='"+ (_num & 4)/4 +"']")[0].checked = true;
    jQuery("input[name='focus'][value='"+ (_num & 8)/8 +"']")[0].checked = true;
    jQuery("input[name='click'][value='"+ (_num & 16)/16 +"']")[0].checked = true;
    jQuery("input[name='showname'][value='"+ (_num & 32)/32 +"']")[0].checked = true;
    jQuery("input[name='isTop'][value='"+ (_num & 64)/64 +"']")[0].checked = true;
    jQuery("input[name='cellTitle'][value='"+ (_num & 128)/128 +"']")[0].checked = true;
    jQuery("input[name='isImok'][value='"+ (_num & 256)/256 +"']")[0].checked = true;
    //console.log(_num,(_num & 1),(_num & 2)/2,(_num & 4)/4,(_num & 8)/8,(_num & 16)/16,(_num & 32)/32,(_num & 64)/64,(_num & 128)/128,(_num & 256)/256);
}

//打开预览图片
function openUrl(url) {
	window.open(url);
}

//保存模块参数
function saveModuleOpt() {
	if(!submitCheck()) {
		return void (0);
	}
	actionOpt.cellColor = jQuery("#colorValue").val();
	var color = actionOpt.cellColor || cellDefaultColor;
	color = getRgbaColor(color, jQuery("#color_op").val());
	jQuery("#" + actionOpt.id).css("background-color", color);
	actionOpt.cellAlpha = jQuery("#color_op").val();
	var scale = jQuery("input[name='scale']:checked").val();
	var drage = jQuery("input[name='drage']:checked").val();
	var del = jQuery("input[name='delete']:checked").val();
	var focus = jQuery("input[name='focus']:checked").val();
	var click = jQuery("input[name='click']:checked").val();
	var showname = jQuery("input[name='showname']:checked").val();
    var isTop = jQuery("input[name='isTop']:checked").val();
    var cellTitle = jQuery("input[name='cellTitle']:checked").val();
    var isImok = jQuery("input[name='isImok']:checked").val();
    var num=""+isImok+cellTitle+isTop+showname+click+focus+del+drage+scale;
	actionOpt.cellState = parseInt(num, 2).toString(10);
	actionOpt.newMove = "1";
	actionOpt.isAuth = jQuery("input[name='isAuth']:checked").val();
	actionOpt.minSoftVer = jQuery("#minSoftVer_op").val();
    actionOpt.productCode = jQuery("#productCode").val();
    actionOpt.margin = jQuery("#offsetPos").val();
	isEdit = true;
	hideDialog();
}

//返回rgba色值
function getRgbaColor(color, opacity) {
	if(color.length < 7)
		return;
	var one = transTheNum(color.substring(1, 3), 16, 10);
	var two = transTheNum(color.substring(3, 5), 16, 10);
	var three = transTheNum(color.substring(5, 7), 16, 10);
	var opa = opacity * 1 / 100;
	return "rgba(" + one + "," + two + "," + three + "," + opa + ")";
}

//进制转换 num要转换的值 oldType值原类型 newType要转成的类型
function transTheNum(num, oldType, newType) {
	return parseInt(num, oldType).toString(newType);
}

function hideDialog() {
	jQuery("#bg").hide();
}

//重新生成格子
function reSetCell() {
	jQuery("#content").empty();
	jQuery("#content").siblings().remove();
	jQuery(".all_content").scrollLeft(0);
	buildMContent(content, "content");
}

//添加格子
function addCell() {
	var opt = {
		x : 15,
		y : 8
	};
	buildMContent(opt, "content");
}

//加载下一页数据
function loadNextPage() {
	if(array_tag.length) {
		var arr = array_tag.splice(0, amountOfPage);
		setM(arr);
		array_tag.length ? addColor() : removeColor();
	}
}

function removeColor() {
	jQuery(".getmore input").css("color", "#928E8E");
}

function addColor() {
	jQuery(".getmore input").css("color", "#006ec1");
}

//全部加载
function loadAll() {
	if(array_tag.length) {
		var arr = array_tag.splice(0, array_tag.length);
		setM(arr);
		array_tag.length ? addColor() : removeColor();
	}
}

//资源过滤
function filterModule(index, type) {
	//var a=new Date();
	reSetCell();
	ModuleFocus(index);
	var arr = [];
	for(var i = 0; i < sou.length; i++) {
		var id = sou[i].categoryResourceId;
		var o = jQuery(".layout #" + id);
		if(o.length == 0) {
			if(!type) {
				arr.push(sou[i]);
			} else if(sou[i].resourceType == type) {
				arr.push(sou[i]);
			}
		}
	}
	array_tag = arr;
	loadNextPage();
}

//颜色选择
/*jQuery("#color_check").bind("change", function() {
	jQuery("#colorValue").val(this.value);
});*/






jQuery("input[type=color]").each(function(){
	$(this).bind("change", function() {
		$(this).prev().val(this.value);
	})
	
});











//填写颜色
jQuery("#colorValue").bind("blur", function() {
	var exg = /\#[a-f0-9]{6}/;
	if(exg.test(this.value)) {
		jQuery("#color_check").val(this.value);
	} else {
//		this.value = "";
		//alert("对不起，输入的颜色值格式不对！");
	}
});
//修改透明度
jQuery("#color_op").bind("change", changeOpa);
jQuery("#color_op").bind("input", changeOpa);
function changeOpa() {
	var v = this.value * 1;
	if(v > 100)
		this.value = 100;
	else if(v < 0)
		this.value = 0;
	var color = jQuery("#colorValue").val() || cellDefaultColor;
	var rbga = getRgbaColor(color, this.value);
	jQuery("#imageView>div>div").css("background-color", rbga);
}

//默认颜色选项
jQuery("#colorType").bind("change", function() {
	if(this.checked) {
		jQuery("#colorValue").val("");
		var color = getRgbaColor(cellDefaultColor, jQuery("#color_op").val());
		jQuery("#imageView>div>div").css("background-color", color);
	} else {
		jQuery("#colorValue").val(jQuery("#color_check").val());
		var color = getRgbaColor(jQuery("#color_check").val(), jQuery("#color_op").val());
		jQuery("#imageView>div>div").css("background-color", color);
	}
});
