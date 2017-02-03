/**
 * 数据字段查询前端代码逻辑封装
 */
var dictParamsQuery = {};
(function() {
	dictParamsQuery.fetchDictParams = function(fieldId, url) {//获取远程字段参数 list
		$.getJSON(url, function(data) {
			data.unshift({
				'paramKey' : '',
				'paramValue' : '请选择',
				'selected' : true
			});
			$('#' + fieldId).combobox('loadData', data);
		});
	};

	dictParamsQuery.fetchCPList = function(fieldId, url) {//获取远程CP集合list
		$.ajax({
			url : url,
			method : 'GET',
			dataType : 'json',
			success : function(data) {
				data.unshift({
					'providerId' : '',
					'abbrName' : '请选择',
					'selected' : true
				});
				$('#' + fieldId).combobox('loadData', data);
			},
			async : false
		});
	};

	dictParamsQuery.fetchAssetShowList = function(fieldId, url) {//获取远程assetShow集合list
		$.ajax({
			url : url,
			method : 'GET',
			dataType : 'json',
			success : function(data) {
				data.unshift({
					'showType' : '',
					'showName' : '请选择',
					'selected' : true
				});
				$('#' + fieldId).combobox('loadData', data);
			},
			async : false
		});
	};
})();