package com.wasu.pub.annotation;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface WsTitle {
      public String chName() default "";  //标注该字段列表中显示的中文Title
      public String code() default "";  //标注该字段的code值是多少，默认采用fieldName，此字段有值，则用此字段
      public boolean isShow() default true;//此字段标志是否显示在列表中
      public boolean isQuery() default false;//此字段是否需要进行查询
      public boolean isRelate() default false; //此字段是否关联到其他的
      public String  rlClass() default "";//关联的实例名称
      public String  rlFieldCode() default "";//关联的字段
      public String  rlFieldName() default "";//需要获取的字段
      public String  rlShowFieldName() default "";//默认为空的话展示rlFieldName的值
      
      public boolean isSelect() default false;
      public String selectOptionName() default  "";
      
      public boolean isAutoCode() default false;
      public boolean isSwitch() default false;
      public boolean isNumber() default false;
      public boolean isDup() default false;//是否校验重复，如果校验重复，则去后台判断该值是否存在
      
      public boolean isPrecise() default true;//是否精确查找,默认精确查找，如需模糊查询需指定
      
      public int seq() default 0; //展示排序
}
