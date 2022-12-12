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
package psnl.frms.form.processor.lexer;

import psnl.frms.form.annotation.*;
import psnl.frms.form.compiler.abstraction.AbstractDBColumn;
import psnl.frms.form.db.FormColumn;
import psnl.frms.form.utils.Message;

import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

import static psnl.frms.form.utils.Message.print;

/**
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/08/03 10:02
 */
public class LexerEntityClass
{
	/**
	 * 动态分析标签对象
	 * @param object
	 * @return
	 */
	public static FormColumn lexerEntity(final Object object)
	{
		if(object instanceof FormColumn) {
			return (FormColumn) object;
		}
		final Class<?> klass = object.getClass();
		final String implName = klass.getCanonicalName();
		final Entity entityAnnotation = klass.getAnnotation(Entity.class);

		final FormColumn formColumn = new FormColumn();

		if(entityAnnotation == null)
		{
			Message.printError("lexerEntity 参数所指示的类，必须要有注解。");
			return formColumn;
		}

		// 表名
		String tableName = entityAnnotation.tableName();
		tableName = tableName.isEmpty() ? implName : tableName;


//		print("实体类具体位置 =" + implName);
//		print("表名 ="+tableName);\
		formColumn.setName(tableName);

		String name; // 字段名字
		Object value = null; // 值
//		String type; // 值类型
		boolean isPrimary = false;
		ColumnInfo columnInfo;

		for (final Field field : klass.getDeclaredFields())
		{
			if (field.isAnnotationPresent(ColumnIgnore.class))
				continue;

			isPrimary = field.isAnnotationPresent(PrimaryKey.class);
//			type = field.getType().getTypeName();
			name = field.getName();

			try {
				value = field.get(object);
			} catch (IllegalAccessException pE) {
				pE.printStackTrace();
			}

			if(field.isAnnotationPresent(ColumnInfo.class))
			{
				columnInfo = field.getAnnotation(ColumnInfo.class);
				name = columnInfo.name();

				if(Objects.equals(name, ColumnInfo.INHERIT_FIELD_NAME))
					name = field.getName();
			}
			formColumn.put(name, AbstractDBColumn.getType(value), isPrimary, value);
//			print("name: "+name + ", value: "+value + ", 类型：" + (value==null? "null" : value.getClass()) + ", 定义类型="+type);
		}
		return formColumn;
	}
}
