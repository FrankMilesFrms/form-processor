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

/**
 * 数据库控制器，数据控制器应该是单例的。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/29 22:29
 */
public abstract class AbstractDBController
	 <T extends AbstractDatabase<R, E>, R extends AbstractDBTable<E>, E extends AbstractDBColumn>
	implements DBOperationalTools<T>, DBInterpolator<T>
{
	protected AbstractDBController() {}

//	/**
//	 * 添加回调
//	 * @param pCallback 回调
//	 */
//	public abstract void addCallback(AbstractDBCallback pCallback);

}
