package com.easyooo.framework.rule.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easyooo.framework.common.util.IoUtil;
import com.easyooo.framework.common.util.MapUtil;
import com.easyooo.framework.common.util.TemplateUtil;
import com.easyooo.framework.rule.Language;
import com.easyooo.framework.rule.Names;
import com.easyooo.framework.rule.Rule;
import com.easyooo.framework.rule.RuleException;

/**
 * 脚本内部类，封装写入等
 * @author Killer
 */
public class ScriptWriter{
	
	protected static final Logger logger = LoggerFactory.getLogger(ScriptWriter.class);
	
	static final String TEMPLATE_NAME = "rule/rule.template"; 
	static final String DEFAULT_NAME_PREFFIX = "Rule_{0}_{1}";
	
	static final String PACKAGE;
	static final String rootClassPath;
	static final String ruleClasspath;
	static String templateFileName;
	
	// template content
	// Need to be initialized
	private static String template;
	
	// class name
	private String shortName;
	// outside properties
	private Rule rule;
	
	static {
		// template file path
		ClassLoader cl = ScriptWriter.class.getClassLoader();
		URL turl = cl.getResource(TEMPLATE_NAME);
		if(turl != null){
			templateFileName = turl.getFile();
			
			// read template
			try {
				template = IoUtil.readLines(templateFileName);
			} catch (IOException e) {
				logger.error("IOException", e);
			}
		}else{
			logger.error("RuleTemplate file not found");
		}
		
		// pkg to path
		PACKAGE = getPackage();
		rootClassPath = cl.getResource("").getPath();
		ruleClasspath = rootClassPath + File.separator
				+ PACKAGE.replace(".", File.separator) + File.separator;
		
		if(new File(ruleClasspath).mkdirs()){
			// do nothing
		}
		logger.info(String.format("Rule Class output: %s", ruleClasspath));
	}
	
	public ScriptWriter(Rule rule){
		this.rule = rule;
		this.shortName = MessageFormat.format(DEFAULT_NAME_PREFFIX, rule.getRuleId(), rule.getVersion());
	}
	
	private static String getPackage(){
		Pattern p = Pattern.compile("(package\\s*[\\w\\.]+\\s*;)");
		Matcher m = p.matcher(template);
		if(m.find()){
			String group = m.group();
			String pkg = group.substring(7);
			return pkg.replace(";", "").trim();
		}else{
			logger.error("Rule Template without package.");
			return null;
		}
	}
	
	public Class<?> getScriptClass()throws RuleException{
		File file = new File(ruleClasspath + shortName + getClassExtension());
		
		if(!file.exists()){
			logger.debug("No rule["+ shortName +"] stub, recompile");
			return null;
		}
		
		String className = PACKAGE + "." + shortName;
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuleException(e);
		}
	}
	
	public File createOrRead() throws RuleException {
		File script = buildScriptFile();
		try {
			if(!script.exists()){
				script.createNewFile();
			}
			writeScript(script);
		} catch (IOException e) {
			throw new RuleException("An error occurred when writing script file" , e);
		}
		return script;
	}
	
	/**
	 * 删除字节码文件，<b>谨慎操作</b>
	 * @throws IOException
	 */
	public boolean deleteClass()throws IOException{
		File file = new File(ruleClasspath + shortName + getClassExtension());
		if(file.exists()){
			return file.delete();
		}
		return false;
	}
	
	private void writeScript(File script)throws IOException{
		// template with args
		Map<String, Object> args = MapUtil.gmap(
				"className", shortName,
				"globalPropertyName", Names.GLOBAL,
				"localPropertyName", Names.LOCAL,
				"methodBody", rule.getRuleText());
		String source = TemplateUtil.format(template, args);
		
		if(logger.isDebugEnabled()){
			logger.debug("script path: " + script);
		}
		
		// write script and not append
		IoUtil.write(script, source, false);
	}
	
	private File buildScriptFile(){
		String fileName = shortName + getKind(rule);
		StringBuffer scriptBuffer = new StringBuffer(ruleClasspath);
		scriptBuffer.append(fileName);
		return new File(scriptBuffer.toString());
	}
	
	public String getKind(Rule rule){
		if(rule.getLanguage() == Language.GROOVY){
			return Kinds.GROOVY_EXTENSION;
		}else if(rule.getLanguage() == Language.JAVA){
			return Kinds.JAVA_EXTENSION;
		}else{
			return Kinds.JAVASCRIPT_EXTENSION;
		}
	}
	
	public String getClassExtension(){
		return Kinds.CLASS_EXTENSION;
	}
	
	public String getRootDirectory(){
		return rootClassPath;
	}
	
	private class Kinds{
		
		static final String GROOVY_EXTENSION = ".groovy";
		
		static final String JAVA_EXTENSION = ".java";
		
		static final String JAVASCRIPT_EXTENSION = ".js";
		
		static final String CLASS_EXTENSION = ".class";
	}
}