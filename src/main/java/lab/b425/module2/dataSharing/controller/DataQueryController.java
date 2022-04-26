package lab.b425.module2.dataSharing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lab.b425.module2.dataSharing.service.DataQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "数据查询")
public class DataQueryController {
    private DataQueryService dataQueryService;

    @Autowired
    public void setDataQueryService(DataQueryService dataQueryService) {
        this.dataQueryService = dataQueryService;
    }


    @ApiOperation(value = "查询资产", notes = "查询所有当前通道内指定用户的资产")
    @RequestMapping(value = "/queryAsset", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryAsset(@RequestParam @ApiParam( "组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryAsset(orgId);
    }

    @ApiOperation(value = "查询指定资产", notes = "按编号精确查询具体条目的资产")
    @RequestMapping(value = "/queryAssetByCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryAssetByCode(@RequestParam @ApiParam("资产编号") String assetCode) {
        return dataQueryService.queryAssetByCode(assetCode);
    }

    @ApiOperation(value = "查询制造记录", notes = "查询所有当前通道内指定用户的所有制造记录")
    @RequestMapping(value = "/queryMaking", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryMaking(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryMaking(orgId);
    }

    @ApiOperation(value = "查询指定制造记录", notes = "按编号精确查询具体条目的制造记录")
    @RequestMapping(value = "/queryMakingByAssetCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryMakingByAssetCode(@RequestParam @ApiParam("资产编号") String assetCode) {
        return dataQueryService.queryMakingByAssetCode(assetCode);
    }

    @ApiOperation(value = "查询物料记录", notes = "查询所有当前通道内指定用户的所有物料记录，敏感信息价格除外")
    @RequestMapping(value = "/queryMaterial", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryMaterial(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryMaterial(orgId);
    }

    @ApiOperation(value = "查询指定物料记录", notes = "按编号精确查询具体条目的物料记录，非权限用户不能获得价格信息")
    @RequestMapping(value = "/queryMaterialByCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryMaterialByCode(@RequestParam @ApiParam("材料编号") String materialCode) {
        return dataQueryService.queryMaterialByCode(materialCode);
    }

    @ApiOperation(value = "查询采购记录", notes = "查询所有当前通道内指定用户的所有采购记录，敏感信息价格除外")
    @RequestMapping(value = "/queryPurchase", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryPurchase(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryPurchase(orgId);
    }

    @ApiOperation(value = "查询指定采购记录", notes = "按编号精确查询具体条目的采购记录，非权限用户不能获得价格信息")
    @RequestMapping(value = "/queryPurchaseByCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryPurchaseByCode(@RequestParam @ApiParam("购入编号") String purchaseCode) {
        return dataQueryService.queryPurchaseByCode(purchaseCode);
    }

    @ApiOperation(value = "查询结算记录", notes = "查询所有当前通道内指定用户的所有结算记录")
    @RequestMapping(value = "/querySettlement", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String querySettlement(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.querySettlement(orgId);
    }

    @ApiOperation(value = "查询仓储记录", notes = "查询所有当前通道内指定用户的全部仓储记录")
    @RequestMapping(value = "/queryWareHouse", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryWareHouse(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryWareHouse(orgId);
    }

    @ApiOperation(value = "查询维修记录", notes = "查询所有当前通道内指定用户的全部维修记录")
    @RequestMapping(value = "/queryRepair", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryRepair(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryRepair(orgId);
    }

    @ApiOperation(value = "查询销售记录", notes = "查询所有当前通道内指定用户的全部销售记录，敏感信息价格除外")
    @RequestMapping(value = "/querySale", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String querySale(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.querySale(orgId);
    }

    @ApiOperation(value = "查询指定销售记录", notes = "按编号精确查询具体条目的零售记录，非权限用户不能获得价格信息")
    @RequestMapping(value = "/querySaleByCode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String querySaleByCode(@RequestParam @ApiParam("销售编号") String saleCode) {
        return dataQueryService.querySaleByCode(saleCode);
    }

    @ApiOperation(value = "查询交易记录", notes = "查询所有当前通道内指定用户的全部交易记录，敏感信息价格除外")
    @RequestMapping(value = "/queryTransaction", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryTransaction(@RequestParam @ApiParam("组织id,值为all时查询全通道") String orgId) {
        return dataQueryService.queryTransaction(orgId);
    }

    @ApiOperation(value = "查询指定交易记录", notes = "按ID精确查询具体条目的交易记录，非权限用户不能获得价格信息")
    @RequestMapping(value = "/queryTransactionById", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryTransactionById(@RequestParam @ApiParam("交易id") String transactionId) {
        return dataQueryService.queryTransactionById(transactionId);
    }

    @ApiOperation(value = "查询跨链交易记录", notes = "查询所有当前通道内的全部跨链交易记录，敏感信息价格除外")
    @RequestMapping(value = "/queryTransactionCrossChain", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryCrossChainTransaction() {
        return dataQueryService.queryCrossChainTransaction();
    }

}
