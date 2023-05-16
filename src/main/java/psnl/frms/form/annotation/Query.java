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
 * 此方法以废弃，不可使用
 * 此注解需用于{@link Dao}类，才可以生效。
 * 方法应该给一个参数{@link psnl.frms.form.compiler.DBWhere}，用来插入此值。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/29 22:13
 * @see Dao
 * @see psnl.frms.form.compiler.DBWhere
 */
@Deprecated
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query
{
}
