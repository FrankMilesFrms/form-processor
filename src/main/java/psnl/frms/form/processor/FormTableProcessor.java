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

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import psnl.frms.form.annotation.*;
import psnl.frms.form.compiler.abstraction.AbstractDBColumn;
import psnl.frms.form.db.FormColumn;
import psnl.frms.form.db.FormTable;
import psnl.frms.form.utils.CodeUtils;
import psnl.frms.form.utils.Kits;
import psnl.frms.form.utils.ProcessorData;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/02 16:01
 */
public class FormTableProcessor extends AbstractProcessor
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
		roundEnv.getElementsAnnotatedWith(Entity.class)
			.forEach(
				this::lexerTableDatabase
			);
		return true;
	}

	private void lexerTableDatabase(Element pElement)
	{
		if(pElement.getKind() != ElementKind.CLASS)
			throw new AnnotationFormatError(
				"Entity.Class 只能修饰类(Class)， it's " + pElement.getKind()
					+"，form :" + pElement.getSimpleName());

		final Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) pElement;

		final Entity AnnoEntity = pElement.getAnnotation(Entity.class);

		String name = AnnoEntity.tableName();
		name = name.isEmpty()? classSymbol.fullname.toString() : name;


		// 写入方法 get，用于创建表
		final MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("get")
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
			.returns(FormTable.class)
			.addStatement("final $T formColumn = new FormColumn()", FormColumn.class);

		classSymbol.getEnclosedElements().forEach(
			it -> {
				if(!(it instanceof Symbol.VarSymbol))
					return;

				if(it.getAnnotation(ColumnIgnore.class) != null )
				{
					mProcessorData.printNote("已忽略 "+ classSymbol.fullname + " 的字段 "+ it.getSimpleName());
					return;
				}

				final Symbol.VarSymbol varSymbol = (Symbol.VarSymbol) it;

				methodSpec.addStatement(
					"formColumn.put("
					+ addParameter(varSymbol, it.getAnnotation(ColumnInfo.class))
					+")"
				);

			}
		);


		methodSpec.addStatement("final $T formTable = new FormTable(formColumn, \""+ name +"\")", FormTable.class);
		methodSpec.addStatement("return formTable");

		// 添加主类
		final TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classSymbol.getSimpleName() + "_Table")
			.addModifiers(Modifier.PUBLIC)
			.addMethod(methodSpec.build());

		final JavaFile javaFile = JavaFile.builder(
				classSymbol.packge().toString(),
				typeSpec.build()
			)
			.indent("\t")
			.build();

		// 写入 [class_name]_Table 文件
		CodeUtils.saveFile(
			mProcessorData,
			classSymbol.fullname+"_Table",
			javaFile.toString()
		);

	}

	/**
	 * 补全参数
	 * (String name, int valueType, boolean primaryKey, Object object
	 * @param pVarSymbol
	 * @param pAnnotation
	 * @return
	 */
	private  String addParameter(Symbol.VarSymbol pVarSymbol, ColumnInfo pAnnotation)
	{
		String name = pVarSymbol.getSimpleName().toString();

		if(pAnnotation != null && !Objects.equals(pAnnotation.name(), ColumnInfo.INHERIT_FIELD_NAME)) {
			name = pAnnotation.name();
		}


		final String type = pVarSymbol.asType().toString();

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
			.append('"').append(name).append('"').append(',')
			.append(FormColumn.getTypeInt(type)).append(',')
			.append(pVarSymbol.getAnnotation(PrimaryKey.class) != null).append(',')
			.append(FormColumn.getTypeObject(type));

		return stringBuilder.toString();
	}


	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Kits.of(Entity.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

}
