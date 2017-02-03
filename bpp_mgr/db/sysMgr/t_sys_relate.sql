DROP TABLE IF EXISTS `t_sys_relate`;
CREATE TABLE `t_sys_relate` (
  `id` varchar(255) NOT NULL,
  `objectId` varchar(255) DEFAULT NULL COMMENT 'orgId/userId/roleId',
  `relatedId` varchar(255) DEFAULT NULL COMMENT 'roleId/roleId/resourceId',
  `typeId` varchar(255) DEFAULT NULL COMMENT 'orgRole字符串/orgId/appId',
  PRIMARY KEY (`id`),
  KEY `objectId` (`objectId`) USING BTREE,
  KEY `relatedId` (`relatedId`) USING BTREE,
  KEY `typeId` (`typeId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;