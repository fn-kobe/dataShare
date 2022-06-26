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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * 数据上链服务类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Service
public class DataToBlockChainService {
    /**
     * Mapper及HTTP相关注册
     */
    private AssetMapper assetMapper;
    private MakingMapper makingMapper;
    private MaterialMapper materialMapper;
    private PurchaseMapper purchaseMapper;
    private RepairMapper repairMapper;
    private SaleMapper saleMapper;
    private SettlementMapper settlementMapper;
    private WarehouseMapper warehouseMapper;
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
    private void setRepairMapper(RepairMapper repairMapper) {
        this.repairMapper = repairMapper;
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
    private void setWarehouseMapper(WarehouseMapper warehouseMapper) {
        this.warehouseMapper = warehouseMapper;
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
    public String addPurchase(String purchaseCode,
                              String materialCode,
                              String description,
                              String owner,
                              Integer purchaseNumber,
                              Double price,
                              String datetime) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        //判断 purchaseCode， materialCode唯一性
        if (purchaseMapper.selectOne(new QueryWrapper<Purchase>().eq("purchase_code", purchaseCode)) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "采购编号重复", ErrorEntity.INVALID_ARGUMENT));
        }
        if (materialMapper.selectOne(new QueryWrapper<Material>().eq("material_code", materialCode)) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "物料编号重复", ErrorEntity.INVALID_ARGUMENT));
        }
        //判断数量，价格合法性
        if (purchaseNumber <= 0) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "采购数量非法", ErrorEntity.INVALID_ARGUMENT));
        }
        if (price <= 0) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "采购价格非法", ErrorEntity.INVALID_ARGUMENT));
        }

        //添加采购
        Integer purchaseCount = purchaseMapper.selectCount(null);
        purchaseCount++;
        String PurchaseId = "PURCHASE" + purchaseCount;
        Purchase purchase = new Purchase();
//        purchase.setPurchaseId(PurchaseId);
        purchase.setPurchaseCode(purchaseCode);
        purchase.setDescription(description);
        purchase.setOwner(owner);
        purchase.setPurchaseNumber(purchaseNumber);
        purchase.setPrice(price);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        purchase.setDatetime(date);
        purchase.setUploader(loginOrg);
        purchase.setMaterialCode(materialCode);
        purchase.setChannel(channel);
        purchaseMapper.insert(purchase);

        //添加物料
        Material material = new Material();
        Integer materialCount = materialMapper.selectCount(null);
        materialCount++;
        String materialId = "MATERIAL" + materialCount;
//        material.setMaterialId(materialId);
        material.setMaterialCode(materialCode);
        material.setDescription(description);
        material.setPurchaseCode(purchaseCode);
        material.setOwner(owner);
        material.setPurchaseNumber(purchaseNumber);
        material.setCurrentNumber(purchaseNumber);
        material.setPrice(price);
        material.setUploader(loginOrg);
        material.setChannel(channel);
        materialMapper.insert(material);

        /*try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "上传采购成功", PurchaseId));
    }

    @Transactional
    public String addMaking(String purchaseCode,
                            String materialCode,
                            Integer costNumber,
                            String assetCode,
                            String make,
                            String datetime,
                            String description) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");

        //添加制造记录
        Integer makingCount = makingMapper.selectCount(null);
        String makingId = "MAKING" + makingCount.toString();
        Making making = new Making();
        making.setMakingId(makingId);
        making.setPurchaseCode(purchaseCode);
        making.setMaterialCode(materialCode);
        making.setCostNumber(costNumber);
        making.setVinCode(assetCode);
        making.setMake(make);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        making.setDatetime(date);
        making.setUploader(loginOrg);
        making.setChannel(channel);
        makingMapper.insert(making);

        //消耗物料
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("material_code", materialCode);
        Material material = materialMapper.selectOne(wrapper);
        //判断采购编号和物料编号合法
        if (material == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "物料编号为空", ErrorEntity.INVALID_ARGUMENT));
        } else if (!material.getPurchaseCode().equals(purchaseCode)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "物料编号非法", ErrorEntity.INVALID_ARGUMENT));
        }
        if (costNumber <= 0 || material.getCurrentNumber() - costNumber < 0) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "物料消耗数量非法", ErrorEntity.INVALID_ARGUMENT));
        }
        Integer remains = material.getCurrentNumber() - costNumber;
        material.setCurrentNumber(remains);
        materialMapper.updateById(material);

        //判断同通道内assetCode是否重复
        if (assetMapper.selectOne(new QueryWrapper<Asset>().eq("asset_code", assetCode).eq("channel", channel)) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "产品编号重复", ErrorEntity.INVALID_ARGUMENT));
        }

        //添加产品
        Integer assetCount = assetMapper.selectCount(null);
        assetCount++;
        String assetId = "ASSET" + assetCount;
        Asset asset = new Asset();
        asset.setAssetId(assetId);
        asset.setState("1");
        asset.setAssetCode(assetCode);
        asset.setMake(make);
        asset.setNumber(1);
        asset.setDescription(description);
        asset.setChannel(channel);
        asset.setOwner(loginOrg);
        assetMapper.insert(asset);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "生产记录成功", makingId));
    }

    @Transactional
    public String addSettlement(String purchaseCode,
                                String materialCode,
                                String settlementMethod,
                                String settlementDatetime) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");

        //判断结算记录是否存在，或则采购编号和物料编号是否合法
        if (settlementMapper.selectOne(new QueryWrapper<Settlement>().eq("purchase_code", purchaseCode)) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "改采购编号已添加结算记录", ErrorEntity.INVALID_ARGUMENT));
        }
        Purchase purchase = purchaseMapper.selectOne(new QueryWrapper<Purchase>().eq("purchase_code", purchaseCode));
        if (purchase.getPurchaseCode() == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "采购编号不存在", ErrorEntity.INVALID_ARGUMENT));
        }
        if (!purchase.getMaterialCode().equals(materialCode)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "材料编号对应错误", ErrorEntity.INVALID_ARGUMENT));
        }


        //添加结算记录
        Integer count = settlementMapper.selectCount(null);
        count++;
        String settlementId = "SETTLEMENT" + count;
        Settlement settlement = new Settlement();
        settlement.setSettlementId(settlementId);
        settlement.setPurchaseCode(purchaseCode);
        settlement.setMaterialCode(materialCode);
        settlement.setUploader(loginOrg);
        settlement.setChannel(channel);
        settlement.setSettlementMethod(settlementMethod);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(settlementDatetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        settlement.setSettlementDatetime(date);
        settlementMapper.insert(settlement);
        //更新物料结算信息
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("material_code", materialCode);
        Material material = materialMapper.selectOne(wrapper);
        material.setSettlementMethod(settlementMethod);
        material.setSettlementDatetime(date);
        materialMapper.updateById(material);

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "添加结算记录成功", settlementId));
    }

    @Transactional
    public String addWarehouse(String purchaseCode,
                               String materialCode,
                               String warehouseReservoir,
                               String warehousePosition,
                               String datetime) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        //判断结算记录是否存在，或则采购编号和物料编号是否合法
        if (warehouseMapper.selectOne(new QueryWrapper<Warehouse>().eq("purchase_code", purchaseCode)) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "该编号采购已添加仓储记录", ErrorEntity.INVALID_ARGUMENT));
        }
        Purchase purchase = purchaseMapper.selectOne(new QueryWrapper<Purchase>().eq("purchase_code", purchaseCode));
        if (purchase.getPurchaseCode() == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "采购编号不存在", ErrorEntity.INVALID_ARGUMENT));
        }
        if (!purchase.getMaterialCode().equals(materialCode)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "物料编号对应错误", ErrorEntity.INVALID_ARGUMENT));
        }


        //添加仓储记录
        Integer count = warehouseMapper.selectCount(null);
        count++;
        String warehouseId = "WAREHOUSE" + count;
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(warehouseId);
        warehouse.setPurchaseCode(purchaseCode);
        warehouse.setMaterialCode(materialCode);
        warehouse.setWarehouseReservoir(warehouseReservoir);
        warehouse.setWarehousePosition(warehousePosition);
        warehouse.setChannel(channel);
        warehouse.setUploader(loginOrg);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        warehouse.setDatetime(date);
        warehouseMapper.insert(warehouse);
        //更新物料仓库信息
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("material_code", materialCode);
        Material material = materialMapper.selectOne(wrapper);
        material.setWarehouseReservoir(warehouseReservoir);
        material.setWarehousePosition(warehousePosition);
        materialMapper.updateById(material);

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "添加仓储记录成功", warehouseId));
    }

    @Transactional
    public String addSale(String assetCode,
                          String saleCode,
                          String clientCode,
                          String dealerCode,
                          Double price,
                          String ownerName,
                          String ownerPhone,
                          String datetime) {
        //判断销售记录是否重复
        if (saleMapper.selectOne(new QueryWrapper<Sale>().eq("sale_code", saleCode)) != null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "销售记录已存在", ErrorEntity.INVALID_ARGUMENT));
        }


        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        Integer count = saleMapper.selectCount(null);
        count++;
        String saleId = "SALE" + count;
        QueryWrapper<Asset> wrapper = new QueryWrapper<>();
        wrapper.eq("asset_code", assetCode);
        //判断待售产品是否合法
        Asset saleAsset = assetMapper.selectOne(wrapper);
        if (!saleAsset.getOwner().equals(loginOrg) || !saleAsset.getState().equals("1")) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "待销售产品非法", ErrorEntity.INVALID_ARGUMENT));
        }
        saleAsset.setState("已零售");
        assetMapper.updateById(saleAsset);
        Sale sale = new Sale();
        sale.setSaleId(saleId);
        sale.setVinCode(assetCode);
        sale.setSaleCode(saleCode);
        sale.setClientCode(clientCode);
        sale.setDealerCode(dealerCode);
        sale.setPrice(price);
        sale.setOwnerName(ownerName);
        sale.setOwnerPhone(ownerPhone);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        sale.setDatetime(date);
        sale.setUploader(loginOrg);
        sale.setChannel(channel);
        saleMapper.insert(sale);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "添加销售记录成功", saleId));
    }

    @Transactional
    public String addRepair(String vinCode,
                            String trustCode,
                            String serviceProviderCode,
                            String licensePlate,
                            String datetime) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        Integer count = repairMapper.selectCount(null);
        count++;
        String repairId = "REPAIR" + count;
        Repair repair = new Repair();
        repair.setRepairId(repairId);
        repair.setVinCode(vinCode);
        repair.setServiceProviderCode(serviceProviderCode);
        repair.setLicensePlate(licensePlate);
        repair.setTrustCode(trustCode);
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "日期格式错误", ErrorEntity.INVALID_ARGUMENT));
        }
        repair.setDatetime(date);
        repair.setUploader(loginOrg);
        repair.setChannel(channel);
        repairMapper.insert(repair);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "添加维修记录成功", repairId));
    }



}
