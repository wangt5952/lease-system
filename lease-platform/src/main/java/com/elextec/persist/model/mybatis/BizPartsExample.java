package com.elextec.persist.model.mybatis;

import com.elextec.persist.field.enums.PartsType;
import com.elextec.persist.field.enums.RecordStatus;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BizPartsExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected Integer pageBegin;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected Integer pageSize;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public BizPartsExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public void setPageBegin(Integer pageBegin) {
        this.pageBegin=pageBegin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public Integer getPageBegin() {
        return pageBegin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize=pageSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andPartsCodeIsNull() {
            addCriterion("parts_code is null");
            return (Criteria) this;
        }

        public Criteria andPartsCodeIsNotNull() {
            addCriterion("parts_code is not null");
            return (Criteria) this;
        }

        public Criteria andPartsCodeEqualTo(String value) {
            addCriterion("parts_code =", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeNotEqualTo(String value) {
            addCriterion("parts_code <>", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeGreaterThan(String value) {
            addCriterion("parts_code >", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeGreaterThanOrEqualTo(String value) {
            addCriterion("parts_code >=", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeLessThan(String value) {
            addCriterion("parts_code <", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeLessThanOrEqualTo(String value) {
            addCriterion("parts_code <=", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeLike(String value) {
            addCriterion("parts_code like", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeNotLike(String value) {
            addCriterion("parts_code not like", value, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeIn(List<String> values) {
            addCriterion("parts_code in", values, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeNotIn(List<String> values) {
            addCriterion("parts_code not in", values, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeBetween(String value1, String value2) {
            addCriterion("parts_code between", value1, value2, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsCodeNotBetween(String value1, String value2) {
            addCriterion("parts_code not between", value1, value2, "partsCode");
            return (Criteria) this;
        }

        public Criteria andPartsNameIsNull() {
            addCriterion("parts_name is null");
            return (Criteria) this;
        }

        public Criteria andPartsNameIsNotNull() {
            addCriterion("parts_name is not null");
            return (Criteria) this;
        }

        public Criteria andPartsNameEqualTo(String value) {
            addCriterion("parts_name =", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameNotEqualTo(String value) {
            addCriterion("parts_name <>", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameGreaterThan(String value) {
            addCriterion("parts_name >", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameGreaterThanOrEqualTo(String value) {
            addCriterion("parts_name >=", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameLessThan(String value) {
            addCriterion("parts_name <", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameLessThanOrEqualTo(String value) {
            addCriterion("parts_name <=", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameLike(String value) {
            addCriterion("parts_name like", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameNotLike(String value) {
            addCriterion("parts_name not like", value, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameIn(List<String> values) {
            addCriterion("parts_name in", values, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameNotIn(List<String> values) {
            addCriterion("parts_name not in", values, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameBetween(String value1, String value2) {
            addCriterion("parts_name between", value1, value2, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsNameNotBetween(String value1, String value2) {
            addCriterion("parts_name not between", value1, value2, "partsName");
            return (Criteria) this;
        }

        public Criteria andPartsBrandIsNull() {
            addCriterion("parts_brand is null");
            return (Criteria) this;
        }

        public Criteria andPartsBrandIsNotNull() {
            addCriterion("parts_brand is not null");
            return (Criteria) this;
        }

        public Criteria andPartsBrandEqualTo(String value) {
            addCriterion("parts_brand =", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandNotEqualTo(String value) {
            addCriterion("parts_brand <>", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandGreaterThan(String value) {
            addCriterion("parts_brand >", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandGreaterThanOrEqualTo(String value) {
            addCriterion("parts_brand >=", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandLessThan(String value) {
            addCriterion("parts_brand <", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandLessThanOrEqualTo(String value) {
            addCriterion("parts_brand <=", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandLike(String value) {
            addCriterion("parts_brand like", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandNotLike(String value) {
            addCriterion("parts_brand not like", value, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandIn(List<String> values) {
            addCriterion("parts_brand in", values, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandNotIn(List<String> values) {
            addCriterion("parts_brand not in", values, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandBetween(String value1, String value2) {
            addCriterion("parts_brand between", value1, value2, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsBrandNotBetween(String value1, String value2) {
            addCriterion("parts_brand not between", value1, value2, "partsBrand");
            return (Criteria) this;
        }

        public Criteria andPartsPnIsNull() {
            addCriterion("parts_pn is null");
            return (Criteria) this;
        }

        public Criteria andPartsPnIsNotNull() {
            addCriterion("parts_pn is not null");
            return (Criteria) this;
        }

        public Criteria andPartsPnEqualTo(String value) {
            addCriterion("parts_pn =", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnNotEqualTo(String value) {
            addCriterion("parts_pn <>", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnGreaterThan(String value) {
            addCriterion("parts_pn >", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnGreaterThanOrEqualTo(String value) {
            addCriterion("parts_pn >=", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnLessThan(String value) {
            addCriterion("parts_pn <", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnLessThanOrEqualTo(String value) {
            addCriterion("parts_pn <=", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnLike(String value) {
            addCriterion("parts_pn like", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnNotLike(String value) {
            addCriterion("parts_pn not like", value, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnIn(List<String> values) {
            addCriterion("parts_pn in", values, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnNotIn(List<String> values) {
            addCriterion("parts_pn not in", values, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnBetween(String value1, String value2) {
            addCriterion("parts_pn between", value1, value2, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsPnNotBetween(String value1, String value2) {
            addCriterion("parts_pn not between", value1, value2, "partsPn");
            return (Criteria) this;
        }

        public Criteria andPartsTypeIsNull() {
            addCriterion("parts_type is null");
            return (Criteria) this;
        }

        public Criteria andPartsTypeIsNotNull() {
            addCriterion("parts_type is not null");
            return (Criteria) this;
        }

        public Criteria andPartsTypeEqualTo(PartsType value) {
            addCriterion("parts_type =", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeNotEqualTo(PartsType value) {
            addCriterion("parts_type <>", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeGreaterThan(PartsType value) {
            addCriterion("parts_type >", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeGreaterThanOrEqualTo(PartsType value) {
            addCriterion("parts_type >=", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeLessThan(PartsType value) {
            addCriterion("parts_type <", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeLessThanOrEqualTo(PartsType value) {
            addCriterion("parts_type <=", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeLike(PartsType value) {
            addCriterion("parts_type like", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeNotLike(PartsType value) {
            addCriterion("parts_type not like", value, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeIn(List<PartsType> values) {
            addCriterion("parts_type in", values, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeNotIn(List<PartsType> values) {
            addCriterion("parts_type not in", values, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeBetween(PartsType value1, PartsType value2) {
            addCriterion("parts_type between", value1, value2, "partsType");
            return (Criteria) this;
        }

        public Criteria andPartsTypeNotBetween(PartsType value1, PartsType value2) {
            addCriterion("parts_type not between", value1, value2, "partsType");
            return (Criteria) this;
        }

        public Criteria andMfrsIdIsNull() {
            addCriterion("mfrs_id is null");
            return (Criteria) this;
        }

        public Criteria andMfrsIdIsNotNull() {
            addCriterion("mfrs_id is not null");
            return (Criteria) this;
        }

        public Criteria andMfrsIdEqualTo(String value) {
            addCriterion("mfrs_id =", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdNotEqualTo(String value) {
            addCriterion("mfrs_id <>", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdGreaterThan(String value) {
            addCriterion("mfrs_id >", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdGreaterThanOrEqualTo(String value) {
            addCriterion("mfrs_id >=", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdLessThan(String value) {
            addCriterion("mfrs_id <", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdLessThanOrEqualTo(String value) {
            addCriterion("mfrs_id <=", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdLike(String value) {
            addCriterion("mfrs_id like", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdNotLike(String value) {
            addCriterion("mfrs_id not like", value, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdIn(List<String> values) {
            addCriterion("mfrs_id in", values, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdNotIn(List<String> values) {
            addCriterion("mfrs_id not in", values, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdBetween(String value1, String value2) {
            addCriterion("mfrs_id between", value1, value2, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andMfrsIdNotBetween(String value1, String value2) {
            addCriterion("mfrs_id not between", value1, value2, "mfrsId");
            return (Criteria) this;
        }

        public Criteria andPartsStatusIsNull() {
            addCriterion("parts_status is null");
            return (Criteria) this;
        }

        public Criteria andPartsStatusIsNotNull() {
            addCriterion("parts_status is not null");
            return (Criteria) this;
        }

        public Criteria andPartsStatusEqualTo(RecordStatus value) {
            addCriterion("parts_status =", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusNotEqualTo(RecordStatus value) {
            addCriterion("parts_status <>", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusGreaterThan(RecordStatus value) {
            addCriterion("parts_status >", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusGreaterThanOrEqualTo(RecordStatus value) {
            addCriterion("parts_status >=", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusLessThan(RecordStatus value) {
            addCriterion("parts_status <", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusLessThanOrEqualTo(RecordStatus value) {
            addCriterion("parts_status <=", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusLike(RecordStatus value) {
            addCriterion("parts_status like", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusNotLike(RecordStatus value) {
            addCriterion("parts_status not like", value, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusIn(List<RecordStatus> values) {
            addCriterion("parts_status in", values, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusNotIn(List<RecordStatus> values) {
            addCriterion("parts_status not in", values, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusBetween(RecordStatus value1, RecordStatus value2) {
            addCriterion("parts_status between", value1, value2, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andPartsStatusNotBetween(RecordStatus value1, RecordStatus value2) {
            addCriterion("parts_status not between", value1, value2, "partsStatus");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNull() {
            addCriterion("create_user is null");
            return (Criteria) this;
        }

        public Criteria andCreateUserIsNotNull() {
            addCriterion("create_user is not null");
            return (Criteria) this;
        }

        public Criteria andCreateUserEqualTo(String value) {
            addCriterion("create_user =", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotEqualTo(String value) {
            addCriterion("create_user <>", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThan(String value) {
            addCriterion("create_user >", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
            addCriterion("create_user >=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThan(String value) {
            addCriterion("create_user <", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLessThanOrEqualTo(String value) {
            addCriterion("create_user <=", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserLike(String value) {
            addCriterion("create_user like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotLike(String value) {
            addCriterion("create_user not like", value, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserIn(List<String> values) {
            addCriterion("create_user in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotIn(List<String> values) {
            addCriterion("create_user not in", values, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserBetween(String value1, String value2) {
            addCriterion("create_user between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateUserNotBetween(String value1, String value2) {
            addCriterion("create_user not between", value1, value2, "createUser");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNull() {
            addCriterion("update_user is null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIsNotNull() {
            addCriterion("update_user is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateUserEqualTo(String value) {
            addCriterion("update_user =", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotEqualTo(String value) {
            addCriterion("update_user <>", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThan(String value) {
            addCriterion("update_user >", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserGreaterThanOrEqualTo(String value) {
            addCriterion("update_user >=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThan(String value) {
            addCriterion("update_user <", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLessThanOrEqualTo(String value) {
            addCriterion("update_user <=", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserLike(String value) {
            addCriterion("update_user like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotLike(String value) {
            addCriterion("update_user not like", value, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserIn(List<String> values) {
            addCriterion("update_user in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotIn(List<String> values) {
            addCriterion("update_user not in", values, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserBetween(String value1, String value2) {
            addCriterion("update_user between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateUserNotBetween(String value1, String value2) {
            addCriterion("update_user not between", value1, value2, "updateUser");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNull() {
            addCriterion("update_time is null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIsNotNull() {
            addCriterion("update_time is not null");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeEqualTo(Date value) {
            addCriterion("update_time =", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotEqualTo(Date value) {
            addCriterion("update_time <>", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThan(Date value) {
            addCriterion("update_time >", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("update_time >=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThan(Date value) {
            addCriterion("update_time <", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("update_time <=", value, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeIn(List<Date> values) {
            addCriterion("update_time in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotIn(List<Date> values) {
            addCriterion("update_time not in", values, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("update_time between", value1, value2, "updateTime");
            return (Criteria) this;
        }

        public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("update_time not between", value1, value2, "updateTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table biz_parts
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table biz_parts
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}