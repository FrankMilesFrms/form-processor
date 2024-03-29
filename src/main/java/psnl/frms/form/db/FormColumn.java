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
package psnl.frms.form.db;

import psnl.frms.form.compiler.DatabaseName;
import psnl.frms.form.compiler.abstraction.AbstractDBColumn;
import psnl.frms.form.utils.Message;
import psnl.frms.form.utils.Pair;
import psnl.frms.form.utils.Triple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

/**
 * 条目
 * 该类表示一个条目，其中包含主键和普通值两个HashSet对象。
 * 每个HashSet对象存储一个Unit对象（即最小单元，Unit继承自Triple类），
 * 一个Unit对象表示条目中的一个最小单元，包括单元名称、单元类型和单元实例。
 * @author Frms(Frank Miles)
 * @email 3505826836@qq.com
 * @time 2022/07/30 9:51
 */
public class FormColumn extends AbstractDBColumn implements Serializable, DatabaseName
{


	private static final long serialVersionUID = -4890767576626100668L;

	private final HashSet<Unit> primaryValue;

	private final HashSet<Unit> normalValue;


	public FormColumn() {
		primaryValue = new HashSet<>();
		normalValue = new HashSet<>();
	}

	public HashSet<Unit> getPrimaryValue()
	{
		return primaryValue;
	}

	public HashSet<Unit> getNormalValue()
	{
		return normalValue;
	}

	/**
	 * 以单线程获取Unit，优先访问主键如果主键没有，则访问一般键。
	 * @param name 单位名
	 * @param type 单位类型
	 * @return 如果能找到，则返Pair< Unit, Boolean>
	 */
	public Pair<Unit, Boolean> getUnit(String name, @DBType int type)
	{
		final Unit unit = new Unit(name, type, null);

		Unit unit1 = searchUnit(primaryValue, unit);

		if(unit1 == null)
		{
			unit1 = searchUnit(normalValue, unit);

			if(unit1 == null) {
				return new Pair<>(null, false);
			}

		}
		return new Pair<>(unit1,true);
	}

	/**
	 * 自动返回类型，如果没找到，则返回给定的实例值
	 * @param name 单位名
	 * @param type 单位类型
	 * @return 不存在则返回null
	 * @param <Q> 类型
	 */
	public<Q> Q getUnit(String name, Q type) {
		Pair<Unit, Boolean> q =  getUnit(name, getType(type));

		if(q.second) {
			return (Q)q.first.third;
		}

		Message.printWarning(
			"getUnit无法查找结果！于"+getName()
				+" 查找 name="+name
				+" 和 类型="+type.getClass().getCanonicalName()
				+'('+getType(type)+')'
			);
		return type;
	}


	private Unit searchUnit(HashSet<Unit> pUnits, Unit target)
	{
		for (Unit unit : pUnits)
		{
			if(
				Objects.equals(unit.first, target.first)
				&& Objects.equals(unit.second, target.second)
			)
				return unit;
		}
		return null;
	}
	private FormColumn(HashSet<Unit> pPrimaryValue, HashSet<Unit> pNormalValue)
	{
		primaryValue = (HashSet<Unit>) pPrimaryValue.clone();
		normalValue = (HashSet<Unit>) pNormalValue.clone();
	}

	@Override
	public FormColumn clone()
	{
		return new FormColumn(primaryValue, normalValue);
	}

	/**
	 * 添加一个条目,条目名重复，则打印信息并覆盖原条目。
	 *
	 * @param name       名字
	 * @param valueType  值
	 * @param primaryKey 是否为主键
	 * @param object obj
	 * @return this
	 */
	@Override
	public FormColumn put(String name, @DBType int valueType, boolean primaryKey, Object object)
	{
		final Unit unit = new Unit(name, valueType, object);
		if(primaryKey)
		{
			if(primaryValue.contains(unit)) {
				Message.printError("primaryValue 列表已包含");
			}

			primaryValue.add(unit);
		} else
		{
			if(normalValue.contains(unit)) {
				Message.printError("normalValue 列表已包含");
			}

			normalValue.add(unit);
		}
		return this;
	}

	/**
	 * 比较主键所有单元的名称、值来判定是否为同一类。
	 * 用于比较同类。
	 * @return hash
	 */
	@Override
	public int getTypeHashCode()
	{
		int h = 0;
		for (Unit obj : primaryValue)
		{
			h += obj.hashCode();
		}
		return h;
	}

	@Override
	public boolean isEmpty()
	{
		return primaryValue.isEmpty() && normalValue.isEmpty();
	}

	@Override
	public boolean equals(Object object)
	{
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;
		FormColumn that = (FormColumn) object;
		return
			Objects.equals(primaryValue, that.primaryValue)
			&&  Objects.equals(normalValue, that.normalValue);
	}

	/**
	 * 通过所有单元的名称、类型、值比较是否为同一条目。
	 * @return
	 */
	@Override
	public int hashCode()
	{
		int h = 0;
		for(Unit unit : primaryValue) {
			h += unit.realHashCode();
		}

		for(Unit unit : normalValue) {
			h += unit.realHashCode();
		}
		return h;
	}

	@Override
	public String toString()
	{
		return "FormColumn{" +
			"primaryValue=" + Arrays.toString(primaryValue.toArray(new Unit[0])) +
			", normalValue=" + Arrays.toString(normalValue.toArray(new Unit[0])) +
			'}';
	}

	private String mTableName = null;

	/**
	 * 临时存储表名，不建议使用，不会由{@link #toString()}表示，
	 * 也不会因{@link #clone()}而保留。
	 * @return mTableName
	 */
	@Override
	public String getName()
	{
		return mTableName;
	}

	/**
	 * @see #setName(String)
	 * @param name 名字
	 */
	@Override
	public void setName(String name)
	{
		mTableName = name;
	}

	/**
	 * 用于返回格式
	 * @return
	 */
	public String getTypeString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		for (Unit unit : primaryValue) {
			stringBuilder.append(',').append("[主键，单元名称=").append(unit.first).append("，单元类型=").append(getTypeName(unit.second)).append("]");
		}

		for (Unit unit : normalValue) {
			stringBuilder.append(',').append("[非主键，单元名称=").append(unit.first).append("，单元类型=").append(getTypeName(unit.second)).append("]");
		}
		return stringBuilder.substring(1);
	}

	/**
	 * 最小单元，用于保存最小值，他们是：单元名称、单元类型和单元实例
	 */
	public class Unit extends Triple<String, Integer, Object>
	{
		public Unit(String pName, int pValueType, Object pObject)
		{
			super(pName, pValueType, pObject);
		}

		/**
		 * 单元 间的比较，应该只是 名称、类型 比较，而不牵扯值。
		 * @return
		 */
		@Override
		public int hashCode()
		{
			return Objects.hash(getFirst(), getSecond());
		}

		/**
		 * 用于比较是否为同一单元（即名称、类型、值完全相同）考虑数值问题。
		 * @return
		 */
		public int realHashCode()
		{
			return Objects.hash(getFirst(), getSecond(), getThird());
		}
	}

	/**
	 * 通过String来实现字面实例。
	 * @param pType
	 * @return
	 */
	public static String getTypeObject(String pType)
	{
		if(pType == null)
			return "null";
		switch (pType)
		{
			case "java.lang.String"://String.class.getCanonicalName():
				return "\"\"";
			case "int":
			case "java.lang.Integer":
				return "0";
			case "float":
			case "java.lang.Float":
				return "0.0F";
			case "double":
			case "java.lang.Double":
				return "0.0d";
			case "java.lang.Object":
			default:
				return "null";
		}
	}

	/**
	 * 通过String来获取值{@link psnl.frms.form.compiler.abstraction.AbstractDBColumn.DBType}。
	 * @param pType
	 * @return
	 */
	public static @DBType int getTypeInt(String pType)
	{
		if(pType == null)
			return OBJECT;
		switch (pType)
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

	public static String getTypeName(@DBType int type)
	{
		switch (type)
		{
			case STRING:return "java.lang.String";
			case INT:return "java.lang.Integer";
			case FLOAT:return "java.lang.Float";
			case DOUBLE:return "java.lang.Double";
			case OBJECT:
			default:
				return "java.lang.Object";
		}
	}
}
