package com.tly.sqlutil;

public interface SqlPageBuilder {
	public String buildPageSql(String sql, int pageNo, int pageSize);
}
