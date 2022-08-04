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
 * 你可以定制{@link Entity} 列名称，这意味着默认使用字段名作为列名将失效。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/10 10:41
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnInfo
{
	/**
	 * 使用字段名作为列名。
	 */
	String INHERIT_FIELD_NAME = "[form-field-name]";

	/**
	 * 定值列名，默认跟随字段名。
	 * todo 可能冲突
	 */
	String name() default INHERIT_FIELD_NAME;


}
