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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.HashSet;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.tools.JavaFileObject;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/16 11:27
 */
public class CodeUtils
{
	/**
	 * 将变量转化为静态代码。
	 * @param klass
	 * @return
	 */
	public static String toString(Class<?>[] klass)
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(Class<?> c : klass)
		{
			stringBuilder.append(c.getCanonicalName()).append(".class,\n");
		}

		return stringBuilder.toString();
	}

	/**
	 * 获取元素包名（含路径）
	 * @param typeElement
	 * @return
	 */
	public static String getPackageName(Element typeElement)
	{
		while (typeElement.getKind() != ElementKind.PACKAGE) {
			typeElement = typeElement.getEnclosingElement();
		}
		return ((PackageElement)typeElement).getQualifiedName().toString();
	}

	static final HashSet<String> createFiles = new HashSet<>();

	/**
	 * 写入代码文件
	 * @param pProcessorData 所在数据
	 * @param qualifiedName 写入类完整名
	 * @param code 代码
	 */
	public static void saveFile(ProcessorData pProcessorData, String qualifiedName, String code)
	{
		if(createFiles.contains(qualifiedName)) {
			return;
		}

		try {
			JavaFileObject sourceFile = pProcessorData.mFiler.createSourceFile(qualifiedName);
			Writer writer = sourceFile.openWriter();
			writer.write(code);
			writer.flush();
			writer.close();
			createFiles.add(qualifiedName);
		} catch (IOException e) {
			pProcessorData.printError("写入错误！"+e);
		}
	}

	/**
	 * package [];
	 * import [user全名];
	 * public class User_Impl extends Column
	 * {
	 * 	private static User_Impl _instance_User = null;
	 *
	 * 	public static synchronized User_Impl getInstance()
	 *        {
	 * 		if(_instance_User == null) {
	 * 			_instance_User = new User_Impl();
	 *        }
	 * 		return _instance_User;
	 *    }
	 * }
	 * @param daoName
	 * @return
	 */
	public static String getDaoImplCode(String daoName, String packageName, String qualifiedName)
	{

		return (packageName.isEmpty()? "" : "package "+packageName+"; \n")+
		"import "+qualifiedName+"; \n"+
//		"import psnl.frms.form.compiler.base.Database;\n"+
		"public class "+ daoName +"_Impl extends "+daoName+"\n" +
		"{\n" +
		"\tprivate static _db = null;\n"+
//		"\t\npublic Database"+
		"\tprivate static "+ daoName +"_Impl _instance_"+ daoName +" = null;\n" +
		"\tpublic static synchronized "+ daoName +"_Impl getInstance(/*Database db*/)\n"
			+ "\t{\n"
			+ "\t\t//_db = db\n"
			+ "\t\tif(_instance_"+ daoName +" == null) {\n"
			+ "\t\t\t_instance_"+ daoName +" = new "+ daoName +"_Impl();\n"
			+ "\t\t}\n"
			+"\t\treturn _instance_"+ daoName +";\n" +
		"\t}\n" +
		"}\n";
	}

	/**
	 * 将对象序列化到指定文件中
	 * @param obj obj
	 * @param fileName path
	 * @throws IOException
	 */
	public static void serializeObject(Object obj, String fileName) throws IOException {
		final ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(fileName));
		objOut.writeObject(obj);
		objOut.close();
	}

	/**
	 * 从指定文件中反序列化对象
	 * @param fileName name
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static <T> T deserializeObj(String fileName) throws IOException, ClassNotFoundException {
		final ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(fileName));
		return (T) objIn.readObject();
	}
}
