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
import psnl.frms.form.utils.IntDef;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.Pair;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 查询约束条件
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/01 15:38
 */
public class DBWhere
{

	/**@hide */
	@IntDef(value = {
		UNIT_TABLE, UNIT_COLUMN
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface SearchUnit {}

	/**
	 * 以表来划分最小线程
	 */
	@SearchUnit
	public static final int UNIT_TABLE = 1;

	/**
	 *  以条目来划分最小线程
	 */
	@SearchUnit
	public static final int UNIT_COLUMN = 0;

	/**
	 * 采用线程池，通过异步的方式返回。
	 * @param pDBSingleRules
	 */
	public static synchronized void getAsyncResult(
		final FormController pFormController,
		DBSingleRules pDBSingleRules,
		DBSingleResult pDBSingleResult,
		@SearchUnit int type
	) {
		List<FormColumn> list = Collections.synchronizedList(new LinkedList<>());

		ExecutorService executorService;

		if(type == UNIT_COLUMN) {
			executorService = getColumnExecutorService(pFormController, pDBSingleRules, list);
		} else
//			if(type == UNIT_TABLE)
		{
			executorService = getTableExecutorService(pFormController, pDBSingleRules, list);
		}

		ExecutorService single = Executors.newSingleThreadExecutor();
			single.submit(() -> {
			try {
				executorService.shutdown();
				executorService.awaitTermination(5, TimeUnit.MINUTES);
			} catch (InterruptedException pE) {
				throw new RuntimeException(pE);
			} finally {
				pDBSingleResult.result(list);
			}
		});
		single.shutdown();
	}

	/**
	 * 在采用线程池的基础上，同步等待加载完毕
	 * @param pFormController
	 * @param pDBSingleRules
	 * @param type {@link #UNIT_COLUMN} & {@link #UNIT_TABLE}
	 */
	public static synchronized List<FormColumn> getSyncResult(
		final FormController pFormController,
		DBSingleRules pDBSingleRules,
		@SearchUnit int type
	) {
		List<FormColumn> list = Collections.synchronizedList(new LinkedList<>());

		ExecutorService executorService;

		if(type == UNIT_COLUMN) {
			executorService = getColumnExecutorService(pFormController, pDBSingleRules, list);
		} else
//			if(type == UNIT_TABLE)
		{
			executorService = getTableExecutorService(pFormController, pDBSingleRules, list);
		}

		try {
			executorService.shutdown();
			executorService.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException pE) {
			throw new RuntimeException(pE);
		}
		return list;
	}

	/**
	 * 以线程池的形来执行检索
	 * @param pFormController
	 * @param pDBSingleRules
	 * @param list
	 * @return
	 */
	private static ExecutorService getTableExecutorService(
		FormController pFormController,
		DBSingleRules pDBSingleRules,
		List<FormColumn> list
	) {
		ExecutorService executorService = Executors.newCachedThreadPool();

		FormDB formDB = pFormController.getNext();
		formDB.reset();

		while (formDB.hasNext())
		{
			FormTable formTable = formDB.getNext();
			/*
				每个表都会创建线程来检索
			*/
			executorService.submit(() -> {
				formTable.reset();
				while (formTable.hasNext())
				{
					FormColumn formColumn = formTable.getNext();

					if(pDBSingleRules.rule(formColumn)) {
						list.add(formColumn);
					}
				}
			});
		}
		return executorService;
	}

	/**
	 * 每个条目都会创建线程来检索，线程分割以cpu核数来实现
	 * @param pFormController
	 * @param pDBSingleRules
	 * @param list
	 * @return
	 */
	private static ExecutorService getColumnExecutorService(
		FormController pFormController,
		DBSingleRules pDBSingleRules,
		List<FormColumn> list
	) {
		// 2倍来划分
		final int processors = Runtime.getRuntime().availableProcessors() << 1;
		ExecutorService executorService = Executors.newCachedThreadPool();

		FormDB formDB = pFormController.getNext();
		formDB.reset();

		while (formDB.hasNext())
		{
			FormTable formTable = formDB.getNext();
			formTable.reset();
			// 处理块大小
			int len = (formTable.getFormColumnHashSet().size() / processors) + 1;
			int lenIndex = 0;
			int i = 0;

			LinkedList<FormColumn>[] lists = new LinkedList[processors+1];

			while (formTable.hasNext())
			{
				if(lists[i] == null) {
					lists[i] = new LinkedList<>();
				}

				lists[i].add(formTable.getNext());
				lenIndex++;
				if(lenIndex % len == 0) {
					i++;
					lenIndex = 0;
				}
			}

			for(int l=0; l<=i; l++)
			{
				int finalL = l;
				executorService.submit(() -> {
					for(FormColumn formColumn : lists[finalL]) {
						if(pDBSingleRules.rule(formColumn)) {
							list.add(formColumn);
						}
					}
				});
			}
		}
		return executorService;
	}

	/**
	 * 通用性检查，只要满足一般约束条件，就会返回值
	 */
	public interface DBSingleRules {
		boolean rule(FormColumn pFormColumn);
	}

	/**
	 * 异步返回结果类。
	 */
	public interface DBSingleResult {
		void result(List<FormColumn> pFormColumnList0);
	}
}
