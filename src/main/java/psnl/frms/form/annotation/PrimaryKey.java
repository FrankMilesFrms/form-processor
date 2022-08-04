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
 * 设置一个字段或方法为唯一主键。
 *<p>
 * 暂时不支持多主键。
 *<p>
 *  每个{@link Entity}注解的类，至少要有一个字段拥有{@link PrimaryKey}
 * 注解。
 * todo 支持父类？
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/10 10:11
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey
{
	/**
	 * 自动给每个实体类以独特的id。
	 * <p>
	 * 字段应该是 int / long 或者其包装类及其可能的子类，不能是其他类型。
	 * <p>
	 * 是否自动增量，需要手动增量，请 todo。
	 * 例如：
	 * <pre>
	 * {@literal @}PrimaryKey(autoGenerate = true)
	 * private final long id = 0;
	 * </pre>
	 */
	boolean autoGenerate() default false;
}
