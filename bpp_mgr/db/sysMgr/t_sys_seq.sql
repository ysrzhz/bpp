DROP TABLE IF EXISTS `t_sys_seq`;
CREATE TABLE `t_sys_seq` (
  `table_name` varchar(255) NOT NULL,
  `current_value` bigint(20) DEFAULT NULL,
  `cycle` int(11) NOT NULL,
  `increment_value` bigint(20) DEFAULT NULL,
  `max_value` bigint(20) DEFAULT NULL,
  `min_value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;