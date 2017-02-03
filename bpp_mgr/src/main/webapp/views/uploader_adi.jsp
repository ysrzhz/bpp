<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


	<div class="easyui-layout" fit="true">
		<div region="center"> 
			<table  fit="true">
				<tr>
					<td class="align_right"><em><font color="red">*</font></em> <label style="font:26px;">选择ADI</label>
					</td>
					<td class="input_style">
						<div class="upload_div">
							<input id="myfiles" name="myfiles" class="upload_button" type="file" value="本地上传"/>
							<div id="file_queue"></div>
							<h4>每次最多可上传1000，单个文件限制在5M以下</h4>
							<h4>需要配合dsadapter才能使用</h4>
						</div>
					</td>
				</tr>
			</table>
		</div>

	</div>

<script type="text/javascript">

//页面入口处
$(function(){
	$("#myfiles").uploadify({
		'auto':true,  
		'scriptAccess':'always',
        //按钮额外自己添加点的样式类.upload
        'buttonClass'        :    'upload',
        //限制文件上传大小
        'fileSizeLimit'        :    '5MB',
        //文件选择框显示
        'fileTypeDesc'        :     '选择ADI文件',
        //文件类型过滤
        'fileTypeExts'         :     '*.xml',
        //请求类型
        'method'            :    'post',
        //是否支持多文件上传
        'multi'                :     true,
        //队列ID，用来显示文件上传队列与进度
        'queueID'            :    'file_queue',
        //上传完成后删除队列
        'removeCompleted'    :true,
        'removeTimeout':	 '60',//60秒
        //队列一次最多允许的文件数，也就是一次最多进入上传队列的文件数
        'queueSizeLimit'    :    1000,
        //上传动画，插件文件下的swf文件
        'swf'                :    globalWebAppURL+'/resources/js/upload/flash/uploadify.swf',
        //处理上传文件的服务类
        'uploader'            :    '/xx/xxx.do',
        //上传文件个数限制
        'uploadLimit'        :   1000,
        //上传按钮内容显示文本
        'buttonText'        :    '选择待上传ADI文件'
    });


});


	</script>
