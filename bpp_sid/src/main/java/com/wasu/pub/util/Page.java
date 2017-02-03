package com.wasu.pub.util;

import java.io.Serializable;
import java.util.List;
 
/**
 * 分页封装�?
 * 用于做分页查询的基础类，封装了一些分页的相关属�??
 * @version v1.0
 * @param <T>
 */
public class Page<T> implements Serializable {
 
    //下一页
    private int pageNo;
 

 
    //每页个数
    private int pageSize =10;
 
    //总条数
    private int totalCount = 0;
 
    //总页数
    private int pageCount;
 
    //记录
    private List<T> results;
    
    public Page(){}
    public Page(int rows,int page){
    	this.pageSize=rows;
    	this.pageNo=page;
    }
 
    public int getPageCount() {
        return pageCount;
    }
 
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
 
    public int getPageNo() {
        if (pageNo <= 0) {
            return 1;
        } else{
            return pageNo > pageCount ? pageCount : pageNo;
        }
    }
 
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
 
    public List<T> getResults() {
        return results;
    }
 
    public void setResults(List<T> results) {
        this.results = results;
    }
 

 
    public int getPageSize() {
        return pageSize;
    }
 
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
    }
 
    public int getTotalCount() {
        return totalCount;
    }
 
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        if(pageSize>0){
        	pageCount=totalCount%pageSize==0?totalCount/pageSize:totalCount/pageSize+1;
        }
    }
 
    public String toString(){
    	String result= "pageNo="+pageNo+" pageSize="+pageSize+" totalCount="+totalCount+" pageCount="+pageCount+" results=[";
    	if(results!=null){
    		for(Object o:results){
    			if(o!=null)
    			result+=" ["+o.toString()+" ]";
    			else
    			result+=" [null]";
    		}
    	}
    	return result;
    }
 
}