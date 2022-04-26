package lab.b425.module2;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class HashMapTest {
    public static void main(String[] args) {
        Map<Object, Object> map = new HashMap<>();
        map.put("hhh",111);
        map.put("hhh",222);
        System.out.println(JSON.toJSONString(map));
    }

}
