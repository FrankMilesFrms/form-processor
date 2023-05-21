/*
 * Copyright (C) 2022 Frank Miles - Frms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package psnl.frms.form.processor;

import com.squareup.javapoet.*;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import psnl.frms.form.annotation.Dao;
import psnl.frms.form.annotation.Database;
import psnl.frms.form.db.FormController;
import psnl.frms.form.db.FormDB;
import psnl.frms.form.utils.CodeUtils;
import psnl.frms.form.utils.Kits;
import psnl.frms.form.utils.ProcessorData;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.lang.annotation.AnnotationFormatError;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/02 11:32
 */
public class FormDBProcessor extends AbstractProcessor
{
	private ProcessorData mProcessorData;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);
		mProcessorData = new ProcessorData(processingEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		roundEnv.getElementsAnnotatedWith(Database.class)
			.forEach(
				this::lexerFormDatabase
			);
		return true;
	}

	/**
	 * 该方法是一个私有方法，接受一个Element类型的参数。它是编译时注解处理器的一部分，用于从数据库注解中提取信息，并生成相应的代码文件。该代码文件是一种名为“[class_name]_DB.java”的Java类文件，用于帮助访问和管理数据库。
	 * 首先，它检查传递给它的元素是否是类。如果不是，则抛出一个注释格式错误。
	 * 接下来，它读取数据库注释(Database Annotation)中的信息，并保存在一个StringBuilder对象中。
	 * 然后它定义了一个私有字段，名为“mFormDB”，并创建了一个构造函数来初始化它。
	 * 接下来，它创建了一个Java类文件，生成一个名为“[class_name]_DB.java”的Java类文件，并将其写入磁盘。
	 * 最后它调用了“overrideDao”方法，重写数据库类（Database Class）中带有“@Dao”注解的所有抽象方法，以提供Dao实现方法。
	 * @param element
	 */
	private void lexerFormDatabase(Element element)
	{
		if(element.getKind() != ElementKind.CLASS)
			throw new AnnotationFormatError("Database.Class 只能修饰类(Class)");

		final Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) element;

		// 读取database内容
		final Database AnnoDatabase = element.getAnnotation(Database.class);

		// entities, 格式："[]_Table.get(), []_Table.get(), ···"，类是全路径。
		final StringBuilder entities = new StringBuilder();
		loadDatabaseEntities(element, entities);

		// 初始字段
		final FieldSpec fieldSpec = FieldSpec.builder(
			FormDB.class,
			"mFormDB",
			Modifier.PRIVATE
			).build();

		// 构造器
		final MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(FormController.class, "formController", Modifier.FINAL)
			.addStatement("mFormDB = new FormDB(\""+ AnnoDatabase.DBName() +"\", "+ entities + ')', FormDB.class)
			// 如果是从文件中进行加载，则将内容写入进去，以便保存。
//			.addStatement("formController.put(mFormDB)")
			// 函数已经发生改变
//			.beginControlFlow("if(!formController.put(mFormDB) && formController.isLoadedFile())")
//			.addStatement("mFormDB = formController.getFormByName(\""+ AnnoDatabase.DBName() +"\")")
//			.endControlFlow()
			.addStatement("formController.put(mFormDB)")
//			.beginControlFlow("else")
//			.addStatement("Message.printError(\"DB 写入Form产生的 未知错误！\")")
//			.endControlFlow()
			// 见 put方法
			.addStatement("mFormDB = formController.getNext()")
			.build();

		// 添加主类
		final TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classSymbol.getSimpleName() + "_DB")
			.addModifiers(Modifier.PUBLIC)
			.addField(fieldSpec)
			// 这是先编译的，所以使用Class.forName并不能获取到类
			.superclass(ClassName.bestGuess(classSymbol.fullname.toString()))
			.addMethod(constructorMethodSpec);

		// 重写方法，提供Dao实现方法。
		overrideDao(classSymbol, typeSpec);

		final JavaFile javaFile = JavaFile.builder(
				classSymbol.packge().toString(),
				typeSpec.build()
			)
			.build();
		// 写入 [class_name]_DB 文件
		CodeUtils.saveFile(
			mProcessorData,
			classSymbol.fullname+"_DB",
			javaFile.toString()
		);
	}

	/**
	    该方法是一个私有方法，接受两个参数：一个类符号类(SYMBOL.ClassSymbol)和一个Java类定义(TypeSpec.Builder)。
	 *  它用于重写数据库类中带有“@Dao”注解的所有抽象方法。
	 *  首先，它遍历类符号中的所有元素。如果该元素不是方法或构造函数，则跳过该元素。
	 *  否则，如果该元素是一个抽象方法，则检查它是否带有“@Dao”注解。
	 *  如果没有，它将抛出一个错误。如果带有该注解，则它将为该方法创建一个新的方法规范(MethodSpec)。
	 *  这个新方法将调用一个名为“getInstance”的静态方法，该方法采用mFormDB作为参数，并返回一个带有类型className的实例。最后，它将添加这个新方法规范(MethodSpec)到Java类中。
	 * 需要注意的是，在调用“getInstance”方法时，它将使用方法的返回类型来获取Dao的类名称，并在其后面添加“_Dao”。
	 *
	 * 例如，如果返回类型为“MyDao”，则它将调用“MyDao_Dao.getInstance(mFormDB)”来获取Dao实例。
	 * @param classSymbol
	 * @param typeSpec
	 */
	private void overrideDao(Symbol.ClassSymbol classSymbol, TypeSpec.Builder typeSpec)
	{
		classSymbol.getEnclosedElements().forEach(
			it ->
			{
				// 非方法类或构造函数类均不进行 可能的 对象重写
				if(
					!(it instanceof Symbol.MethodSymbol)
					|| it.getQualifiedName().toString().equals("<init>")
					|| !it.getModifiers().contains(Modifier.ABSTRACT))
				{
					return;
				}

				final Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) it;

				if(it.hasAnnotations() && it.getAnnotation(Dao.class) != null)
				{
					if(methodSymbol.getParameters().size() > 0)
					{
						mProcessorData.printError(
							"@Dao 注解不应该有参数："
							+ methodSymbol.getParameters().toString()
							+ ", 位于方法"
							+ it.getSimpleName()
						);
					}

					final ClassName className = ClassName.bestGuess(methodSymbol.getReturnType().toString());
					final MethodSpec methodSpec = MethodSpec.methodBuilder(it.getSimpleName().toString())
						.addAnnotation(Override.class)
						.addModifiers(Modifier.PUBLIC)
						.returns(className)
						.addStatement(
							"return " + className.simpleName() + "_Dao.getInstance(mFormDB)"
						)
						.build();
					typeSpec.addMethod(methodSpec);

				} else
				{
					mProcessorData.printError(
						"所有抽象方法必须有注解“@Dao”，否则不允许使用抽象方法。"
						+ "form :" + methodSymbol + ", fullname = "+ classSymbol.fullname
					);
				}
			}
		);
	}

	/**
	 * 这里面提取的，是Database entities内容
	 * @see <a href="为什么这么做？"> https://blog.csdn.net/u011215710/article/details/106059772</a>
	 *
	 * @param element
	 * @param entities
	 */
	private void loadDatabaseEntities(Element element, StringBuilder entities)
	{
		for(AnnotationMirror compound : element.getAnnotationMirrors())
		{
			if( !(compound instanceof Attribute.Compound))
				continue;

			if (compound.toString().contains("psnl.frms.form.annotation.Database"))
			{
				// sel = class com.sun.tools.javac.code.Symbol$MethodSymbol
				// value = class com.sun.tools.javac.code.Symbol$MethodSymbol
				compound.getElementValues().forEach((sel, value) ->
				{
//					 sel 注解名， value 参数
					if (sel.toString().equals("entities()"))
					{
						final Attribute.Array array = (Attribute.Array) value;
						for(Attribute attribute : array.values)
						{
//							type =java.lang.Class<psnl.frms.form.Teacher>, Value =psnl.frms.form.Teacher, psnl.frms.form.Teacher.class
//							mProcessorData.printNote("type =" + attribute.type + ", Value =" + attribute.getValue()+", "+attribute);
							entities.append(attribute.getValue()).append("_Table.get(),");
						}
					}
//					mProcessorData.printNote(
//						"sel = {class= "+sel.getClass()+", str = "+sel+"}, " +
//						"value = {class = "+ sel.getClass() +", str = "+value+"}"
//					);
				});
				break;
			}
		}

		if(entities.length() == 0)
			mProcessorData.printError("标有@Database注解的类，其entities不能为空！ form : "+element.getSimpleName());
		else
		{
			entities.deleteCharAt(entities.length()-1);
		}
	}

	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Kits.of(Database.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}
}
