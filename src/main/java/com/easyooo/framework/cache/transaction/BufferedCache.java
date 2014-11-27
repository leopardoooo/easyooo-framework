package com.easyooo.framework.cache.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.easyooo.framework.cache.CacheException;
import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.storage.ICache;
import com.easyooo.framework.cache.storage.JVMCache;
import com.easyooo.framework.cache.transaction.Command.Operation;
import com.easyooo.framework.support.transaction.RemoteSenderMorly;
import com.easyooo.framework.support.transaction.SimpleBufferedSender;

/**
 * 缓存事务实现，在实际缓存操作类之前注入该类，在每次操作缓存时将缓存数据刷入内存，
 * 而不是直接发送到缓存数据中心，可利用该类实现，在数据库事务提交完成之后，再提交缓存的数据更新，
 * 从而实现一个简单的 "1pc" 事务
 * 
 * @author Killer
 */
public class BufferedCache implements ICache {
	
	private ICache delegate;
	
	/**
	 * 确保只有这一个实例，如果产生多个实例，则多个实例间的操作是隔离的
	 * 提交也会分多次提交
	 */
	private static SimpleBufferedSender<Command> commandSender ;
	
	private final CacheLevel level;
	
	private BufferedCollectionUtil bufferUtil = new BufferedCollectionUtil();
	
	public BufferedCache(ICache delegate, RemoteSenderMorly<Command> remoteSender, CacheLevel level){
		super();
		setDelegate(delegate);
		this.level = level;
		
		if(commandSender == null){
			commandSender = new SimpleBufferedSender<>(remoteSender); 
		}
	}
	
	@Override
	public boolean set(String cacheKey, String value) throws CacheException {
		return putQueue(Command.newSetCommand(cacheKey, value, level));
	}

	@Override
	public boolean mod(String cacheKey, String value) throws CacheException {
		return putQueue(Command.newModCommand(cacheKey, value, level));
	}

	@Override
	public Long del(String cacheKey) throws CacheException {
		Boolean bool = putQueue(Command.newDelCommand(cacheKey, level));
		return bool ? 1L : 0L;
	}

	@Override
	public String get(String cacheKey) throws CacheException {
		Collection<Command> bufferCmds = commandSender.getOriginalBufferList();
		Command command = bufferUtil.getLastFromBufferd(bufferCmds, cacheKey, level);
		
		// 缓冲区不存在命令，则交给委托处理
		if(command == null){
			return delegate.get(cacheKey);
		}
		// 如果是删除命令，则返回null
		if(command.getOp() == Operation.DEL){
			return null;
		}
		
		// 如果是SET或MOD命令则返回最新的值
		return command.getCacheValue();
	}

	@Override
	public List<String> gets(String... cacheKey) throws CacheException {
		String[] target = new String[cacheKey.length];
		
		Collection<Command> bufferCmds = commandSender.getOriginalBufferList();
		List<Command> commands = bufferUtil.getLastFromBufferd(bufferCmds,level, cacheKey);
		// 提取不在缓冲区的命令
		// delegateIndex 索引槽用于target返回数据占位符
		// 如果缓冲区不存在命令，加入委托集
		List<String> delegateCacheKey = new ArrayList<String>();
		List<Integer> delegateIndex = new ArrayList<Integer>();
		for (int i = 0; i < cacheKey.length; i++) {
			String key = cacheKey[i];
			Command cmd = commands.get(i);
			if(null == cmd) {
				delegateCacheKey.add(key);
				delegateIndex.add(i);
				target[i] = null;
			} else {
				// 如果是删除命令，则返回null
				target[i] = (cmd.getOp() == Operation.DEL) ? null : cmd.getCacheValue();
			}
		}
		
		// 缓冲区不存在的命令，则交给委托处理
		if(delegateCacheKey.size() > 0){
			List<String> parts = delegate.gets(delegateCacheKey
					.toArray(new String[] {}));
			// replace target null value
			for (int i = 0; i < delegateCacheKey.size(); i++) {
				target[delegateIndex.get(i)] = parts.get(i);
			}
		}
		return Arrays.asList(target);
	}

	@Override
	public boolean sets(String... keyvalues) throws CacheException {
		return putQueue(Command.newSetsCommand(keyvalues, level));
	}

	@Override
	public Long addMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		putQueue(Command.newAddMembersCommand(groupKey, entityCacheKeys, level));
		return (long)entityCacheKeys.length;
	}

	@Override
	public Long delMembers(String groupKey, String... entityCacheKeys)
			throws CacheException {
		putQueue(Command.newDelMembersCommand(groupKey, entityCacheKeys, level));
		return (long)entityCacheKeys.length;
	}

	@Override
	public Set<String> getMembers(String groupKey) throws CacheException {
		// delegate get all members
		Set<String> members = delegate.getMembers(groupKey);
		final int rows = (members == null) ? -1 : members.size();
		
		// if members is null 
		if(rows < 0){
			members = JVMCache.newMemberContainer();
		}
		
		// replace part members
		Collection<Command> bufferCmds = commandSender.getOriginalBufferList();
		bufferUtil.replaceMembersFromBufferd(bufferCmds, members, groupKey);
		
		if(rows < 0 && members.size() == 0){
			return null;
		}
		return members;
	}

	@Override
	public int getSize() {
		return this.delegate.getSize();
	}
	
	protected void setDelegate(ICache delegate) {
		if(delegate != null && delegate != this){
			this.delegate = delegate;
		}
	}
	
	/**
	 * 将命令放入队列
	 */
	private boolean putQueue(Command cmd)throws CacheException{
		try {
			return commandSender.send(cmd);
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}
}
