package com.lyh.settings.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Test2 {
    @Test
    public void test(){
        List<String> clueList = new ArrayList<String>();
        clueList.add("sss");

        System.out.println("clueList = " + clueList);
        System.out.println(clueList.toArray());
    }
}
