package com.easyooo.framework.support.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Tuple;


/**
 * 定义Redis操作接口， 接口方法取自REDIS命令的小部分
 * 
 * @see 请参考 http://www.redisdoc.com/en/latest/
 *
 * @author Killer
 */
public interface RedisOperation {
	
	/**
	 * 当 key 不存在时，返回 nil ，否则，返回 key 的值。
	 * 如果 key 不是字符串类型，那么返回一个错误。
	 */
	String get(String key);
	
	/**
	 * 返回所有(一个或多个)给定 key 的值。
	 * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。
	 * 因此，该命令永不失败。
	 */
	List<String> gets(String ...keys);
	
	/**
	 * 同时设置一个或多个 key-value 对。
	 * 如果某个给定 key 已经存在，那么 MSET 会用新值覆盖原来的旧值，如果这不是你所希望的效果，
	 * 请考虑使用 MSETNX 命令：它只会在所有给定 key 都不存在的情况下进行设置操作。
	 * MSET 是一个原子性(atomic)操作，所有给定 key 都会在同一时间内被设置，
	 * 某些给定 key 被更新而另一些给定 key 没有改变的情况，不可能发生。
	 */
	String sets(String ...keyvalues);
	
	/**
	 * 将字符串值 value 关联到 key 。
	 * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。
     * 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 */
	String set(String key, int seconds, String value);
	
	/**
	 * 将字符串值 value 关联到 key
	 * 该方法不存在失效的问题
	 * {@link #set(String, int, String)}
	 */
	String set(String key, String value);
	
	/**
	 * 删除给定的一个或多个 key 。
	 * 不存在的 key 会被忽略。
	 * 返回被删除 key 的数量
	 */
	Long del(String ... keys);
	
	/**
	 * 检查给定 key 是否存在。
	 */
	boolean exists(String key); 
	
	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 * 可以对一个已经带有生存时间的 key 执行 EXPIRE 命令，新指定的生存时间会取代旧的生存时间。
	 */
	Long expire(String key, Integer seconds);
	
	/**
	 * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。
	 * 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
	 */
	Long expireAt(final String key, final long unixTime);
	
	/**
	 * 移除给定 key 的生存时间，将这个 key 从『易失的』
	 * (带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
	 */
	Long persist(String key);
	
	/**
	 * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
	 * @param key
	 * @return
	 */
	Long ttl(String key);
	
	/**
	 * 将 key 中储存的数字值增一。
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 */
	Long incr(String key);
	
	/**
	 * 将 key 所储存的值加上增量 increment 。
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。
	 * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 * 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 */
	Long incrby(String key, Long increment);
	
	/**
	 * 将 key 中储存的数字值减一
	 */
	Long decr(String key);
	
	/**
	 * 将 key 所储存的值减去减量 decrement
	 */
	Long decrby(String key, Long decrement);
	
	/**
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
	 * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
	 * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值
	 * count = 0 : 移除表中所有与 value 相等的值。
	 */
	Long lrem(String key,  Long count , String value);
	
	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
	 * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
	 * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
	 * 当 key 存在但不是列表类型时，返回一个错误。
	 */
	Long rpush(String key, String ...values);
	
	/**
	 * 将一个或多个值 value 插入到列表 key 的表头
	 * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 
	 * 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
	 * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
	 * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
	 * 当 key 存在但不是列表类型时，返回一个错误
	 */
	Long lpush(String key, String ...values);
	
	/**
	 * 移除并返回列表 key 的头元素。
	 * 列表的头元素。当 key 不存在时，返回 nil 。
	 */
	String lpop(String key);
	
	/**
	 * 移除并返回列表 key 的尾元素
	 * 列表的尾元素。当 key 不存在时，返回 nil 。
	 */
	String rpop(String key);
	
	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
	 * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 * 返回列表包括stop下标
	 */
	List<String> lrange(String key, Long start, Long stop);
	
	/**
	 * 将列表 key 下标为 index 的元素的值设置为 value 。
	 * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
	 */
	String lset(String key, Long index, String value);
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// hash command
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * 为哈希表 key 中的域 field 的值加上增量 increment 。
	 * 增量也可以为负数，相当于对给定域进行减法操作。如果 key 不存在，
	 * 一个新的哈希表被创建并执行 HINCRBY 命令。
	 * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
	 * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
	 */
	Long hincrBy(String key, String field, Long increment);
	
	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
	 * 此命令会覆盖哈希表中已存在的域。
	 * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
	 */
	String hmset(String key, Map<String ,String> fieldValues);
	
	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。
	 * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
	 * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
	 */
	List<String> hmget(String key, String ...fields);
	
	/**
	 * 返回哈希表 key 中，所有的域和值。
	 * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
	 */
	Map<String,String> hgetAll(String key);
	
	/**
	 * 返回哈希表 key 中的所有域。
	 * 一个包含哈希表中所有域的表。当 key 不存在时，返回一个空表。
	 */
	Set<String> hkeys(String key);
	
	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 * @return 被成功移除的域的数量，不包括被忽略的域。
	 */
	Long hdel(String key, String ...fields); 
	
	/**
	 * 将哈希表 key 中的域 field 的值设为 value 。
	 * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
	 * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
	 * 
	 * @return boolean
	 * 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
	 * 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
	 */
	boolean hset(String key, String field, String value);
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// set command
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
	 * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
	 * 当 key 不是集合类型时，返回一个错误。
	 * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
	 */
	Long sadd(String key, String...members);
	
	/**
	 * 返回集合 key 的基数(集合中元素的数量)。
	 * @return 集合的基数。当 key 不存在时，返回 0 。
	 */
	Long scard(String key);
	
	/**
	 * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合
	 */
	Set<String> smembers(String key);
	
	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
	 * 当 key 不是集合类型，返回一个错误。
	 * @return 被成功移除的元素的数量，不包括被忽略的元素。
	 */
	Long srem(String key, String ...values);
	
	
	/**
	 * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
	 * 从 Redis 2.6 版本开始， SRANDMEMBER 命令接受可选的 count 参数：
	 * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
	 * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
	 * SRANDMEMBER 则仅仅返回随机元素，而不对集合进行任何改动。
	 */
	List<String> srandmember(String key, Integer count);
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// zadd command
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
	 * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
	 * score 值可以是整数值或双精度浮点数。
	 * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
	 * 当 key 存在但不是有序集类型时，返回一个错误。
	 * @param key
	 * @param score 比分（排序值）
	 * @param member 实际值
	 * @return
	 */
	Long zadd(final String key, final Double score, final String member);
	
	/**
	 * @see #zadd(String, Double, String)
	 * @param key
	 * @param scoreMember
	 * @return
	 */
	Long zadd(final String key, Map<String, Double> scoreMember);
	
	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
	 * 当 key 存在但不是有序集类型时，返回一个错误。
	 * @param key
	 * @param member
	 * @return
	 */
	Long zrem(String key, String... member);
	
	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
	 * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
	 * 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
	 * 当 key 不是有序集类型时，返回一个错误。
	 * score 值可以是整数值或双精度浮点数。
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	Double zincrby(String key, double score, String member);
	
	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
	 * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
	 * 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
	 * @param key
	 * @param member
	 * @return
	 */
	Long zrank(String key, String member);

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
	 * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
	 * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名
	 * @param key
	 * @param member
	 * @return
	 */
    Long zrevrank(String key, String member);
    
    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     * @return
     */
    Long zcount(String key, double min, double max);
    
    /**
     * 返回有序集 key 中，成员 member 的 score 值。
	 * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
     */
    Double zscore(String key, String member);

    /**
     * 返回有序集 key 中，指定区间内的成员。
	 * 其中成员的位置按 score 值递增(从小到大)来排序。
	 * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
	 * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
	 * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
	 * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
	 * 超出范围的下标并不会引起错误。比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。
	 * 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。
	 * 可以通过使用 WITHSCORES 选项，来让成员和它的 score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。
	 * 客户端库可能会返回一些更复杂的数据类型，比如数组、元组等。
     * @return
     */
    Set<String> zrangeByIndex(String key, long start, long end);
    
    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
	 * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
     * @return
     */
    Set<String> zrangeByScore(String key, double min, double max);
    
    /**
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
     * {@link #zrangeByIndex(String, long, long)}
     */
    Set<String> zrevrangeByIndex(String key, long start, long end);
    
    /**
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
     * {@link #zrangeByScore(String, double, double)}
     * @param key
     * @param max
     * @param min
     * @return
     */
    Set<String> zrevrangeByScore(String key, double max, double min);
    
    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 有序集成员按 score 值递增(从小到大)次序排列。
     *  LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT LIMIT offset, count )，
     *  注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集
     */
    Set<String> zrangeByScore(String key, double min, double max, int offset,
    	    int count);
    
    /**
     * {@link #zrangeByScore(String, double, double, int, int)}
     */
    Set<String> zrevrangeByScore(String key, double max, double min,
    	    int offset, int count);
    
    /**
     * {@link #zrangeByIndex(String, long, long)
     */
    Set<Tuple> zrangeWithScores(String key, long start, long end);

    /**
     * {@link #zrevrangeByIndex(String, long, long)}
     */
    Set<Tuple> zrevrangeWithScores(String key, long start, long end);
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// other command
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * 确保在一个事务块中执行redis操作
	 * @param callback 所有的操作写在callback中
	 */
	List<Object> transaction(final TransactionCallback callback);
	
	/**
	 * 批量执行多条命令
	 * @param callback 所有的操作卸载该回调函数中
	 */
	void pipelined(final PiplineCallback callback);
}
