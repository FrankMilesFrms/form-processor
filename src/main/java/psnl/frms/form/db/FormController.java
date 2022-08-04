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
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 实现的控制器
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/30 9:50
 */
public class FormController extends AbstractDBController<FormDB, FormTable, FormColumn> implements Serializable
{


	private static final long serialVersionUID = -75065447454881535L;
	// TODO: 2022/7/31 愚蠢且危险
	private HashSet<FormDB> mFormDBS;

	private transient Iterator<FormDB> mIterator = null;

	private FormCallback mCallback = null;
	private File mSaveFile = null;
	private static volatile FormController sFormController = null;


	private FormController(boolean createNew) throws Exception
	{
		mFormDBS = new HashSet<>();
		loadByFile(
			new File(
				System.getProperty("user.dir") + File.separatorChar + "target"+ File.separatorChar +"formDB.db"
			),
			createNew
			);
	}

	private FormController(File pFile) throws Exception
	{
		mFormDBS = new HashSet<>();
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
		for (FormDB formDB : mFormDBS)
		{
			formDB.unityCallback(pCallback);
		}
	}


	/**
	 * 获取的实例，只是保存在内存中，程序一旦结束，数据就会丢失，你可以缓存数据，但这并不是你替换更好方法的理由。
	 * 最好的方法是，保存在外存中：{@link FormController#FormController(File)}
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
	public static FormController getInstance(File pFile) throws Exception
	{
		if(sFormController == null)
		{
			synchronized (FormController.class)
			{
				if(sFormController == null)
				{
					sFormController = new FormController(pFile);
					return sFormController;
				}
			}
		}
		return sFormController;
	}

	public static FormController getInstance() {
		if(needInit())
			Message.printError("你必须在运行前初始化FormController!");
		return sFormController;
	}

	/**
	 * 通过数据库名字检索数据库
	 * @param name
	 */
	public FormDB getFormByName(@NotNull String name)
	{
		FormDB formDB;
		while (hasNext()) {
			formDB = getNext();
			if(formDB.getName().equals(name))
				return formDB;
		}
		reset();
		return null;
	}
	/**
	 * todo 不做克隆：数据封装性遭到破坏。
	 * @return form db
	 */
	@Override
	public FormDB getNext()
	{
		getIterator();
		return mIterator.next();
	}

	@Override
	public void reset()
	{
		mIterator = null;
	}

	@Override
	public boolean hasNext()
	{
		getIterator();
		return mIterator.hasNext();
	}

	@Override
	public boolean put(FormDB element)
	{
		if(!mFormDBS.contains(element))
		{
			mFormDBS.add(element);
			if(mCallback != null)
				mCallback.putDB(element);
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(FormDB element)
	{
		if(mFormDBS.contains(element))
		{
			mFormDBS.remove(element);
			if(mCallback != null)
				mCallback.deleteDB(element);
			return true;
		}
		return false;
	}

	private void getIterator()
	{
		if(mIterator == null) {
			mIterator = mFormDBS.iterator();
		}
	}

	/**
	 * 给出空文件夹，或指定一个包含数据文件的文件夹，以此执行本地化存储或读取数据库。
	 * @param pFile file
	 * @throws IOException e
	 */
	private void loadByFile(File pFile, boolean cache) throws Exception
	{
		mSaveFile = pFile;

		if(cache && pFile.isFile() && !pFile.delete())
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
			mFormDBS = packageForm.mFormDBS;
			mCallback = packageForm.mCallback;
			mSaveFile = packageForm.mFile;
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
		final PackageForm packageForm = new PackageForm(mFormDBS, mCallback, mSaveFile);
		Kits.saveObject(packageForm, mSaveFile);

		if(mCallback != null)
			mCallback.onSaved(mSaveFile);
	}

	/**
	 * @hide
	 * 用于实例化的类，避免破坏单例
	 */
	public class PackageForm implements Serializable
	{
		private static final long serialVersionUID = -3351440349064207479L;

		public final HashSet<FormDB> mFormDBS;
		public final FormCallback mCallback;
		public final File mFile;

		public PackageForm(HashSet<FormDB> pFormDBS, FormCallback pCallback, File pFile)
		{
			mFormDBS = pFormDBS;
			mCallback = pCallback;
			mFile = pFile;
		}
	}
}
