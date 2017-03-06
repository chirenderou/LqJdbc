package com.lq.util.jdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 分页对象. 包含当前页数据及分页信息如总记录数.<br>
 * @author 吃人的肉
 * QQ:376870344<br>
 * email:liuqingrou@163.com
 */
@SuppressWarnings({"serial"})
public class Page implements Serializable {
	
	public static int DEFAULT_GROUP_PAGE_SIZE=Integer.valueOf(LqDBOperator.groupPageSize);
	/**
	 * 每页的记录数
	 */
	public static int DEFAULT_PAGE_SIZE =Integer.valueOf(LqDBOperator.pageSize);
	/**
	 * 每页的记录数
	 */
	private int pageSize = DEFAULT_PAGE_SIZE; // 每页的记录数
	/**
	 * 当前页第一条数据在List中的位置,从0开始
	 */
	private long start; // 当前页第一条数据在List中的位置,从0开始
	/**
	 * 当前页中存放的记录,类型一般为List
	 */
	private List data; // 当前页中存放的记录,类型一般为List
	/**
	 * 总记录数
	 */
	private long totalCount; // 总记录数
	/**
	 * 组结束页
	 */
	private int groupEndPage;
	/**
	 * 组开始页
	 */
	private int groupBeginPage;
	/**
	 * 第一页
	 */
	private long beginPage;
	/**
	 * 上一页
	 */
	private long upPage;
	/**
	 * 下一页
	 */
	private long downPage;
	/**
	 * 最后一页
	 */
	private long endPage;
	/**
	 * 当前页
	 */
	private long beforePage;
	

	/**
	 * 构造方法，只构造空页.
	 */
	public Page() {
		this(0, 0, DEFAULT_PAGE_SIZE, new ArrayList(),DEFAULT_GROUP_PAGE_SIZE);
	}

	/**
	 * 默认构造方法.
	 * 
	 * @param start
	 *            本页数据在数据库中的起始位置
	 * @param totalSize
	 *            数据库中总记录条数

	 * 
	 * @param pageSize
	 *            本页容量
	 * @param data
	 *            本页包含的数据

	 * 
	 */
	public Page(long start, long totalSize, int pageSize, List data,int defaultGroupPageSize) {
		this.pageSize = pageSize;
		this.start = start;
		this.totalCount = totalSize;
		this.data = data;
		this.beginPage=1;
		if (getCurrentPageNo()-1<=0) {
			this.upPage=1;
		}else {
			this.upPage=getCurrentPageNo()-1;
		}
		if (hasNextPage()) {
			this.downPage=getCurrentPageNo()+1;
		}else {
			this.downPage=getCurrentPageNo();
		}
		this.beforePage=getCurrentPageNo();
		this.endPage=getTotalPageCount();
		int DEFAULT_GROUP_PAGE_SIZE=defaultGroupPageSize;//一页几组
		int bbb=DEFAULT_GROUP_PAGE_SIZE-(int) (getCurrentPageNo()%DEFAULT_GROUP_PAGE_SIZE);//一种可能是最后一组不满默认组数
		if (bbb==DEFAULT_GROUP_PAGE_SIZE) {
			bbb=0;
		}
		int aaa=(int) (getCurrentPageNo()/DEFAULT_GROUP_PAGE_SIZE);//当前第几组
		if (aaa==0) {
			aaa=1;
		}else {
			if (getCurrentPageNo()%DEFAULT_GROUP_PAGE_SIZE!=0) {
				aaa++;
			}
		}
		aaa=aaa*DEFAULT_GROUP_PAGE_SIZE;
		int GroupBeginPage=aaa-DEFAULT_GROUP_PAGE_SIZE;//当前组的开始页数
		int GroupEndPage=aaa;//当前组的结束页数
		if (GroupBeginPage==0) {
			GroupBeginPage++;
		}
		//如果是最后一组
		if ((int) (getCurrentPageNo()/DEFAULT_GROUP_PAGE_SIZE)==getTotalPageCount()/DEFAULT_GROUP_PAGE_SIZE) {
			if (GroupEndPage>getTotalPageCount()) {
				GroupEndPage=GroupEndPage-(GroupEndPage-(int)getTotalPageCount());
				//GroupEndPage-=bbb;
			}else {
				GroupEndPage=GroupEndPage+((int)getTotalPageCount()-GroupEndPage);
			}
		}
		this.groupBeginPage=GroupBeginPage;
		this.groupEndPage=GroupEndPage;
	}

	/**
	 * 取总记录数.
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * 取总页数.
	 */
	public long getTotalPageCount() {
//		return 500;
		if (totalCount % pageSize == 0)
			return totalCount / pageSize;
		else
			return totalCount / pageSize + 1;
	}

	/**
	 * 取每页数据容量.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 取当前页中的记录.
	 */
	public List getResult() {
		return data;
	}

	/**
	 * 设置页面中的记录
	 * 
	 * @param data
	 *            包装成值对象的列表
	 */
	public void setResult(List data) {
		this.data = data;
	}

	/**
	 * 取该页当前页码,页码从1开始.
	 */
	public long getCurrentPageNo() {
		return start / pageSize + 1;
	}

	/**
	 * 该页是否有下一页.
	 */
	public boolean hasNextPage() {
		return this.getCurrentPageNo() < this.getTotalPageCount();
	}

	/**
	 * 该页是否有上一页.
	 */
	public boolean hasPreviousPage() {
		return this.getCurrentPageNo() > 1;
	}

	/**
	 * 获取任一页第一条数据在数据集的位置，每页条数使用默认值.
	 * 
	 * @see #getStartOfPage(int,int)
	 */
	protected static int getStartOfPage(int pageNo) {
		return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 获取任一页第一条数据在数据集的位置.
	 * 
	 * @param pageNo
	 *            从1开始的页号
	 * @param pageSize
	 *            每页记录条数
	 * @return 该页第一条数据

	 * 
	 */
	public static int getStartOfPage(int pageNo, int pageSize) {
		return (pageNo - 1) * pageSize;
	}
	
	
	
	
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public int getGroupEndPage() {
		return groupEndPage;
	}
	public void setGroupEndPage(int groupEndPage) {
		this.groupEndPage = groupEndPage;
	}
	public int getGroupBeginPage() {
		return groupBeginPage;
	}
	public void setGroupBeginPage(int groupBeginPage) {
		this.groupBeginPage = groupBeginPage;
	}
	public long getBeginPage() {
		return beginPage;
	}
	public void setBeginPage(long beginPage) {
		this.beginPage = beginPage;
	}
	public long getUpPage() {
		return upPage;
	}
	public void setUpPage(long upPage) {
		this.upPage = upPage;
	}
	public long getDownPage() {
		return downPage;
	}
	public void setDownPage(long downPage) {
		this.downPage = downPage;
	}
	public long getEndPage() {
		return endPage;
	}
	public void setEndPage(long endPage) {
		this.endPage = endPage;
	}
	public long getBeforePage() {
		return beforePage;
	}
	public void setBeforePage(long beforePage) {
		this.beforePage = beforePage;
	}
}