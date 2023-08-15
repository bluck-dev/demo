package com.zbb.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;
    private String name;
    private Integer age;
    private String address;
    private String tappId;

    public static void main(String[] args) {
//        int[] ints = new int[8];
////        int [] s =[1,1,2,2,2];
//
//
//        int arr[] = {23,344,3,5,435634};
//        int[] intss = new int[8];

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(21);


//        for (Object o : arrayList) {
//            System.out.println(o);
//        }

//        for (int i = 0; i < arrayList.size(); i++) {
//            Object o = arrayList.get(i);
//            System.out.println(o);
//
//        }

        HashMap<Object, Object> hashMap = new HashMap<>();
        hashMap.put("aaa",2);
        hashMap.put("aaa",2);
        hashMap.put("c",234);
        Set<Object> set = hashMap.keySet();
        for (Object o : set) {
            System.out.println(o);
        }
        for (Object o : hashMap.keySet()) {
            Object o1 = hashMap.get(o);
            System.out.println(o1);
        }
        if (hashMap.containsKey("aaa")) {
            System.out.println("aaaaaaaaaaaaa");
        }



    }

}
