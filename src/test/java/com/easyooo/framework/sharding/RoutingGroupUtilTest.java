package com.easyooo.framework.sharding;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.easyooo.framework.common.util.CollectionUtil;

/**
 * 
 * @author Killer
 */
public class RoutingGroupUtilTest {

	private static List<RoutingGroup> groups = new ArrayList<RoutingGroup>();
	
	@BeforeClass
	public static void initialize(){
		RoutingGroup rg2 = new RoutingGroup();
		rg2.setModule(new Module("user"));
		rg2.setDataSourceKeys(CollectionUtil.gList(new DataSourceKey("node5")));
		rg2.setStartId(10000L);
		
		RoutingGroup rg = new RoutingGroup();
		rg.setModule(new Module("user"));
		rg.setDataSourceKeys(CollectionUtil.gList(new DataSourceKey("node1"),
				new DataSourceKey("node2"), new DataSourceKey("node3")));
		
		RoutingGroup rg1 = new RoutingGroup();
		rg1.setModule(new Module("user"));
		rg1.setDataSourceKeys(CollectionUtil.gList(new DataSourceKey("node1"),
				new DataSourceKey("node2"),new DataSourceKey("node3"),
				new DataSourceKey("node4")));
		rg1.setStartId(2000L);
		
		RoutingGroup rg3 = new RoutingGroup();
		rg3.setModule(new Module("user"));
		rg3.setDataSourceKeys(CollectionUtil.gList(new DataSourceKey("node6")));
		rg3.setStartId(15000L);
		
		groups.add(rg1);
		groups.add(rg2);
		groups.add(rg);
		groups.add(rg3);
	}
	
	@Test
	public void testOrderAndRange(){
		new RoutingGroupUtil(groups).orderAndRange();
		
		for (RoutingGroup routingGroup : groups) {
			System.out.println(routingGroup.getRange() + "," + routingGroup.getDataSourceKeys());
		}
	}
	
}
