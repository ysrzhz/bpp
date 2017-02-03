INSERT INTO `t_sys_application` VALUES ('CyoWdmPgzG8H', 'mms', '消息管理系统');

INSERT INTO `t_sys_organization` VALUES ('QFS2vZycXf1t', 'wasu', '1', '超级管理员', '初始化建立的管理员组织');
INSERT INTO `t_sys_organization` VALUES ('HpudA14ta5ta', 'admin', '0', '普通管理员', '权限低于超极管理员');
INSERT INTO `t_sys_organization` VALUES ('NdMqlZgaFKoy', 'user', '0', '操作员', '运营人员');

INSERT INTO `t_sys_user` VALUES ('1k0S4ploD0y0', 'admin', '1', '管理员', 'QFS2vZycXf1t', '96e79218965eb72c92a549dd5a330112', null, null, null, null, null, null, null, null, null, null, null, null, null);

INSERT INTO `t_sys_role` VALUES ('pgDqbf2V6FWD', '系统管理角色', '', null);

INSERT INTO `t_sys_resource` VALUES ('CyoWdmPgzG61', '/views/manage/role.jsp', 'CyoWdmPgzG8H', 'roleMgr', '角色管理', 'Zbp7kkvkNqsX', '4');
INSERT INTO `t_sys_resource` VALUES ('CyoWdmPgzG71', '/views/manage/app.jsp', 'CyoWdmPgzG8H', 'appMgr', '应用管理', 'Zbp7kkvkNqsX', '1');
INSERT INTO `t_sys_resource` VALUES ('CyoWdmPgzG81', '/views/manage/org.jsp', 'CyoWdmPgzG8H', 'orgMgr', '组织管理', 'Zbp7kkvkNqsX', '2');
INSERT INTO `t_sys_resource` VALUES ('DTNQHrTuqk34', '/views/manage/resource.jsp', 'CyoWdmPgzG8H', 'resMgr', '资源管理', 'Zbp7kkvkNqsX', '5');
INSERT INTO `t_sys_resource` VALUES ('hER91pMDjEZH', '/views/messManager/messManager_list.jsp', 'CyoWdmPgzG8H', 'getMsgList', '消息管理', 'p4l6CtXm0Yia', '1');
INSERT INTO `t_sys_resource` VALUES ('iCBlWFX9hTk9', '/views/manage/user.jsp', 'CyoWdmPgzG8H', 'userMgr', '用户管理', 'Zbp7kkvkNqsX', '3');
INSERT INTO `t_sys_resource` VALUES ('Js6rnYgzKQTt', '', 'CyoWdmPgzG8H', 'mms', '消息管理系统', '', '1');
INSERT INTO `t_sys_resource` VALUES ('p4l6CtXm0Yia', '', 'CyoWdmPgzG8H', 'msgMgr', '消息管理', 'Js6rnYgzKQTt', '1');
INSERT INTO `t_sys_resource` VALUES ('VckZQeQJV96M', '/views/manage/log.jsp', 'CyoWdmPgzG8H', 'logMgr', '日志管理', 'Zbp7kkvkNqsX', '6');
INSERT INTO `t_sys_resource` VALUES ('Zbp7kkvkNqsX', '', 'CyoWdmPgzG8H', 'sysMgr', '系统管理', 'Js6rnYgzKQTt', '2');

INSERT INTO `t_sys_relate` VALUES ('3kSAbDWklSen', 'pgDqbf2V6FWD', 'CyoWdmPgzG71', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('8lPha3AVGmwK', 'pgDqbf2V6FWD', 'Zbp7kkvkNqsX', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('8Zysi13cl6m7', '1k0S4ploD0y0', 'pgDqbf2V6FWD', 'QFS2vZycXf1t');
INSERT INTO `t_sys_relate` VALUES ('B8bcuAc72TQ6', 'pgDqbf2V6FWD', 'iCBlWFX9hTk9', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('bt5wYrBVbaD8', 'pgDqbf2V6FWD', 'p4l6CtXm0Yia', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('CdpaZqyK6lmh', 'pgDqbf2V6FWD', 'hER91pMDjEZH', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('JiQUdbDcYpJs', 'QFS2vZycXf1t', 'pgDqbf2V6FWD', 'orgRole');
INSERT INTO `t_sys_relate` VALUES ('r9aSV8F72S57', 'pgDqbf2V6FWD', 'VckZQeQJV96M', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('t4MMf7F6BdEG', 'pgDqbf2V6FWD', 'DTNQHrTuqk34', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('TVFGIfiHyQHH', 'pgDqbf2V6FWD', 'Js6rnYgzKQTt', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('vwTHc7ARFgCy', 'pgDqbf2V6FWD', 'CyoWdmPgzG61', 'CyoWdmPgzG8H');
INSERT INTO `t_sys_relate` VALUES ('x7RIUsb7Twar', 'pgDqbf2V6FWD', 'CyoWdmPgzG81', 'CyoWdmPgzG8H');