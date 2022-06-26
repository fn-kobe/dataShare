package lab.b425.module2.dataSharing.service;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lab.b425.module2.dataSharing.entity.*;
import lab.b425.module2.dataSharing.mapper.AssetMapper;
import lab.b425.module2.dataSharing.mapper.PeerMapper;
import lab.b425.module2.dataSharing.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 交易管理服务类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Service
public class TransactionService {
    /**
     * Mapper及HTTP相关注册
     */
    private AssetMapper assetMapper;
    private PeerMapper peerMapper;
    private TransactionMapper transactionMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Autowired
    private void setAssetMapper(AssetMapper assetMapper) {
        this.assetMapper = assetMapper;
    }

    @Autowired
    private void setPeerMapper(PeerMapper peerMapper) {
        this.peerMapper = peerMapper;
    }

    @Autowired
    private void setTransactionMapper(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    @Autowired
    private void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    private void setResponse(HttpServletResponse response) {
        this.response = response;
    }


    @Transactional
    public String addTransaction(String sender,
                                 String receiver,
                                 String assetCode,
                                 String datetime,
                                 Double price) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        //判断交易对象是否存在
        if (!sender.equals(loginOrg)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "发起交易组织错误", ErrorEntity.INVALID_ARGUMENT));
        }
        if (peerMapper.selectOne(new QueryWrapper<Peer>().eq("organization_id", receiver)) == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "交易对象不存在", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断价格是否合法
        if (price < 0) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "价格非法", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断资产状态
        Asset asset = assetMapper.selectOne(new QueryWrapper<Asset>().eq("asset_code", assetCode));
        if (asset == null ||
                !asset.getOwner().equals(loginOrg) ||
                !asset.getState().equals("1") ||
                transactionMapper.selectOne(new QueryWrapper<Transaction>().eq("asset_code", assetCode).eq("state", "0")) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "资产不存在或者状态非法", ErrorEntity.INVALID_ARGUMENT));
        }
        //添加交易记录
        Integer count = transactionMapper.selectCount(null);
        count++;
        String transactionId = "TRANSACTION" + count;
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAssetCode(assetCode);
        transaction.setPrice(price);
        transaction.setState("0");
        transaction.setChannel(channel);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        transaction.setDatetime(date);
        transactionMapper.insert(transaction);
        //修改资产状态，锁定待交易资产

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "发起交易申请成功", transactionId));
    }

    @Transactional
    public String getTransaction() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Transaction> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver", loginOrg)
                .eq("state", "0")
                .eq("channel", channel);
        List<Transaction> transactions = transactionMapper.selectList(wrapper);
        if (transactions.isEmpty()) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "没有需要确认的交易"));
        } else {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "获取待处理交易成功", transactions));
        }
    }

    @Transactional
    public String confirmTransaction(String transactionId,
                                     String checkCode) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");


        Transaction transaction = transactionMapper.selectById(transactionId);
        if (!channel.equals(transaction.getChannel())) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户操作通道错误", ErrorEntity.INVALID_ARGUMENT));
        }
        if (!loginOrg.equals(transaction.getReceiver())) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户是不交易目标", ErrorEntity.INVALID_ARGUMENT));
        }
        if (checkCode.equals("1")) {
            Asset asset = assetMapper.selectOne(new QueryWrapper<Asset>().eq("asset_code", transaction.getAssetCode()));
            if (!asset.getOwner().equals(transaction.getSender())) {
                transaction.setState("2");
                transactionMapper.updateById(transaction);
                /**
                 * 交易原子性判断需要验证
                 */
                response.setStatus(400);
                return JSON.toJSONString(new ErrorEntity(400, "交易信息已过期", ErrorEntity.INVALID_ARGUMENT));
            }
            transaction.setState("1");
            transactionMapper.updateById(transaction);
            asset.setOwner(transaction.getReceiver());
            assetMapper.updateById(asset);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "认证交易成功"));
        } else if (checkCode.equals("2")) {
            transaction.setState("2");
            transactionMapper.updateById(transaction);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "拒绝交易成功"));
        } else {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "确认码非法", ErrorEntity.INVALID_ARGUMENT));
        }
    }

    @Transactional
    public String crossChainTransaction(String sender,
                                        String receiver,
                                        String assetCode,
                                        String datetime,
                                        Double price,
                                        String targetChannel) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        //判断通道是否合法
        if (channel.equals(targetChannel)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "目标通道错误", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断交易对象是否存在
        if (!sender.equals(loginOrg)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "发起交易组织错误", ErrorEntity.INVALID_ARGUMENT));
        }
        if (peerMapper.selectOne(new QueryWrapper<Peer>().eq("organization_id", receiver)) == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "交易对象不存在", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断价格是否合法
        if (price < 0) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "价格非法", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断资产状态
        Asset asset = assetMapper.selectOne(new QueryWrapper<Asset>().eq("asset_code", assetCode));
        if (asset == null ||
                !asset.getOwner().equals(loginOrg) ||
                !asset.getState().equals("1") ||
                transactionMapper.selectOne(new QueryWrapper<Transaction>().eq("asset_code", assetCode).eq("state", "0")) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "资产不存在或者状态非法", ErrorEntity.INVALID_ARGUMENT));
        }

        //添加交易记录
        Integer count = transactionMapper.selectCount(null);
        count++;
        String transactionId1 = "TRANSACTION" + count;
        Transaction transaction1 = new Transaction();
        transaction1.setTransactionId(transactionId1);
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver + "@CHANNEL" + targetChannel);
        transaction1.setAssetCode(assetCode);
        transaction1.setPrice(price);
        transaction1.setState("c0");
        transaction1.setChannel(channel);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        transaction1.setDatetime(date);
        transactionMapper.insert(transaction1);

//        count++;
//        String transactionId2 = "TRANSACTION" + count.toString();
//        Transaction transaction2 = new Transaction();
//        transaction2.setTransactionId(transactionId2);
//        transaction2.setSender(sender+"@CHANNEL"+channel);
//        transaction2.setReceiver(receiver);
//        transaction2.setAssetId(assetId);
//        transaction2.setPrice(price);
//        transaction2.setState("c0");
//        transaction2.setChannel(targetChannel);
//        transactionMapper.insert(transaction2);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "发起跨链交易申请成功", transactionId1));
    }

    @Transactional
    public String getCrossChainTransaction() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Transaction> wrapper = new QueryWrapper<>();
        wrapper.eq("receiver", loginOrg + "@CHANNEL" + channel);
        wrapper.eq("state", "c0");
        List<Transaction> transactions = transactionMapper.selectList(wrapper);
        if (transactions.isEmpty()) {
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "没有需要确认的交易"));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "获取待处理跨链交易成功", transactions));
    }

    @Transactional
    public String confirmCrossChainTransaction(String transactionId,
                                               String checkCode) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");

        Transaction transaction = transactionMapper.selectById(transactionId);
        if (!transaction.getReceiver().equals(loginOrg + "@CHANNEL" + channel)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户不是交易目标", ErrorEntity.INVALID_ARGUMENT));
        }
        if (checkCode.equals("c1")) {
            //判断待交易资产状态
            Asset asset = assetMapper.selectOne(new QueryWrapper<Asset>().eq("asset_code", transaction.getAssetCode()));
            if (!asset.getOwner().equals(transaction.getSender())) {
                transaction.setState("c2");
                transactionMapper.updateById(transaction);
                /**
                 * 交易原子性判断需要验证
                 */
                response.setStatus(400);
                return JSON.toJSONString(new ErrorEntity(400, "交易信息已过期", ErrorEntity.INVALID_ARGUMENT));
            }
            transaction.setState("c1");
            transactionMapper.updateById(transaction);
            asset.setOwner(transaction.getReceiver() + "@CHANNEL" + channel);
            assetMapper.updateById(asset);
            Integer count = assetMapper.selectCount(null);
            count++;
            asset.setAssetId("ASSET" + count);
            asset.setOwner(loginOrg);
            asset.setChannel(channel);
            assetMapper.insert(asset);
            /**
             * 考虑增加一笔跨链交易的记录
             */

            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "认证交易成功"));
        } else if (checkCode.equals("c2")) {
            transaction.setState("c2");
            transactionMapper.updateById(transaction);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "拒绝交易成功"));
        } else {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "确认码非法", ErrorEntity.INVALID_ARGUMENT));
        }
    }

}
