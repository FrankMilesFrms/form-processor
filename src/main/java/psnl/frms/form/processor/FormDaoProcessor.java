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
import com.sun.tools.javac.code.Symbol;
import psnl.frms.form.annotation.Dao;
import psnl.frms.form.annotation.Delete;
import psnl.frms.form.annotation.Insert;
import psnl.frms.form.annotation.Query;
import psnl.frms.form.db.FormColumn;
import psnl.frms.form.db.FormDB;
import psnl.frms.form.processor.lexer.LexerEntityClass;
import psnl.frms.form.utils.CodeUtils;
import psnl.frms.form.utils.Kits;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.ProcessorData;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.AnnotationFormatError;
import java.util.Set;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/02 16:01
 */
public class FormDaoProcessor extends AbstractProcessor
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
		roundEnv.getElementsAnnotatedWith(Dao.class)
			.forEach(
				this::lexerDao
			);
		return true;
	}

	private void lexerDao(Element pElement)
	{
		if(pElement.getKind() == ElementKind.METHOD)
			return;

		if(pElement.getKind() != ElementKind.CLASS)
			throw new AnnotationFormatError(
				"Dao.Class 只能修饰类和方法(Class、METHOD)， it's " + pElement.getKind()
					+"，form :" + pElement.getSimpleName());

		final Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) pElement;

		// 将要创建的类名
		final String className = classSymbol.fullname + "_Dao";

		final FieldSpec fieldBaseDB = FieldSpec.builder(
			FormDB.class,
			"mBaseDB",
			Modifier.PUBLIC, Modifier.FINAL
		).build();

		// 单例模式，私有化构造器
		final MethodSpec constructor = MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PRIVATE)
			.addParameter(FormDB.class, "pBaseDB", Modifier.FINAL)
			.addStatement("mBaseDB = pBaseDB")
			.build();

		// 静态字段 private static volatile
		final FieldSpec fieldSpec = FieldSpec.builder(
			ClassName.bestGuess(className),
			"sDao",
			Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE
		)
			.initializer("null")
			.build();

		// getInstance
		final MethodSpec instance = MethodSpec.methodBuilder("getInstance")
			.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
			.addParameter(FormDB.class, "pBaseDB", Modifier.FINAL)
			.returns(ClassName.bestGuess(className))
			.beginControlFlow("if(sDao == null) ")
			.beginControlFlow("synchronized ($T.class)", ClassName.bestGuess(className))
			.beginControlFlow("if (sDao == null) ")
			.addStatement("sDao = new $T(pBaseDB)", ClassName.bestGuess(className))
			.addStatement("return sDao")
			.endControlFlow()
			.endControlFlow()
			.endControlFlow()
			.addStatement("return sDao")
			.build();

		// 添加主类
		final TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classSymbol.getSimpleName() + "_Dao")
			.addModifiers(Modifier.PUBLIC)
			.superclass(ClassName.bestGuess(classSymbol.fullname.toString()))
			.addMethod(constructor)
			.addField(fieldSpec)
			.addField(fieldBaseDB)
			.addMethod(instance);

		// 实现抽象方法：
		overrideDaoMethod(classSymbol, typeSpec);

		final JavaFile javaFile = JavaFile.builder(
				classSymbol.packge().toString(),
				typeSpec.build()
			)
			.build();

		// 写入 [class_name]_Table 文件
		CodeUtils.saveFile(
			mProcessorData,
			className,
			javaFile.toString()
		);

	}

	private void overrideDaoMethod(Symbol.ClassSymbol classSymbol, TypeSpec.Builder typeSpec)
	{
		classSymbol.getEnclosedElements().forEach(
			it ->
			{
				// 非方法类或构造函数类均不进行 可能的 对象重写
				if(
					!(it instanceof Symbol.MethodSymbol)
						|| it.getQualifiedName().toString().equals("<init>")
						|| !it.getModifiers().contains(Modifier.ABSTRACT)
				) {
					return;
				}

				final Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) it;

				if(methodSymbol.getParameters().size() != 1)
					mProcessorData.printError("@Insert、@Delete、@Query的抽象方法，有且只能有一个参数。");

				// 写入参数
				final Symbol.VarSymbol varSymbol = methodSymbol.getParameters().get(0);

				// 检查是否存在多种操作和无操作意义的抽象函数。
				int type = 0;

				final MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodSymbol.getSimpleName().toString())
					.addAnnotation(Override.class)
					.addModifiers(Modifier.PUBLIC)
					.addParameter(
						ClassName.bestGuess(varSymbol.asType().toString()),
						varSymbol.toString()
					);
				mProcessorData.printNote("params : " + varSymbol);

				if(it.getAnnotation(Insert.class) != null)
				{
					type++;
					lexerInsert(methodSymbol, methodSpec, varSymbol.toString());
				}

				if(it.getAnnotation(Delete.class) != null)
				{
					type++;
					lexerDelete(methodSymbol, methodSpec, varSymbol.toString());
				}

				if(it.getAnnotation(Query.class) != null)
				{
					type++;
					lexerQuery(methodSymbol, methodSpec, varSymbol.toString());
				}


				if(type < 1)
				{
					mProcessorData.printError(
						"所有抽象方法必须有注解“@Insert、@Delete、@Query”之一，否则不允许使用抽象方法。"
							+ "form :" + methodSymbol + ", fullname = "+ classSymbol.fullname
					);
				} else if(type > 1)
				{
					mProcessorData.printError(
						"所有抽象方法只能有“@Insert、@Delete、@Query”其中一个。"
							+ "form :" + methodSymbol + ", fullname = "+ classSymbol.fullname
					);
				} else
				{
					typeSpec.addMethod(methodSpec.build());
				}
			}
		);
	}

	private void lexerQuery(Symbol.MethodSymbol pMethodSymbol, MethodSpec.Builder pBuilder, String varName)
	{
		pBuilder
			.addStatement(varName + ".runAsync(mBaseDB)");
	}

	private void lexerDelete(Symbol.MethodSymbol pMethodSymbol, MethodSpec.Builder pBuilder, String varName)
	{
		pBuilder
			.addStatement(
				"final $T formColumn = $T.lexerEntity(" + varName+ ")",
				FormColumn.class, LexerEntityClass.class
			)
			.beginControlFlow(" if(!mBaseDB.delete(formColumn))")
			.addStatement("$T.printError(\"删除失败\")", Message.class)
			.endControlFlow();
	}

	private void lexerInsert(Symbol.MethodSymbol pMethodSymbol, MethodSpec.Builder pBuilder, String varName)
	{
		pBuilder
			.addStatement(
				"final $T formColumn = $T.lexerEntity(" + varName+ ")",
				FormColumn.class, LexerEntityClass.class
			)
			.beginControlFlow(" if(!mBaseDB.put(formColumn, null))")
			.addStatement("$T.printError(\"添加失败\")", Message.class)
			.endControlFlow();
	}


	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Kits.of(
			Dao.class.getCanonicalName(),
			Insert.class.getCanonicalName(),
			Delete.class.getCanonicalName(),
			Query.class.getCanonicalName()
		);
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

}
