package lab.b425.module2.dataSharing.utils;

import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class ChainCodeUtils {
    public static Contract getContract() {
        try {
            //获取相应参数
            Properties properties = new Properties();
            InputStream inputStream = ChainCodeUtils.class.getResourceAsStream("/fabric.config.properties");
            properties.load(inputStream);
            String networkConfigPath = properties.getProperty("networkConfigPath");
            String channelName = properties.getProperty("channelName");
            String contractName = properties.getProperty("contractName");
            //使用org1中的user1初始化一个网关wallet账户用于连接网络
            String certificatePath = properties.getProperty("certificatePath");
            X509Certificate certificate = readX509Certificate(Paths.get(certificatePath));

            String privateKeyPath = properties.getProperty("privateKeyPath");
            PrivateKey privateKey = getPrivateKey(Paths.get(privateKeyPath));

            Wallet wallet = Wallets.newInMemoryWallet();
            wallet.put("user1", Identities.newX509Identity("Org1MSP", certificate, privateKey));

            //根据connection.json 获取Fabric网络连接对象
            Gateway.Builder builder = Gateway.createBuilder()
                    .identity(wallet, "user1")
                    .networkConfig(Paths.get(networkConfigPath));
            //连接网关
            Gateway gateway = builder.connect();
            //获取通道
            Network network = gateway.getNetwork(channelName);
            //获取合约对象
            return network.getContract(contractName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("get contract error");
    }

    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)) {
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)) {
            return Identities.readPrivateKey(privateKeyReader);
        }
    }
}
