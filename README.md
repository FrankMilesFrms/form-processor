# 初始配置

 1. 你需要打包作为 jar加载到项目
 2. 抑或者使用`mvm install`。
 使用`mvm install`如下：
 > 目前最新版为 `1.5.7`
```xml
 <dependencies>
        <dependency>
            <groupId>psnl.frms</groupId>
            <artifactId>form-processor</artifactId>
            <version>1.5.7</version>
        </dependency>
....
 </dependencies>
```

# 初始化项目
## 添加实体类
这里先添加实体类 `Student`、`Teacher`：
我们以`Student`做特例处理：

 - 先给类添加注解`@Entity`表示这是实体类
 - 添加`@PrimaryKey`到**公共字段**表示作为主键，主键可以是任意多的。
 - `@ColumnInfo(name = "query_sd")`表示不用**默认字段名**，而是改为注解内的名，注意：**不要有冲突的可能。**
 - `@ColumnIgnore`：忽略此字段

```java
@Entity
public class Student
{
	@PrimaryKey
	public double student_id = 0;

	@PrimaryKey
	@ColumnInfo(name = "query_sd")
	public  String name = "undefined";

	@ColumnInfo(name = "SEX_SEC")
	public  int sex = 0;
	
	public  float phone = 0;

	@ColumnIgnore
	public  int private_Phone = 0;

	public Student(double pStudent_id, String pName, int pSex, float pPhone)
	{
		student_id = pStudent_id;
		name = pName;
		sex = pSex;
		phone = pPhone;
	}

}
```
`Teacher`的内容如下：

 - `@Entity(tableName = "teacher")`指定了表名。

```java
@Entity(tableName = "teacher")
public class Teacher
{
	@ColumnInfo(name = "teacher_id")
	@PrimaryKey
	public  int id = 0;

	@PrimaryKey
	public String name = "undefined";

	public int sex = 0;

	public int phone = 0;
}

```

## 配置Dao
Dao的配置简单的多，注意要使用抽象类，编译器会实现方法。方法名可以自取。
```java
@Dao
public abstract class MyDBDao
{
	@Insert
	public abstract void put(Teacher pTeacher);

	@Insert
	public abstract void put(Student pStudent);

	@Delete
	public abstract void delete(Teacher pTeacher);

	@Delete
	public abstract void delete(Student pStudent);

	@Query
	public abstract void search(DBWhere pWhere);
}

```
## 组装到数据库

 - 给**抽象类**打上注解，**指定所属的实体类**
 - 版本号**暂时停用**，请不要使用
 - 指定数据库名字，**不要有冲突的可能**
 - Dao的获取写法固定，但你可以写多个此类似方法，并自定义方法名。
 - 最后，一定要以单例的方法获取，写法是固定的。

```java
@Database(
	entities = {Teacher.class, Student.class},
	version = 1,
	DBName = "school_system"
)
public abstract class MyDatabase
{
	@Dao
	public abstract MyDBDao getDao();

	public static MyDatabase getInstance() throws Exception
	{
		return FormBuilder
				.createCacheDatabase(MyDatabase.class, true)
				.build();
	}
}
```
# 运行实例
最后，可以如下使用：

```java
public class Main
{
	public static void main(String[] args) throws Exception
	{
		final MyDatabase myDatabase = MyDatabase.getInstance();

		final MyDBDao myDBDao = myDatabase.getDao();

		final FormController formController = FormController.getInstance();

		formController.unityCallback(new FormCallback() {
			@Override
			public void onCreate(File path)
			{
				super.onCreate(path);
				System.out.println("onCreate = " + path);
			}

			@Override
			public void putColumn(FormTable table, FormColumn putColumn)
			{
				super.putColumn(table, putColumn);
				System.out.println("putColumn = " + table.getName() + ", putColumn = " + putColumn.getTypeHashCode());
			}

			@Override
			public void putTableByColumn(FormDB db, FormColumn byColumn, String name)
			{
				super.putTableByColumn(db, byColumn, name);
				System.out.println("putTableByColumn = " + db.getName());
			}
		});

		final Student student1 = new Student(0d, "野兽先辈", 0, 114514f);
		final Student student2 = new Student(1d, "下泽北", 1, 1919810f);
		final Student student3 = new Student(2d, "林檎", 1, 1210f);

		myDBDao.put(student1);
		myDBDao.put(student2);
		myDBDao.put(student3);


		final DBWhere dbWhere = new DBWhere();
		dbWhere.addTypeColumn(
			student1,
			pFormColumn ->
			{
				final Pair<FormColumn.Unit, Boolean> pair =
					pFormColumn.getUnit("SEX_SEC", AbstractDBColumn.INT);

				Object obj = pair.first.third;
				if(obj instanceof Integer)
				{
					return (Integer) obj == 1;
				}
				return false;
			}
		);

		dbWhere.getResult(new DBWhere.DBResult()
		{
			@Override
			public void result(HashSet<FormColumn> pHashSet)
			{
				pHashSet.forEach(
					column -> {
						System.out.println("res--------");
						System.out.println(column);
					}
				);
			}
		});
		myDBDao.search(dbWhere);

		formController.saveAll();
	}

}

```
