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
 * 数据库的增删改工具，查 应该使用插值器{@link DBInterpolator}。
 * <pre>
 * 改 则是两者结合：
 * if(delete(E))
 * {
 *     change E ;
 *
 *     put(E);
 * }
 * </pre>
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/30 9:32
 */
public interface DBOperationalTools<T>
{
	/**
	 * 放入元素（如果符合的话）
	 * @param element
	 * @return
	 */
	boolean put(T element);

	/**
	 * 删除指定元素
	 * @param element
	 * @return
	 */
	boolean delete(T element);
}
