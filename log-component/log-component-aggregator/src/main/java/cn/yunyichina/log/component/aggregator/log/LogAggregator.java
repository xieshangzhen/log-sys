package cn.yunyichina.log.component.aggregator.log;

import cn.yunyichina.log.common.log.LoggerWrapper;
import cn.yunyichina.log.component.index.builder.imp.ContextIndexBuilder;
import cn.yunyichina.log.component.index.scanner.imp.LogFileScanner;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Leo
 * @Blog: http://blog.csdn.net/lc0817
 * @CreateTime: 2016/11/17 10:56
 * @Description:
 */
public class LogAggregator {

    private static final LoggerWrapper logger = LoggerWrapper.getLogger(LogAggregator.class);

    private int beginIndex;
    private int endIndex;
    private List<File> logList;

    protected LogAggregator(ContextIndexBuilder.ContextInfo contextInfo, String baseDir) throws Exception {
        ContextIndexBuilder.IndexInfo begin = contextInfo.getBegin();
        ContextIndexBuilder.IndexInfo end = contextInfo.getEnd();
        if (begin == null || end == null) {
            throw new Exception("日志聚合器无法聚合残缺的上下文.");
        }
        this.beginIndex = begin.getIndexOfLogFile();
        this.endIndex = end.getIndexOfLogFile();
        logger.info("正在初始化聚合器:" + this.beginIndex + "," + this.endIndex + "," + baseDir);
        LogFileScanner scanner = new LogFileScanner(begin.getLogFile(), end.getLogFile(), baseDir);
        Map<String, File> fileMap = scanner.scan();
        logger.info("扫描到的文件总数:" + fileMap.values().size());
        logList = new ArrayList<>(fileMap.values());
    }

    public static String aggregate(ContextIndexBuilder.ContextInfo contextInfo, String baseDir) {
        try {
            logger.info("即将初始化聚合器:" + JSON.toJSONString(contextInfo, true) + "," + baseDir);
            LogAggregator aggregator = new LogAggregator(contextInfo, baseDir);
            return aggregator.aggregate();
        } catch (Exception e) {
            logger.error("聚合异常:" + JSON.toJSONString(contextInfo, true) + " , 异常信息:" + e.getLocalizedMessage(), e);
            return "";
        }
    }

    protected String aggregate() throws Exception {
        if (logList.size() == 1) {
            File log = logList.get(0);
            String logContent = Files.asCharSource(log, Charsets.UTF_8).read();
            return logContent.substring(beginIndex, endIndex);
        } else if (logList.size() > 1) {
            StringBuilder logBuilder = new StringBuilder(1024 * 1024 * 10);
            int logListSize = logList.size();

            String firstLogConetnt = Files.asCharSource(logList.get(0), Charsets.UTF_8).read();
            logBuilder.append(firstLogConetnt.substring(beginIndex));

            for (int i = 1; i < logListSize - 2; i++) {
                logBuilder.append(Files.asCharSource(logList.get(i), Charsets.UTF_8).read());
            }

            String lastLogConetnt = Files.asCharSource(logList.get(logListSize - 1), Charsets.UTF_8).read();
            logBuilder.append(lastLogConetnt.substring(0, endIndex));

            return logBuilder.toString();
        } else {
            return "";
        }
    }

}
