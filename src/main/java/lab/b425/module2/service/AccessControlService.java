package lab.b425.module2.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lab.b425.module2.config.BlockChainConfig;
import lab.b425.module2.entity.*;
import lab.b425.module2.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务类 对应路由类的业务逻辑代码实现
 */
@Service
public class AccessControlService {
    private AssetMapper assetMapper;
    private PeerMapper peerMapper;
    private RecordMapper recordMapper;
    private UserMapper userMapper;
    private UserAssetRoleMapper userAssetRoleMapper;
    private UserRecordRoleMapper userRecordRoleMapper;
    private HttpServletRequest request;
    private OperationLogMapper operationLogMapper;

    @Autowired
    public void setAssetMapper(AssetMapper assetMapper) {
        this.assetMapper = assetMapper;
    }

    @Autowired
    public void setPeerMapper(PeerMapper peerMapper) {
        this.peerMapper = peerMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    public void setUserRecordRoleMapper(UserRecordRoleMapper userRecordRoleMapper) {
        this.userRecordRoleMapper = userRecordRoleMapper;
    }

    @Autowired
    public void setUserAssetRoleMapper(UserAssetRoleMapper userAssetRoleMapper) {
        this.userAssetRoleMapper = userAssetRoleMapper;
    }

//    @Autowired
//    public void setRequest(HttpServletRequest request) {
//        this.request = request;
//    }

    @Autowired
    public void setOperationLogMapper(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    /**
     * 用户注册 对应user表
     *
     * @param id       用户id
     * @param name     用户名称
     * @param peerId   区块链账户
     * @param password 密码
     */
    @Transactional
    public String signup(String id,
                         String name,
                         String peerId,
                         String password,
                         String info) {

        /*
        检查id是否重复,或者节点是否存在
         */
        if (userMapper.selectById(id) != null || peerMapper.selectById(peerId) == null) {
            return JSON.toJSONString(
                    new ResponseEntity()
                            .setCode(ResponseEntity.BAD_REQUEST)
                            .setMessage("用户已存在"));
        } else {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPeerId(peerId);
            user.setPassword(password);
            user.setInfo(info);
            userMapper.insert(user);
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.CREATE, "注册成功"));
    }

    /**
     * 用户登录 对应user表
     *
     * @param id       用户id
     * @param password 用户密码
     * @param session  会话session
     */
    public String login(String id,
                        String password,
                        HttpSession session,
                        HttpServletResponse response) {
        User thisUser = userMapper.selectById(id);
        if (thisUser.getPassword().equals(password)) {
            Peer peer = peerMapper.selectById(thisUser.getPeerId());
            session.setAttribute("loginUser", id);
            session.setAttribute("loginOrg", peer.getOrganizationId());
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "登录成功"));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "用户名或密码错误"));
        }
    }

    /**
     * 用户登出
     */
    public String logout(HttpSession session) {
        session.removeAttribute("loginUser");
        session.removeAttribute("loginOrg");
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "注销成功"));
    }

    /**
     * 删除用户
     *
     * @param id       用户id
     * @param password 用户密码
     */
    @Transactional
    public String deleteUser(String id,
                             String password) {
        if (userMapper.selectById(id).getPassword().equals(password)) {
            userMapper.deleteById(id);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "删除成功"));

        } else {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "用户名或密码错误"));
        }
    }

    /**
     * 更新用户
     *
     * @param name     新用户姓名
     * @param password 新用户密码
     * @param info     新的描述信息
     */
    @Transactional
    public String updateUser(String name,
                             String password,
                             String info) {
        String id = (String) request.getSession().getAttribute("loginUser");
        if (userMapper.selectById(id) == null) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "用户不存在"));
        } else {
            User user = userMapper.selectById(id);
            user.setName(name);
            user.setPassword(password);
            user.setInfo(info);
            userMapper.updateById(user);
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "更新成功"));
    }

    /**
     * 添加资产 为用户单独添加资产 模拟实现 实际实现需要对接fabric
     *
     * @param id                 资产id
     * @param type               类别
     * @param state              状态
     * @param component          构成组件、
     * @param quantity           数量
     * @param value              价格
     * @param producedDate       生产日期
     * @param description        描述
     * @param warehouse_position 仓储位置
     */
    @Transactional
    public String addAsset(String id,
                           String type,
                           String state,
//                           String componentOf,
                           String component,
                           Integer quantity,
                           Double value,
//                           String holderId,
//                           String exHolderId,
                           String producerId,
                           String producedDate,
                           String description,
                           String warehouse_position) throws ParseException {

        /*
        判断资产id是否重复
         */
        if (assetMapper.selectById(id) != null) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "资产id已存在"));
        }
        /*
        判断生产日期是否合法
        */
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(producedDate);
        if (date.compareTo(new Date()) >= 0) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "日期不合法"));
        }

        /*
        判断生产者是否合法
         */
        /*if (organizationMapper.selectById(producerId)==null) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "生产商不存在"));
        }*/
        Asset asset = new Asset();
        asset.setId(id);
        asset.setType(type);
        asset.setState(state);
        asset.setComponent(component);
        asset.setQuantity(quantity);
        asset.setValue(value);
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        asset.setHolderId(loginOrg);
        asset.setProducerId(producerId);
        asset.setProducedDate(date);
        asset.setDescription(description);
        asset.setWarehousePosition(warehouse_position);
        assetMapper.insert(asset);

        /**
         *待添加 需要对接区块链模块内容
         */
        if(BlockChainConfig.BLOCKCHAIN_CONNECTION){

        }

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "添加资产成功"));
    }


    /**
     * 发起交易请求 考虑到使用资产类别会导致选定策略的问题，使用资产id进行交易
     *
     * @param from     发起者组织
     * @param to       发起对象组织
     * @param assetId  资产id
     * @param price    交易价格
     * @param quantity 交易数量
     */
    @Transactional
    public String requestTransaction(String from,
                                     String to,
                                     String assetId,
                                     Double price,
                                     Integer quantity) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        /*
        判断登录对象是否合法
         */
        if (!loginOrg.equals(from) && !loginOrg.equals(to)) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "登录用户与交易不相符"));
        }
        /*
        判断资产是否存在以及状态是否为库存和数量是否合法
         */
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null || !asset.getState().equals("库存") || asset.getQuantity() < quantity) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "资产不存在或者状态数量不符合"));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("to", to);
        jsonObject.put("assetId", assetId);
        jsonObject.put("price", price);
        jsonObject.put("quantity", quantity);
        Record record = new Record();
        String id = UUID.randomUUID().toString();
        record.setId(id);
        record.setRecord(jsonObject.toJSONString());
        record.setOperator(loginOrg);
        record.setEndorser(to);
        record.setRecordType("交易");
        record.setState("0");
        recordMapper.insert(record);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "发起交易请求成功"));
    }

    @Transactional
    public String crossChainTransfer(String from,
                                     String to,
                                     String assetId,
                                     Double price,
                                     Integer quantity) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        /*
        判断登录对象是否合法
         */
        if (!loginOrg.equals(from) && !loginOrg.equals(to)) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "登录用户与交易不相符"));
        }
        /*
        判断资产是否存在以及状态是否为库存和数量是否合法
         */
        Asset asset = assetMapper.selectById(assetId);
        if (asset == null || !asset.getState().equals("库存") || asset.getQuantity() < quantity) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "资产不存在或者状态数量不符合"));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("to", to);
        jsonObject.put("assetId", assetId);
        jsonObject.put("price", price);
        jsonObject.put("quantity", quantity);
        Record record = new Record();
        String id = UUID.randomUUID().toString();
        record.setId(id);
        record.setRecord(jsonObject.toJSONString());
        record.setOperator(loginOrg);
        record.setEndorser(to);
        record.setRecordType("跨链交易");
        record.setState("0");
        recordMapper.insert(record);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "发起跨链交易申请成功"));
    }

    /**
     * 拉取待审批记录， 尚未进行任何非法参数的比对
     */
    public String getTransactionRequest() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("endorser", loginOrg)
                .eq("state", "0");
        List<Record> records = recordMapper.selectList(queryWrapper);
        if (records.isEmpty()) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "没有待审批的记录"));
        }
        Map<Object, Object> res = new HashMap<>();
        for (Record record : records) {
            res.put("recordId", record.getId());
            res.put("record", record.getRecord());
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "获取待审批记录成功", JSON.toJSONString(res)));
    }

    /**
     * 审批交易申请并将执行， 尚未进行任何非法参数的对比
     *
     * @param recordId  记录id
     * @param checkCode 确认结果
     */
    @Transactional(rollbackFor = Exception.class)
    public String approveTransactionRequest(String recordId,
                                            String checkCode,
                                            Integer crashPoint) {
        //记录保存点
        Object savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();

        /*
        判断对应记录是否合法
         */
        if (!recordMapper.selectById(recordId).getState().equals("0")) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "不需要审查的记录"));
        }
        Record record = recordMapper.selectById(recordId);

        String echo;
        if (checkCode.equals("1")) {
            echo = "确认交易成功";
            Map<String, Object> maps = JSONObject.parseObject(record.getRecord(),
                    new TypeReference<Map<String, Object>>() {
                    });
            Asset asset = assetMapper.selectById((Serializable) maps.get("assetId"));
            BigDecimal price = (BigDecimal) maps.get("price");
            Integer quantity = (Integer) maps.get("quantity");

            boolean needSplit = false;
            /*
            验证资产存在性、状态及数量
             */
            if (asset == null || !asset.getState().equals("库存") || asset.getQuantity() < quantity) {
                record.setState("3");
                recordMapper.updateById(record);
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "资产不存在或者状态数量不符合"));
            }
            /*
            存在资产剩余，进行拆分
             */
            else if (asset.getQuantity() > quantity) {
                needSplit = true;
            }

            /**
             需要对接区块链模块
             */

            String transferType = record.getRecordType();
            //当交易为同链交易的话,无需对区块链操作进行记录，一致性默认保存
            if (transferType.equals("交易")) {
                try {
                    //对区块链的交易
                    Thread.sleep(1);

                } catch (Exception e) {
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "服务端出现错误，请稍后再试"));
                }
            }
            //当交易为跨链交易的话，对交易进行记录，暂时使用数据库记录的方式，记录方式后续需要转换成为文件记录形式
            else if (transferType.equals("跨链交易")) {
                OperationLog operationLog = new OperationLog();
                String transactionId = UUID.randomUUID().toString();
                operationLog.setTransactionId(transactionId);
                operationLog.setRecordId(recordId);
                operationLog.setState("0");

                try {
                    //崩溃点1,在开始事务日志之前
                    if (crashPoint == 1) {
                        int a = 1 / 0;
                    }
                    operationLogMapper.insert(operationLog);
                    savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "服务端出现错误，请稍后再试"));
                }
                try {
                    //崩溃点2，在开始事务日志之后
                    if (crashPoint == 2) {
                        int a = 1 / 0;
                    }
                    operationLog.setState("10");
                    operationLogMapper.insert(operationLog);
                    savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "服务端出现错误，请稍后再试"));
                }
                try {


                    //对区块链一的交易

                    Thread.sleep(1);
                    //崩溃点3，在开始对区块链1进行操作的过程中
                    if (crashPoint == 3) {
                        int a = 1 / 0;
                    }
                    operationLog.setState("11");
                    operationLogMapper.insert(operationLog);
                    savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "区块链系统出现错误，请稍后再试"));
                }


                try {
                    //崩溃点4，完成对区块链1操作，对区块链2操作开始之前
                    if (crashPoint == 4) {
                        int a = 1 / 0;
                    }
                    operationLog.setState("20");
                    operationLogMapper.insert(operationLog);
                    savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "服务端出现错误，请稍后再试"));
                }
                try {
                    //崩溃点5，在开始对区块链2进行操作的过程中
                    if (crashPoint == 5) {
                        int a = 1 / 0;
                    }
                    operationLog.setState("21");
                    operationLogMapper.insert(operationLog);
                    savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                    //对区块链二的交易

                    Thread.sleep(1);

                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "区块链系统出现错误，请稍后再试"));
                }
                try {
                    //崩溃点6，完成对区块链2的操作过后
                    if (crashPoint == 6) {
                        int a = 1 / 0;
                    }
                    operationLog.setState("3");
                    operationLogMapper.insert(operationLog);
                    savepoint = TransactionAspectSupport.currentTransactionStatus().createSavepoint();
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "服务端出现错误，请稍后再试"));
                }
                try {
                    //崩溃点7，完成对区块链2的操作过后
                    if (crashPoint == 7) {
                        int a = 1 / 0;
                    }
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().rollbackToSavepoint(savepoint);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.SERVICE_UNAVAILABLE, "服务端出现错误，请稍后再试"));
                }
            }

            //如果需要数据分割，对数据库的修改部分
            if (needSplit) {
                /**
                 * 待修改 新生成的拼接字符串的策略
                 */
                Asset newAsset = new Asset(asset);
                newAsset.setId(asset.getId() + "-" + recordId.substring(0, 2));
                newAsset.setQuantity(asset.getQuantity() - quantity);
                /*
                考虑记录拆分部件的组成，以及拆分过程是否需要追溯，额外问题：是否需要合并部件
                 */
                //newAsset.setComponent(asset.getComponent());
                assetMapper.insert(newAsset);
            }
            asset.setQuantity(quantity);
            asset.setValue(price.doubleValue());
            asset.setExHolderId(asset.getHolderId());
            asset.setHolderId((String) maps.get("to"));
            assetMapper.updateById(asset);
            record.setState("1");
        } else if (checkCode.equals("2")) {
            record.setState("2");
            echo = "已驳回请求";
        } else {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "确认码非法"));
        }
        recordMapper.updateById(record);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, echo));
    }

    /**
     * 内部调配接口 模拟实现 实际实现需要对接多条fabric
     *
     * @param from      内部交易发起节点
     * @param to        内部交易接受节点
     * @param assetType 资产类型
     * @param quantity  资产数量
     */
    @Transactional
    public String internalTransaction(String from,
                                      String to,
                                      String assetType,
                                      Integer quantity) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", from);
        jsonObject.put("to", to);
        jsonObject.put("assetType", assetType);
        jsonObject.put("quantity", quantity);
        Record record = new Record();
        String id = UUID.randomUUID().toString();
        record.setId(id);
        record.setRecordType("交易");
        record.setRecord(jsonObject.toJSONString());
        String loginUser = (String) request.getSession().getAttribute("loginUser");
        record.setOperator(loginUser);
        record.setEndorser(to);
        record.setState("1");
        recordMapper.insert(record);
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", assetType)
                .eq("quantity", quantity);
        Asset asset = assetMapper.selectOne(queryWrapper);
        asset.setExHolderId(from);
        asset.setHolderId(to);
        assetMapper.updateById(asset);

        /**
         *待添加 需要增加一笔权限记录以及一笔区块链操作
         */

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "内部交易成"));
    }

    /**
     * 内部加工装配
     *
     * @param id                 资产id
     * @param type               资产类别
     * @param state              资产状态
     * @param quantity           资产数量
     * @param value              资产价格
     * @param description        资产描述
     * @param warehouse_position 资产仓库位置
     * @param assetIdAndNums     组成资产id和数量
     */
    @Transactional
    public String manufacturer(String id,
                               String type,
                               String state,
                               Integer quantity,
                               Double value,
                               String producedDate,
                               String description,
                               String warehouse_position,
                               String... assetIdAndNums) throws ParseException {
        /*
        待解决 需要用id生成算法进行替换
         */
        Record record = new Record();
        String recordId = UUID.randomUUID().toString();
        /*
        判断新生成资产id是否重复
         */
        if (assetMapper.selectById(id) != null) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "资产id已存在"));
        }
        for (String assetIdAndNum : assetIdAndNums) {
            String[] re = assetIdAndNum.split("#");
            Asset asset = assetMapper.selectById(re[0]);
            int num = Integer.parseInt(re[1]);
            /*
            判断零件数目和库存匹配
             */
            if (asset == null || num > asset.getQuantity() || !asset.getState().equals("库存")) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "组件不存在或者状态数量不符合"));
            } else if (num < asset.getQuantity()) {
                /*
                拆分资产
                 */
                Asset newAsset = new Asset(asset);
                /**
                 * 待修改 新生成的拼接字符串的策略 ,重复拼接导致id重复
                 */
                newAsset.setId(asset.getId() + "-" + recordId.substring(0, 2));
                newAsset.setQuantity(asset.getQuantity() - num);
                newAsset.setComponent(asset.getId());
                assetMapper.insert(newAsset);
            }
            /*
            对已使用的资产进行更新
             */
            asset.setQuantity(num);
            asset.setComponentOf(id);
            asset.setState("已使用");
            asset.setComponentOf(id);
            assetMapper.updateById(asset);
        }
        /*
        添加新制造的资产
         */
        Asset newAsset = new Asset();
        newAsset.setId(id);
        newAsset.setType(type);
        newAsset.setState(state);
        newAsset.setComponent(JSON.toJSONString(assetIdAndNums));
        newAsset.setQuantity(quantity);
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        newAsset.setHolderId(loginOrg);
        newAsset.setProducerId(loginOrg);
        Date pDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(producedDate);
        /*
        判断生产日期是否合法
         */
        if (pDate.compareTo(new Date()) >= 0) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "日期不合法"));
        }
        newAsset.setProducedDate(pDate);
        newAsset.setValue(value);
        newAsset.setDescription(description);
        newAsset.setWarehousePosition(warehouse_position);
        assetMapper.insert(newAsset);

        /*
        添加一笔制造记录，待抽离，添加记录接口
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("type", type);
        jsonObject.put("state", state);
        jsonObject.put("quantity", quantity);
        jsonObject.put("value", value);
        jsonObject.put("producedDate", producedDate);
        jsonObject.put("description", description);
        jsonObject.put("warehouse_position", warehouse_position);
        jsonObject.put("assetIdAndNums", JSON.toJSONString(assetIdAndNums));


        record.setId(recordId);
        record.setRecord(jsonObject.toJSONString());
        record.setOperator(loginOrg);
        record.setEndorser(null);
        record.setRecordType("制造");
        record.setState("1");
        recordMapper.insert(record);

        /**
         *待添加 需要和区块链模块进行对接
         */

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "提交生产数据成功"));
    }

    /**
     * 拉取用户选定资产类型的选定属性， 查询其他用户时必须选定资产类型
     *
     * @param orgId     目标组织id
     * @param assetType 资产类型
     * @param params    选取的参数
     */
    @Transactional
    public String queryAsset(String assetType,
                             String orgId,
                             String... params) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();

        /*
        判断是否为本组织查询
         */
        if (!orgId.equals(loginOrg)) {
            /*
            判断其他组织查询参数是否足够
             */
            if (assetType == null) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "查询其他用户必须选定资产类型"));
            }
            QueryWrapper<UserAssetRole> ACQueryWrapper = new QueryWrapper<>();
            ACQueryWrapper.eq("user_id", loginOrg)
                    .eq("target_Org", orgId)
                    .eq("asset_type", assetType);
            UserAssetRole assetRole = userAssetRoleMapper.selectOne(ACQueryWrapper);
            /*
            判断权限是否存在
             */
            if (assetRole == null) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户没有对应的权限"));
            }
            /*
            判断权限是否过期
             */
            else if (new Date().compareTo(assetRole.getExpirationTime()) >= 0) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户权限过期"));
            }
            /*
            根据已有的权限筛选
             */
            HashSet<String> authorities = JSONObject.parseObject(assetRole.getAuthority(),
                    new TypeReference<HashSet<String>>() {
                    });
            List<String> selectedParams = new ArrayList<>();

            if (params != null) {
                if (authorities != null) {
                    for (String param : params) {
                        if (authorities.contains(param)) {
                            selectedParams.add(param);
                        }
                    }
                } else {
                    selectedParams.addAll(Arrays.asList(params));
                }

            } else {
                if (authorities != null) {
                    selectedParams.addAll(authorities);
                }
            }
            queryWrapper.eq("holder_id", orgId);
            queryWrapper.eq("type", assetType);
            queryWrapper.select(selectedParams.toArray(new String[]{}));
            List<Asset> assetList = assetMapper.selectList(queryWrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询其他组织资产清单成功", JSON.toJSONString(assetList)));
        }
        /*
        本组织查询
         */
        else {
            queryWrapper.eq("holder_id", loginOrg);
            if (assetType != null) {
                queryWrapper.eq("type", assetType);
            }
            if (params != null) {
                queryWrapper.select(params);
            }
            List<Asset> assetList = assetMapper.selectList(queryWrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询本组织资产清单成功", JSON.toJSONString(assetList)));
        }
    }

    /**
     * 查询组织所有操作记录接口，实际实现需要对接fabric 对应不同链间信息对接  待解决：如何细化分类可以改进
     */
    @Transactional
    public String queryRecord(String recordType,
                              String orgId) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id").last("limit 1");

        /*
        判断是否查询本组织记录
         */
        if (!loginOrg.equals(orgId)) {
            /*
            判断其他组织查询参数是否足够
             */
            if (recordType == null) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "查询其他用户必须选定记录类型"));
            }
            QueryWrapper<UserRecordRole> ACQueryWrapper = new QueryWrapper<>();
            ACQueryWrapper.eq("user_id", loginOrg)
                    .eq("target_org", orgId)
                    .eq("record_type", recordType);
            UserRecordRole recordRole = userRecordRoleMapper.selectOne(ACQueryWrapper);
            /*
            判断权限是否存在
             */
            if (recordRole == null || !recordRole.getRecordType().equals(recordType)) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户没有对应的权限"));
            }
            /*
            判断权限是否过期
             */
            else if (new Date().compareTo(recordRole.getExpirationTime()) >= 0) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户权限过期"));
            }
            /**
             *拼接SQL尚未测试,可以尝试调换顺序
             */
            queryWrapper.eq("record_type", recordType).and(i -> i.eq("operator", orgId).or().eq("endorser", orgId));
        }
        /*
        查询本组织时
         */
        else {
            if (recordType == null) {
                queryWrapper.eq("operator", orgId).or().eq("endorser", orgId);
            } else {
                queryWrapper.eq("record_type", recordType).and(i -> i.eq("operator", orgId).or().eq("endorser", orgId));
            }
        }
        List<Record> assetList = recordMapper.selectList(queryWrapper);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "拉取记录列表成功", JSON.toJSON(assetList).toString()));
    }

    /**
     * 为用户分配资产权限 对应user_asset_role表 ，role表暂时弃用  时间问题尚未解决
     *
     * @param assetType 对应的资产类别
     * @param roleId    角色类型
     * @param userId    拥有此权限的用户id
     * @param targetOrg 权限可以对应哪一组织的资产
     * @param time      角色有效时长，单位为天
     * @param authority 所分配的权限
     */
    @Transactional
    public String allocAssetRole(String assetType,
                                 String roleId,
                                 String userId,
                                 String targetOrg,
                                 Integer time,
                                 String... authority) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        boolean isOwner = loginOrg.equals(targetOrg);
        UserAssetRole userAssetRole = new UserAssetRole();
        UserAssetRole selectOne;

        /*
        判断调用者身份权限,非root用户的情况
         */
        if (!isOwner) {
            QueryWrapper<UserAssetRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", "管理员")
                    .eq("asset_type", assetType)
                    .eq("user_id", loginOrg);
            selectOne = userAssetRoleMapper.selectOne(queryWrapper);
            /*
            不为管理员时,或者管理员不能重写自己的权限，或者分配管理员
             */
            if (selectOne == null || selectOne.getUserId().equals(userId) || !roleId.equals("共享用户")) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "用户权限不足"));
            }
            /*
            管理员权限到期时
             */
            else if (new Date().compareTo(selectOne.getExpirationTime()) >= 0) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户权限过期"));
            }
            /*
             管理员只能继承使用管理本身的权限，和时间
             */
            userAssetRole.setExpirationTime(selectOne.getExpirationTime());
            userAssetRole.setAuthority(selectOne.getAuthority());
        } else {
            /*
            root用户任意设置时间和权限
             */
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DATE, time);
            userAssetRole.setExpirationTime(instance.getTime());
            if (authority != null) {
                userAssetRole.setAuthority(JSON.toJSONString(authority));
            }
        }

        userAssetRole.setRoleId(roleId);
        userAssetRole.setAssetType(assetType);
        userAssetRole.setUserId(userId);
        userAssetRole.setCreatorId(loginOrg);
        userAssetRole.setTargetOrg(targetOrg);

        userAssetRoleMapper.insert(userAssetRole);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "用户资产权限分配成功"));
    }

    /**
     * 更新资产权限 对应user_asset_role表 暂时仅支持root用户修改时间
     *
     * @param assetType 对应的资产类型
     * @param roleId    角色类型
     * @param userId    拥有此权限的用户id
     * @param targetOrg 权限可以对应哪一组织的资产
     * @param time      角色有效时长，单位为天
     * @param authority 所分配的权限，未包括此参数时开启所有权限
     */
    @Transactional
    public String updateAssetRole(String assetType,
                                  String roleId,
                                  String userId,
                                  String targetOrg,
                                  Integer time,
                                  String... authority) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        /*
        判定是否登录用户调用此接口
         */
        if (targetOrg.equals(loginOrg)) {
            QueryWrapper<UserAssetRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("asset_type", assetType)
                    .eq("target_org", targetOrg);
            UserAssetRole updateOne = userAssetRoleMapper.selectOne(queryWrapper);
            /*
            判断重写的记录是否存在
             */
            if (updateOne == null) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "目标对象不存在"));
            } else {
                if (roleId != null && (roleId.equals("管理员") || roleId.equals("共享用户"))) {
                    updateOne.setRoleId(roleId);
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar instance = Calendar.getInstance();
                instance.setTime(new Date());
                instance.add(Calendar.DATE, time);
                updateOne.setExpirationTime(instance.getTime());
                if (authority != null) {
                    updateOne.setAuthority(JSON.toJSONString(authority));
                }
                userAssetRoleMapper.updateById(updateOne);
            }
        }
        /*
        非资产持有者调用进行驳回
         */
        else {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "目前仅支持资产拥有用户调用"));
        }
        /**
         * 需要添加一笔记录
         */

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "更新资产权限成功"));
    }

    /**
     * 为用户分配记录角色 对应user_record_role表
     *
     * @param recordType 对应的记录类型
     * @param roleId     角色类型
     * @param userId     拥有此权限的用户id
     * @param targetOrg  权限可以对应哪一组织的记录
     * @param time       有效时长
     */
    @Transactional
    public String allocRecordRole(String recordType,
                                  String roleId,
                                  String userId,
                                  String targetOrg,
                                  Integer time) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        UserRecordRole userRecordRole = new UserRecordRole();
        UserRecordRole selectOne;
        /*
        判断调用者身分
         */
        if (!loginOrg.equals(targetOrg)) {
            QueryWrapper<UserRecordRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", "管理员")
                    .eq("record_type", recordType)
                    .eq("user_id", userId);
            selectOne = userRecordRoleMapper.selectOne(queryWrapper);
            /*
            不为管理员时，或者管理员不能重写自己的权限，或者分配管理员
             */
            if (selectOne == null || selectOne.getUserId().equals(userId) || !roleId.equals("共享用户")) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "用户权限不足"));
            }
            /*
            管理员权限到期时
             */
            else if (new Date().compareTo(selectOne.getExpirationTime()) >= 0) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "用户权限过期"));
            }
            /*
            管理员只能使用管理本身时间
             */
            userRecordRole.setExpirationTime(selectOne.getExpirationTime());

        } else {
            /*
            root用户任意设置时间和权限
             */
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DATE, time);
            userRecordRole.setExpirationTime(instance.getTime());
        }
        /*
        设置其余部分的值
         */
        userRecordRole.setRoleId(roleId);
        userRecordRole.setUserId(userId);
        userRecordRole.setRecordType(recordType);
        userRecordRole.setTargetOrg(targetOrg);
        userRecordRole.setCreatorId(loginOrg);
        userRecordRoleMapper.insert(userRecordRole);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "记录权限分配成功"));
    }

    /**
     * 更新记录权限 对应user_asset_role表 暂时仅支持root用户修改时间
     *
     * @param recordType 对应的记录类型
     * @param roleId     角色类型
     * @param userId     拥有此权限的用户id
     * @param targetOrg  权限可以对应哪一组织的资产
     * @param time       角色有效时长，单位为天
     */
    @Transactional
    public String updateRecordRole(String recordType,
                                   String roleId,
                                   String userId,
                                   String targetOrg,
                                   Integer time) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        /*
        判定是否登录用户调用此接口
         */
        if (targetOrg.equals(loginOrg)) {
            QueryWrapper<UserRecordRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("record_type", recordType)
                    .eq("target_org", targetOrg);
            UserRecordRole updateOne = userRecordRoleMapper.selectOne(queryWrapper);
            /*
            判断待重写记录是否存在
             */
            if (updateOne == null) {
                return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "目标对象不存在"));
            } else {
                if (roleId != null && (roleId.equals("管理员") || roleId.equals("共享用户"))) {
                    updateOne.setRoleId(roleId);
                }
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar instance = Calendar.getInstance();
            instance.setTime(new Date());
            instance.add(Calendar.DATE, time);
            updateOne.setExpirationTime(instance.getTime());
            userRecordRoleMapper.updateById(updateOne);
        }
        /*
        非记录持有者调用进行驳回
         */
        else {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "目前仅支持记录拥有用户调用"));
        }
        /**
         * 需要添加一笔记录
         */

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "更新记录权限成功"));
    }

    /**
     * 对于登录用户，查询拥有的对于某一类别的资产权限
     * ps：role表格角色弃用 待添加：过期信息
     *
     * @param assetType 对应的资产类型
     */
    @Transactional
    public String checkAssetRoleType(String assetType) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");

        QueryWrapper<UserAssetRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginOrg)
                .eq("asset_type", assetType);
        List<UserAssetRole> selectList = userAssetRoleMapper.selectList(queryWrapper);
        if (selectList.size() == 0) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "没有存在的记录"));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询资产类型权限成功", JSON.toJSONString(selectList)));
    }

    /**
     * 对于登录用户,查询对于某一组织拥有的资产权限
     * ps：role表格角色弃用 待添加：过期信息
     *
     * @param targetOrg 对应组织
     */
    @Transactional
    public String checkAssetRoleOrg(String targetOrg) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        if (loginOrg.equals(targetOrg)) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "本组织记录拥有完整权限"));
        }
        QueryWrapper<UserAssetRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginOrg)
                .eq("target_org", targetOrg);
        List<UserAssetRole> selectList = userAssetRoleMapper.selectList(queryWrapper);
        if (selectList.size() == 0) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "没有存在的记录"));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询资产类型权限成功", JSON.toJSONString(selectList)));
    }

    /**
     * 查询有关登录组织的的资产权限
     *
     * @return 所有本组织相关的资产权限
     */
    @Transactional
    public String queryAssetRoleForSelf() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        QueryWrapper<UserAssetRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_org", loginOrg);
        List<UserAssetRole> userAssetRoleList = userAssetRoleMapper.selectList(queryWrapper);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "拉取本组织相关权限成功", JSON.toJSONString(userAssetRoleList)));

    }

    /**
     * 对于登录用户，查询拥有的对于某一类别的记录权限
     * ps：role表格角色弃用 待添加：过期信息
     *
     * @param recordType 对应的记录类型
     */
    @Transactional
    public String checkRecordTypeRole(String recordType) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");

        QueryWrapper<UserRecordRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginOrg)
                .eq("record_type", recordType);
        List<UserRecordRole> selectList = userRecordRoleMapper.selectList(queryWrapper);
        if (selectList.size() == 0) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "没有存在的记录"));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询资产类型权限成功", JSON.toJSONString(selectList)));
    }

    /**
     * 对于登录用户,查询对于某一组织拥有的资产权限
     * ps：role表格角色弃用 待添加：过期信息
     *
     * @param targetOrg 对应组织
     */
    @Transactional
    public String checkRecordTypeOrg(String targetOrg) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        if (loginOrg.equals(targetOrg)) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "本组织记录拥有完整权限"));
        }
        QueryWrapper<UserRecordRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", loginOrg)
                .eq("target_org", targetOrg);
        List<UserRecordRole> selectList = userRecordRoleMapper.selectList(queryWrapper);
        if (selectList.size() == 0) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "没有存在的记录"));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询资产类型权限成功", JSON.toJSONString(selectList)));
    }

    /**
     * 查询有关登录组织的的记录权限
     *
     * @return 所有本组织相关的记录权限
     */
    @Transactional
    public String queryRecordRoleForSelf() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        QueryWrapper<UserRecordRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("target_org", loginOrg);
        List<UserRecordRole> userRecordRoleList = userRecordRoleMapper.selectList(queryWrapper);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "拉取本组织相关权限成功", JSON.toJSONString(userRecordRoleList)));
    }

    /**
     * 对于登录用户,移除一笔资产权限
     *
     * @param assetRoleId 资产权限id
     */
    @Transactional
    public String deleteAssetRole(String assetRoleId) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        UserAssetRole userAssetRole = userAssetRoleMapper.selectById(assetRoleId);
        if (userAssetRole == null) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "没有存在的记录"));
        }
        if (!userAssetRole.getCreatorId().equals(loginOrg) && !userAssetRole.getTargetOrg().equals(loginOrg)) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "权限不足"));
        }
        userAssetRoleMapper.deleteById(assetRoleId);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "移除权限成功"));
    }

    /**
     * 对于登录用户,移除一笔记录权限
     *
     * @param recordRoleId 记录权限id
     */
    @Transactional
    public String deleteRecordRole(String recordRoleId) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        UserRecordRole userRecordRole = userRecordRoleMapper.selectById(recordRoleId);
        if (userRecordRole == null) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.BAD_REQUEST, "没有存在的记录"));
        }
        if (!userRecordRole.getCreatorId().equals(loginOrg) && !userRecordRole.getTargetOrg().equals(loginOrg)) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.FORBIDDEN, "权限不足"));
        }
        userAssetRoleMapper.deleteById(recordRoleId);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "移除权限成功"));
    }


    /**
     * 系统启动恢复事务自动执行方法
     */
    @PostConstruct
    public void autoRecover() {
        System.out.println("系统启动，检查恢复状况······");
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id").last("limit 1");
        List<OperationLog> logList = operationLogMapper.selectList(queryWrapper);
        //当不存在交易记录的情况下
        if (logList.size() == 0) {
            System.out.println("无需要恢复的记录······");
            return;
        }
        OperationLog lastOne = logList.get(0);
        //当前最近一次的跨链交易完成提交或者被终止，则不进行任何恢复操作
        if (lastOne.getState().equals("3") || lastOne.getState().equals("4")) {
            System.out.println("无需要恢复的记录······");
            return;
        }
        //当最近的一次跨链交易不满足条件之时，依次判断当前需要恢复的节点位置
        else {
            System.out.println("检查到需要恢复的记录······");
            System.out.println("检查补偿交易中······");
            //完成链上交易之后的崩溃
            if (lastOne.getState().equals("21")) {
                System.out.println("补偿交易中······");
                //根据操作记录直接撤回全部记录
                Record record = recordMapper.selectById(lastOne.getRecordId());

                String operator = record.getOperator();
                String endorser = record.getEndorser();
                //区块链操作待对接
            }
            //开始链上交易之前的崩溃
            else if (lastOne.getState().equals("0")) {

            }
            //开始链上交易过程中的崩溃
            else {

                //根据操作的记录record_id重复检查对应的记录
                Record record = recordMapper.selectById(lastOne.getRecordId());
                String operator = record.getOperator();
                String endorser = record.getEndorser();
                //根据operator检查链上第一笔交易是否完成
                //如果未完成，跳过交易

                //如果已完成，执行补偿交易

                //根据endorser检查链上第二笔交易是否完成
                //如果未完成，跳过交易

                //如果已完成，执行补偿交易
            }
            System.out.println("日志恢复中······");
            //对所有未完成操作直接添加结束字段
            OperationLog operationLog = new OperationLog();
            operationLog.setState("4");
            operationLog.setRecordId(lastOne.getRecordId());
            operationLog.setTransactionId(lastOne.getTransactionId());
            operationLogMapper.insert(operationLog);
            System.out.println("重启恢复状态完成！");
        }
    }




    /*                 预留方法:尚未使用                  */

    /**
     * 注：目前此方法已弃用！！
     * 查询对应组织已有资产接口，实际实现需要对接fabric 尚未添加权限部分，问题：如何细化分类？ 修改方式
     *
     * @param assetType 资产类型
     * @param orgId     所属组织id
     */
    public String queryAssetList(String assetType,
                                 String orgId) {
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", assetType)
                .eq("holder_id", orgId);
        List<Asset> assetList = assetMapper.selectList(queryWrapper);
        int count = 0;
        for (Asset asset : assetList) {
            count += asset.getQuantity();
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK,
                "拉取资产列表成功,记录总数为" + count + "条",
                JSON.toJSONString(assetList)));
    }

    public String checkLogin(HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        String loginOrg = (String) session.getAttribute("loginOrg");

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询当前登录用户", "loginUser: " + loginUser + ", loginOrg: " + loginOrg));
    }
}
