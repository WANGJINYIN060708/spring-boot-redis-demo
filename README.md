# redis作为缓存整合电商项目
## 1.整体思路
redis主要作为缓存，解决项目的性能问题，一般地，一个系统的最大性能瓶颈就是***数据库的IO操作***，另外数***据库调优***性价比是最高的。<br>
一般分为两个层面，一是提高数据库sql语句本身的性能，二是尽量避免查询数据库<br>
使用redis作为缓存可以大幅度提高系统的性能<br>
![image](https://github.com/WANGJINYIN060708/spring-boot-redis-demo/blob/master/redis%E7%BC%93%E5%AD%98%E5%8E%9F%E7%90%86%E5%9B%BE.png)
## 2.redis整合工程（Spring boot 项目 推荐使用注解方式来完成配置）
### 2.1引入依赖（根据自身需要可以选择不同的客户端，这里选择jedis）
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
        <version>2.9.0</version>
    </dependency>
### 2.2 创建RedisConfig类负责在spring容器启动时自动注入，而RedisUtil就是被注入的工具类以供其他模块调用
    public class RedisUtil {
        private JedisPool jedisPool;
        public  void  initJedisPool(String host,int port,int database){
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            // 总数
            jedisPoolConfig.setMaxTotal(200);
            // 获取连接时等待的最大毫秒
            jedisPoolConfig.setMaxWaitMillis(10*1000);
            // 最少剩余数
            jedisPoolConfig.setMinIdle(10);
            // 如果到最大数，设置等待
            jedisPoolConfig.setBlockWhenExhausted(true);
            // 在获取连接时，检查是否有效
            jedisPoolConfig.setTestOnBorrow(true);
    // 创建连接池
    jedisPool = new  JedisPool(jedisPoolConfig,host,port,20*1000);
        }
        public Jedis getJedis(){
            Jedis jedis = jedisPool.getResource();
            return jedis;
        }
    }
    将redisUtils加入spring容器中
        @Configuration
        public class RedisConfig {

            //读取配置文件中的redis的ip地址
            @Value("${spring.redis.host:disabled}")
            private String host;

            @Value("${spring.redis.port:0}")
            private int port;

            @Value("${spring.redis.database:0}")
            private int database;

            @Bean
            public RedisUtil getRedisUtil(){
                if(host.equals("disabled")){
                    return null;
                }
                RedisUtil redisUtil=new RedisUtil();
                redisUtil.initPool(host,port,database);
                return redisUtil;
            }

        }
    配置文件application.propertites或application.yml
        spring.redis.host= 192.168.67.204
        spring.redis.port=6379
        spring.redis.database=0
    测试redis
        try {
            Jedis jedis = redisUtil.getJedis();
            jedis.set("test","text_value" );
        }catch (JedisConnectionException e){
            e.printStackTrace();
        }
## 3.使用Redis整合业务逻辑
### 3.1.企业中常用redis键的命名方式
    object:id:field
    比如:user:1092:info
### 3.2.定义常量
    public class ManageConst {
        public static final String SKUKEY_PREFIX="sku:";
        public static final String SKUKEY_SUFFIX=":info";
        public static final int SKUKEY_TIMEOUT=24*60*60;
    }
### 3.3.整合
    public SkuInfo getSkuInfo(String skuId) {
        // 缓存测试-
        Jedis jedis = redisUtil.getJedis();
        SkuInfo skuInfo=null;
        String skuInfoKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
        if (jedis.exists(skuInfoKey)){
            // 取出数据
            String skuInfoJson = jedis.get(skuInfoKey);
            if (skuInfoJson!=null && skuInfoJson.length()!=0){
                skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
            }
            // 将数据转换成对象
        }else{
            // **从数据库中取得数据**
           <font color=red>skuInfo = getSkuInfoDB(skuId); </font>
            // 将最新的数据放入到缓存中
            String jsonString = JSON.toJSONString(skuInfo);
            jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT,jsonString);
        }
        jedis.close();
        return skuInfo;
    }
### 3.4.redis服务没有启动，宕机！如何解决？
Try-catch 获取mysql数据返回
### 3.5.访问redis的时候，可能会有产生高并发，如何解决这种高并发访问？
#### 3.5.1.缓存击穿
    在用户查询的过程中， 某一个热点key失效  1000万人获取数据
    解决方法：分布式锁
#### 3.5.2.缓存穿透
    是指用户查询一个根本不存在的key
    解决方法:将该可以的值设置为null，并设置过期时间 返回一个null
#### 3.5.3 雪崩反应
    所有的key在同一时刻都失效了，此时有大量的用户访问，则会产生数据库压力过大而崩溃这就是雪崩反应。
    解决方法：设置缓存的key不能在同一时刻失效。
### 3.6.解决缓存击穿问题
    set sku:1:info “OK” NX PX 10000
    EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value 。
    PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value 。
    NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value 。
    XX ：只在键已经存在时，才对键进行设置操作。
    
    @Override
      public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = null;
      //使用try..catch 来处理redis宕机的情况

          try {
          //正真的业务实现
          Jedis jedis = redisUtils.getJedis();
          //redis 可以起名 sku:1:info
          String  skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;

          String skuJson = jedis.get(skuKey);
          if (skuJson == null && skuJson.length() == 0) {

            //设置分布式锁
            System.out.println("获取分布式锁");
            //先定义一个分布式锁的key
             String skuLockKey=ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKULOCK_SUFFIX;
                // 生成锁 锁设置成功会返回ok
                String lockKey  = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);  

                if("OK".equals(lockKey)) {  //

                  System.out.println("获取锁！");
                    // 从数据库中取得数据
                    skuInfo = getSkuInfoDB(skuId);
                    // 将数据放入缓存
                    // 将对象转换成字符串
                    String skuRedisStr = JSON.toJSONString(skuInfo);
                    jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT,skuRedisStr);
                    jedis.close();
                    return skuInfo;
                } else {
                  //其他人等待
                  Thread.sleep(1000);
                  //继续查询
                  getSkuInfo(skuId);

                }
          } else { 
            //有缓存
            skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
            jedis.close();
            return skuInfo2;
          }
      } catch (Exception e) {
        e.printStackTrace();
      }

        return getSkuInfoDB(skuId); //从数据库中查找
      }




    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
