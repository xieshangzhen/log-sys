package cn.yunyichina.log.common.entity.entity.do_;

import java.util.Date;

/**
 * Created by Jonven on 2017/1/4.
 */
public class MidCollectorKeyword {

    private Integer id;
    private Integer collector_id;
    private Integer keyword_id;
    private Date create_time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCollector_id() {
        return collector_id;
    }

    public void setCollector_id(Integer collector_id) {
        this.collector_id = collector_id;
    }

    public Integer getKeyword_id() {
        return keyword_id;
    }

    public void setKeyword_id(Integer keyword_id) {
        this.keyword_id = keyword_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
