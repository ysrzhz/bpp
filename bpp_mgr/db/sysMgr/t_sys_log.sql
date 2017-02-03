DROP TABLE IF EXISTS `t_sys_log`;
CREATE TABLE `t_sys_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app` int(11) DEFAULT NULL,
  `content` TEXT,
  `ip` varchar(255) DEFAULT NULL,
  `lev` int(11) DEFAULT NULL,
  `operationtime` datetime DEFAULT NULL,
  `u_account` varchar(255) DEFAULT NULL,
  `u_id` varchar(255) DEFAULT NULL,
  `u_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;