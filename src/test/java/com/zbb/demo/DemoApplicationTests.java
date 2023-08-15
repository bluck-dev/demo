package com.zbb.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
            int [] a ={17,41,55,65,85};
            int [] b ={12,45,57,68,84};
            int [] c =new int[a.length+b.length];
//src是源数组
//srcPos是源数组复制的起始位置
//dest是目标数组
//destPos是目标数组接收复制数据的起始位置
//length是复制的长度(源数组中从复制起始位置srcPos开始需要复制的长度)
        System.arraycopy(a,0,c,0,a.length);
        System.arraycopy(b,0,c,a.length,b.length);
        for (int i = 0; i < c.length; i++) {

        }
        int i2 = 0;
        for (int i : c) {
            i2=i;
            System.out.print(i);
        }
        System.out.println("新数组:"+i2);

    }

}
