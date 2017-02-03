 var _menus = {};
 
$(function() {
	$.ajax({
		type:"post",
		url:globalWebAppURL+"/menuTree?module="+module,
		data:null,
		async:false,
		success:function(data){
			_menus.basic = data;
		}
	});
	
	tabClose();
	tabCloseEven();

	$('#css3menu a').click(function() {
		$('#css3menu a').removeClass('active');
		$(this).addClass('active');
		
		var d = _menus[$(this).attr('name')];
		Clearnav();
		addNav(d);
		InitLeftMenu();
	});

	//导航菜单绑定初始化
	$("#wnav").accordion( {
		animate : false,
		fit : false,
		width : 'auto',
		height : 'auto'
	});

	var firstMenuName = $('#css3menu a:first').attr('name');
	addNav(_menus[firstMenuName]);
	InitLeftMenu();
	
	$("#wnav .panel").css('width','100%'); 
	$("#wnav .panel .panel-header").css('width','100%');
	$("#wnav .panel .panel-body").css('width','100%');
});

function Clearnav() {
	var pp = $('#wnav').accordion('panels');

	$.each(pp, function(i, n) {
		if (n) {
			var t = n.panel('options').title;
			$('#wnav').accordion('remove', t);
		}
	});

	pp = $('#wnav').accordion('getSelected');
	if (pp) {
		var title = pp.panel('options').title;
		$('#wnav').accordion('remove', title);
	}
}

function addNav(data) {

	$.each(data, function(i, sm) {
		var menulist = "";
		menulist += '<ul>';
		var num = 0;
		$.each(sm.subResources, function(j, o) {
			if(o.auth == 1){
				menulist += '<li><div><a id="' + o.code + '" ref="' + o.id + '" href="#" rel="'+globalWebAppURL
				+ o.action + '" ><span class="icon icon-nav" >&nbsp;</span><span class="nav">' + o.name
				+ '</span></a></div></li> ';
				num++;
			}
		});
		menulist += '</ul>';

		if (num !=0) {
			$('#wnav').accordion('add', {
				title : sm.name,
				content : menulist,
				iconCls : 'icon icon-sys'
			});
		}

	});

	var pp = $('#wnav').accordion('panels');
	if (pp.length > 0) {
		var t = pp[0].panel('options').title;
		$('#wnav').accordion('select', t);
	}

}

//初始化左侧
function InitLeftMenu() {
	
	hoverMenuItem();

	$('#wnav li a').on('click', function() {
		var tabTitle = $(this).children('.nav').text();

		var url = $(this).attr("rel");
		var menuId = $(this).attr("ref");
		var icon = getIcon(menuId, icon);

		addTab("tab"+menuId,tabTitle, url, icon);
		$('#wnav li div').removeClass("selected");
		$(this).parent().addClass("selected");
	});

}

/**
 * 菜单项鼠标Hover
 */
function hoverMenuItem() {
	$(".easyui-accordion").find('a').hover(function() {
		$(this).parent().addClass("hover");
	}, function() {
		$(this).parent().removeClass("hover");
	});
}

//获取左侧导航的图标
function getIcon(menuId) {
	var icon = 'icon ';
	$.each(_menus, function(i, n) {
		$.each(n, function(j, o) {
			$.each(o.subResources, function(k, m){
				if (m.id == menuId) {
					icon += "icon-nav"
					return false;
				}
			});
		});
	});
	return icon;
}

function addTab(id,subtitle, url, icon) {
	if (!$('#tabs').tabs('exists', subtitle)) {
		$('#tabs').tabs('add', {
			title : subtitle,
			content : createFrame(id,url),
			closable : true,
			icon : icon
		});
	} else {
		$('#tabs').tabs('select', subtitle);
		$('#mm-tabupdate').click();
	}
	refreshTab(id,subtitle,url);
	tabClose();
}

function refreshTab(id,title,url){
	 if ($('#tabs').tabs('exists', title)){  
         var currTab = $('#tabs').tabs('getTab', title),  
             iframe = $(currTab.panel('options').content),  
             content = '<iframe id="'+id+'" scrolling="auto" frameborder="0"  src="' + iframe.attr('src') + '" style="width:100%;height:100%;"></iframe>';  
         $('#tabs_frame>div.window>div.window-body').window('destroy');
         $('#tabs').tabs('update', {tab: currTab, options: {content: content, closable: true}});  
     }  
}

function createFrame(id,url) {
	var s = '<iframe id="'+id+'" scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>';
	return s;
}

function tabClose() {
	/* 双击关闭TAB选项卡 */
	$(".tabs-inner").dblclick(function() {
		var subtitle = $(this).children(".tabs-closable").text();
		$('#tabs').tabs('close', subtitle);
	});
	/* 为选项卡绑定右键 */
	$(".tabs-inner").bind('contextmenu', function(e) {
		$('#mm').menu('show', {
			left : e.pageX,
			top : e.pageY
		});

		var subtitle = $(this).children(".tabs-closable").text();

		$('#mm').data("currtab", subtitle);
		$('#tabs').tabs('select', subtitle);
		return false;
	});
}
//绑定右键菜单事件
function tabCloseEven() {
	//刷新
	$('#mm-tabupdate').click(function() {
		var currTab = $('#tabs').tabs('getSelected');
		var url = $(currTab.panel('options').content).attr('src');
		$('#tabs').tabs('update', {
			tab : currTab,
			options : {
				content : createFrame(url)
			}
		});
	});
	//关闭当前
	$('#mm-tabclose').click(function() {
		var currtab_title = $('#mm').data("currtab");
		$('#tabs').tabs('close', currtab_title);
	});
	//全部关闭
	$('#mm-tabcloseall').click(function() {
		$('.tabs-inner span').each(function(i, n) {
			var t = $(n).text();
			if ("欢迎页面" != t) {
				$('#tabs').tabs('close', t);
			}
		});
	});
	//关闭除当前之外的TAB
	$('#mm-tabcloseother').click(function() {
		$('#mm-tabcloseright').click();
		$('#mm-tabcloseleft').click();
	});
	//关闭当前右侧的TAB
	$('#mm-tabcloseright').click(function() {
		var nextall = $('.tabs-selected').nextAll();
		if (nextall.length == 0) {
			//msgShow('系统提示','后边没有啦~~','error');
			//alert('后边没有啦~~');
			return false;
		}
		nextall.each(function(i, n) {
			var t = $('a:eq(0) span', $(n)).text();
			if ("欢迎页面" != t) {
				$('#tabs').tabs('close', t);
			}
		});
		return false;
	});
	//关闭当前左侧的TAB
	$('#mm-tabcloseleft').click(function() {
		var prevall = $('.tabs-selected').prevAll();
		if (prevall.length == 0) {
			//alert('到头了，前边没有啦~~');
			return false;
		}
		prevall.each(function(i, n) {
			var t = $('a:eq(0) span', $(n)).text();
			if ("欢迎页面" != t) {
				$('#tabs').tabs('close', t);
			}
		});
		return false;
	});

	//退出
	$("#mm-exit").click(function() {
		$('#mm').menu('hide');
	});
}

//弹出信息窗口 title:标题 msgString:提示信息 msgType:信息类型 [error,info,question,warning]
function msgShow(title, msgString, msgType) {
	$.messager.alert(title, msgString, msgType);
}

//本地时钟
function clockon() {
	var now = new Date();
	var year = now.getFullYear(); //getFullYear getYear
	var month = now.getMonth();
	var date = now.getDate();
	var day = now.getDay();
	var hour = now.getHours();
	var minu = now.getMinutes();
	var sec = now.getSeconds();
	var week;
	month = month + 1;
	if (month < 10)
		month = "0" + month;
	if (date < 10)
		date = "0" + date;
	if (hour < 10)
		hour = "0" + hour;
	if (minu < 10)
		minu = "0" + minu;
	if (sec < 10)
		sec = "0" + sec;
	var arr_week = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
	week = arr_week[day];
	var time = "";
	time = year + "年" + month + "月" + date + "日" + " " + hour + ":" + minu
			+ ":" + sec + " " + week;

	$("#bgclock").html(time);

	var timer = setTimeout("clockon()", 200);
}
