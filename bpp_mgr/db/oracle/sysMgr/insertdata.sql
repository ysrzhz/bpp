delete from t_sys_application;
delete from t_sys_organization;
delete from t_sys_user;
delete from t_sys_role;
delete from t_sys_resource;
delete from t_sys_relate;

INSERT INTO t_sys_application VALUES ('YY001', 'mms', '��Ϣ����ϵͳ');

INSERT INTO t_sys_organization VALUES ('ZZ001', 'wasu', '1', '��������Ա', '��ʼ�������Ĺ���Ա��֯');
INSERT INTO t_sys_organization VALUES ('ZZ002', 'admin', '0', '��ͨ����Ա', 'Ȩ�޵��ڳ�������Ա');
INSERT INTO t_sys_organization VALUES ('ZZ003', 'user', '0', '����Ա', '��Ӫ��Ա');

INSERT INTO t_sys_user VALUES ('YH001', 'admin', '1', '����Ա', 'ZZ001', '96e79218965eb72c92a549dd5a330112', null, null, null, null, null, null, null, null, null, null, null, null, null);

INSERT INTO t_sys_role VALUES ('JS001', 'ϵͳ�����ɫ', '', null);

INSERT INTO t_sys_resource VALUES ('ZY0014', '/views/manage/role.jsp', 'YY001', 'roleMgr', '��ɫ����', 'ZY001', '4');
INSERT INTO t_sys_resource VALUES ('ZY0011', '/views/manage/app.jsp', 'YY001', 'appMgr', 'Ӧ�ù���', 'ZY001', '1');
INSERT INTO t_sys_resource VALUES ('ZY0012', '/views/manage/org.jsp', 'YY001', 'orgMgr', '��֯����', 'ZY001', '2');
INSERT INTO t_sys_resource VALUES ('ZY0015', '/views/manage/resource.jsp', 'YY001', 'resMgr', '��Դ����', 'ZY001', '5');
INSERT INTO t_sys_resource VALUES ('ZY0021', '/views/messManager/messManager_list.jsp', 'YY001', 'getMsgList', '��Ϣ����', 'ZY002', '1');
INSERT INTO t_sys_resource VALUES ('ZY0013', '/views/manage/user.jsp', 'YY001', 'userMgr', '�û�����', 'ZY001', '3');
INSERT INTO t_sys_resource VALUES ('ZY01', '', 'YY001', 'mms', '��Ϣ����ϵͳ', '', '1');
INSERT INTO t_sys_resource VALUES ('ZY002', '', 'YY001', 'msgMgr', '��Ϣ����', 'ZY01', '1');
INSERT INTO t_sys_resource VALUES ('ZY0016', '/views/manage/log.jsp', 'YY001', 'logMgr', '��־����', 'ZY001', '6');
INSERT INTO t_sys_resource VALUES ('ZY001', '', 'YY001', 'sysMgr', 'ϵͳ����', 'ZY01', '2');

INSERT INTO t_sys_relate VALUES ('GL001', 'ZZ001', 'JS001', 'orgRole');
INSERT INTO t_sys_relate VALUES ('GL002', 'YH001', 'JS001', 'ZZ001');
INSERT INTO t_sys_relate VALUES ('GL003', 'JS001', 'ZY01', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL004', 'JS001', 'ZY001', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL005', 'JS001', 'ZY002', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL006', 'JS001', 'ZY0011', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL007', 'JS001', 'ZY0012', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL008', 'JS001', 'ZY0013', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL009', 'JS001', 'ZY0014', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL010', 'JS001', 'ZY0015', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL011', 'JS001', 'ZY0016', 'YY001');
INSERT INTO t_sys_relate VALUES ('GL012', 'JS001', 'ZY0021', 'YY001');
commit;