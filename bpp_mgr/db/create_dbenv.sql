#db文件夹必须放在Mysql bin路径下：source ./db/create_dbenv.sql
#创建一个完整生成管理平台环境的数据库脚本：所有drop脚本、create脚本依次执行
#注意：该脚本将清除当前DB的所有数据(该脚本仅用于建立初始运行环境或测试环境)
source ./db/mms_msg.sql
source ./db/sysMgr\t_sys_seq.sql
source ./db/sysMgr\t_sys_application.sql
source ./db/sysMgr\t_sys_log.sql
source ./db/sysMgr\t_sys_org_param.sql
source ./db/sysMgr\t_sys_organization.sql
source ./db/sysMgr\t_sys_relate.sql
source ./db/sysMgr\t_sys_resource.sql
source ./db/sysMgr\t_sys_role.sql
source ./db/sysMgr\t_sys_user.sql
source ./db/sysMgr\insertdata.sql