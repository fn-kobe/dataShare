package lab.b425.module2.dataSharing.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lab.b425.module2.dataSharing.entity.*;
import lab.b425.module2.dataSharing.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Service
public class DataQueryService {
    /**
     * Mapper及HTTP相关注册
     */
    private AssetMapper assetMapper;
    private MakingMapper makingMapper;
    private MaterialMapper materialMapper;
    private PurchaseMapper purchaseMapper;
    private SaleMapper saleMapper;
    private SettlementMapper settlementMapper;
    private TransactionMapper transactionMapper;
    private WarehouseMapper warehouseMapper;
    private RepairMapper repairMapper;
    private AuthorityMapper authorityMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Autowired
    private void setAssetMapper(AssetMapper assetMapper) {
        this.assetMapper = assetMapper;
    }

    @Autowired
    private void setMakingMapper(MakingMapper makingMapper) {
        this.makingMapper = makingMapper;
    }

    @Autowired
    private void setMaterialMapper(MaterialMapper materialMapper) {
        this.materialMapper = materialMapper;
    }

    @Autowired
    private void setPurchaseMapper(PurchaseMapper purchaseMapper) {
        this.purchaseMapper = purchaseMapper;
    }

    @Autowired
    private void setSaleMapper(SaleMapper saleMapper) {
        this.saleMapper = saleMapper;
    }

    @Autowired
    private void setSettlementMapper(SettlementMapper settlementMapper) {
        this.settlementMapper = settlementMapper;
    }

    @Autowired
    private void setTransactionMapper(TransactionMapper transactionMapper) {
        this.transactionMapper = transactionMapper;
    }

    @Autowired
    private void setWarehouseMapper(WarehouseMapper warehouseMapper) {
        this.warehouseMapper = warehouseMapper;
    }

    @Autowired
    private void setRepairMapper(RepairMapper repairMapper) {
        this.repairMapper = repairMapper;
    }

    @Autowired
    private void setAuthorityMapper(AuthorityMapper authorityMapper) {
        this.authorityMapper = authorityMapper;
    }

    @Autowired
    private void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    private void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String queryAsset(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        if (orgId.equals("all")) {
            List<Asset> assetList = assetMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部资产成功", assetList));
        } else {
            wrapper.eq("owner", orgId);
            List<Asset> assetList = assetMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户资产成功", assetList));
        }
    }

    public String queryAssetByCode(String assetCode) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("asset_code", assetCode)
                .eq("channel", channel);
        Asset asset = assetMapper.selectOne(wrapper);
        if (asset == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "查询不到相关记录", ErrorEntity.INVALID_ARGUMENT));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询指定资产成功", asset));
    }

    public String queryMaking(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Making> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        if (orgId.equals("all")) {
            List<Making> makingList = makingMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部制造记录成功", makingList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Making> makingList = makingMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户制造记录成功", makingList));
        }
    }

    public String queryMakingByAssetCode(String assetCode) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Making> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel)
                .eq("vin_code", assetCode);
        Making making = makingMapper.selectOne(wrapper);
        if (making == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "查询不到相关记录", ErrorEntity.INVALID_ARGUMENT));
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询制造记录成功", making));
    }

    public String queryMaterial(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        wrapper.select(Material.class, tableFieldInfo -> !tableFieldInfo.getColumn().equals("price"));
        if (orgId.equals("all")) {
            List<Material> materialList = materialMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部材料成功", materialList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Material> materialList = materialMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户材料成功", materialList));
        }
    }

    @Transactional
    public String queryMaterialByCode(String materialCode) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("material_code", materialCode).eq("channel", channel);
        Material material = materialMapper.selectOne(wrapper);

        //判断记录是否存在
        if (material == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "查询不到相关记录", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断是否是本组织
        if (!material.getUploader().equals(loginOrg)) {
            //判断用户是否拥有权限
            QueryWrapper<Authority> authorityWrapper = new QueryWrapper<>();
            authorityWrapper.eq("target_table", "material")
                    .eq("target_org", material.getUploader())
                    .eq("org_id", loginOrg);
            List<Authority> authorityList = authorityMapper.selectList(authorityWrapper);
            boolean haveAuthority = false;
            for (Authority authority : authorityList) {
                if (new Date().compareTo(authority.getExpirationTime()) >= 0) {
                    //删除过期权限
                    authorityMapper.deleteById(authority);

                } else {
                    haveAuthority = true;
                }
            }
            if (!haveAuthority) {
                material.setPrice(null);
            }
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询材料成功", material));
    }

    public String queryPurchase(String orgId) {

        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Purchase> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        wrapper.select(Purchase.class, info -> !info.getColumn().equals("price"));
        if (orgId.equals("all")) {
            List<Purchase> purchaseList = purchaseMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部采购记录成功", purchaseList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Purchase> purchaseList = purchaseMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户采购记录成功", purchaseList));
        }

    }

    @Transactional
    public String queryPurchaseByCode(String purchaseCode) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Purchase> wrapper = new QueryWrapper<>();
        wrapper.eq("purchase_code", purchaseCode).eq("channel", channel);
        Purchase purchase = purchaseMapper.selectOne(wrapper);

        //判断记录是否存在
        if (purchase == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "查询不到相关记录", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断是否是本组织
        if (!purchase.getUploader().equals(loginOrg)) {
            //判断用户是否拥有权限
            QueryWrapper<Authority> authorityWrapper = new QueryWrapper<>();
            authorityWrapper.eq("target_table", "purchase")
                    .eq("target_org", purchase.getUploader())
                    .eq("org_id", loginOrg);
            List<Authority> authorityList = authorityMapper.selectList(authorityWrapper);
            boolean haveAuthority = false;
            for (Authority authority : authorityList) {
                if (new Date().compareTo(authority.getExpirationTime()) >= 0) {
                    //删除过期权限
                    authorityMapper.deleteById(authority);
                } else {
                    haveAuthority = true;
                }
            }
            if (!haveAuthority) {
                purchase.setPrice(null);
            }
        }

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询采购记录成功", purchase));
    }


    public String querySettlement(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Settlement> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        if (orgId.equals("all")) {
            List<Settlement> settlementList = settlementMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部结算记录成功", settlementList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Settlement> settlementList = settlementMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户结算记录成功", settlementList));
        }
    }

    public String queryWareHouse(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Warehouse> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        if (orgId.equals("all")) {
            List<Warehouse> warehouseList = warehouseMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部仓储记录成功", warehouseList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Warehouse> warehouseList = warehouseMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户仓储记录成功", warehouseList));
        }
    }

    public String queryRepair(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Repair> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        if (orgId.equals("all")) {
            List<Repair> repairList = repairMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部维修记录成功", repairList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Repair> repairList = repairMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户维修记录成功", repairList));
        }
    }

    public String querySale(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Sale> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel);
        wrapper.select(Sale.class, info -> !info.getColumn().equals("price"));
        if (orgId.equals("all")) {
            List<Sale> saleList = saleMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部销售记录成功", saleList));
        } else {
            wrapper.eq("uploader", orgId);
            List<Sale> saleList = saleMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部销售记录成功", saleList));
        }
    }

    @Transactional
    public String querySaleByCode(String saleCode) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Sale> wrapper = new QueryWrapper<>();
        wrapper.eq("sale_code", saleCode).eq("channel", channel);
        Sale sale = saleMapper.selectOne(wrapper);
        //判断记录是否存在
        if (sale == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "查询不到相关记录", ErrorEntity.INVALID_ARGUMENT));
        }

        //判断是否是本组织
        if (!sale.getUploader().equals(loginOrg)) {
            //判断用户是否拥有权限
            QueryWrapper<Authority> authorityWrapper = new QueryWrapper<>();
            authorityWrapper.eq("target_table", "sale")
                    .eq("target_org", sale.getUploader())
                    .eq("org_id", loginOrg);
            List<Authority> authorityList = authorityMapper.selectList(authorityWrapper);
            boolean haveAuthority = false;
            for (Authority authority : authorityList) {
                if (new Date().compareTo(authority.getExpirationTime()) >= 0) {
                    //删除过期权限
                    authorityMapper.deleteById(authority);
                } else {
                    haveAuthority = true;
                }
            }
            if (!haveAuthority) {
                sale.setPrice(null);
            }
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询销售记录成功", sale));
    }

    public String queryTransaction(String orgId) {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Transaction> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel).eq("state", "1");
        wrapper.select(Transaction.class, info -> !info.getColumn().equals("price"));
        if (orgId.equals("all")) {
            List<Transaction> transactionList = transactionMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部交易记录成功", transactionList));
        } else {
            wrapper.eq("sender", orgId);
            List<Transaction> transactionList = transactionMapper.selectList(wrapper);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询用户交易记录成功", transactionList));
        }

    }

    @Transactional
    public String queryTransactionById(String transactionId) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Transaction> wrapper = new QueryWrapper<>();
        wrapper.eq("transaction_id", transactionId).eq("channel", channel);
        Transaction transaction = transactionMapper.selectOne(wrapper);
        //判断结果是否为空
        if (transaction == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "查询不到相关记录", ErrorEntity.INVALID_ARGUMENT));
        }
        //判断是否是本组织交易记录
        if (!(transaction.getSender().equals(loginOrg) || transaction.getReceiver().equals(loginOrg))) {
            //判断用户是否拥有权限
            boolean haveAuthority = false;
            //从发起者获得权限
            QueryWrapper<Authority> authorityWrapper = new QueryWrapper<>();
            authorityWrapper.eq("target_table", "transaction")
                    .eq("target_org", transaction.getSender())
                    .eq("org_id", loginOrg);
            List<Authority> authorityList = authorityMapper.selectList(authorityWrapper);
            for (Authority authority : authorityList) {
                if (new Date().compareTo(authority.getExpirationTime()) >= 0) {
                    //删除过期权限
                    authorityMapper.deleteById(authority);
                } else {
                    haveAuthority = true;
                }
            }
            //从接收者获得权限
            QueryWrapper<Authority> authorityWrapper2 = new QueryWrapper<>();
            authorityWrapper.eq("target_table", "transaction")
                    .eq("target_org", transaction.getReceiver())
                    .eq("org_id", loginOrg);
            List<Authority> authorityList2 = authorityMapper.selectList(authorityWrapper2);
            for (Authority authority : authorityList2) {
                if (new Date().compareTo(authority.getExpirationTime()) >= 0) {
                    //删除过期权限
                    authorityMapper.deleteById(authority);
                } else {
                    haveAuthority = true;
                }
            }
            if (!haveAuthority) {
                transaction.setPrice(null);
            }
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询交易记录成功", transaction));
    }

    public String queryCrossChainTransaction() {
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Transaction> wrapper = new QueryWrapper<>();
        wrapper.eq("channel", channel).eq("state", "c1");
        wrapper.select(Transaction.class, info -> !info.getColumn().equals("price"));
        List<Transaction> transactionList = transactionMapper.selectList(wrapper);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询全部交易跨链记录成功", transactionList));
    }

    @Transactional
    public String resetAll() {
        assetMapper.delete(null);
        authorityMapper.delete(null);
        makingMapper.delete(null);
        materialMapper.delete(null);
        purchaseMapper.delete(null);
        repairMapper.delete(null);
        saleMapper.delete(null);
        settlementMapper.delete(null);
        transactionMapper.delete(null);
        warehouseMapper.delete(null);
        return JSON.toJSONString(new ResponseEntity(200, "重置环境成功"));
    }


}
