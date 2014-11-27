package com.easyooo.framework.sharding;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * 首位相连的链表结构
 *
 * @author Killer
 */
public class RoutingGroupUtil implements Comparator<RoutingGroup>{

	private List<RoutingGroup> groups;
	
	public RoutingGroupUtil(List<RoutingGroup> groups){
		this.groups = groups;
	}
	
	/**
	 * 排序并且按区间值划分
	 * 
	 * @param groups
	 */
	public void orderAndRange(){
		if(groups == null || groups.size() == 0){
			return ;
		}
		
		if(groups.size() == 1){
			groups.get(0).setRange(new NumberRange(0, Long.MAX_VALUE));
		}
		
		// 超过2条路由组按区间值划分
		// 必须按照StartId ASC排序
		Collections.sort(groups, this);
		
		for (int i = 0; i < groups.size(); i++) {
			RoutingGroup tmp = groups.get(i);
			Long nextSi = getStartId(i, Direction.NEXT);
			Long si = getStartId(i, Direction.INPLACE);
			tmp.setRange( new NumberRange(si, nextSi));
		}
	}
	
	private enum Direction{
		PREV, NEXT, INPLACE
	}
	
	private Long getStartId(int currentIndex, Direction d){
		if(d == Direction.NEXT && groups.size() - 1 == currentIndex){
			return Long.MAX_VALUE;
		}
		if(d == Direction.PREV && currentIndex == 0){
			return Long.MIN_VALUE;
		}
		if(d == Direction.INPLACE){
			return groups.get(currentIndex).getStartId();
		}
		int num = (d == Direction.NEXT) ? 1 : -1;
		return groups.get(currentIndex + num).getStartId() - 1;
	}
	
	@Override
	public int compare(RoutingGroup o1, RoutingGroup o2) {
		return o1.getStartId().intValue() - o2.getStartId().intValue();
	}

}
