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


import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/14 11:20
 */
public class Kits
{
	@SafeVarargs
	public static <T> Set<T> of(T... values)
	{
		return new HashSet<>(Arrays.asList(values));
	}

	public static synchronized void saveObject(@NotNull Object object, @NotNull File pFile) throws Exception
	{
		try (
			FileOutputStream fout = new FileOutputStream(pFile);
			ObjectOutputStream out = new ObjectOutputStream(fout)
		)
		{
			out.writeObject(object);
		}
	}

	// 读取对象，反序列化
	public static Object readObject(@NotNull File pFile) throws Exception {
		try (
			FileInputStream fin = new FileInputStream(pFile);
			ObjectInputStream in = new ObjectInputStream(fin)
		)
		{
			Object object = in.readObject();
			return  object;
		}
	}
}
