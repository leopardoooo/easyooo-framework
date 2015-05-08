/*
 * Copyright © 2014 YAOCHEN Corporation, All Rights Reserved
 */
package com.easyooo.framework.cache.config.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.easyooo.framework.cache.CacheLevel;
import com.easyooo.framework.cache.ConfigurationException;
import com.easyooo.framework.cache.annotations.Cache;
import com.easyooo.framework.cache.annotations.Group;
import com.easyooo.framework.cache.annotations.KeyProperty;
import com.easyooo.framework.cache.annotations.Merging;
import com.easyooo.framework.cache.annotations.MergingList;
import com.easyooo.framework.cache.annotations.MiniTable;
import com.easyooo.framework.cache.annotations.Refrence;
import com.easyooo.framework.cache.annotations.Refrences;
import com.easyooo.framework.cache.annotations.ThreadSafety;
import com.easyooo.framework.cache.annotations.Transient;
import com.easyooo.framework.cache.config.CacheBean;
import com.easyooo.framework.cache.config.Configuration;
import com.easyooo.framework.cache.config.GroupBean;
import com.easyooo.framework.cache.config.KeyPropertyBean;
import com.easyooo.framework.cache.config.MergingBean;
import com.easyooo.framework.cache.config.ModelField;
import com.easyooo.framework.cache.config.RefrenceBean;
import com.easyooo.framework.common.util.ListUtil;

/**
 * 该类主要实现了基于注解的方式解析缓存配置
 * 
 * @see Cache
 * @see Merging
 * @see com.yaochen.KeyPropertyBean.common.cache.annotations.KeyProperty
 * @see com.yaochen.GroupBean.common.cache.annotations.Group
 * @see Transient
 * 
 * @author Killer
 */
@ThreadSafety
public class AnnotationConfiguration extends AbstractConfiguration{

	public AnnotationConfiguration(){
		super();
	}
	
	/**
	 * 
	 * 解析实体Class缓存注解，它并不会判断检查Cache Class是否存在，
	 * 
	 * @param type 需要实现 {@link PropertyMapper}接口
	 * @return
	 * @throws ConfigurationException
	 */
	@Override
	public CacheBean parserConfiguration(
			Class<?> type)throws ConfigurationException {
		if(type == null) 
			return null;
		logger.debug(" Load the " +  type.getName() +"- annotation configuration");
		CacheBean target = new ParserAnnotation(type).parseAndGet();
		return super.validate(target);
	}
	
	/**
	 * 解析缓存注解具体实现
	 * @author Killer
	 */
	private static class ParserAnnotation{
		
		private Class<?> type;
		
		private CacheBean target = new CacheBean();
		private ArrayList<Field> fields;
		
		public ParserAnnotation(Class<?> type){
			this.type = type;
		}
		
		public CacheBean parseAndGet()throws ConfigurationException{
			this.prepare();
			parseCache();
			
			if(target.getXbeanClass() == null)
				return null;

			parseMiniTable();
			
			parseMergings();
			
			// parse fields
			// & exclude Transient field
			parseFields();
			
			parseKeyProperty();
			parseGroup();
			
			if(needAppendInnerGroup()){
				appendInnerGroup();
			}
			
			return this.target;
		}
		
		private void prepare(){
			// clear transient fields
			Field[] _fields = type.getDeclaredFields();
			this.fields = new ArrayList<Field>(_fields.length);
			for (Field field : _fields) {
				Transient t = field.getAnnotation(Transient.class);
				if(null == t){
					this.fields.add(field);
				}
			}
			this.fields.trimToSize();
		}
		
		
		private void parseGroup()throws ConfigurationException{
			List<GroupBean> groups = new ArrayList<GroupBean>(); 
			for (Field field : fields) {
				Group group = field.getAnnotation(Group.class);
				
				if(group != null){
					String groupName = group.name();
					if("".equals(groupName)){
						groupName = Configuration.DEFAULT_GROUP_NAME;
					}
					groups.add(new GroupBean(groupName, field.getName(), group.value()));
				}
			}
			
			Map<String, List<GroupBean>> groupMap = ListUtil.grouping(groups);
			Set<String> keySet = groupMap.keySet();
			for (String groupName : keySet) {
				Collections.sort(groupMap.get(groupName));
			}
			this.target.setGroups(groupMap);
		}

		private void parseFields() throws ConfigurationException{
			List<ModelField> fieldList = new ArrayList<ModelField>(fields.size());
			for (Field field : fields) {
				fieldList.add(new ModelField(field.getName()));
			}
			target.setFields(fieldList);
		}

		private void parseKeyProperty() throws ConfigurationException{
			List<KeyPropertyBean> keys = new ArrayList<KeyPropertyBean>(); 
			for (Field field : fields) {
				KeyProperty kp = field.getAnnotation(KeyProperty.class);
				
				if(kp != null){
					keys.add(new KeyPropertyBean(field.getName(), kp.value()));
				}
			}

			Collections.sort(keys);
			this.target.setKeyPropertyList(keys);
		}

		private void parseMergings() throws ConfigurationException{
			List<Merging> mergingList = new ArrayList<Merging>();
			Merging merging = type.getAnnotation(Merging.class);
			if(merging != null){
				mergingList.add(merging);
			}else{
				MergingList ml = type.getAnnotation(MergingList.class);
				if(ml == null)
					return;
				mergingList.addAll(Arrays.asList(ml.value()));
			}
			
			Map<Class<?>, List<RefrenceBean>> refrences = ListUtil
					.grouping(_parseRefrence());
			Map<Class<?>, MergingBean> mapMg = new HashMap<Class<?>, MergingBean>();
			for (Merging mg : mergingList) {
				Class<?> dtoClass = mg.value();
				mapMg.put(dtoClass, _parseMerging(mg, refrences));
			}
			
			this.target.setMergings(mapMg);
		}
		
		private MergingBean _parseMerging(Merging mg, Map<Class<?>, List<RefrenceBean>> refrences) throws ConfigurationException{
			MergingBean mbean = new MergingBean();
			mbean.setTargetClass(mg.value());
			
			Class<?>[] rfClass = mg.refrenceClass();
			Map<Class<?>, List<RefrenceBean>> target = new HashMap<Class<?>, List<RefrenceBean>>();
			for (Class<?> rfc : rfClass) {
				List<RefrenceBean> lrs = refrences.get(rfc);
				if(lrs != null && lrs.size() > 0){
					target.put(rfc, lrs);
				}
			}
			mbean.setRefrences(target);
			return mbean;
		}
		
		private List<RefrenceBean> _parseRefrence(){
			List<RefrenceBean> refrences = new ArrayList<RefrenceBean>(); 
			for (Field field : fields) {
				Refrence rf = field.getAnnotation(Refrence.class);
				if(rf != null)
					refrences.add(new RefrenceBean(field.getName(), rf));
				Refrences rfs = field.getAnnotation(Refrences.class);
				if(rfs != null){
					List<Refrence> refs = Arrays.asList(rfs.value());
					for (Refrence refrence : refs) {
						refrences.add(new RefrenceBean(field.getName(), refrence));
					}
				}
			}
			return refrences;
		}

		private void parseCache() throws ConfigurationException{
			Cache cache = type.getAnnotation(Cache.class);
			if(cache == null)
				return;

			// parse prefix
			String prefix = cache.value();
			if(null == prefix || "".equals(prefix)){
				prefix = type.getSimpleName();
			}
			target.setXbeanClass(type);
			
			// TODO 
			// 手工修改数据造成JVM数据不一致的情况，
			// 因此这里暂时将JVM标志视为REDIS级别
			// target.setLevel(cache.level());
			target.setLevel(CacheLevel.REDIS);
			target.setKeyBuilder(cache.keyBuilder());
			target.setPrefix(prefix);
		}
		
		private void parseMiniTable(){
			MiniTable mt = type.getAnnotation(MiniTable.class);
			target.setMiniTable(mt == null ? false : true );
		}
		
		private boolean needAppendInnerGroup(){
			return target.isMiniTable();
		}
		
		private void appendInnerGroup(){
			Map<String, List<GroupBean>> groups = target.getGroups();
			List<GroupBean> innerGroups = new ArrayList<>();
			innerGroups.add(new GroupBean(Configuration.DEFAULT_MINI_TABLE, null, 0));
			
			groups.put(Configuration.DEFAULT_MINI_TABLE, innerGroups);
		}
	}
	
}