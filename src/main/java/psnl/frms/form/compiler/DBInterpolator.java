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
 * 插值器，用于以逐条检索来查找数据库，避免大数据造成溢出。
 * 增、删、改应该使用 {@link DBOperationalTools}
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/31 15:27
 */
public interface DBInterpolator<T>
{
	/**
	 * 返回下一个元素（如果有的话）
	 * @return
	 */
	T getNext();

	/**
	 * 重置
	 */
	void reset();

	/**
	 * 是否有下一个
	 * @return
	 */
	boolean hasNext();

}
