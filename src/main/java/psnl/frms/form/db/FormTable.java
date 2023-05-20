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



import psnl.frms.form.compiler.abstraction.AbstractDBCallback;
import psnl.frms.form.compiler.DBInterpolator;
import psnl.frms.form.compiler.abstraction.AbstractDBTable;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import static psnl.frms.form.db.FormController.mCallback;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/30 9:51
 */
public class FormTable extends AbstractDBTable<FormColumn> implements Serializable
{

	private static final long serialVersionUID = -3182040615882961077L;

	// 指定表是什么表
	private final FormColumn typeColumn;

	/**
	 * 所有条目
	 */
	private final HashSet<FormColumn> mFormColumnHashSet;

	private String mName;

	private transient Iterator<FormColumn> mIterator;

	/**
	 * 仅用于序列化
	 * @param pTypeColumn
	 * @param pFormColumns
	 * @param pName
	 */
	private FormTable(
		FormColumn pTypeColumn,
		HashSet<FormColumn> pFormColumns,
		String pName
	) {
		typeColumn = pTypeColumn.clone();
		mFormColumnHashSet = (HashSet<FormColumn>) pFormColumns.clone();
		mName = pName;
	}

	public FormColumn getTypeColumn()
	{
		return typeColumn;
	}

	/**
	 * 获取所有条目
	 * @return
	 */
	public HashSet<FormColumn> getFormColumnHashSet()
	{
		return mFormColumnHashSet;
	}

	/**
	 * 指定表类型，并不存储。
	 * @param pFormColumn
	 */
	public FormTable(FormColumn pFormColumn, String pName)
	{
		typeColumn = pFormColumn;
		mFormColumnHashSet = new HashSet<>();
		mName = pName;
	}

	/**
	 * 添加标志进入表内。
	 * @return
	 */
	public boolean addTypeColumn()
	{
		return put(typeColumn);
	}

	/**
	 * 获取名字
	 *
	 * @return 名字
	 */
	@Override
	public String getName()
	{
		return mName;
	}

	/**
	 * 设置名字
	 *
	 * @param name 名字
	 */
	@Override
	public void setName(@NotNull String name)
	{
		if(mName != null || name == null) {
			Message.printError("FormTable 名字已经设置了 或 设置的名字为 null");
			return;
		}
		mName = name;
	}

	/**
	 * 放入元素：如果能，返回true。
	 *
	 * @param element 元素
	 * @return 是否成功放入
	 */
	@Override
	public boolean put(FormColumn element)
	{
		if(
			element.getTypeHashCode() == typeColumn.getTypeHashCode()
			&& !mFormColumnHashSet.contains(element))
		{
			mFormColumnHashSet.add(element);

			if(mCallback != null)
				mCallback.putColumn(this, element);
			return true;
		}
		if(element.getTypeHashCode() == typeColumn.getTypeHashCode()) {
			Message.printError("已经存在相同的条目，已忽略。意图增加的条目=" + element);
		} else {
			Message.printError("放入的条目格式不正确。\n\t需要的格式="+typeColumn.getTypeString()+"\n\t意图增加的格式="+element.getTypeString());
		}

		return false;
	}

	@Override
	public boolean delete(FormColumn element)
	{
		if(
			element.getTypeHashCode() == typeColumn.getTypeHashCode()
			&& mFormColumnHashSet.contains(element)
		)
		{
			mFormColumnHashSet.remove(element);

			if(mCallback != null)
				mCallback.deleteColumn(this, element);
			return true;
		}
		return false;
	}

	/**
	 * 不会记录{@link FormTable#mIterator}
	 * @return
	 */
	@Override
	public FormTable clone()
	{
		return new FormTable(typeColumn, mFormColumnHashSet, mName);
	}


	@Override
	public boolean isEmpty()
	{
		return mFormColumnHashSet.isEmpty();
	}

	@Override
	public boolean equals(Object object)
	{
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		FormTable formTable = (FormTable) object;
		return Objects.equals(typeColumn, formTable.typeColumn);
	}

	/**
	 * 通过存储的类型条目和表名来确定是否为同一类
	 * @return
	 */
	@Override
	public int hashCode()
	{
		return getRealHashCode(typeColumn, mName);
	}

	public int getTypeColumnHashCode()
	{
		return typeColumn.getTypeHashCode();
	}

	public static int getRealHashCode(FormColumn pTypeColumn, String pName)
	{
		return pTypeColumn.getTypeHashCode() + (pName == null? 0 : pName.hashCode());
	}

	/**
	 * 获取的值，只是克隆值。{@link DBInterpolator#getNext()}
	 *
	 * @return
	 */
	@Override
	public FormColumn getNext()
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

	private void getIterator()
	{
		if(mIterator == null) {
			mIterator = mFormColumnHashSet.iterator();
		}
	}


	public void unityCallback(FormCallback pCallback)
	{
		mCallback = pCallback;
	}
}
