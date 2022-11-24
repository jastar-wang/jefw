package com.jastarwang.jefw.redis.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 老师
 *
 * @author Jastar Wang
 * @date 2022/11/24
 * @since 1.0
 */
public class Teacher {

    private String name;
    private Integer age;
    private Long no;
    private Date hireDate;
    private LocalDate birthDay;
    private LocalTime birthTime;
    private LocalDateTime birth;

    @JsonCreator
    public Teacher(@JsonProperty("name") String name,
                   @JsonProperty("age") Integer age,
                   @JsonProperty("no") Long no,
                   @JsonProperty("hireDate") Date hireDate,
                   @JsonProperty("birth") LocalDateTime birth) {
        this.name = name;
        this.age = age;
        this.no = no;
        this.hireDate = hireDate;
        this.birth = birth;
        this.birthDay = birth.toLocalDate();
        this.birthTime = birth.toLocalTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public LocalTime getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(LocalTime birthTime) {
        this.birthTime = birthTime;
    }

    public LocalDateTime getBirth() {
        return birth;
    }

    public void setBirth(LocalDateTime birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", no=" + no +
                ", hireDate=" + hireDate +
                ", birthDay=" + birthDay +
                ", birthTime=" + birthTime +
                ", birth=" + birth +
                '}';
    }
}
