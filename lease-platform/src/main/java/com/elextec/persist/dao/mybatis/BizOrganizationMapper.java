package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.BizOrganization;
import com.elextec.persist.model.mybatis.BizOrganizationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BizOrganizationMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int countByExample(BizOrganizationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int deleteByExample(BizOrganizationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int insert(BizOrganization record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int insertSelective(BizOrganization record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    List<BizOrganization> selectByExample(BizOrganizationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    BizOrganization selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BizOrganization record, @Param("example") BizOrganizationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BizOrganization record, @Param("example") BizOrganizationExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BizOrganization record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_organization
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BizOrganization record);
}