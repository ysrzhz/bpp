DROP TABLE IF EXISTS `t_sys_resource`;
CREATE TABLE `t_sys_resource` (
  `id` varchar(255) NOT NULL,
  `action` varchar(255) DEFAULT NULL,
  `appId` varchar(255) DEFAULT NULL COMMENT '应用ID',
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parentId` varchar(255) DEFAULT NULL COMMENT '菜单父ID',
  `rank` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `appId` (`appId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;