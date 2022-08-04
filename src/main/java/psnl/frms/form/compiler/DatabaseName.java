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

/**
 * 设置数据库名字
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/29 23:06
 */
public interface DatabaseName
{
	/**
	 * 获取名字
	 * @return 名字
	 */
	String getName();

	/**
	 * 设置名字
	 * @param name 名字
	 */
	void setName(String name);
}
