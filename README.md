# lib-jdbc-mysql


@Configuration
public class DBConfiguration {
	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "sqlite.c3p0")
	public DataSource dataSource() {
		return DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
	}

}

sqlite.c3p0.jdbcUrl=jdbc:sqlite:db/test.db
sqlite.c3p0.user=
sqlite.c3p0.password=
sqlite.source.c3p0.driverClass=org.sqlite.JDBC


@Component
public class example implements CommandLineRunner {
	@Autowired
	user_service user_svr;

	protected Logger logger = LoggerFactory.getLogger(example.class);

	@Override
	public void run(String... args) throws Exception {
		insert();
		update1();
		update2();
		searchList();
		searchPage();
		count_num();
	}

	private void insert() {
		for (int i = 1; i < 100; i++) {
			user user1 = new user();
			user1.setName("用户" + i);
			user1.setAge(i);
			logger.info("保存结果：" + user_svr.insUser(user1));
		}
	}
  
  private void update1() {
		user user1 = user_svr.getById("202110031321073452el");
		logger.info("读取数据,name is：" + user1.getName());
		user1.setName("修改后");
		user1.setAge(100);
		logger.info("保存结果：" + user_svr.update(user1));
	}
  
	private void update2() {
		user bean = new user();
		//bean.setName("ttt");
		bean.setAge(30);
		user user1 = user_svr.getOne(bean);
		if (user1 != null) {
			logger.info("读取数据,name is：" + user1.getName());
			user1.setName("修改后");
			user1.setAge(100);
			logger.info("保存结果：" + user_svr.update(user1));
		}
	}
  
	private void searchList() {
		user bean = new user();
		bean.setName("用户23");
		List<user> list = user_svr.getList(bean);
		for(user user : list) {
			logger.info(user.getName());
		}		
	}
  
	private void searchPage() {
		user bean = new user();
		//bean.setName("用户23");
		Sort sort = Sort.by("name").descending();
		Page<user> list = user_svr.getPage("1",bean,sort);
		for(user user : list) {
			logger.info(user.getName());
		}		
	}

	private void count_num() {
		logger.info("记录数量:" + user_svr.count_num(new user()));		
	}

}
