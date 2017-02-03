package com.wasu.pub.cache.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wasu.pub.util.DateUtil;

/*
 * ssss
 * @author Administrator
 * @period
 */
public class CacheServers {
  private static Logger logger = LoggerFactory.getLogger(CacheServers.class);

  private static Map<String, CacheServer> activeServers = new HashMap();

  private static Map<String, CacheServer> downServers = new HashMap();

  private static ThreadLocal<String> shardToken = new ThreadLocal();

  private static ScheduledExecutorService heartBeatPool = Executors
      .newScheduledThreadPool(1);

  public static void setShardToken(String token) {
    shardToken.set(token);
  }

  public static CacheServer getShardServer() {
    if (getActiveServers().size() == 0) {
      return new EmptyCacheServer();
    }
    final int shard = getShard();
    return getActiveServers().get(shard);
  }

  public static int getShard() {
    String token = shardToken.get();
    token = token == null ? "0" : token;
    final int hash = Math.abs(token.hashCode());
    return hash % activeServers.size();
  }

  public static boolean isActive() {
    return activeServers.size() > 0;
  }

  public static List<CacheServer> getActiveServers() {
    final List servers = new ArrayList();
    servers.addAll(activeServers.values());
    return servers;
  }

  public static CacheServer getActiveServer(String server) {
    final CacheServer cacheServer = activeServers.get(server);
    return cacheServer;
  }

  public static void init() {
    if (!CacheConfig.isCacheEnable()) {
      return;
    }

    for (final CacheServer server : CacheConfig.getServers()) {
      server.init();
      logger.info("server.checkActive()"+server.checkActive());
      if (server.checkActive()) {
          activeServers.put(server.toString(), server);
          logger.debug("可用缓存服务器" + server);
      } else {
          downServers.put(server.toString(), server);
          logger.warn("失效缓存服务器" + server);
      }
    }

      heartBeatPool.scheduleAtFixedRate(() -> {
      long b=System.currentTimeMillis();
      try{
        
        String beginDate=DateUtil.getNormal(new Date());
        
        if(logger.isDebugEnabled())
          logger.debug("缓存开始检测------"+beginDate);
      final List<CacheServer> checkDownServers = new ArrayList();
      for (final CacheServer server1 : CacheServers.activeServers.values()) {
        if (!server1.checkActive()) {
            logger.error("缓存失效" + server1);
            checkDownServers.add(server1);
        }else{
          if (logger.isDebugEnabled())  
              logger.debug("缓存保持正常：" + server1);
        }
      }
      final List<CacheServer> checkActiveServers = new ArrayList();
      for (final CacheServer server2 : CacheServers.downServers.values()) {
        if (server2.checkActive()) {
          try {
            logger.debug("缓存恢复" + server2);
            server2.clear();
            checkActiveServers.add(server2);
          } catch (final Exception e) {
            logger.error("缓存异常", e);
          }
        }else{
          logger.debug("缓存仍未恢复：" + server2);
        }
      }
      for (final CacheServer downServer : checkDownServers) {
        CacheServers.activeServers.remove(downServer.toString());
        CacheServers.downServers.put(downServer.toString(), downServer);
      }

      for (final CacheServer activeServer : checkActiveServers) {
        CacheServers.downServers.remove(activeServer.toString());
        CacheServers.activeServers.put(activeServer.toString(), activeServer);
      }
      
      }catch(Exception ex){
          ex.printStackTrace();
          logger.error("缓存检测失败",ex);
      }
      long e=System.currentTimeMillis();
      String endDate=DateUtil.getNormal(new Date());
      if (logger.isDebugEnabled())
          logger.debug("缓存检测结束------"+endDate+" execute:"+(e-b));
    }, CacheConfig.getHeartBeatInterval(), CacheConfig.getHeartBeatInterval(),
        TimeUnit.SECONDS);
  }

  private static class EmptyCacheServer extends CacheServer {
    @Override
    protected boolean test() {
      return false;
    }

    @Override
    public void init() {
    }

    @Override
    public void set(String key, Object value) throws Exception {
    }

    @Override
    public void mset(List<Object> keyValues) throws Exception {
    }

    @Override
    public Object get(String key) throws Exception {
      return null;
    }

    @Override
    public List<Object> mget(String[] keys) throws Exception {
      return null;
    }

    @Override
    public void remove(String key) throws Exception {
    }

    @Override
    public void clear() throws Exception {
    }

    @Override
    public Set<String> keys(String pattern) throws Exception {
      return null;
    }

	@Override
	public Long push(String key, String... values) throws Exception {
		return null;
	}

	@Override
	public void expire(String key, int delayTime) throws Exception {
		
	}

  }
}
