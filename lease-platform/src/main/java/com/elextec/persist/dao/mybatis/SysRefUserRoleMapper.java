package com.elextec.persist.dao.mybatis;

import com.elextec.persist.model.mybatis.SysRefUserRoleExample;
import com.elextec.persist.model.mybatis.SysRefUserRoleKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SysRefUserRoleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int countByExample(SysRefUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int deleteByExample(SysRefUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(SysRefUserRoleKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int insert(SysRefUserRoleKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int insertSelective(SysRefUserRoleKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    List<SysRefUserRoleKey> selectByExample(SysRefUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") SysRefUserRoleKey record, @Param("example") SysRefUserRoleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_ref_user_role
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") SysRefUserRoleKey record, @Param("example") SysRefUserRoleExample example);
}