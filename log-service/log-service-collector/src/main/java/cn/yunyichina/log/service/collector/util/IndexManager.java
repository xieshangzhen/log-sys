package cn.yunyichina.log.service.collector.util;

import cn.yunyichina.log.common.entity.entity.dto.SearchCondition;
import cn.yunyichina.log.component.aggregator.index.imp.ContextIndexAggregator;
import cn.yunyichina.log.component.aggregator.index.imp.KeyValueIndexAggregator;
import cn.yunyichina.log.component.aggregator.index.imp.KeywordIndexAggregator;
import cn.yunyichina.log.component.index.builder.imp.ContextIndexBuilder;
import cn.yunyichina.log.component.index.builder.imp.KeyValueIndexBuilder;
import cn.yunyichina.log.component.index.builder.imp.KeywordIndexBuilder;
import cn.yunyichina.log.component.index.scanner.imp.LogFileScanner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Leo
 * @Blog: http://blog.csdn.net/lc0817
 * @CreateTime: 2016/11/18 11:10
 * @Description:
 */
public class IndexManager {
    private Collection<File> logFiles;
    private Set<KeyValueIndexBuilder.KvTag> kvTagSet;
    private Set<String> keywordSet;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    Map<Long, ContextIndexBuilder.ContextInfo> contextIndexMap;
    Map<String, Set<KeywordIndexBuilder.IndexInfo>> keywordIndexMap;
    Map<String, Map<String, Set<KeyValueIndexBuilder.IndexInfo>>> keyValueIndexMap;

    public IndexManager(SearchCondition searchCondition, Set<KeyValueIndexBuilder.KvTag> kvTagSet, Set<String> keywordSet, String beginDatetime, String logRootDir) {
//        TODO 实时日志时间区间有待商榷
//        TODO 实时日志时间区间有待商榷
//        TODO 实时日志时间区间有待商榷
        String endDatetime = sdf.format(searchCondition.getEndDateTime());
        this.keywordSet = keywordSet;
        this.kvTagSet = kvTagSet;
        LogFileScanner logFileScanner = new LogFileScanner(beginDatetime, endDatetime, logRootDir);
        Map<String, File> fileMap = logFileScanner.scan();
        logFiles = fileMap.values();

        buildContextIndexMap();
        buildKeywordIndexMap();
        buildKeyValueIndexMap();
    }

    private void buildKeyValueIndexMap() {
        KeyValueIndexAggregator aggregator = new KeyValueIndexAggregator();

        for (File logFile : logFiles) {
            KeyValueIndexBuilder builder = new KeyValueIndexBuilder(kvTagSet, logFile);
            Map<String, Map<String, Set<KeyValueIndexBuilder.IndexInfo>>> map = builder.build();
            aggregator.aggregate(map);
        }

        keyValueIndexMap = aggregator.getAggregatedCollection();
    }

    private void buildKeywordIndexMap() {
        KeywordIndexAggregator aggregator = new KeywordIndexAggregator();

        for (File logFile : logFiles) {
            KeywordIndexBuilder builder = new KeywordIndexBuilder(logFile, keywordSet);
            Map<String, Set<KeywordIndexBuilder.IndexInfo>> map = builder.build();
            aggregator.aggregate(map);
        }

        keywordIndexMap = aggregator.getAggregatedCollection();
    }

    private void buildContextIndexMap() {
        ContextIndexAggregator aggregator = new ContextIndexAggregator();
        for (File logFile : logFiles) {
            ContextIndexBuilder builder = new ContextIndexBuilder(logFile);
            Map<Long, ContextIndexBuilder.ContextInfo> map = builder.build();
            aggregator.aggregate(map);
        }

        contextIndexMap = aggregator.getAggregatedCollection();
    }

    public Map<Long, ContextIndexBuilder.ContextInfo> getContextIndexMap() {
        return contextIndexMap;
    }

    public Map<String, Set<KeywordIndexBuilder.IndexInfo>> getKeywordIndexMap() {
        return keywordIndexMap;
    }

    public Map<String, Map<String, Set<KeyValueIndexBuilder.IndexInfo>>> getKeyValueIndexMap() {
        return keyValueIndexMap;
    }
}
