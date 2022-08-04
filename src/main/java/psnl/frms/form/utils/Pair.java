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
package psnl.frms.form.utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/14 22:51
 */
public class Pair<T, E> implements Serializable
{
	public T first;
	public E second;

	public Pair() {}

	public Pair(T pFirst, E pSecond)
	{
		first = pFirst;
		second = pSecond;
	}

	public T getFirst()
	{
		return first;
	}

	public E getSecond()
	{
		return second;
	}

	public Pair<T, E> setFirst(final T pFirst)
	{
		first = pFirst;
		return this;
	}

	public Pair<T, E> setSecond(final E pSecond)
	{
		second = pSecond;
		return this;
	}

	@Override
	public String toString()
	{
		return "Pair{" + "first=" + first + ", second=" + second + '}';
	}

	@Override
	public boolean equals(final Object pO)
	{
		if (this == pO)
		{
			return true;
		}
		if (pO == null || getClass() != pO.getClass())
		{
			return false;
		}
		final Pair<?, ?> pair = (Pair<?, ?>) pO;
		return Objects.equals(getFirst(), pair.getFirst()) && Objects.equals(getSecond(), pair.getSecond());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getFirst(), getSecond());
	}
}
