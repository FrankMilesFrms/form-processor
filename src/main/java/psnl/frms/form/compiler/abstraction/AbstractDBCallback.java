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

import java.io.File;

/**
 * 回调，用于监听数据库内指令。
 * 注意，执行的操作不一定成功，只是说明有发起的命令
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/5/8 23:45
 */
public abstract class AbstractDBCallback
	<T extends AbstractDatabase<R, E>, R extends AbstractDBTable<E>, E extends AbstractDBColumn>
{
	/**
	 * 删除栏目
	 * @param table 表
	 * @param deleteColumn 删除的栏目
	 */
	public abstract void deleteColumn(R table, E deleteColumn);

	/**
	 * 放入栏目
	 * @param table 表
	 * @param putColumn 放入的栏目
	 */
	public abstract void putColumn(R table, E putColumn);

	/**
	 * 放入表
	 * @param db 数据库
	 * @param putTable 放入表
	 */
	public abstract void putTable(T db, R putTable);

	/**
	 * 删除表
	 * @param db 数据库
	 * @param deleteTable 删除表
	 */
	public abstract void deleteTable(T db, R deleteTable);

	/**
	 * 放入数据库
	 * @param db 数据库
	 */
	public abstract void putDB(T db);

	/**
	 * 删除数据库
	 * @param db 数据库
	 */
	public abstract void deleteDB(T db);

	/**
	 * 系统创建
	 * @param path 文件路径
	 */
	public abstract void onCreate(File path);

	/**
	 * 系统保存
	 * @param path 路径
	 */
	public abstract void onSaved(File path);


}
