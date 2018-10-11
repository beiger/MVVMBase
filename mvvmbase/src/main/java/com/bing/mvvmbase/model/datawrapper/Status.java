package com.bing.mvvmbase.model.datawrapper;

/**
 * 数据请求状态
 */
public enum Status {
	/**
	 * 正在加载中
	 */
	LOADING,

	/**
	 * 加载成功
	 */
	SUCCESS,

	/**
	 * 加载失败
	 */
	ERROR,

	/**
	 * 内容为空
	 */
	NONE
}
