/**
*
* VIETEK JSC - VOpen - Vietnam open framework
* Create date: Aug 13, 2016
* Author: tuanpa
*
*/
package base.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.CascadeType;

import base.VModuleDefine;
import base.controller.VController;
import base.model.ButtonField;
import base.model.FunctionField;
import base.model.HolderClass;
import base.model.Many2ManyField;
import base.model.Many2OneField;
import base.model.One2ManyField;
import base.model.VField;
import base.model.VModel;
import base.model.VModule;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class VClassLoader {
	private static Map<String, Class<?>> mapModel = new HashMap<>();
	private static Map<Class<?>, String> mapModelInverse = new HashMap<>();
	
	public static Collection<Class<?>> processBaseModel() {
		HashMap<String, Class<?>> classMap = new HashMap<>();
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ClassClassPath(VClassLoader.class));
		Class<? extends VModel> clazz = VModule.class;
		CtClass vobj = null;
		try {
			VModel modelController = (VModel) clazz.newInstance();
			VController.addModelClass(modelController.getModelName(), clazz);
			vobj = pool.get("base.model.VObject");
			
			//Create hibernate class to persist data, all extends VObject
			CtClass newHibernateModel = null;
			String modelClassName = generateClassName(modelController.getModelName());
			newHibernateModel = pool.makeClass(modelClassName);
			newHibernateModel.setSuperclass(vobj);
			//@Entity
			//@Table(name="")
			ClassFile cfile = newHibernateModel.getClassFile();
	        ConstPool cpool = cfile.getConstPool();
	        AnnotationsAttribute attr =
	                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
	        Annotation annot = new Annotation(Entity.class.getName(), cpool);
	        attr.addAnnotation(annot);
	        annot = new Annotation(Table.class.getName(), cpool);
	        annot.addMemberValue("name", new StringMemberValue(modelController.getTableName(), cpool));
	        attr.addAnnotation(annot);
	        cfile.addAttribute(attr);
	        
	        CtField field = null;
	        
			//Add field of object
			for (String fieldName : modelController.getAllFields().keySet()) {
				VField modelField = modelController.getAllFields().get(fieldName);
				field = modelField.getCtField(newHibernateModel, pool, fieldName);
				if (field != null && !(modelField instanceof FunctionField) && !(modelField instanceof ButtonField)) {
					newHibernateModel.addField(field);
				}
			}
			
			//Add id, generate
			//@Id
			//@GeneratedValue
	        CtField cfield  = newHibernateModel.getField("id");
	        attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
	        annot = new Annotation("javax.persistence.Id", cpool);
	        attr.addAnnotation(annot);
	        annot = new Annotation("javax.persistence.GeneratedValue", cpool);
	        attr.addAnnotation(annot);
	        cfield.getFieldInfo().addAttribute(attr);
	        
	        Class<?> newClazz = null;
			newClazz = newHibernateModel.toClass();
			mapModel.put(modelController.getModelName(), newClazz);
			mapModelInverse.put(newClazz, modelController.getModelName());
			classMap.put(clazz.getName(), newClazz);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return classMap.values();
	}
	
	@SuppressWarnings("unchecked")
	public static Collection<Class<?>> processModel(ArrayList<VModuleDefine> modules) {
		ArrayList<Class<?>> classList = new ArrayList<>();
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ClassClassPath(VClassLoader.class));
		
		for (VModuleDefine module : modules) {
			if (module.getModelClasses() != null) {
				for (Class<?> clazz : module.getModelClasses()) {
					if (clazz.getSuperclass().equals(VModel.class)) {
						try {
							VModel modelController = (VModel) clazz.newInstance();
							VController.addModelClass(modelController.getModelName(), (Class<? extends VModel>) clazz);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		//Scan for all VModelController
		// VOpen inherit
		Map<String, String> mapClassName = new HashMap<>();
		CtClass vobj = null;
		for (String modelName : VController.getAllModelName()) {
			try {
				vobj = pool.get("base.model.VObject");
				
				VController modelBridge = new VController(modelName);
				//Create hibernate class to persist data, all extends VObject
				CtClass newHibernateModel = null;
				String modelClassName = generateClassName(modelName);
				newHibernateModel = pool.makeClass(modelClassName);
				mapClassName.put(modelName, modelClassName);
				newHibernateModel.setSuperclass(vobj);
				//@Entity
				//@Table(name="")
				ClassFile cfile = newHibernateModel.getClassFile();
		        ConstPool cpool = cfile.getConstPool();
		        AnnotationsAttribute attr =
		                new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		        Annotation annot = new Annotation(Entity.class.getName(), cpool);
		        attr.addAnnotation(annot);
		        annot = new Annotation(Table.class.getName(), cpool);
		        annot.addMemberValue("name", new StringMemberValue(modelBridge.getTableName(), cpool));
		        attr.addAnnotation(annot);
		        cfile.addAttribute(attr);
		        
		        CtField field = null;
				//Add field of object
				for (String fieldName : modelBridge.getAllFields().keySet()) {
					VField modelField = modelBridge.getAllFields().get(fieldName);
					field = modelField.getCtField(newHibernateModel, pool, fieldName);
					if (field != null) {
						newHibernateModel.addField(field);
					}
				}
				//Add id, generate
				//@Id
				//@GeneratedValue
		        CtField cfield  = newHibernateModel.getField("id");
		        attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
		        annot = new Annotation("javax.persistence.Id", cpool);
		        attr.addAnnotation(annot);
		        annot = new Annotation("javax.persistence.GeneratedValue", cpool);
		        attr.addAnnotation(annot);
		        cfield.getFieldInfo().addAttribute(attr);
			}
			catch (Exception e) {
				System.out.println("[ERROR] VModelController: [" + modelName + "] - " + e.getMessage());
			}
		}
		
		//Create all abstract class for model
		for (String modelName : VController.getAllModelName()) {
			try {
				VController modelBridge = new VController(modelName);
				
				CtClass newHibernateModel = null;
				CtClass tmpClass = pool.get(HolderClass.class.getName());
				CtClass collectionClass = pool.get(Collection.class.getName());
				newHibernateModel = pool.get(mapClassName.get(modelName));
				newHibernateModel.defrost();
				ClassFile cfile = newHibernateModel.getClassFile();
		        ConstPool cpool = cfile.getConstPool();
				for (CtField field : newHibernateModel.getFields()) {
					//for many2one field
					if (field.getType().equals(tmpClass)) {
						Many2OneField modelField = (Many2OneField) modelBridge.getAllFields().get(field.getName());
						CtClass fieldType = pool.get(mapClassName.get(modelField.parentModel));
						field.setType(fieldType);
//						AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
//				        Annotation annot = new Annotation(ManyToOne.class.getName(), cpool);
//				        EnumMemberValue fetchEnum = new EnumMemberValue(cpool);
//				        fetchEnum.setType(FetchType.class.getName());
//				        fetchEnum.setValue(FetchType.EAGER.name());
//				        annot.addMemberValue("fetch", fetchEnum);
//				        attr.addAnnotation(annot);
//				        field.getFieldInfo().addAttribute(attr);
					}
					//for one2many, many2many
					else if (field.getType().equals(collectionClass)) {
						if (modelBridge.getAllFields().get(field.getName()) instanceof One2ManyField) {
							//@OneToMany(mappedBy="department", targetEntity=?, cascade=?) 
							One2ManyField modelField = (One2ManyField) modelBridge.getAllFields().get(field.getName());
							AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
					        Annotation annot = new Annotation(OneToMany.class.getName(), cpool);
					        annot.addMemberValue("mappedBy", new StringMemberValue(modelField.joinField, cpool));
					        annot.addMemberValue("targetEntity", new ClassMemberValue(mapClassName.get(modelField.childModel), cpool));
					        EnumMemberValue fetchEnum = new EnumMemberValue(cpool);
					        fetchEnum.setType(FetchType.class.getName());
					        fetchEnum.setValue(FetchType.LAZY.name());
					        annot.addMemberValue("fetch", fetchEnum);
					        attr.addAnnotation(annot);
					        
//					        annot = new Annotation(Cascade.class.getName(), cpool);
//					        if (modelField.cascade == null) {
//					        	modelField.cascade = CascadeType.SAVE_UPDATE;
//					        }
//					        if (modelField.cascade != null) {
//					        	EnumMemberValue cascadeEnum = new EnumMemberValue(cpool);
//						        cascadeEnum.setType(CascadeType.class.getName());
//						        cascadeEnum.setValue(modelField.cascade.name());
//						        ArrayMemberValue amv = new ArrayMemberValue(cpool);
//						        amv.setValue(new MemberValue[]{cascadeEnum});
//						        annot.addMemberValue("value", amv);
//					        }
//					        attr.addAnnotation(annot);
					        
					        annot = new Annotation(OrderBy.class.getName(), cpool);
					        annot.addMemberValue("value", new StringMemberValue(modelField.orderby, cpool));
					        attr.addAnnotation(annot);
					        field.getFieldInfo().addAttribute(attr);
						}
						else if (modelBridge.getAllFields().get(field.getName()) instanceof Many2ManyField) {
							Many2ManyField modelField = (Many2ManyField) modelBridge.getAllFields().get(field.getName());
							//@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
							AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
							Annotation annot = new Annotation(ManyToMany.class.getName(), cpool);
							
							annot.addMemberValue("targetEntity", new ClassMemberValue(mapClassName.get(modelField.friendModel), cpool));
							if (modelField.cascade != null) {
						        EnumMemberValue cascadeEnum = new EnumMemberValue(cpool);
						        cascadeEnum.setType(CascadeType.class.getName());
						        cascadeEnum.setValue(modelField.cascade.name());
						        annot.addMemberValue("cascade", cascadeEnum);
					        }
//							EnumMemberValue fetchEnum = new EnumMemberValue(cpool);
//							fetchEnum.setType(FetchType.class.getName());
//							fetchEnum.setValue(FetchType.LAZY.name());
//						    annot.addMemberValue("fetch", fetchEnum);
							attr.addAnnotation(annot);
							
							//@JoinTable(name="EMPLOYEE_MEETING", 
							//		joinColumns={@JoinColumn(name="EMPLOYEE_ID")}, 
							//		inverseJoinColumns={@JoinColumn(name="MEETING_ID")})
							annot = new Annotation(JoinTable.class.getName(), cpool);
							annot.addMemberValue("name", new StringMemberValue(modelField.joinTable, cpool));
							//@JoinColumn(name="EMPLOYEE_ID")
							Annotation joinAnno = new Annotation(JoinColumn.class.getName(), cpool);
							joinAnno.addMemberValue("name", new StringMemberValue(modelField.joinField, cpool));
							//{@JoinColumn(name="EMPLOYEE_ID")}
							ArrayMemberValue amv = new ArrayMemberValue(new AnnotationMemberValue(joinAnno, cpool), cpool);
							AnnotationMemberValue[] tm = new AnnotationMemberValue[1]; 
							tm[0] = new AnnotationMemberValue(joinAnno, cpool);
							amv = new ArrayMemberValue(cpool);
							amv.setValue(tm);
							annot.addMemberValue("joinColumns", amv);
							//@JoinColumn(name="EMPLOYEE_ID")
							joinAnno = new Annotation(JoinColumn.class.getName(), cpool);
							joinAnno.addMemberValue("name", new StringMemberValue(modelField.friendField, cpool));
							//{@JoinColumn(name="EMPLOYEE_ID")}
							tm = new AnnotationMemberValue[1]; 
							tm[0] = new AnnotationMemberValue(joinAnno, cpool);
							amv = new ArrayMemberValue(cpool);
							amv.setValue(tm);
							annot.addMemberValue("inverseJoinColumns", amv);
							attr.addAnnotation(annot);
							field.getFieldInfo().addAttribute(attr);
						}
					}
				}
				
				Class<?> newClazz = null;
				newClazz = newHibernateModel.toClass();
				mapModel.put(modelName, newClazz);
				mapModelInverse.put(newClazz, modelName);
				classList.add(newClazz);
			}
			catch (Exception e) {
				System.out.println("[ERROR] VModelController: [" + modelName + "] - " + e.getMessage());
			}
		}
		return classList;
	}
	public static Class<?> getModelClass(String modelName) {
		return mapModel.get(modelName);
	}
	
	public static String getModelName(Class<?> clazz) {
		return mapModelInverse.get(clazz);
	}
	
	private static String generateClassName(String modelName) {
		return modelName + RandomStringUtils.randomAlphabetic(5);
	}
}
