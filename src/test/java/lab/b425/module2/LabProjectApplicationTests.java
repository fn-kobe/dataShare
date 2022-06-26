package lab.b425.module2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lab.b425.module2.dataSharing.entity.ResponseEntity;
import lab.b425.module2.dataSharing.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class LabProjectApplicationTests {

    private UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }



    //    @Test
    public void jsonTest() {
        ResponseEntity body = new ResponseEntity();
        body.setCode(ResponseEntity.BAD_REQUEST);
        body.setMessage("测试测试");
        System.out.println(JSON.toJSON(body));
    }

    //    @Test
    public void jsonTest2() {
        System.out.println(JSON.toJSON(
                new ResponseEntity()
                        .setCode(ResponseEntity.BAD_REQUEST)
                        .setMessage("test")));
    }

    //    @Test
    public void getName() {

        String name = new Exception().getStackTrace()[1].getMethodName();
        System.out.println(name);
//        System.out.println(Arrays.toString(getClass().getMethods()));
    }

    //    @Test
    public void jsonTest3() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", "1");
        jsonObject.put("to", "2");
        jsonObject.put("assetType", "car");
        jsonObject.put("price", "10000");
        jsonObject.put("quantity", "1");
//        System.out.println(jsonObject.toJSONString());
        Map maps = (Map) JSON.parse(jsonObject.toString());
        System.out.println(maps);
        System.out.println(maps.get("price"));

    }

    @Data
    class Student {
        int id;
        String name;
    }

//    @Test
    public void jsonTest4() {

        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Student user = new Student();
            user.setId(i);
            user.setName("zhangsan" + i);
            list.add(user);
        }
        System.out.println(JSON.toJSON(list).toString());

    }

//    @Test
    public void dotTest() {
        testUtil("a#1", "b#2", "c#3");
    }

    private static void testUtil(String... s) {

        System.out.println(Arrays.toString(s));

    }



//    @Test
    public void jsonTest6() {
        ArrayList<String> set = new ArrayList<>();
        set.add("quantity");
        set.add("producer_id");
        System.out.println(JSON.toJSON(set));
        List<String> set1 = JSONObject.parseObject(JSON.toJSON(set).toString(),
                new TypeReference<List<String>>() {
                });
        System.out.println(set1.get(1));

    }

//    @Test
    public void jsonTest7() {
        String[] arr = new String[] {"quantity","producer_id"};
        System.out.println(JSON.toJSONString(arr));
        System.out.println(JSON.toJSON(arr));
        List<String> set1 = JSONObject.parseObject(JSON.toJSON(arr).toString(),
                new TypeReference<List<String>>() {
                });
        System.out.println(set1.get(1));

    }

//    @Test
    public void jsonTest8() {
        HashSet<String> set = new HashSet<>();
        set.add("quantity");
        set.add("producer_id");
        System.out.println(set.toString());
    }

//    @Test
    public void jsonTest9() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", "from");
        jsonObject.put("to", "to");
        jsonObject.put("assetId", "assetId");
        jsonObject.put("price", 1000.0);
        jsonObject.put("quantity",2);
        Map<String, Object> maps = JSONObject.parseObject(JSON.toJSONString(jsonObject),
                new TypeReference<Map<String, Object>>() {
                });
        System.out.println(maps.get("quantity").getClass());
        System.out.println(maps.get("price").getClass());
    }

//    @Test
    public void dateTest() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = dateFormat.parse("2021-11-11 10:00:00");
        System.out.println(date);

    }

//    @Test
    public void dateTest2() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = dateFormat.parse("2021-11-11 10:00:00");
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void dateTest3() {

//        if (recordRole.getDuration() != -1) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(recordRole.getUpdateTime());
//            calendar.add(Calendar.DATE, recordRole.getDuration());
//            if (new Date().compareTo(calendar.getTime()) >= 0) {
//                return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户权限过期"));
//            }
//        }
    }

//    @Test
    public void hashTest(){
        String hash = HashUtil.hash("key", "MD5");
        System.out.println(hash);
        String newHash = HashUtil.hash("key","MD5");
        System.out.println(newHash);
        System.out.println(hash.equals(newHash));
    }


    public static class HashUtil {
        /**
         * 计算字符串的hash值
         * @param string    明文
         * @param algorithm 算法名
         * @return          字符串的hash值
         */
        public static String hash(String string, String algorithm) {
            if (string.isEmpty()) {
                return "";
            }
            MessageDigest hash = null;
            try {
                hash = MessageDigest.getInstance(algorithm);
                byte[] bytes = hash.digest(string.getBytes(StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                for (byte b : bytes) {
                    String temp = Integer.toHexString(b & 0xff);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    result.append(temp);
                }
                return result.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }

        /**
         * 计算文件的hash值
         * @param file      文件File
         * @param algorithm 算法名
         * @return          文件的hash值
         */
        public static String hash(File file, String algorithm) {
            if (file == null || !file.isFile() || !file.exists()) {
                return "";
            }
            FileInputStream in = null;
            StringBuilder result = new StringBuilder();
            byte[] buffer = new byte[84];
            int len;
            try {
                MessageDigest hash = MessageDigest.getInstance(algorithm);
                in = new FileInputStream(file);
                while ((len = in.read(buffer)) != -1) {
                    hash.update(buffer, 0, len);
                }
                byte[] bytes = hash.digest();

                for (byte b : bytes) {
                    String temp = Integer.toHexString(b & 0xff);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }
                    result.append(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(null!=in){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result.toString();
        }
    }

//    @Test
    public void timerTest(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("计时器任务循环");
            }
        }, 1000, 5000);

    }

//    @Test
    public void test2(){
        System.out.println("hello world");
    }

    public static void main(String[] args) {
        String a = "2021-10-01 18:20:00";
        String b = "2022-10-01 16:30:59";
        System.out.println(a.compareTo(b));
    }


}
