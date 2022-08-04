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
 * 标志类作为数据访问对象。
 * <p>在定义数据库交互的抽象类，他们可以包括各种拥有{@link Insert}、{@link Delete}、{@link Query}之一的抽象方法用于操作。
 * <P>
 * &emsp;类标记{@code @Dao}应该是一个抽象类或抽象方法。一定要有公共无参数构造方法。<P>
 * &emsp;在编译时, 抽象类拥有指定注解的将生成一个实现的方法。<P>
 * 而抽象方法应该作为{@link Database}的返回抽象类的注解。
 * 抽象方法不建议有参数，因为没有意义。
 * <P>
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/10 11:39
 * @see Insert
 * @see Delete
 * @see Query
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dao
{

}
