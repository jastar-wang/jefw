package com.jastarwang.jefw.redis.test;

/**
 * 学生
 *
 * @author Jastar Wang
 * @date 2020/10/13
 * @since 1.0
 */
public class Student {

    private String name;
    private Long age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Student() {
    }

    public Student(String name, Long age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
