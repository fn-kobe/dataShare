package lab.b425.module2.service;

import com.alibaba.fastjson.JSON;
import lab.b425.module2.chaincode.SampleChainCode;
import lab.b425.module2.entity.ResponseEntity;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 暂时弃用 尚未分开两个模块的service
 */
@Service
public class DataService {
     Contract contract = SampleChainCode.contract;

    public String queryData() throws ContractException, InterruptedException, TimeoutException {
        byte[] queryAllAssets = contract.createTransaction("GetAllAssets").submit();
        String ans =  new String(queryAllAssets, StandardCharsets.UTF_8);

        return JSON.toJSON(
                new ResponseEntity()
                        .setCode(ResponseEntity.OK)
                        .setMessage(ans)).toString();
    }

    public String transfer(String asset, String id) throws ContractException, InterruptedException, TimeoutException {
        contract.createTransaction("TransferAsset").submit(asset, id);
         return JSON.toJSON(
                new ResponseEntity()
                        .setCode(ResponseEntity.OK)
                        .setMessage("转账交易成功")).toString();
    }

    public void m() throws ContractException, InterruptedException, TimeoutException {
        contract.createTransaction("InitLedger").submit();
        //调用合约查询所有资产
        byte[] queryAllAssets = contract.evaluateTransaction("GetAllAssets");
        System.out.println("所有资产：" + new String(queryAllAssets, StandardCharsets.UTF_8));

        //交易资产
        contract.createTransaction("TransferAsset").submit("asset6", "Tomoko");

        //新建资产
        contract.createTransaction("CreateAsset").submit("asset7", "black", "1", "mfl", "100");

        //查询更新后的资产
        byte[] queryAllAssetsAfter = contract.evaluateTransaction("GetAllAssets");
        String res = new String(queryAllAssetsAfter, StandardCharsets.UTF_8);
        System.out.println(res);

        //转换成数组备用
//            List<Map<String,String>> listObjectFir = (List<Map<String,String>>) JSONArray.parse(res);
//            System.out.println(listObjectFir);
    }
}
