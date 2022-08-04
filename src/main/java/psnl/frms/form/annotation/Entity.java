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
package psnl.frms.form.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定一个类为表，你可以在表内对字段进行值的修改；
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/10 10:01
 * @see ColumnIgnore
 * @see ColumnInfo
 * @see PrimaryKey
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity
{
	/**
	 * 表名. 如无，默认为全路径类名。
	 *
	 * @return 实体类的tableName.
	 */
	String tableName() default "";
}
