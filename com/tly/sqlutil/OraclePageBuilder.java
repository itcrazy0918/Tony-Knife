package com.tly.sqlutil;

/**
oracle分页工具类
@author tony
**/
public class OraclePageBuilder implements SqlPageBuilder {

	@Override
	public String buildPageSql(String sql, int pageNo, int pageSize) {
		int offset = (pageNo - 1) * pageSize;
		int limit = pageSize;
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT B.* FROM ( ");
        sb.append("SELECT A.*, rownum AS r__n FROM ( ");

        sb.append(sql);
        sb.append(" ) A ");
        sb.append(" ) B WHERE ");

        if (offset > 0) {
            sb.append(" B.r__n > ");
            sb.append(offset);
            if (limit > 0) {
                sb.append(" AND B.r__n <= ");
                sb.append(offset + limit);
            }
        } else {
            sb.append(" B.r__n <= ");
            sb.append(limit);
        }
        
        return sb.toString();
	}

}
