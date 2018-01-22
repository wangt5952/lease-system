package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizBattery;
import com.elextec.persist.model.mybatis.BizBatteryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BizBatteryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int countByExample(BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int deleteByExample(BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int insert(BizBattery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int insertSelective(BizBattery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    List<BizBattery> selectByExampleWithBLOBs(BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    List<BizBattery> selectByExample(BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    BizBattery selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BizBattery record, @Param("example") BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") BizBattery record, @Param("example") BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BizBattery record, @Param("example") BizBatteryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BizBattery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(BizBattery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_battery
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BizBattery record);
}