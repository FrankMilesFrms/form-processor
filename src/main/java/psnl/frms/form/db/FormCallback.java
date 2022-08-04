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

import java.io.File;
import java.io.Serializable;

/**
 * 实现的数据库回调，你可以使用匿名类选择任意方法。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/31 17:00
 */
public class FormCallback extends AbstractDBCallback<FormDB, FormTable, FormColumn> implements Serializable
{
	@Override
	public void deleteColumn(FormTable table, FormColumn deleteColumn)
	{}

	@Override
	public void putColumn(FormTable table, FormColumn putColumn)
	{}

	@Override
	public void putTable(FormDB db, FormTable putTable)
	{}

	/**
	 * 通过标志条目添加的值
	 * @param db
	 * @param byColumn
	 * @param name
	 */
	public void putTableByColumn(FormDB db, FormColumn byColumn, String name)
	{}

	@Override
	public void deleteTable(FormDB db, FormTable deleteTable)
	{}

	@Override
	public void putDB(FormDB db)
	{

	}

	@Override
	public void deleteDB(FormDB db)
	{

	}

	@Override
	public void onCreate(File path)
	{

	}

	@Override
	public void onSaved(File path)
	{

	}
}
