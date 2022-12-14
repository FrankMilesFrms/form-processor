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

	private void lexerFormDatabase(Element element)
	{
		if(element.getKind() != ElementKind.CLASS)
			throw new AnnotationFormatError("Database.Class ???????????????(Class)");

		final Symbol.ClassSymbol classSymbol = (Symbol.ClassSymbol) element;

		// ??????database??????
		final Database AnnoDatabase = element.getAnnotation(Database.class);

		// entities, ?????????"[]_Table.get(), []_Table.get(), ??????"?????????????????????
		final StringBuilder entities = new StringBuilder();
		loadDatabaseEntities(element, entities);

		// ????????????
		final FieldSpec fieldSpec = FieldSpec.builder(
			FormDB.class,
			"mFormDB",
			Modifier.PRIVATE
			).build();

		// ?????????
		final MethodSpec constructorMethodSpec = MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(FormController.class, "formController", Modifier.FINAL)
			.addStatement("mFormDB = new FormDB(\""+ AnnoDatabase.DBName() +"\", "+ entities + ')', FormDB.class)
			// ??????????????????????????????????????????????????????????????????????????????
//			.addStatement("formController.put(mFormDB)")
			.beginControlFlow("if(!formController.put(mFormDB) && formController.isLoadedFile())")
			.addStatement("mFormDB = formController.getFormByName(\""+ AnnoDatabase.DBName() +"\")")
			.endControlFlow()

//			.beginControlFlow("else")
//			.addStatement("Message.printError(\"DB ??????Form????????? ???????????????\")")
//			.endControlFlow()
			.build();

		// ????????????
		final TypeSpec.Builder typeSpec = TypeSpec.classBuilder(classSymbol.getSimpleName() + "_DB")
			.addModifiers(Modifier.PUBLIC)
			.addField(fieldSpec)
			// ?????????????????????????????????Class.forName?????????????????????
			.superclass(ClassName.bestGuess(classSymbol.fullname.toString()))
			.addMethod(constructorMethodSpec);

		// ?????????????????????Dao???????????????
		overrideDao(classSymbol, typeSpec);

		final JavaFile javaFile = JavaFile.builder(
				classSymbol.packge().toString(),
				typeSpec.build()
			)
			.build();
		// ?????? [class_name]_DB ??????
		CodeUtils.saveFile(
			mProcessorData,
			classSymbol.fullname+"_DB",
			javaFile.toString()
		);
	}

	private void overrideDao(Symbol.ClassSymbol classSymbol, TypeSpec.Builder typeSpec)
	{
		classSymbol.getEnclosedElements().forEach(
			it ->
			{
				// ?????????????????????????????????????????? ????????? ????????????
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
							"@Dao ???????????????????????????"
							+ methodSymbol.getParameters().toString()
							+ ", ????????????"
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
						"????????????????????????????????????@Dao??????????????????????????????????????????"
						+ "form :" + methodSymbol + ", fullname = "+ classSymbol.fullname
					);
				}
			}
		);
	}

	/**
	 * ????????????????????????Database entities??????
	 * @see <a href="?????????????????????"> https://blog.csdn.net/u011215710/article/details/106059772</a>
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
//					 sel ???????????? value ??????
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
			mProcessorData.printError("??????@Database??????????????????entities??????????????? form : "+element.getSimpleName());
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
