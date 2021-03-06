package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizDeviceConf;
import com.elextec.persist.model.mybatis.BizDeviceConfExample;
import com.elextec.persist.model.mybatis.BizDeviceConfKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BizDeviceConfMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int countByExample(BizDeviceConfExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int deleteByExample(BizDeviceConfExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(BizDeviceConfKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int insert(BizDeviceConf record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int insertSelective(BizDeviceConf record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    List<BizDeviceConf> selectByExample(BizDeviceConfExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    BizDeviceConf selectByPrimaryKey(BizDeviceConfKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BizDeviceConf record, @Param("example") BizDeviceConfExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BizDeviceConf record, @Param("example") BizDeviceConfExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BizDeviceConf record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BizDeviceConf record);
}