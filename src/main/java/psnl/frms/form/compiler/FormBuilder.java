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
package psnl.frms.form.compiler;

import psnl.frms.form.db.FormCallback;
import psnl.frms.form.db.FormController;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * 数据库构建类，由于构建一个数据库。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/02 10:58
 */
public class FormBuilder<R>
{
	private final FormController formController;
	private final Class<R> mRClass;

	/**
	 * @param pClass 数据库所在类
	 * @param pFile 保存的路径，为null则设置缓存
	 * @param createNew 是否覆盖原来缓存数据库，为true，删除之前缓存，缓存有效。
	 * @param isFolder 是否保存的为文件夹，非缓存有效
	 * @throws Exception err
	 */
	private FormBuilder(Class<R> pClass, File pFile, boolean createNew, boolean isFolder) throws Exception
	{
		if (pFile == null) {
			formController = FormController.getInstance(createNew);
		}
		else {
			formController = FormController.getInstance(pFile, isFolder);
		}
		mRClass = pClass;
	}


	/**
	 * 统一 Callback, 设置此方法后，会清除原有回调，但你仍然可以重新分配。
	 * @return builder
	 */
	public FormBuilder<R> unityCallback(FormCallback pCallback)
	{
		formController.unityCallback(pCallback);
		return this;
	}

	public R build() throws Exception
	{
		return getInstantiateObject(mRClass);
	}

	/**
	 * 通过内存创建
	 * @param db
	 * @param <T>
	 * @return
	 */
	public static <T> FormBuilder<T> createCacheDatabase(Class<T> db, boolean reloadTemp) throws Exception {
		return new FormBuilder<>(db, null, !reloadTemp, false);
	}

	public static <T> FormBuilder<T> createFileDatabase(Class<T> db, File pFile, boolean isFolder) throws Exception {
		return new FormBuilder<>(db, pFile, false, isFolder);
	}

	/**
	 * 加载DB实例
	 * @param pClass 数据库所在类
	 * @param <T> 数据库所在类实例
	 * @return 实例
	 */
	public static <T> T getInstantiateObject(Class<T> pClass) throws Exception
	{
		Object result =
			Class.forName(pClass.getCanonicalName() + "_DB")
				.getConstructor(FormController.class)
				.newInstance(FormController.getInstance());
		return (T) result;
	}
}
