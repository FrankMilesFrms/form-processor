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
package psnl.frms.form.utils;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/14 15:20
 */
public final class ProcessorData
{
	private final Types typeUtils;
	public Messager messager;
	public JavacTrees trees;
	public TreeMaker treeMaker;
	public Names names;
	public Filer mFiler;

	public ProcessorData(ProcessingEnvironment processingEnv)
	{
		this.messager = processingEnv.getMessager();
		this.trees = JavacTrees.instance(processingEnv);
		Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
		this.treeMaker = TreeMaker.instance(context);
		this.names = Names.instance(context);
		this.typeUtils = processingEnv.getTypeUtils();
		mFiler = processingEnv.getFiler();
	}

	public void printNote(String message)
	{
		messager.printMessage(Diagnostic.Kind.NOTE, message);
	}

	public void printError(String message)
	{
		messager.printMessage(Diagnostic.Kind.ERROR, message);
	}

}
