package com.tly.sqlutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 防止sql语句注入工具类
 * @author tly
 * 
 * 当使用union all 查询时注意给两个方法传入同一个SqlBuilder对象
 *
 */
public class SqlBuilder {
	protected StringBuilder sqlBuf = new StringBuilder();
	protected List<Object> values = new ArrayList<Object>();
	protected Map<String, Object> paramMap = new HashMap<String, Object>();

	/***
	 * 拼接sql
	 * @param sql
	 * @return SqlBuilder
	 */
	public SqlBuilder appendSql(String sql) {
		sqlBuf.append(sql);
		return this;
	}

	/***
	 * 替换sql中的占位符
	 * @param value
	 * @return SqlBuilder
	 */
	public SqlBuilder appendValue(Object value) {
		sqlBuf.append('?');
		values.add(value);
		return this;
	}

	/***
	 * 替换多个占位符:如in
	 * @param values
	 * @return SqlBuilder
	 */
	public SqlBuilder appendValues(Object[] values) {
		sqlBuf.append('(');
		for (int i = 0, c = values.length; i < c; ++i) {
			sqlBuf.append('?').append(',');
			this.values.add(values[i]);
		}
		int last = sqlBuf.length() - 1;
		if (last > 0 && sqlBuf.charAt(last) == ',') {
			sqlBuf.setCharAt(last, ')');
		}
		return this;
	}
	
	/***
	 * 获取sql语句把?用p0:等代替
	 */
	public String getSqlExt() {
        String sql = getSql();
        int index = 0;
        while (sql.indexOf('?') != -1) {
            String param = "p"+index;
            sql = sql.replaceFirst("\\?", ':'+param);
            paramMap.put(param, values.get(index));
            //AppLog.LOG.debug("getSqlExt - {}: {}", index, sql);
            index++;
        }
        return sql;
    }

	/***
	 * 获取匹配的参数值
	 * @return
	 */
    public Map<String, Object> getValuesExt() {
        if(paramMap.isEmpty()) {
            getSqlExt();
        }
        return paramMap;
    }

	public String getSql() {
		return sqlBuf.toString();
	}

	public Object[] getValues() {
		return values.toArray();
	}
	
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
}
