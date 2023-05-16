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

import psnl.frms.form.annotation.*;
import psnl.frms.form.compiler.*;
import psnl.frms.form.compiler.abstraction.*;
import psnl.frms.form.db.*;
import psnl.frms.form.processor.*;
import psnl.frms.form.processor.lexer.LexerEntityClass;

/**
 *  @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/13 23:16
 */
@Entity
public class MainTest
{
	public static void main(String[] args)
	{
		final Class[] classes = {
			ColumnIgnore.class,
			ColumnInfo.class,
			Dao.class,
			Database.class,
			Delete.class,
			Entity.class,
			Insert.class,
			PrimaryKey.class,
			Query.class,

			AbstractDatabase.class,
			AbstractDBCallback.class,
			AbstractDBColumn.class,
			AbstractDBController.class,
			AbstractDBTable.class,

			DatabaseName.class,
			DBInterpolator.class,
			DBOperationalTools.class,
			DBWhere.class,
			FormBuilder.class,

			FormCallback.class,
			FormColumn.class,
			FormController.class,
			FormDB.class,
			FormIterator.class,
			FormTable.class,

			LexerEntityClass.class,
			FormDaoProcessor.class,
			FormDBProcessor.class,
			FormTableProcessor.class,
		};

		for(Class klass : classes) {
			System.out.println("检测成功：" + klass.getName());
		}

	}
}

