package com.finops.freight.dao;

import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.BillTemplateBean;
import com.finops.freight.bean.FreightRow;
import com.finops.freight.bean.SailingScheduleBean;
import com.finops.report.model.ReportBean;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
public class SailingScheduleDAO extends AbstractDAO {

    public List fetchList(SailingScheduleBean bean) {
        QueryBuilder fetchListQuery = new QueryBuilder(QueryType.SELECT).COLUMNS(" VSL param1, VOY param2, CARRIER param3,STR_TO_DATE(CFS_DATE,'%Y-%m-%d') param4, STR_TO_DATE(CY_DATE ,'%Y-%m-%d') param5, POL param6, " + "STR_TO_DATE(ETD ,'%Y-%m-%d') param7, POD param8, STR_TO_DATE(ETA ,'%Y-%m-%d') param9, SS_SEQ_NUMBER param10, SS_NUMBER param11, CT_VSL param12, " + "CT_VOY param13, CT_POL param14, CT_POD param15, STR_TO_DATE(CT_ETD,'%Y-%m-%d') param16, STR_TO_DATE(CT_ETA ,'%Y-%m-%d') param17").FROM("SAILING_SCHEDULE_D", "SS").WHERE("1", "1");

        String[] fields = {"VSL", "VOY", "CARRIER", "CFS_DATE", "CY_DATE", "POL", "ETD", "POD", "ETA"};

        addFilter(fetchListQuery, bean, fields);
        addLimit("SS_SEQ_NUMBER DESC", fetchListQuery, bean);

        return jdbcTemplate.query(fetchListQuery.build().toString(), new BeanPropertyRowMapper<>(ReportBean.class));

    }

    public SailingScheduleBean retrieveSS(SailingScheduleBean bean) {
        String query = "SELECT SS_SEQ_NUMBER,SS_NUMBER, CT_SS_NUMBER, VSL, VOY, POL, P.DESCRIPTION1 POL_DESC, " + "STR_TO_DATE(ETD,'%Y-%m-%d') ETD, POD, L.DESCRIPTION1 POD_DESC,D.DESCRIPTION PARTNER_NAME," + "CT_VSL,CT_VOY,CT_POL,CT_POD,STR_TO_DATE(CT_ETD ,'%Y-%m-%d') CT_ETD, " + "STR_TO_DATE(CT_ETA ,'%Y-%m-%d') CT_ETA, STR_TO_DATE(ETA ,'%Y-%m-%d') ETA, CARRIER, " + "STR_TO_DATE(CFS_DATE,'%Y-%m-%d') CFS_DATE, CFS_TIME, STR_TO_DATE(CY_DATE,'%Y-%m-%d') CY_DATE, " + "CY_TIME, S.DISPLAY,REMARK FROM SAILING_SCHEDULE_D S, PARTNER_D D, PORT_D P, PORT_D L " + "WHERE P.PORT_CODE = S.POL AND L.PORT_CODE = S.POD AND D.PARTNER_CODE = S.CARRIER " + "AND SS_NUMBER = ? ORDER BY POL ";

        return jdbcTemplate.query(query, new ResultSetExtractor<SailingScheduleBean>() {
            @Override
            public SailingScheduleBean extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<FreightRow> polList = new ArrayList<>();
                List<FreightRow> podList = new ArrayList<>();
                while (rs.next()) {
                    bean.setSalingScheduleSeqId(rs.getInt("SS_SEQ_NUMBER"));
                    bean.setVesselCode(rs.getString("VSL"));
                    bean.setVoyageCode(rs.getString("VOY"));
                    bean.setPartnerCode(rs.getString("CARRIER"));
                    bean.setDescription(rs.getString("PARTNER_NAME"));
                    bean.setPublicFlag(Boolean.valueOf(rs.getString("DISPLAY")));
                    bean.setRemark(rs.getString("REMARK"));
                    bean.setVoyageCodeCB(rs.getString("CT_VOY"));
                    bean.setVesselCodeCB(rs.getString("CT_VSL"));
                    bean.setConnectByPol(rs.getString("CT_POL"));
                    bean.setConnectByPod(rs.getString("CT_POD"));
                    bean.setConnectByETA(rs.getString("CT_ETA"));
                    bean.setConnectByETD(rs.getString("CT_ETD"));
                    bean.setConnectById(rs.getString("CT_SS_NUMBER"));
                    if (StringUtils.hasText(bean.getConnectById())) {
                        bean.setConnectByValue(bean.getVesselCodeCB() + "/" + bean.getVoyageCodeCB() + "/" + bean.getConnectByPol() + "/" + bean.getConnectByETD() + "/" + bean.getConnectByETA());
                    }
                    String polCode = rs.getString("POL");
                    if (StringUtils.hasText(polCode)) {
                        FreightRow row = new FreightRow();
                        row.setValue1(polCode);
                        row.setValue2(rs.getString("POL_DESC"));
                        row.setValue3(rs.getString("CFS_DATE"));
                        row.setValue4(rs.getString("CFS_TIME"));
                        row.setValue5(rs.getString("CY_DATE"));
                        row.setValue6(rs.getString("CY_TIME"));
                        row.setValue7(rs.getString("ETD"));
                        polList.add(row);
                    }

                    String podCode = rs.getString("POD");
                    if (StringUtils.hasText(podCode)) {
                        FreightRow row = new FreightRow();
                        row.setValue1(podCode);
                        row.setValue2(rs.getString("POD_DESC"));
                        row.setValue3(rs.getString("ETA"));
                        podList.add(row);
                    }
                }
                fillFreightRows(polList, 10);
                bean.setPolList(polList);
                fillFreightRows(podList, 7);
                bean.setPodList(podList);
                return bean;
            }
        }, bean.getSalingScheduleId());

    }

    public void fillFreightRows(List<FreightRow> list, int size) {
        int listSize = list.size();
        for (int i = 0; i < size - listSize; i++) {
            list.add(new FreightRow());
        }
    }

    public void deleteSS(SailingScheduleBean ssBean) {
        String delQuery = "DELETE FROM sailing_schedule_d WHERE SS_NUMBER = ?";

        jdbcTemplate.update(delQuery, ssBean.getSalingScheduleId());
    }

    public void createSSIDs(SailingScheduleBean ssBean) {
        String ssIdQuery = "SELECT MAX(SS_SEQ_NUMBER)+1 salingScheduleSeqId, MAX(SS_NUMBER)+1 salingScheduleId FROM SAILING_SCHEDULE_D ";

        SailingScheduleBean tempSS = jdbcTemplate.queryForObject(ssIdQuery, new BeanPropertyRowMapper<>(SailingScheduleBean.class));

        ssBean.setSalingScheduleSeqId(tempSS.getSalingScheduleSeqId());
        ssBean.setSalingScheduleId(tempSS.getSalingScheduleId());
    }

    public void insert(SailingScheduleBean bean) {

        String INSERT_SS = " INSERT INTO SAILING_SCHEDULE_D ( SS_SEQ_NUMBER,  SS_NUMBER, VSL, VOY, POL, POD, ETD, ETA, CARRIER, CFS_DATE, " + "CFS_TIME, CY_DATE, CY_TIME, DISPLAY, CREATED_BY, CREATION_DATE, AMENDED_BY, AMENDED_DATE, REMARK ) " + " VALUES ( ?, ?, ?, ?, ?, ?, ? , ? , ?, ? , ?, ? , ?, ?, ?, getDate(), ?, getDate(), ? ) ";

        Object[] params = new Object[17];
        params[0] = bean.getSalingScheduleSeqId();
        params[3] = bean.getVesselCode();
        params[4] = bean.getVoyageCode();
        params[9] = bean.getPartnerCode();
        params[14] = "Y";
        params[15] = bean.getUserId();
        params[16] = "";
        params[17] = bean.getRemark();

        int ssId = bean.getSalingScheduleId() + 1;
        for (FreightRow fr1 : bean.getPodList()) {
            if (StringUtils.hasText(fr1.getValue1())) {
                params[6] = fr1.getValue1();
                params[8] = fr1.getValue3();
                for (FreightRow fr2 : bean.getPolList()) {
                    if (StringUtils.hasText(fr2.getValue1())) {
                        break;
                    }
                    params[2] = ssId++;
                    params[5] = fr2.getValue1();
                    params[10] = fr2.getValue3();
                    params[11] = fr2.getValue4();
                    params[12] = fr2.getValue5();
                    params[13] = fr2.getValue6();
                    params[7] = fr2.getValue7();

                    jdbcTemplate.update(INSERT_SS, params);
                }
            } else {
                break;
            }
        }

    }

    public void save(SailingScheduleBean sailingScheduleBean) {
        String ccQuery = "SELECT MAX(SS_SEQ_NUMBER) salingScheduleSeqId, MAX(SS_NUMBER) salingScheduleId FROM SAILING_SCHEDULE_D ";

        String updateQuery = "UPDATE SAILING_SCHEDULE_D SET VSL = ?, VOY = ?, POL = ?, POD = ?, ETD = ?, ETA = ?, CARRIER = ?, CFS_DATE = ?, " + "CFS_TIME = ?, " +
                "CY_DATE = ?, CY_TIME = ?, AMENDED_BY = ?, AMENDED_DATE = SYSDATE(), REMARK = ?  WHERE SS_SEQ_NUMBER = ? ";

        String insertQuery = "INSERT INTO SAILING_SCHEDULE_D (SS_SEQ_NUMBER, SS_NUMBER, VSL, VOY, POL, POD, ETD, ETA, CARRIER, CFS_DATE, " +
                "CFS_TIME, CY_DATE, CY_TIME, DISPLAY, CREATED_BY, CREATION_DATE, AMENDED_BY, AMENDED_DATE, REMARK) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE(), ?, SYSDATE(), ?) ";

        List<FreightRow> podList = sailingScheduleBean.getPodList();
        List<FreightRow> polList = sailingScheduleBean.getPolList();

        if (sailingScheduleBean.getSalingScheduleSeqId() == 0) {
            SailingScheduleBean ssBean = jdbcTemplate.queryForObject(ccQuery, new BeanPropertyRowMapper<>(SailingScheduleBean.class));
            int seqNumber = ssBean.getSalingScheduleSeqId();
            int ssNumber = ssBean.getSalingScheduleId();
            for (FreightRow fr1 : podList) {
                if (StringUtils.hasText(fr1.getValue1())) {
                    seqNumber++;
                    for (FreightRow fr2 : polList) {
                        if (!StringUtils.hasText(fr2.getValue1())) {
                            break;
                        }
                        jdbcTemplate.update(insertQuery, seqNumber, ++ssNumber, sailingScheduleBean.getVesselCode(), sailingScheduleBean.getVoyageCode(),
                                fr2.getValue1(), fr1.getValue1(), fr2.getValue7(), fr1.getValue3(), sailingScheduleBean.getPartnerCode(), defaultNull(fr2.getValue3()),
                                defaultNull(fr2.getValue4()), defaultNull(fr2.getValue5()), defaultNull(fr2.getValue6()), "Y", sailingScheduleBean.getUserId(), sailingScheduleBean.getUserId(),
                                sailingScheduleBean.getRemark());
                    }
                }
            }

        } else {
            if(podList != null && !podList.isEmpty()){
                if(polList != null && !polList.isEmpty()){
                    FreightRow fr1 = podList.get(0);
                    FreightRow fr2 = polList.get(0);
                    jdbcTemplate.update(updateQuery, sailingScheduleBean.getVesselCode(), sailingScheduleBean.getVoyageCode(),
                            fr2.getValue1(), fr1.getValue1(), fr2.getValue7(), fr1.getValue3(),
                            sailingScheduleBean.getPartnerCode(), defaultNull(fr2.getValue3()),
                            defaultNull(fr2.getValue4()), defaultNull(fr2.getValue5()), defaultNull(fr2.getValue6()),
                            sailingScheduleBean.getUserId(), sailingScheduleBean.getRemark(), sailingScheduleBean.getSalingScheduleSeqId());
                }
            }

        }
    }
}
