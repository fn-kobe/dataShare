package lab.b425.module2.chaincode;

import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

//样例 通过手动输入来配置channel和chaincode，也可以通过.properties文件来配置
public class Sample {
    public static void main(String[] args) throws IOException {

        // 载入一个存在的持有用户身份的钱包，用于访问fabric网络
        Path walletDirectory = Paths.get("wallet");
        Wallet wallet = Wallets.newFileSystemWallet(walletDirectory);

        //Path为fabric网络配置文件的路径
        Path networkConfigFile = Paths.get("connection.json");

        // 配置gateway连接用于访问fabric网络（用户和网络配置文件）
        Gateway.Builder builder = Gateway.createBuilder()
                .identity(wallet, "user1")
                .networkConfig(networkConfigFile);

        // 创建一个gateway连接
        try (Gateway gateway = builder.connect()) {

            //根据gateway获取指定的通道网络
            Network network = gateway.getNetwork("mychannel");
            //根据chaincode名称从通道网络中获取智能合约
            Contract contract = network.getContract("fabcar");

            // 提交事务 存储到账本
            byte[] createCarResult = contract.createTransaction("createCar")
                    .submit("CAR10", "VW", "Polo", "Grey", "Mary");
            System.out.println(new String(createCarResult, StandardCharsets.UTF_8));

            // 从账本中查询状态
            byte[] queryAllCarsResult = contract.evaluateTransaction("queryAllCars");
            System.out.println(new String(queryAllCarsResult, StandardCharsets.UTF_8));

        } catch (ContractException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
