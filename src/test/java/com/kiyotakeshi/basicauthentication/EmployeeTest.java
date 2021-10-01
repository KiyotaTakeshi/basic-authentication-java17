package com.kiyotakeshi.basicauthentication;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    int id = 1;
    String name = "mike";
    String department = "sales";

    @Test
    void testGetter() {
        // new Employee(id,null,department);
        // null check で怒ってくれた
        // java.lang.NullPointerException
        // at java.base/java.util.Objects.requireNonNull(Objects.java:208)
        // at com.kiyotakeshi.basicauthentication.Employee.<init>(Employee.java:9)

        var emp = new Employee(id,name,department);
        // emp.getNameLength();
        assertEquals(name, emp.name());

        var emp2 = new Employee(id, name,null);
        assertNull(emp2.department());
    }

    /**
     * オブジェクトの等価性を判定
     * 自分で作成したクラスは、オブジェクトが等価であることを判定するために
     * equals メソッドをオーバライドして実装する必要があるが
     * record class が equals method を override できているか確認
     */
    @Test
    void testEquals() {
        var emp1 = new Employee(id, name, department);
        var emp2 = new Employee(id, name, department);

        // assertEquals で書けた
        // assertTrue(emp1.equals(emp2));
        assertEquals(emp1, emp2);

        if(emp1.equals(emp2)){
            System.out.println("objects values are same");
        }
    }

    @Test
    void testHashCode() {
        var emp1 = new Employee(id, name, department);
        var emp2 = new Employee(id, name, department);

        Set<Employee> employees = new HashSet<>();

        employees.add(emp1);
        employees.add(emp2);

        assertEquals(emp1.hashCode(), emp2.hashCode());

        // hashCode メソッドを実装していないと2件になる(hashCode が異なるため)
        // Employee が hashCode を override していない場合、
        // Object クラスのものが使われる
        // これはオブジェクトが異なると異なる値を返すため、
        // 重複を想定しない HashSet にフィールドの値が等しいオブジェクトが複数存在することになる
        assertEquals(1, employees.size());

        // toString も override されている
        System.out.println(emp1); // Employee[id=1, name=mike, department=sales]
        System.out.println(emp2);
    }
}