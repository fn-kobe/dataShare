package lab.b425.module2.dataSharing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lab.b425.module2.dataSharing.service.DataToBlockChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "数据上链")
public class DataToBlockChainController {
    private DataToBlockChainService dataToBlockChainService;

    @Autowired
    public void setDataToBlockChainService(DataToBlockChainService dataToBlockChainService) {
        this.dataToBlockChainService = dataToBlockChainService;
    }

    @ApiOperation(value = "添加采购记录", notes = "添加采购记录上链，并生产相应物料上链")
    @RequestMapping(value = "/addPurchase", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addPurchase(@RequestParam @ApiParam("采购编号") String purchaseCode,
                              @RequestParam @ApiParam("购入材料编号") String materialCode,
                              @RequestParam @ApiParam("描述") String description,
                              @RequestParam @ApiParam("拥有者") String owner,
                              @RequestParam @ApiParam("采购数量") Integer purchaseNumber,
                              @RequestParam @ApiParam("采购价格") Double price,
                              @RequestParam @ApiParam("采购日期") String datetime) {
        return dataToBlockChainService.addPurchase(purchaseCode, materialCode, description, owner, purchaseNumber, price, datetime);
    }

    @ApiOperation(value = "添加制造记录", notes = "添加制造记录上链，消耗并改变链上物料状态，生成产品信息")
    @RequestMapping(value = "/addMaking", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addMaking(@RequestParam @ApiParam("采购编号") String purchaseCode,
                            @RequestParam @ApiParam("购入材料编号") String materialCode,
                            @RequestParam @ApiParam("消耗数量") Integer costNumber,
                            @RequestParam @ApiParam("产品编号") String assetCode,
                            @RequestParam @ApiParam("品牌型号") String make,
                            @RequestParam @ApiParam("制造日期") String datetime,
                            @RequestParam @ApiParam("产品描述") String description) {
        return dataToBlockChainService.addMaking(purchaseCode, materialCode, costNumber, assetCode, make, datetime, description);
    }

    @ApiOperation(value = "添加结算记录", notes = "为采购的物料，添加结算纪录上链")
    @RequestMapping(value = "/addSettlement", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addSettlement(@RequestParam @ApiParam("采购编号") String purchaseCode,
                                @RequestParam @ApiParam("购入材料编号") String materialCode,
                                @RequestParam @ApiParam("结算方式") String settlementMethod,
                                @RequestParam @ApiParam("结算日期") String settlementDatetime) {
        return dataToBlockChainService.addSettlement(purchaseCode, materialCode, settlementMethod, settlementDatetime);
    }

    @ApiOperation(value = "添加仓储记录", notes = "为采购的物料，添加仓储纪录上链")
    @RequestMapping(value = "/addWarehouse", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addWarehouse(@RequestParam @ApiParam("采购编号") String purchaseCode,
                               @RequestParam @ApiParam("购入材料编号") String materialCode,
                               @RequestParam @ApiParam("储存类型") String warehouseReservoir,
                               @RequestParam @ApiParam("储存地点") String warehousePosition,
                               @RequestParam @ApiParam("储存日期") String datetime) {
        return dataToBlockChainService.addWarehouse(purchaseCode, materialCode, warehouseReservoir, warehousePosition, datetime);
    }

    @ApiOperation(value = "添加销售记录", notes = "添加零售记录上链，并修改产品的状态")
    @RequestMapping(value = "/addSale", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addSale(@RequestParam @ApiParam("销售产品编号") String assetCode,
                          @RequestParam @ApiParam("销售编号") String saleCode,
                          @RequestParam @ApiParam("客户编号") String clientCode,
                          @RequestParam @ApiParam("经销商编号") String dealerCode,
                          @RequestParam @ApiParam("销售价格") Double price,
                          @RequestParam @ApiParam("客户姓名") String ownerName,
                          @RequestParam @ApiParam("客户电话号码") String ownerPhone,
                          @RequestParam @ApiParam("销售日期") String datetime) {
        return dataToBlockChainService.addSale(assetCode, saleCode, clientCode, dealerCode, price, ownerName, ownerPhone, datetime);
    }

    @ApiOperation(value = "添加维修记录", notes = "添加维修记录上链")
    @RequestMapping(value = "/addRepair", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addRepair(@RequestParam @ApiParam("车辆识别号") String vinCode,
                            @RequestParam @ApiParam("信用代码") String trustCode,
                            @RequestParam @ApiParam("维修服务商") String serviceProviderCode,
                            @RequestParam @ApiParam("汽车牌照") String licensePlate,
                            @RequestParam @ApiParam("维修日期") String datetime) {
        return dataToBlockChainService.addRepair(vinCode, trustCode, serviceProviderCode, licensePlate, datetime);
    }
}
