package com.easyooo.framework.cache.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.transaction.Command.Operation;

/**
 * 缓冲区集合操检索类
 *
 * @author Killer
 */
public final class BufferedCollectionUtil {
	
	/**
	 * 替换member，当缓存队列存在该缓存时，使用新值替换老值
	 * @param bufferCmds
	 * @param members
	 * @param groupKey
	 */
	public void replaceMembersFromBufferd(Collection<Command> bufferCmds,
			Set<String> members, String groupKey) {
		if(bufferCmds == null || bufferCmds.size() == 0){
			return;
		}
		
		// filter group commands
		List<Command> memberCmds = new ArrayList<>();
		for (Command cmd : bufferCmds) {
			if(!groupKey.equals(cmd.getCacheGroupKey())){
				continue;
			}
			// Find the target
			if(cmd.getOp() == Operation.ADD_MEMBERS ||
					cmd.getOp() == Operation.DEL_MEMBERS){
				memberCmds.add(cmd);
			}
		}
		
		// replace membership by group commands
		for (Command cmd : memberCmds) {
			switch (cmd.getOp()) {
			case ADD_MEMBERS:
				appendMembers(members, cmd.getGroupValues());
				break;
			case DEL_MEMBERS:
				removeMembers(members, cmd.getGroupValues());
				break;
			default:
				break;
			}
		}
	}
	
	private void appendMembers(Set<String> set, String[] members){
		for (String member : members) {
			set.add(member);
		}
	}
	
	private void removeMembers(Set<String> set, String[] members){
		for (String member : members) {
			set.remove(member);
		}
	}
	
	/**
	 * 根据一组缓存KEY，从缓冲区获取一组最后一条命令, 按照传入的顺序返回，
	 * 不存在的cache返回返回null补位
	 * @return
	 */
	public List<Command> getLastFromBufferd(Collection<Command> bufferCmds,
			CacheLevel level, String... cacheKeys) {
		List<Command> lastCommands = new ArrayList<Command>();
		for (String key : cacheKeys) {
			lastCommands.add(getLastFromBufferd(bufferCmds, key, level));
		}
		return lastCommands;
	}
	
	/**
	 * 根据缓存KEY，从缓冲区获取最后一条命令
	 * 
	 * @param bufferCmds
	 * @param cacheKey
	 * @return
	 */
	public Command getLastFromBufferd(Collection<Command> bufferCmds,
			String cacheKey, CacheLevel level) {
		if(bufferCmds == null || bufferCmds.size() == 0){
			return null;
		}
		
		// asc each
		List<Command> sameCommands = new ArrayList<>(); 
		for (Command command : bufferCmds) {
			
			//if sets operation
			if(command.getOp() == Operation.SETS){
				String[] keyvalues = command.getKeyvalues();
				for (int i = 0; i < keyvalues.length ; i+=2) {
					if(keyvalues[i].equals(cacheKey)){
						// To simulate a set command
						sameCommands.add(Command.newSetCommand(cacheKey, keyvalues[i+1], level));
					}
				}
			}
			
			if(cacheKey.equals(command.getCacheKey())){
				sameCommands.add(command);
			}
		}
		
		// return the last command
		if(sameCommands.size() > 0){
			return sameCommands.get(sameCommands.size() - 1);
		}
		return null;
	}
}
