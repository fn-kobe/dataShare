package lab.b425.module2.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lab.b425.module2.service.AccessControlService;
import lab.b425.module2.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

/**
 * 路由类 数据业务
 */
@RestController
public class DataController {
    private DataService dataService;
    private AccessControlService accessControlService;

    @Autowired
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Autowired
    public void setAccessControlService(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @ApiOperation("添加资产")
    @RequestMapping(value = "/addAsset", method = RequestMethod.POST)
    @ResponseBody
    public String addAsset(@RequestParam @ApiParam("资产id") String id,
                           @RequestParam @ApiParam("资产类别") String type,
                           @RequestParam @ApiParam("状态") String state,
//                           @RequestParam @ApiParam("资产流向下一组件") String componentOf,
                           @RequestParam @ApiParam("构成组件") String component,
                           @RequestParam @ApiParam("数量") Integer quantity,
                           @RequestParam @ApiParam("价格") Double value,
//                           @RequestParam @ApiParam("持有者id") String holderId,
//                           @RequestParam @ApiParam("上一持有者id") String exHolderId,
                           @RequestParam @ApiParam("制造者id") String producerId,
                           @RequestParam @ApiParam("生产日期") String producedDate,
                           @RequestParam @ApiParam("描述") String description,
                           @RequestParam @ApiParam("仓储位置") String warehouse_position) throws ParseException {

        return accessControlService.addAsset(id, type, state, component, quantity, value, producerId, producedDate, description, warehouse_position);
    }

    @ApiOperation("发起交易申请")
    @RequestMapping(value = "/requestTransaction", method = RequestMethod.POST)
    @ResponseBody
    public String sendTransaction(@RequestParam @ApiParam("发起者组织") String from,
                                  @RequestParam @ApiParam("目标对象组织") String to,
                                  @RequestParam @ApiParam("资产id") String assetId,
                                  @RequestParam @ApiParam("交易价格") Double price,
                                  @RequestParam @ApiParam("交易数量") Integer quantity) {

        return accessControlService.requestTransaction(from, to, assetId, price, quantity);
    }

    @ApiOperation("跨链交易申请")
    @RequestMapping(value = "/crossChainTransfer",method = RequestMethod.POST)
    @ResponseBody
    public String crossChainTransfer(@RequestParam @ApiParam("发起者组织") String from,
                                     @RequestParam @ApiParam("目标对象组织") String to,
                                     @RequestParam @ApiParam("资产id") String assetId,
                                     @RequestParam @ApiParam("交易价格") Double price,
                                     @RequestParam @ApiParam("交易数量") Integer quantity) {

        return accessControlService.crossChainTransfer(from, to, assetId, price, quantity);
    }

    //待解决，以次只能拉取一条
    @ApiOperation("拉取待审批记录")
    @RequestMapping(value = "/getTransactionRequest", method = RequestMethod.GET)
    @ResponseBody
    public String getTransactionRequest() {

        return accessControlService.getTransactionRequest();
    }

    //待解决，可能需要拆分
    @ApiOperation("审批交易申请并将执行")
    @RequestMapping(value = "/approveTransactionRequest", method = RequestMethod.POST)
    @ResponseBody
    public String approveTransactionRequest(@RequestParam @ApiParam("记录id") String recordId,
                                            @RequestParam @ApiParam("确认结果") String checkCode,
                                            @RequestParam @ApiParam(value = "系统崩溃点，测试使用",defaultValue = "0") Integer crashPoint) {

        return accessControlService.approveTransactionRequest(recordId, checkCode, crashPoint);
    }

    //待解决
    @ApiOperation("内部调配")
    @RequestMapping(value = "/internalTransaction", method = RequestMethod.POST)
    @ResponseBody
    public String internalTransaction(@RequestParam @ApiParam("内部交易发起节点") String from,
                                      @RequestParam @ApiParam("内部交易接受节点") String to,
                                      @RequestParam @ApiParam("资产类型") String assetType,
                                      @RequestParam @ApiParam("资产数量") Integer quantity) {

        return accessControlService.internalTransaction(from, to, assetType, quantity);
    }

    //待解决
    @ApiOperation("生产加工")
    @RequestMapping(value = "/manufacturer", method = RequestMethod.POST)
    @ResponseBody
    public String manufacturer(@RequestParam @ApiParam("资产id") String id,
                               @RequestParam @ApiParam("资产类别") String type,
                               @RequestParam @ApiParam("资产状态") String state,
                               @RequestParam @ApiParam("资产数量") Integer quantity,
                               @RequestParam @ApiParam("资产价格") Double value,
                               @RequestParam @ApiParam("生产日期") String ProducedDate,
                               @RequestParam @ApiParam("资产描述") String description,
                               @RequestParam @ApiParam("资产仓库位置") String warehouse_position,
                               @RequestParam @ApiParam("组成资产id和数量") String... assetIdAndNums) throws ParseException {

        return accessControlService.manufacturer(id, type, state, quantity, value, ProducedDate, description, warehouse_position, assetIdAndNums);
    }



    //待解决
    @ApiOperation("查询资产接口")
    @RequestMapping(value = "/queryAsset", method = RequestMethod.POST)
    @ResponseBody
    public String queryAsset(@RequestParam(required = false) @ApiParam("资产类型") String assetType,
                             @RequestParam @ApiParam("所属组织id") String orgId,
                             @RequestParam(required = false) @ApiParam("选取的参数") String... params) {

        return accessControlService.queryAsset(assetType, orgId, params);
    }

    //待解决
    @ApiOperation("查询记录接口")
    @RequestMapping(value = "/queryRecord", method = RequestMethod.POST)
    @ResponseBody
    public String queryRecordByOrg(@RequestParam(required = false) @ApiParam("记录类型") String recordType,
                                   @RequestParam @ApiParam("所属组织id") String orgId) {

        return accessControlService.queryRecord(recordType, orgId);
    }
}
