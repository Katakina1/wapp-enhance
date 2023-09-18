package com.xforceplus.wapp.modules.syslog.dto;

import com.xforceplus.wapp.repository.entity.TXfSysLogEntity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SysLogQueue {
  private static Queue<TXfSysLogEntity> queue = new ConcurrentLinkedQueue<>();

  public static boolean offer(TXfSysLogEntity sysLog){
    return queue.offer(sysLog);
  }

  public static TXfSysLogEntity poll(){
    return queue.poll();
  }

  public static TXfSysLogEntity peek(){
    return queue.peek();
  }

  public static int size(){
    return queue.size();
  }

  public static void main(String[] args) {
    queue.offer(new TXfSysLogEntity());
    System.out.println(queue.size());
    queue.poll();
    System.out.println(queue.size());
  }

}
