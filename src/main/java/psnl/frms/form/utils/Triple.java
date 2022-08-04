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
 * @time 2022/07/14 23:01
 */
public class Triple<A, B, C> implements Serializable
{
	public A first;
	public B second;
	public C third;

	public Triple() {}

	public Triple(A pFirst, B pSecond, C pThird)
	{
		first = pFirst;
		second = pSecond;
		third = pThird;
	}

	public Triple(A pFirst, Pair<B, C> pPair)
	{
		first = pFirst;
		second = pPair.getFirst();
		third = pPair.getSecond();
	}

	public A getFirst()
	{
		return first;
	}

	public B getSecond()
	{
		return second;
	}

	public C getThird()
	{
		return third;
	}

	public Triple<A, B, C> setFirst(final A pFirst)
	{
		first = pFirst;
		return this;
	}

	public Triple<A, B, C> setSecond(final B pSecond)
	{
		second = pSecond;
		return this;
	}

	public Triple<A, B, C> setThird(final C pThird)
	{
		third = pThird;
		return this;
	}

	@Override
	public String toString()
	{
		return "Triple{" + "first=" + first + ", second=" + second + ", third=" + third + '}';
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
		final Triple<?, ?, ?> triple = (Triple<?, ?, ?>) pO;
		return Objects.equals(getFirst(), triple.getFirst()) && Objects.equals(getSecond(), triple.getSecond()) && Objects.equals(getThird(), triple.getThird());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getFirst(), getSecond(), getThird());
	}
}
