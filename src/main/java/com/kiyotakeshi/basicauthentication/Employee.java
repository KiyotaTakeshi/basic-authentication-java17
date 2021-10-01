package com.kiyotakeshi.basicauthentication;

import java.util.Objects;

public record Employee(int id, String name, String department) {
    public Employee {
        // id field は？明示的に書かなくても null check されるみたい
        // Objects.requireNonNull(id);
        Objects.requireNonNull(name);
    }

    // 独自のメソッドを定義する場合
    // record 型はデータの容れ物的に使うなら、定義する場面は少なそう？
//    public void getNameLength(){
//        System.out.println(name.length());
//    }
}
