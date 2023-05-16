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
package psnl.frms.form.db;

import psnl.frms.form.compiler.DBWhere;
import psnl.frms.form.compiler.FormBuilder;
import psnl.frms.form.compiler.abstraction.*;
import psnl.frms.form.utils.Kits;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.NotNull;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 实现的控制器
 * 控制器只维护一个{@link FormDB}，并且用户无权删除、更改数据库。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/30 9:50
 */
public class FormController extends AbstractDBController<FormDB, FormTable, FormColumn> implements Serializable
{
	private static final long serialVersionUID = -75065447454881535L;

	/**
	 * 仅维护一个数据库
	 */
	private FormDB mFormDB = null;


	private FormCallback mCallback = null;

	private File mSaveFile = null;

	private static volatile FormController sFormController = null;

	private boolean isLoadedFile = false;

	/**
	 * 创建、加载缓存
	 * @param createNew 是否创建新的缓存
	 * @throws Exception 错误
	 */
	private FormController(boolean createNew) throws Exception
	{
		loadByFile(
			new File(
				System.getProperty("user.dir") + File.separatorChar + "target"+ File.separatorChar +"formDB.db"
			),
			createNew
			);
	}

	/**
	 * 创建指定路径的缓存
	 * @param pFile 路径位置
	 * @param isFolder 是否是文件夹
	 * @throws Exception 错误
	 */
	private FormController(File pFile, boolean isFolder) throws Exception
	{
		if(isFolder) {
			pFile = new File(pFile.getAbsoluteFile() + System.getProperty("user.dir") + "formDB.db");
		}
		loadByFile(pFile, false);
	}


	@Override
	public void addCallback(AbstractDBCallback pCallback)
	{
		if(! (pCallback instanceof FormCallback))
		{
			Message.printError("FormController 中，提供的pCallback应该是FormCallback");
			return;
		}

		if(mCallback != null) {
			Message.printWarning("FormController 已存在回调，不应该再次设置。");
		}
		mCallback = (FormCallback) pCallback;
	}

	/**
	 * @see FormBuilder#unityCallback(FormCallback)
	 */
	public void unityCallback(FormCallback pCallback)
	{
		mCallback = pCallback;
		mFormDB.unityCallback(pCallback);
	}


	/**
	 * 获取的实例，只是保存在内存中，程序一旦结束，数据就会丢失，你可以缓存数据，但这并不是你替换更好方法的理由。
	 * 最好的方法是，保存在外存中：{@link FormController#FormController(File, boolean)}
	 * @param createNew 是否覆盖原来数据库，为true，删除之前缓存。
	 * @return FormController
	 */
	public static FormController getInstance(boolean createNew) throws Exception
	{
		if(sFormController == null)
		{
			synchronized (FormController.class)
			{
				if(sFormController == null)
				{
					sFormController = new FormController(createNew);
					return sFormController;
				}
			}
		}
		return sFormController;
	}

	public static boolean needInit()
	{
		return sFormController == null;
	}

	/**
	 * 给出一个空文件夹，或指定一个包含数据文件的文件夹，以此执行本地化存储或读取数据库。
	 * @param pFile 路径
	 * @return FormController
	 * @throws IOException 文件夹包含文件
	 */
	public static FormController getInstance(File pFile, boolean isFolder) throws Exception
	{
		if(sFormController == null)
		{
			synchronized (FormController.class)
			{
				if(sFormController == null)
				{
					sFormController = new FormController(pFile, isFolder);
					return sFormController;
				}
			}
		}
		return sFormController;
	}

	/**
	 * 此实例是在拥有控制器的基础上，才会获取，如果你是二次加载，请注意不要使用此方法。
	 * @return
	 */
	public static FormController getInstance() {
		if(needInit())
			Message.printError("你必须在运行FormBuild之后，再初始化FormController!");
		return sFormController;
	}

	/**
	 * 只有唯一的数据库，因此，你不能获取到下一个，但会返回唯一的值
	 * @return form db
	 */
	@Override
	public FormDB getNext()
	{
		return mFormDB;
	}

	@Deprecated
	@Override
	public void reset() {
		throw new RuntimeException("已弃用");
	}

	/**
	 * 参考{@link #getNext()}
	 * @return
	 */
	@Deprecated
	@Override
	public boolean hasNext()
	{
		return false;
	}

	/**
	 * 不会创建新数据库，会合并统一为一个数据库
	 * @param element
	 * @return
	 */
	@Override
	public boolean put(FormDB element)
	{
		if(mFormDB == null) {
			mFormDB = element;
			return true;
		}

		while (element.hasNext()) {
			if(!mFormDB.put(element.getNext())) {
				Message.printError("已经存在一个完全相同的表类型！");
			}
		}
		return true;
	}

	/**
	 * @deprecated 不可删除唯一的数据库
	 * @param element
	 * @return
	 */
	@Deprecated
	@Override
	public boolean delete(FormDB element)
	{
		throw new RuntimeException("不可删除唯一的数据库");
	}

	/**
	 * 给出空文件夹，或指定一个包含数据文件的文件夹，以此执行本地化存储或读取数据库。
	 * @param pFile file
	 * @param deleteCache 是否删除缓存
	 * @throws IOException e
	 */
	private void loadByFile(File pFile, boolean deleteCache) throws Exception
	{
		mSaveFile = pFile;

		if(deleteCache && pFile.isFile() && !pFile.delete())
		{
			Message.printError("删除失败！");
			return;
		}

		if(pFile.exists())
		{
			final Object object =  Kits.readObject(mSaveFile);
			if(! (object  instanceof PackageForm))
			{
				throw new Exception("不是目标序列化的对象");
			}
			final PackageForm packageForm = (PackageForm) object;
			mFormDB = packageForm.mFormDB;
			mCallback = packageForm.mCallback;
			mSaveFile = packageForm.mFile;

			isLoadedFile = true;

		}
		if(mCallback != null)
			mCallback.onCreate(pFile);

	}

	/**
	 * 保存所有数据到预定文件内，会覆盖源文件。
	 * @throws Exception
	 */
	public void saveAll() throws Exception
	{
		final PackageForm packageForm = new PackageForm(mFormDB, mCallback, mSaveFile);
		Kits.saveObject(packageForm, mSaveFile);

		if(mCallback != null)
			mCallback.onSaved(mSaveFile);
	}

	/**
	 * 是否是从文件中加载的
	 * @return result
	 */
	public boolean isLoadedFile()
	{
		return isLoadedFile;
	}


	/**
	 * @hide
	 * 用于实例化的类，避免破坏单例
	 */
	public class PackageForm implements Serializable
	{
		private static final long serialVersionUID = -3351440349064207479L;

		public final FormDB mFormDB;
		public final FormCallback mCallback;
		public final File mFile;

		public PackageForm(FormDB pFormDB, FormCallback pCallback, File pFile)
		{
			mFormDB = pFormDB;
			mCallback = pCallback;
			mFile = pFile;
		}
	}
}
