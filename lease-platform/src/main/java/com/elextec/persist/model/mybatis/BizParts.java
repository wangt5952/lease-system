package com.elextec.persist.model.mybatis;

import com.elextec.persist.field.enums.MfrsType;
import com.elextec.persist.field.enums.RecordStatus;
import java.io.Serializable;
import java.util.Date;

public class BizParts implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = -5459288957207630742L;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.id
     *
     * @mbggenerated
     */
    private String id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_code
     *
     * @mbggenerated
     */
    private String partsCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_name
     *
     * @mbggenerated
     */
    private String partsName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_brand
     *
     * @mbggenerated
     */
    private String partsBrand;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_pn
     *
     * @mbggenerated
     */
    private String partsPn;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_type
     *
     * @mbggenerated
     */
    private MfrsType partsType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.mfrs_id
     *
     * @mbggenerated
     */
    private String mfrsId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_status
     *
     * @mbggenerated
     */
    private RecordStatus partsStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.create_user
     *
     * @mbggenerated
     */
    private String createUser;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.update_user
     *
     * @mbggenerated
     */
    private String updateUser;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.update_time
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column biz_parts.parts_parameters
     *
     * @mbggenerated
     */
    private String partsParameters;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.id
     *
     * @return the value of biz_parts.id
     *
     * @mbggenerated
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.id
     *
     * @param id the value for biz_parts.id
     *
     * @mbggenerated
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_code
     *
     * @return the value of biz_parts.parts_code
     *
     * @mbggenerated
     */
    public String getPartsCode() {
        return partsCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_code
     *
     * @param partsCode the value for biz_parts.parts_code
     *
     * @mbggenerated
     */
    public void setPartsCode(String partsCode) {
        this.partsCode = partsCode == null ? null : partsCode.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_name
     *
     * @return the value of biz_parts.parts_name
     *
     * @mbggenerated
     */
    public String getPartsName() {
        return partsName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_name
     *
     * @param partsName the value for biz_parts.parts_name
     *
     * @mbggenerated
     */
    public void setPartsName(String partsName) {
        this.partsName = partsName == null ? null : partsName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_brand
     *
     * @return the value of biz_parts.parts_brand
     *
     * @mbggenerated
     */
    public String getPartsBrand() {
        return partsBrand;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_brand
     *
     * @param partsBrand the value for biz_parts.parts_brand
     *
     * @mbggenerated
     */
    public void setPartsBrand(String partsBrand) {
        this.partsBrand = partsBrand == null ? null : partsBrand.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_pn
     *
     * @return the value of biz_parts.parts_pn
     *
     * @mbggenerated
     */
    public String getPartsPn() {
        return partsPn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_pn
     *
     * @param partsPn the value for biz_parts.parts_pn
     *
     * @mbggenerated
     */
    public void setPartsPn(String partsPn) {
        this.partsPn = partsPn == null ? null : partsPn.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_type
     *
     * @return the value of biz_parts.parts_type
     *
     * @mbggenerated
     */
    public MfrsType getPartsType() {
        return partsType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_type
     *
     * @param partsType the value for biz_parts.parts_type
     *
     * @mbggenerated
     */
    public void setPartsType(MfrsType partsType) {
        this.partsType = partsType;
    }

    public String getMfrsId() {
        return mfrsId;
    }

    public void setMfrsId(String mfrsId) {
        this.mfrsId = mfrsId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_status
     *
     * @return the value of biz_parts.parts_status
     *
     * @mbggenerated
     */
    public RecordStatus getPartsStatus() {
        return partsStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_status
     *
     * @param partsStatus the value for biz_parts.parts_status
     *
     * @mbggenerated
     */
    public void setPartsStatus(RecordStatus partsStatus) {
        this.partsStatus = partsStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.create_user
     *
     * @return the value of biz_parts.create_user
     *
     * @mbggenerated
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.create_user
     *
     * @param createUser the value for biz_parts.create_user
     *
     * @mbggenerated
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.create_time
     *
     * @return the value of biz_parts.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.create_time
     *
     * @param createTime the value for biz_parts.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.update_user
     *
     * @return the value of biz_parts.update_user
     *
     * @mbggenerated
     */
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.update_user
     *
     * @param updateUser the value for biz_parts.update_user
     *
     * @mbggenerated
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.update_time
     *
     * @return the value of biz_parts.update_time
     *
     * @mbggenerated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.update_time
     *
     * @param updateTime the value for biz_parts.update_time
     *
     * @mbggenerated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column biz_parts.parts_parameters
     *
     * @return the value of biz_parts.parts_parameters
     *
     * @mbggenerated
     */
    public String getPartsParameters() {
        return partsParameters;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column biz_parts.parts_parameters
     *
     * @param partsParameters the value for biz_parts.parts_parameters
     *
     * @mbggenerated
     */
    public void setPartsParameters(String partsParameters) {
        this.partsParameters = partsParameters == null ? null : partsParameters.trim();
    }
}