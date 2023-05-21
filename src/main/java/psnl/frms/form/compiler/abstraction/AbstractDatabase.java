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
package psnl.frms.form.compiler.abstraction;

import psnl.frms.form.compiler.DBInterpolator;
import psnl.frms.form.compiler.DBOperationalTools;
import psnl.frms.form.compiler.DatabaseName;

import java.io.File;
import java.io.IOException;

/**
 * 抽象数据库的“库”
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2023/05/9 22:51
 */
public abstract class AbstractDatabase<T extends AbstractDBTable<R>, R extends AbstractDBColumn>
	implements DatabaseName, DBOperationalTools<T>, DBInterpolator<T>, Cloneable
{
	/**
	 * 数据库是否为空
	 * @return
	 */
	public abstract boolean isEmpty();


	@Override
	public abstract int hashCode();

	@Override
	public abstract AbstractDatabase<T, R> clone();
}
