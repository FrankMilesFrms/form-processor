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
package psnl.frms.form;

import psnl.frms.form.compiler.DBColumnWhere;
import psnl.frms.form.compiler.DBWhere;
import psnl.frms.form.db.*;

import psnl.frms.form.processor.lexer.LexerEntityClass;
import psnl.frms.form.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 *  @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/13 23:16
 */

public class Main
{

//	public static void create() throws Exception
//	{
//		final FormController formController = FormController.getInstance(true);
//
//		// 添加一个条目
//		final FormColumn formColumn = new FormColumn();
//		formColumn.put("price", FormColumn.STRING, true, "Hello");
//
//		final FormColumn formColumn1 = new FormColumn();
//		formColumn1.put("price", FormColumn.STRING, true, "Hello1");
//
////		System.out.println(formColumn.hashCode() == formColumn1.hashCode());
////		System.out.println(formColumn.getTypeHashCode() == formColumn1.getTypeHashCode());
//
//		final FormTable formTable = new FormTable(formColumn, "Table");
//		formTable.addCallback(new FormCallback() {
//			@Override
//			public void deleteColumn(FormTable table, FormColumn deleteColumn)
//			{
//				super.deleteColumn(table, deleteColumn);
//				System.out.println("已经删除："+deleteColumn);
//			}
//
//			@Override
//			public void putColumn(FormTable table, FormColumn putColumn)
//			{
//				super.putColumn(table, putColumn);
//				System.out.println("已经置入："+putColumn);
//			}
//		});
////		System.out.println(formTable.put(formColumn1));
////		System.out.println(formTable.put(formColumn1));
////		System.out.println(
////			formTable.delete(
////				new FormColumn()
////					.put("price", FormColumn.STRING, true, "Hello1")
////			)
////		);
//
//		// 初始化表的工作应该交给中间翻译层做，
////		formController.add
//
//		formTable.put(formColumn);
//		formTable.put(formColumn1);
//
//		final FormDB formDB = new FormDB("dbName", formTable);
//		formController.addCallback(new FormCallback() {
//			@Override
//			public void onCreate(File path)
//			{
//				super.onCreate(path);
//				System.out.println("创建于 " +path);
//			}
//
//			@Override
//			public void onSaved(File path)
//			{
//				super.onSaved(path);
//				System.out.println("保存在 "+path);
//			}
//		});
//
//		formController.put(formDB);
//		formController.saveAll();
//	}
//
//
//	private static void reload() throws Exception
//	{
//		final FormController formController = FormController.getInstance(false);
//
//
//		final Student student = new Student(0, "sd", 1, 1);
//		final FormDB formDB = formController.getNext();
//
//		final DBWhere dbWhere = new DBWhere();
//		dbWhere.addTypeColumn(
//			student,
//			pFormColumn ->
//			{
//				final Pair<FormColumn.Unit, Boolean> unit = pFormColumn.getUnit(
//					"price", FormColumn.STRING
//				);
//				System.out.println("util =" + unit + ", itor = " + pFormColumn);
//
//				return unit.first.third.equals("Hello");
//			}
//		);
//
//		dbWhere.getResult(resultFormColumn -> resultFormColumn.forEach(
//			System.out::println
//		));
//
//		dbWhere.runAsync(formDB);
//
//		formController.saveAll();
//	}

	public static void main(String[] args) throws Exception
	{
//		create();
//		reload();
//		System.out.println(true);
	}
}

