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

	/**
	 * 采用线程池，通过异步的方式返回。
	 * @param pDBSingleRules
	 */
	public synchronized void getAsyncResult(
		final FormController pFormController,
		DBSingleRules pDBSingleRules,
		DBSingleResult pDBSingleResult
	) {
		List<FormColumn> list = Collections.synchronizedList(new LinkedList<>());

		ExecutorService executorService = getExecutorService(pFormController, pDBSingleRules, list);

		Executors.newSingleThreadExecutor().submit(() -> {
			try {
				executorService.awaitTermination(5, TimeUnit.MINUTES);
			} catch (InterruptedException pE) {
				throw new RuntimeException(pE);
			} finally {
				pDBSingleResult.result(list);
			}
		});
	}

	/**
	 * 在采用线程池的基础上，同步等待加载完毕
	 * @param pFormController
	 * @param pDBSingleRules
	 * @param pDBSingleResult
	 */
	public synchronized void getSyncResult(
		final FormController pFormController,
		DBSingleRules pDBSingleRules,
		DBSingleResult pDBSingleResult
	) {
		List<FormColumn> list = Collections.synchronizedList(new LinkedList<>());

		ExecutorService executorService = getExecutorService(pFormController, pDBSingleRules, list);


		try {
			executorService.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException pE) {
			throw new RuntimeException(pE);
		} finally {
			pDBSingleResult.result(list);
		}
	}

	/**
	 * 以线程池的形来执行检索
	 * @param pFormController
	 * @param pDBSingleRules
	 * @param list
	 * @return
	 */
	private static ExecutorService getExecutorService(
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
