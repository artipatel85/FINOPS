package com.finops.freight.dao;

import com.finops.dao.AbstractDAO;
import com.finops.freight.bean.TrackingBean;
import com.finops.freight.vo.TrackingVO;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class TrackingDAO extends AbstractDAO {

    public List<TrackingVO> getCustomGridDataForUser(TrackingBean bean){
        String sqlQuery = "SELECT DISTINCT GRID_SEQ column1,GRID_NAME column2 from tracking_d  "
                + "WHERE USER_ID=? and PARTNER_CODE=? "
                + "and FORM_ID=? ";

        return jdbcTemplate.query(sqlQuery,
                new BeanPropertyRowMapper<>(TrackingVO.class),
                bean.getUserId(), bean.getLoadingAgent(), bean.getFormId());
    }

    public List<TrackingVO> getCustomGridData(TrackingBean bean) {
        String sqlQuery = "SELECT ORIGNL_DESC column1,USER_DESC column2,DATA_LENGTH column3,DISPLAY column4,SEQ column5,GRID_NAME gridName from tracking_d  "
                + "WHERE USER_ID=? and PARTNER_CODE=? "
                + "and FORM_ID=? AND GRID_SEQ=? " +
                "UNION " +
                "SELECT ORIGNL_DESC column1,USER_DESC column2,DATA_LENGTH column3,DISPLAY column4,SEQ column5,GRID_NAME gridName from tracking_d  " +
                "WHERE USER_ID='-99999' AND FORM_ID=? and ORIGNL_DESC NOT IN " +
                "(SELECT ORIGNL_DESC from tracking_d  " +
                "WHERE USER_ID= ? AND PARTNER_CODE=? and FORM_ID=? AND GRID_SEQ=?) " +
                "ORDER BY COLUMN5 ";

        int gridSeq = 0;
        if (!"-99999".equals(bean.getUserId())) {
            gridSeq = bean.getGridSeq();
        }

        return jdbcTemplate.query(
                sqlQuery,
                new BeanPropertyRowMapper<>(TrackingVO.class),
                bean.getUserId(), bean.getLoadingAgent(), bean.getFormId(),
                gridSeq, bean.getFormId(), bean.getUserId(), bean.getLoadingAgent(),
                bean.getFormId(), gridSeq);
    }

    public void saveGrid(TrackingBean bean) {
        String query = "INSERT INTO tracking_d "
                + "VALUES (?,?,?,?,?,?,?,?,?,?)";

        String updateQuery = "UPDATE tracking_d SET display=?, grid_name=?, data_length=?, user_desc=?,seq=?, orignl_desc=? "
                + "WHERE user_id=? AND form_id=? AND partner_code=? AND grid_seq=? AND seq = ?";

        for (TrackingVO vo : bean.getTrackingVoList()) {
            try {
                jdbcTemplate.update(query,
                        bean.getUserId(), bean.getLoadingAgent(), bean.getFormId(), StringUtils.hasText(vo.getColumn4()) ? vo.getColumn4() : "N",
                        vo.getColumn1(), vo.getColumn3(), vo.getColumn2(), vo.getColumn5(),
                        bean.getGridSeq(), bean.getGridName());
            }catch (Exception e){
                jdbcTemplate.update(updateQuery,
                        StringUtils.hasText(vo.getColumn4()) ? vo.getColumn4() : "N", bean.getGridName(), vo.getColumn3(), vo.getColumn2(),
                        vo.getColumn5(), vo.getColumn1(),bean.getUserId(), bean.getFormId(), bean.getLoadingAgent(), bean.getGridSeq(), vo.getColumn5());
            }
        }
    }

    public void prepareGSONData(TrackingBean bean) {
        int gridSeq = 1;
        for (TrackingVO vo : bean.getTrackingVoList()) {
            if ("Y".equalsIgnoreCase(vo.getColumn3())) {
                bean.setGridSeq(gridSeq++);
            } else {
                gridSeq++;
            }
        }
        List<TrackingVO> list = getCustomGridData(bean);
        AtomicInteger counter = new AtomicInteger(1);
        String columns = list.stream().filter(vo -> "Y".equals(vo.getColumn4()))
                .map(vo -> vo.getColumn1()+" column"+counter.getAndIncrement()).collect(Collectors.joining(","));

        bean.setColumnString(columns);
        bean.setColumnLength(columns.split(",").length);
        StringBuilder subQuery = new StringBuilder("SELECT " + columns + " FROM " + getTableName()).append(" WHERE 1=1 ");
        addFilter(subQuery, "SHPR", bean.getShipper(), false);
        bean.setQuery(subQuery.toString());

        List<TrackingVO> headers = new ArrayList<>();

        for (TrackingVO vo : list) {
            if ("Y".equalsIgnoreCase(vo.getColumn4())) {
                headers.add(vo);
            }
        }
        bean.setHeaderData(headers);
    }

    protected String getTableName() {
        return "so_tracking_v";
    }

    public List<TrackingVO> getSOTracking(TrackingBean bean){
        String[] fields = bean.getColumnString().split(",");;
        String query = bean.getQuery();
        query += " ORDER BY BKG_DATE DESC LIMIT "+bean.getStart()+","+bean.getLength();

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(TrackingVO.class));
    }
}
