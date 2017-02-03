DROP TABLE IF EXISTS mms_msg;

CREATE TABLE mms_msg (
	id BIGINT (20) NOT NULL AUTO_INCREMENT,
	createTime datetime DEFAULT NULL,
	lastUpdateTime datetime DEFAULT NULL,
	title VARCHAR (255) DEFAULT NULL,
	sendTime datetime NOT NULL,
	content VARCHAR (1000) NOT NULL,
	stbId TEXT,
	areaId TEXT,
	custId TEXT,
	cronExpr VARCHAR (100) DEFAULT NULL,
	validTime datetime NOT NULL,
	scope VARCHAR (2) NOT NULL,
	vtype VARCHAR (2) NOT NULL,
	status VARCHAR (5) NOT NULL,
	sysUserId VARCHAR (20) NOT NULL,
	sysRoleId VARCHAR (20) NOT NULL,
	dataSrc VARCHAR (2) NOT NULL,
	PRIMARY KEY (id)
) ENGINE = INNODB AUTO_INCREMENT=1 DEFAULT CHARSET = utf8;

ALTER TABLE `mms_msg` ADD INDEX indx_mms_msg_status (`status`);
ALTER TABLE `mms_msg` ADD INDEX indx_mms_msg_datastat (`dataSrc`, `status`);