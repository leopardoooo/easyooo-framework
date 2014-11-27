package com.easyooo.framework.sharding;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import com.easyooo.framework.sharding.annotation.Table;

/**
 * 
 * Sharding Context binding by aspect
 *
 * @author Killer
 */
@Aspect
public class ShardingContextAspect implements Ordered{
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	private ShardingContextExecutor shardingContextExecutor;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Database Operation Pointcuts & Advice
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	@Pointcut("execution(* com.easyooo.show.data.persistence..*Mapper.*(..))")
	public void operationPointcut() {
	}
	
	@Around("operationPointcut()")
	public Object doOperation(final ProceedingJoinPoint pjp) throws Throwable{
		Class<?> operationClass = pjp.getSignature().getDeclaringType();
		Table table = operationClass.getAnnotation(Table.class);
		return shardingContextExecutor.doProxy(pjp.getArgs(), table, new ShardingCallback() {
			@Override
			public Object doCallback() throws Throwable {
				return pjp.proceed();
			}
		});
	}

	@Override
	public int getOrder() {
		return 0;
	}

	public void setShardingContextExecutor(
			ShardingContextExecutor shardingContextExecutor) {
		this.shardingContextExecutor = shardingContextExecutor;
	}
}
