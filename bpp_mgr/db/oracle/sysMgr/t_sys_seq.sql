DROP TABLE t_sys_seq;
CREATE TABLE t_sys_seq (
  table_name varchar2(255) NOT NULL,
  current_value number(20),
  cycle number(11) NOT NULL,
  increment_value number(20),
  max_value number(20),
  min_value number(20)
);
alter table t_sys_seq add constraint pk_t_sys_seq primary key (table_name)
  using index 
  tablespace SYSTEM
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
comment on table t_sys_seq is '序列表';
comment on column t_sys_seq.table_name is '表名';