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

import psnl.frms.form.compiler.FormBuilder;
import psnl.frms.form.compiler.abstraction.AbstractDatabase;
import psnl.frms.form.compiler.abstraction.AbstractDBCallback;
import psnl.frms.form.compiler.DBInterpolator;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 初始化表的工作应该交给中间翻译层做，因此，这里必须以构造函数的形式初始化表。
 * 如果后续要添加或删除新的表，应该单独通知。
 * 区分表，只依靠名字。
 * 初始化，必须
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/30 9:50
 */
public class FormDB extends AbstractDatabase<FormTable, FormColumn> implements Serializable
{

	@Override
	public boolean equals(Object pO)
	{
		if (this == pO) return true;
		if (pO == null || getClass() != pO.getClass()) return false;
		FormDB formDB = (FormDB) pO;
		return mName.equals(formDB.mName);
	}

	private static final long serialVersionUID = -5840572148768569989L;
	private final HashSet<FormTable> mFormTables;

	private transient Iterator<FormTable> mIterator = null;

	private String mName = null;

	private FormCallback mCallback;


	private FormDB(HashSet<FormTable> pFormTables, String pName, FormCallback pCallback)
	{
		mFormTables = pFormTables;
		mName = pName;
		mCallback = pCallback;
	}

	public HashSet<psnl.frms.form.db.FormTable> getFormTables()
	{
		return mFormTables;
	}

	@SafeVarargs
	public FormDB(String pName, Pair<FormColumn, String>... pPairs)
	{
		mFormTables = new HashSet<>();
		for(Pair<FormColumn, String> pair : pPairs)
		{
			mFormTables.add(new FormTable(pair.first, pair.second));
		}

		mName = pName;
	}

	@SafeVarargs
	public FormDB(String pName, FormTable...pFormTables)
	{
		mFormTables = new HashSet<>();
		mFormTables.addAll(Arrays.asList(pFormTables));

		mName = pName;
	}

	public FormTable getFormTable(FormColumn pFormColumn)
	{
		for (FormTable formTable : mFormTables)
		{
			if(formTable.getTypeColumnHashCode() == pFormColumn.getTypeHashCode())
				return formTable;
		}
		return null;
	}

	@Override
	public String getName()
	{
		return mName;
	}

	@Override
	public void setName(String name)
	{
		if(mName != null || name == null) {
			Message.printError("FormDB 名字已经设置了 或 设置的名字为 null");
			return;
		}
		mName = name;
	}

	/**
	 * @deprecated 失败的序列化不足以支持此方法。
	 * @param pFile
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	@Override
	public boolean saveAll(File pFile) throws IOException
	{
		return false;
	}

	@Override
	public void addCallback(AbstractDBCallback pCallback)
	{
		if(! (pCallback instanceof FormCallback))
		{
			Message.printError("FormDB中，提供的pCallback应该是FormCallback");
			return;
		}

		if(mCallback != null) {
			Message.printWarning("FormDB 已存在回调，不应该再次设置。");
		}
		mCallback = (FormCallback) pCallback;
	}

	@Override
	public boolean isEmpty()
	{
		return mFormTables.isEmpty();
	}

	@Override
	public int hashCode()
	{
		return mName.hashCode() ;
	}

	@Override
	public AbstractDatabase<FormTable, FormColumn> clone()
	{
		return new FormDB(
			(HashSet<FormTable>) mFormTables.clone(),
			mName,
			mCallback
		);
	}

	/**
	 * 不建议直接调用此方法，而是通过 {@link FormDB#put(FormColumn, String)}传入一个标志实现。
	 * @param element
	 * @return
	 */
	@Override
	public boolean put(FormTable element)
	{
		if(!mFormTables.contains(element))
		{
			mFormTables.add(element);

			if(mCallback != null)
				mCallback.putTable(this, element);
			return true;
		}
		return false;
	}

	/**
	 * 以条目形式添加表。
	 * <p>如果：</p>
	 *  <li>
	 *      添加的条目不存在数据库内，新建一个表。并优先从{@link FormColumn#getName()}抽取表名，
	 *      如果为null，再从参数抽取。两者皆为null，此方法暂时失效。
	 *      <p>会将传入的作为{@link FormTable#getTypeColumnHashCode()}的TypeColumn。</p>
	 *  </li>
	 *  <li>
	 *      如果添加的表存在，写入。
	 *  </li>
	 * @param pColumn
	 * @param pName
	 * @return
	 */
	public boolean put(FormColumn pColumn, String pName)
	{
		String tableName = pColumn.getName();
		if(tableName == null)
		{
			if(pName == null)
				return false;
			else {
				tableName = pName;
			}
		}

		for (FormTable formTable : mFormTables)
		{
			if(formTable.getName().equals(tableName))
			{
				if(mCallback != null)
					mCallback.putTableByColumn(this, pColumn, pName);

				return formTable.put(pColumn);
			}
		}

		final FormTable element = new FormTable(pColumn, tableName);

		if(mCallback != null)
		{
			mCallback.putTable(this, element);
			mCallback.putTableByColumn(this, pColumn, pName);
		}

		return element.addTypeColumn();
	}

	@Override
	public boolean delete(FormTable element)
	{
		if(mFormTables.contains(element))
		{
			if(mCallback != null)
				mCallback.deleteTable(this, element);

			return mFormTables.remove(element);
		}
		return false;
	}

	public boolean delete(FormColumn pFormColumn)
	{
		if (pFormColumn.getName() == null)
		{
			for (FormTable formTable : mFormTables)
			{
				if(formTable.getTypeColumnHashCode() == pFormColumn.getTypeHashCode())
				{
					if(mCallback != null)
						mCallback.deleteColumn(formTable, pFormColumn);

					return formTable.delete(pFormColumn);
				}
			}
		} else
		{
			for (FormTable formTable : mFormTables)
			{
				if(formTable.getName().equals(pFormColumn.getName()))
				{
					if(mCallback != null)
						mCallback.deleteColumn(formTable, pFormColumn);

					return formTable.delete(pFormColumn);
				}
			}
		}
		return false;
	}

	/**
	 * 获取的值，只是克隆值。{@link DBInterpolator#getNext()}
	 * todo 这极有可能造成内存溢出，不应该放到内存中。
	 * @return
	 */
	@Override
	public FormTable getNext()
	{
		getIterator();
		return mIterator.next().clone();
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

	private void getIterator()
	{
		if(mIterator == null) {
			mIterator = mFormTables.iterator();
		}
	}

	/**
	 * @see FormBuilder#unityCallback(FormCallback)
	 */
	public void unityCallback(FormCallback pCallback)
	{
		mCallback = pCallback;

		for (FormTable formTable : mFormTables)
		{
			formTable.unityCallback(pCallback);
		}
	}

//	public static class DBUnit extends Pai
}
