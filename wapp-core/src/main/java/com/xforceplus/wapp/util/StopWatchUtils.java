package com.xforceplus.wapp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MarkerFactory;
import org.springframework.util.StopWatch;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class StopWatchUtils {

    private static final ThreadLocal<StopWatch> tlsResource = new ThreadLocal<>();
    private static final String SUMMARY_FORMAT_STR = "|%-8s|%-10s|%-8s|%-10s|\n";

    public static void main(String[] args) throws Exception {
    }

    public static StopWatch createStarted(String taskId) {
        StopWatch stopWatch = new StopWatch(taskId);
        tlsResource.set(stopWatch);
        return stopWatch;
    }

    public static StopWatch start(String taskName) {
        StopWatch stopWatch = tlsResource.get();
        if (null == stopWatch) {
            return null;
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        stopWatch.start(taskName);
        return stopWatch;
    }

    public static StopWatch stop() {
        StopWatch stopWatch = tlsResource.get();
        if (null == stopWatch) {
            return null;
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        return stopWatch;
    }

    @Deprecated
    public static StopWatch nextStepRecord(String taskName) {
        StopWatch stopWatch = tlsResource.get();
        if (null == stopWatch) {
            return null;
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        stopWatch.start(taskName);
        return stopWatch;
    }

    public static String stopSummary() {
        StopWatch stopWatch = tlsResource.get();
        if (null == stopWatch) {
            return "";
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        tlsResource.remove();

        StopWatch.TaskInfo[] taskInfos = stopWatch.getTaskInfo();
        NumberFormat format = NumberFormat.getPercentInstance();
        DecimalFormat secondsDf = new DecimalFormat("#.##");
        String split = StringUtils.leftPad("-", 51, "-");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(split);
        stringBuilder.append('\n');
        stringBuilder.append(String.format(SUMMARY_FORMAT_STR, "Seconds", "Millis", "Percent", "TaskName"));
        stringBuilder.append(split);
        stringBuilder.append('\n');
        for (int i = 0; i < taskInfos.length; ++i) {
            StopWatch.TaskInfo task = taskInfos[i];
            String item = String.format(SUMMARY_FORMAT_STR, secondsDf.format(task.getTimeSeconds()) + "s",
                    task.getTimeMillis() + "ms",
                    format.format(task.getTimeSeconds() / stopWatch.getTotalTimeSeconds()), task.getTaskName());
            stringBuilder.append(item);
        }
        stringBuilder.append('\n');
        stringBuilder.append("StopWatch '" + stopWatch.getId() + "': Total Time = " + stopWatch.getTotalTimeSeconds() + "s");
        stringBuilder.append('\n');
        log.info(MarkerFactory.getMarker("StopWatch"), "\n{}", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public static String stopSumSummary() {
        StopWatch stopWatch = tlsResource.get();
        if (null == stopWatch) {
            return "";
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        tlsResource.remove();

        StopWatch.TaskInfo[] taskInfos = stopWatch.getTaskInfo();
        NumberFormat format = NumberFormat.getPercentInstance();
        DecimalFormat secondsDf = new DecimalFormat("#.##");
        String split = StringUtils.leftPad("-", 51, "-");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(split);
        stringBuilder.append('\n');
        stringBuilder.append(String.format(SUMMARY_FORMAT_STR, "Seconds", "Millis", "Percent", "TaskName"));
        stringBuilder.append(split);
        stringBuilder.append('\n');

        Map<String, List<StopWatch.TaskInfo>> taskInfoMap =
                Arrays.stream(taskInfos).collect(Collectors.groupingBy(StopWatch.TaskInfo::getTaskName));
        for (Map.Entry<String, List<StopWatch.TaskInfo>> item : taskInfoMap.entrySet()) {
            String taskName = item.getKey();
            List<StopWatch.TaskInfo> taskInfoList = item.getValue();
            double timeSeconds = taskInfoList.stream().mapToDouble(StopWatch.TaskInfo::getTimeSeconds).sum();
            double timeMillis = taskInfoList.stream().mapToDouble(StopWatch.TaskInfo::getTimeMillis).sum();
            String _item = String.format(SUMMARY_FORMAT_STR, secondsDf.format(timeSeconds) + "s",
                    timeMillis + "ms",
                    format.format(timeSeconds / stopWatch.getTotalTimeSeconds()), taskName);
            stringBuilder.append(_item);

        }
        stringBuilder.append('\n');
        stringBuilder.append("StopWatch '" + stopWatch.getId() + "': Total Time = " + stopWatch.getTotalTimeSeconds() + "s");
        stringBuilder.append('\n');
        log.info(MarkerFactory.getMarker("StopWatch"), "\n{}", stringBuilder.toString());
        return stringBuilder.toString();
    }

}
