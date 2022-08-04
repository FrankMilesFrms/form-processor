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

import psnl.frms.form.utils.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *  抽象数据库的“条目”
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/29 23:00
 */
public abstract class AbstractDBColumn implements Cloneable
{

	public static final int TYPE = 1 << 4; // 16 [0, 8)

	public static final int DOUBLE = TYPE + 1; // 17
	public static final int FLOAT = TYPE + 2; // 18
	public static final int INT = TYPE + 3; // 19
	@Deprecated
	public static final int REAL_NUMBER = TYPE + 4; // 20

	public static final int STRING = TYPE + 5; // 21

	public static final int OBJECT = TYPE + 6; // 22

	@Override
	public abstract AbstractDBColumn clone();

	/** hide */
	@IntDef(value = {
		REAL_NUMBER, DOUBLE, FLOAT, INT, STRING, OBJECT
	})
	@Retention(RetentionPolicy.SOURCE)
	public @interface DBType {}

	/**
	 * 转换对象作为{@link DBType}的包含的类型。
	 * @param pObject Object
	 * @return
	 */
	public static @DBType int getType(Object pObject)
	{
		if(pObject == null)
			return OBJECT;
		switch (pObject.getClass().getCanonicalName())
		{
			case "java.lang.String"://String.class.getCanonicalName():
				return STRING;
			case "int":
			case "java.lang.Integer":
				return INT;
			case "float":
			case "java.lang.Float":
				return FLOAT;
			case "double":
			case "java.lang.Double":
				return DOUBLE;
			case "java.lang.Object":
				default:
					return OBJECT;
		}
	}

	/**
	 * 添加一个条目
	 * @param name 名字
	 * @param valueType 值
	 * @param primaryKey 是否为主键
	 * @return this
	 */
	public abstract AbstractDBColumn put(String name, @DBType int valueType, boolean primaryKey, Object object);

	/**
	 * 用于比较同类。
	 * @return hash
	 */
	public abstract int getTypeHashCode();


	public abstract boolean isEmpty();
}
