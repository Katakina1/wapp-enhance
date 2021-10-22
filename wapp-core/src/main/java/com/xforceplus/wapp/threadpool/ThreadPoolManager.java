package com.xforceplus.wapp.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 线程池管理类
 * @author sun shiyong
 */
public class ThreadPoolManager {
	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolManager.class);
	//设置核心池大小
	private static final int corePoolSize = Runtime.getRuntime().availableProcessors()*2;
	//设置线程池最大能接受多少线程
	private static final int maximumPoolSize = Runtime.getRuntime().availableProcessors()*4;
	//设置线程池缓处理的最大激活线程数
	private static final int slowDealPoolSize = Runtime.getRuntime().availableProcessors()*3;
	//当前线程数大于corePoolSize、小于maximumPoolSize时，超出corePoolSize的线程数的生命周期
	private static final long keepActiveTime = 200;
	//设置时间单位，秒
	private static final TimeUnit timeUnit = TimeUnit.SECONDS;
	//设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为10
	private static final BlockingQueue<Runnable> workQueueL1 = new ArrayBlockingQueue<>(10);
	private static final BlockingQueue<Runnable> workQueueL2 = new ArrayBlockingQueue<>(10);
	//创建ThreadPoolExecutor线程池对象，并初始化该对象的各种参数
	private static ThreadPoolExecutor customExecutorL1 = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
					keepActiveTime, timeUnit, workQueueL1, new ThreadPoolExecutor.CallerRunsPolicy());
	private static ThreadPoolExecutor customExecutorL2 = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
					keepActiveTime, timeUnit, workQueueL2, new ThreadPoolExecutor.CallerRunsPolicy());

	private static ExecutorService cachedExecutor = Executors.newCachedThreadPool();
	private static ExecutorService fixedExecutor = Executors.newFixedThreadPool(9);
	private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(2);

	/**
	 * 自定义一级线程池任务提交
	 * @param callable
	 * @return
	 */
	public static Future<Boolean>  submitCustomL1(Callable<Boolean> callable){
		if(customExecutorL1 == null){
			customExecutorL1 = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
							keepActiveTime, timeUnit, workQueueL1, new ThreadPoolExecutor.CallerRunsPolicy());
		}

		if (customExecutorL1.getActiveCount() >= slowDealPoolSize && workQueueL1.size()>8 ){
			logger.warn("#################################自定义一级线程池忙碌！！！#################################");
			logger.warn("自定义一级线程池状态参数：{}",getCustomExecutorL1Status());
		}
		return customExecutorL1.submit(callable);
	}

	/**
	 * 自定义二级线程池任务提交
	 * @param callable
	 * @return
	 */
	public static Future<Boolean>  submitCustomL2(Callable<Boolean> callable){
		if(customExecutorL2 == null){
			customExecutorL2 = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
							keepActiveTime, timeUnit, workQueueL2, new ThreadPoolExecutor.CallerRunsPolicy());
		}

		if (customExecutorL2.getActiveCount() >= slowDealPoolSize && workQueueL2.size()>8 ){
			logger.warn("#################################自定义二级线程池忙碌！！！#################################");
			logger.warn("自定义二级线程池状态参数：{}",getCustomExecutorL2Status());
		}
		return customExecutorL2.submit(callable);
	}

	public static Future<Boolean> submitCached(Callable<Boolean> callable){
		if(cachedExecutor == null){
			cachedExecutor = Executors.newCachedThreadPool();
		}
		return cachedExecutor.submit(callable);
	}
	
	public static Future<Boolean> submitFixed(Callable<Boolean> callable){
		if(fixedExecutor == null){
			fixedExecutor = Executors.newFixedThreadPool(5);
		}
		return fixedExecutor.submit(callable);
	}
	
	public static Future<Boolean> submitScheduled(Callable<Boolean> callable,long delay,TimeUnit unit){
		if(scheduledExecutor == null){
			scheduledExecutor = Executors.newScheduledThreadPool(2);
		}
		return scheduledExecutor.schedule(callable, delay, unit);
	}

	public static String getCustomExecutorL1Status(){
		return customExecutorL1.toString();
	}
	public static String getCustomExecutorL2Status(){
		return customExecutorL2.toString();
	}
	public static String getFixedExecutorStatus(){
		return fixedExecutor.toString();
	}
	public static String getCachedExecutorStatus(){
		return cachedExecutor.toString();
	}
	public static String getScheduledExecutorStatus(){
		return scheduledExecutor.toString();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(customExecutorL1.getActiveCount());
	}	
}
