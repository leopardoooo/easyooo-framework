package com.easyooo.framework.support.mybatis;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.easyooo.framework.support.mybatis.util.DialectUtil;
import com.google.common.base.Joiner;

/**
 * <p>分页插件实现，扩展<code>MyBatis Interceptor</code>
 * 	分页插件只在当<code>pagination</code>作为查询参数时有效.
 * </p>
 * <p>
 * 	example：public void selectAll(Pagination){}</p>
 * <p>
 * 	当有参数时，将参数设置到Paginatoin#criteria
 * <p>
 * 
 * 
 * @see Pagination
 * 
 * @author Killer
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = {
		MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}) })
public class PaginationPlugin implements Interceptor, InitializingBean {
	
	static final Logger logger = 
			LoggerFactory.getLogger(PaginationPlugin.class);
	
	private static final char DEFAULT_SEPARATOR = ',';
	private static final Integer MAPPED_STATEMENT_INDEX = 0;
	private static final Integer PARAMETER_INDEX = 1;
	private static final Integer ROW_BOUNDS_INDEX = 2;
	
	private static final String OFFSET_PARAMETER = "_pagination_offset";
	private static final String LIMIT_PARAMETER = "_pagination_limit";
	
	
	// 
	private static final String DEFAULT_DBMS = "MYSQL";
	private Dialect dialect;
	
	// properties
	private String dbms;
	

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		
		// no paging
		if(!isPaging(args)){
			return invocation.proceed();
		}

		// process for paging
		InvocationContext context = getInvocationContext(invocation);
		InvocationContext newContext = processIntercept(context);

		swapParameter(newContext, args);
		Object result = invocation.proceed();
		
		if(result != null && result instanceof List){
			newContext.getPagination().setRecords((List<?>)result);
		}
		
		return result;
	}
	
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {
		// do nothing
	}
	
	/**
	 * check method
	 * @param ms
	 * @param args
	 * @return args was Pagination return true, else false 
	 */
	private boolean isPaging(Object[] args){
		return (args[PARAMETER_INDEX] instanceof Pagination);
	}
	
	/**
	 * 封装代理上下文参数
	 * @param invocation
	 * @return
	 */
	private InvocationContext getInvocationContext(Invocation invocation){
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement)args[MAPPED_STATEMENT_INDEX];
		Pagination ps = (Pagination)args[PARAMETER_INDEX];
		
		return new InvocationContext(ms, ps);
	}
	
	
	/**
	 * 交换<code>MyBatis</code> 参数列表
	 * 
	 * @param newContext
	 * @param args
	 */
	private void swapParameter(InvocationContext newContext, Object[] args){
		Pagination pg = newContext.getPagination();
		// setter new MapperdStatement
		args[MAPPED_STATEMENT_INDEX] = newContext.getMappedStatement();
		// Criteria property swap pagination object
		args[PARAMETER_INDEX] = pg.getCriteria();
		
		//RowBounds rowBounds = new RowBounds(pg.getOffset(), pg.getLimit());
		args[ROW_BOUNDS_INDEX] = new RowBounds();
	}
	
	private InvocationContext processIntercept(InvocationContext context)throws Throwable {
		MappedStatement ms = context.getMappedStatement();
		Pagination pagination = context.getPagination();
		
		BoundSql boundSql = ms.getBoundSql(pagination.getCriteria());
		
		// counting
		Integer counting = new CountingExecutor(ms, dialect, boundSql).execute();
        pagination.setTotalCount(counting);
		
		// paging
		String pagingSql = dialect.getPagingSQL(boundSql.getSql().trim());
		
		// cpy mappings
		List<ParameterMapping> mappings = new ArrayList<ParameterMapping>();
		if(boundSql.getParameterMappings() != null){
			List<ParameterMapping> tmpMappings = boundSql.getParameterMappings();
			for (int i = 0; i < tmpMappings.size(); i++) {
				mappings.add(tmpMappings.get(i));
			}
		}
		
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), pagingSql, mappings, boundSql.getParameterObject());
		cpyAndAppendParameters(ms, pagination, boundSql, newBoundSql);
		
		InvocationContext newContext = new InvocationContext();
        MappedStatement newms = cloneMappedStatement(ms, newBoundSql);
        newContext.setMappedStatement(newms);
        newContext.setPagination(pagination);
        
        return newContext;
	}
	
	private void cpyAndAppendParameters(MappedStatement ms, Pagination pg, BoundSql boundSql, BoundSql newBoundSql){
		// cpy old parameters
		for (ParameterMapping mapping : newBoundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop,
						boundSql.getAdditionalParameter(prop));
			}
		}
		
		// append pagination parameters
		Configuration cf = ms.getConfiguration();
		TypeHandler<?> type =  cf.getTypeHandlerRegistry().getTypeHandler(Integer.class);
		ParameterMapping offsetMapping = new ParameterMapping.Builder(cf,
				OFFSET_PARAMETER, type).build();
		ParameterMapping limitMapping = new ParameterMapping.Builder(cf,
				LIMIT_PARAMETER, type).build();
		
		ParameterMapping[] mappings = new ParameterMapping[]{offsetMapping, limitMapping}; 
		for (Order order : dialect.order()) {
			newBoundSql.getParameterMappings().add(mappings[order.ordinal()]);
		}
		
		newBoundSql.setAdditionalParameter(OFFSET_PARAMETER, pg.getOffset());
		newBoundSql.setAdditionalParameter(LIMIT_PARAMETER, pg.getLimit());
	}

	
	private MappedStatement cloneMappedStatement(MappedStatement old, BoundSql boundSql){
		MappedStatement.Builder builder = new MappedStatement.Builder(
				old.getConfiguration(), old.getId(), new AlwaySqlSource(
						boundSql), old.getSqlCommandType());
        builder.cache(old.getCache());
        builder.databaseId(old.getDatabaseId());
        builder.fetchSize(old.getFetchSize());
        builder.flushCacheRequired(old.isFlushCacheRequired());
        builder.keyGenerator(old.getKeyGenerator());
        builder.keyProperty(join(old.getKeyProperties()));
        builder.keyColumn(join(old.getKeyColumns()));
        builder.lang(old.getLang());
        builder.resource(old.getResource());
        builder.resultMaps(old.getResultMaps());
        builder.resultSetType(old.getResultSetType());
        builder.parameterMap(old.getParameterMap());
        builder.statementType(old.getStatementType());
        builder.timeout(old.getTimeout());
        builder.useCache(old.isUseCache());
        builder.resultOrdered(old.isResultOrdered());
        builder.resulSets(join(old.getResulSets()));

        return builder.build();
	}
	
	private String join(String[] strs){
		if(strs == null || strs.length == 0 ){
			return null;
		}
		return Joiner.on(DEFAULT_SEPARATOR).join(strs);
	}
	
	class AlwaySqlSource implements SqlSource{
		private BoundSql bs;
		public AlwaySqlSource(BoundSql bs){
			this.bs = bs;
		}
		@Override
		public BoundSql getBoundSql(Object po) {
			return bs;
		}
	};

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(dbms == null || dbms.equals("")){
			dbms = DEFAULT_DBMS;
		}
		this.dialect = new DialectUtil().switchDialect(dbms.toUpperCase());
	}
	
}
