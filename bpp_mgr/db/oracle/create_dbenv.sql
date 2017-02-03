--db文件夹必须放在Mysql bin路径下：@create_dbenv.sql
--创建一个完整生成管理平台环境的数据库脚本：所有drop脚本、create脚本依次执行
--注意：该脚本将清除当前DB的所有数据(该脚本仅用于建立初始运行环境或测试环境)
@mms_msg.sql
@sysMgr/t_sys_seq.sql
@sysMgr/t_sys_application.sql
@sysMgr/t_sys_log.sql
@sysMgr/t_sys_org_param.sql
@sysMgr/t_sys_organization.sql
@sysMgr/t_sys_relate.sql
@sysMgr/t_sys_resource.sql
@sysMgr/t_sys_role.sql
@sysMgr/t_sys_user.sql
@sysMgr/insertdata.sql