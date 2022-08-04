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

import psnl.frms.form.db.FormColumn;
import psnl.frms.form.db.FormController;
import psnl.frms.form.db.FormDB;
import psnl.frms.form.db.FormTable;
import psnl.frms.form.processor.lexer.LexerEntityClass;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.Pair;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

/**
 * 查询约束条件
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/01 15:38
 */
public class DBWhere
{
	public static volatile boolean isSearching = true;

	private DBResult mDBResult = resultFormColumn ->
	{
		throw new RuntimeException("DBWhere 必须设置参数");
	};

	private final Stack<Pair<FormColumn, DBColumnWhere>> mStack = new Stack<>();

	/**
	 * 同步获取，这个方法不应该被私自调用。
	 * @param pFormDB FormDB
	 */
	public void runAsync(FormDB pFormDB)
	{
		mDBResult.result(get(pFormDB));
		isSearching = false;
	}

	private HashSet<FormColumn> get(FormDB pFormDB)
	{
		Pair<FormColumn, DBColumnWhere> pair;
		// 结果不应该有重复
		final HashSet<FormColumn> formColumns = new HashSet<>();

		FormTable table;
		FormColumn formColumn;
		while (!mStack.isEmpty())
		{
			 pair = mStack.pop();
			 table = pFormDB.getFormTable(pair.first);

			 if(table == null)
				 continue;

			 while (table.hasNext())
			 {
				 formColumn = table.getNext();

				 if(
					 !formColumns.contains(formColumn)
					 && pair.second.rules(formColumn)
				 )
				 {
					 formColumns.add(formColumn);
				 }
			 }
			 table.reset();
		}
		return formColumns;
	}


	/**
	 * 添加指定表的筛选规则。
	 * @param entity 指定的表（忽略数值）
	 * @param pWhere
	 * @return
	 */
	public DBWhere addTypeColumn(Object entity, DBColumnWhere pWhere)
	{
		mStack.push(new Pair<>(LexerEntityClass.lexerEntity(entity), pWhere));
		return this;
	}


	/**
	 * 获取结果。
	 * @param pDBResult
	 * @return
	 */
	public DBWhere getResult(DBResult pDBResult)
	{
		mDBResult = pDBResult;
		return this;
	}

	/**
	 * 实现接口以获取接口
	 */
	public interface DBResult {
		void result(HashSet<FormColumn> resultFormColumn);
	}
}
