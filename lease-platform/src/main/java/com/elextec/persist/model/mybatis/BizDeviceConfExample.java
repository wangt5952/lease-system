package com.elextec.persist.model.mybatis;

import com.elextec.persist.field.enums.DeviceType;
import java.util.ArrayList;
import java.util.List;

public class BizDeviceConfExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    protected Integer pageBegin;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    protected Integer pageSize;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public BizDeviceConfExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
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
     * This method corresponds to the database table biz_device_conf
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
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
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
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public void setPageBegin(Integer pageBegin) {
        this.pageBegin=pageBegin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public Integer getPageBegin() {
        return pageBegin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize=pageSize;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_device_conf
     *
     * @mbggenerated
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table biz_device_conf
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

        public Criteria andDeviceIdIsNull() {
            addCriterion("device_id is null");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIsNotNull() {
            addCriterion("device_id is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceIdEqualTo(String value) {
            addCriterion("device_id =", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotEqualTo(String value) {
            addCriterion("device_id <>", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdGreaterThan(String value) {
            addCriterion("device_id >", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdGreaterThanOrEqualTo(String value) {
            addCriterion("device_id >=", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLessThan(String value) {
            addCriterion("device_id <", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLessThanOrEqualTo(String value) {
            addCriterion("device_id <=", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdLike(String value) {
            addCriterion("device_id like", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotLike(String value) {
            addCriterion("device_id not like", value, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdIn(List<String> values) {
            addCriterion("device_id in", values, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotIn(List<String> values) {
            addCriterion("device_id not in", values, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdBetween(String value1, String value2) {
            addCriterion("device_id between", value1, value2, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceIdNotBetween(String value1, String value2) {
            addCriterion("device_id not between", value1, value2, "deviceId");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeIsNull() {
            addCriterion("device_type is null");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeIsNotNull() {
            addCriterion("device_type is not null");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeEqualTo(DeviceType value) {
            addCriterion("device_type =", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotEqualTo(DeviceType value) {
            addCriterion("device_type <>", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeGreaterThan(DeviceType value) {
            addCriterion("device_type >", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeGreaterThanOrEqualTo(DeviceType value) {
            addCriterion("device_type >=", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeLessThan(DeviceType value) {
            addCriterion("device_type <", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeLessThanOrEqualTo(DeviceType value) {
            addCriterion("device_type <=", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeLike(DeviceType value) {
            addCriterion("device_type like", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotLike(DeviceType value) {
            addCriterion("device_type not like", value, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeIn(List<DeviceType> values) {
            addCriterion("device_type in", values, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotIn(List<DeviceType> values) {
            addCriterion("device_type not in", values, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeBetween(DeviceType value1, DeviceType value2) {
            addCriterion("device_type between", value1, value2, "deviceType");
            return (Criteria) this;
        }

        public Criteria andDeviceTypeNotBetween(DeviceType value1, DeviceType value2) {
            addCriterion("device_type not between", value1, value2, "deviceType");
            return (Criteria) this;
        }

        public Criteria andPerSetIsNull() {
            addCriterion("per_set is null");
            return (Criteria) this;
        }

        public Criteria andPerSetIsNotNull() {
            addCriterion("per_set is not null");
            return (Criteria) this;
        }

        public Criteria andPerSetEqualTo(Integer value) {
            addCriterion("per_set =", value, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetNotEqualTo(Integer value) {
            addCriterion("per_set <>", value, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetGreaterThan(Integer value) {
            addCriterion("per_set >", value, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetGreaterThanOrEqualTo(Integer value) {
            addCriterion("per_set >=", value, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetLessThan(Integer value) {
            addCriterion("per_set <", value, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetLessThanOrEqualTo(Integer value) {
            addCriterion("per_set <=", value, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetIn(List<Integer> values) {
            addCriterion("per_set in", values, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetNotIn(List<Integer> values) {
            addCriterion("per_set not in", values, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetBetween(Integer value1, Integer value2) {
            addCriterion("per_set between", value1, value2, "perSet");
            return (Criteria) this;
        }

        public Criteria andPerSetNotBetween(Integer value1, Integer value2) {
            addCriterion("per_set not between", value1, value2, "perSet");
            return (Criteria) this;
        }

        public Criteria andResetIsNull() {
            addCriterion("reset is null");
            return (Criteria) this;
        }

        public Criteria andResetIsNotNull() {
            addCriterion("reset is not null");
            return (Criteria) this;
        }

        public Criteria andResetEqualTo(Integer value) {
            addCriterion("reset =", value, "reset");
            return (Criteria) this;
        }

        public Criteria andResetNotEqualTo(Integer value) {
            addCriterion("reset <>", value, "reset");
            return (Criteria) this;
        }

        public Criteria andResetGreaterThan(Integer value) {
            addCriterion("reset >", value, "reset");
            return (Criteria) this;
        }

        public Criteria andResetGreaterThanOrEqualTo(Integer value) {
            addCriterion("reset >=", value, "reset");
            return (Criteria) this;
        }

        public Criteria andResetLessThan(Integer value) {
            addCriterion("reset <", value, "reset");
            return (Criteria) this;
        }

        public Criteria andResetLessThanOrEqualTo(Integer value) {
            addCriterion("reset <=", value, "reset");
            return (Criteria) this;
        }

        public Criteria andResetIn(List<Integer> values) {
            addCriterion("reset in", values, "reset");
            return (Criteria) this;
        }

        public Criteria andResetNotIn(List<Integer> values) {
            addCriterion("reset not in", values, "reset");
            return (Criteria) this;
        }

        public Criteria andResetBetween(Integer value1, Integer value2) {
            addCriterion("reset between", value1, value2, "reset");
            return (Criteria) this;
        }

        public Criteria andResetNotBetween(Integer value1, Integer value2) {
            addCriterion("reset not between", value1, value2, "reset");
            return (Criteria) this;
        }

        public Criteria andRequestIsNull() {
            addCriterion("request is null");
            return (Criteria) this;
        }

        public Criteria andRequestIsNotNull() {
            addCriterion("request is not null");
            return (Criteria) this;
        }

        public Criteria andRequestEqualTo(Integer value) {
            addCriterion("request =", value, "request");
            return (Criteria) this;
        }

        public Criteria andRequestNotEqualTo(Integer value) {
            addCriterion("request <>", value, "request");
            return (Criteria) this;
        }

        public Criteria andRequestGreaterThan(Integer value) {
            addCriterion("request >", value, "request");
            return (Criteria) this;
        }

        public Criteria andRequestGreaterThanOrEqualTo(Integer value) {
            addCriterion("request >=", value, "request");
            return (Criteria) this;
        }

        public Criteria andRequestLessThan(Integer value) {
            addCriterion("request <", value, "request");
            return (Criteria) this;
        }

        public Criteria andRequestLessThanOrEqualTo(Integer value) {
            addCriterion("request <=", value, "request");
            return (Criteria) this;
        }

        public Criteria andRequestIn(List<Integer> values) {
            addCriterion("request in", values, "request");
            return (Criteria) this;
        }

        public Criteria andRequestNotIn(List<Integer> values) {
            addCriterion("request not in", values, "request");
            return (Criteria) this;
        }

        public Criteria andRequestBetween(Integer value1, Integer value2) {
            addCriterion("request between", value1, value2, "request");
            return (Criteria) this;
        }

        public Criteria andRequestNotBetween(Integer value1, Integer value2) {
            addCriterion("request not between", value1, value2, "request");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table biz_device_conf
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
     * This class corresponds to the database table biz_device_conf
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