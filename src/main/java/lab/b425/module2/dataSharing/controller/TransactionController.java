package lab.b425.module2.dataSharing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lab.b425.module2.dataSharing.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "交易管理")
public class TransactionController {

    private TransactionService transactionService;

    @Autowired
    public void setTransactionService(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @ApiOperation(value = "发起交易",notes = "以登录用户组织的身份发起一笔交易")
    @RequestMapping(value = "/addTransaction", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addTransaction(@RequestParam @ApiParam("发起者组织") String sender,
                                 @RequestParam @ApiParam("购入者组织") String receiver,
                                 @RequestParam @ApiParam("交易产品编号") String assetCode,
                                 @RequestParam @ApiParam("交易时间") String datetime,
                                 @RequestParam @ApiParam("交易价格") Double price) {
        return transactionService.addTransaction(sender, receiver, assetCode, datetime, price);
    }

    @ApiOperation(value = "获取待处理交易",notes = "获取交易目标为登录组织的所有需要处理的交易")
    @RequestMapping(value = "/getTransaction", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTransaction() {
        return transactionService.getTransaction();
    }

    @ApiOperation(value = "处理交易",notes = "按照交易的id以确认码的方式处理交易")
    @RequestMapping(value = "/confirmTransaction", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String confirmTransaction(@RequestParam @ApiParam("交易id") String transactionId,
                                     @RequestParam @ApiParam("确认码") String checkCode) {
        return transactionService.confirmTransaction(transactionId, checkCode);
    }

    @ApiOperation(value = "发起跨链交易",notes = "以登录用户组织的身份发起一笔跨链交易，跨链交易必须指明目标通道")
    @RequestMapping(value = "/crossChainTransaction", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String crossChainTransaction(@RequestParam @ApiParam("发起者") String sender,
                                        @RequestParam @ApiParam("购入者") String receiver,
                                        @RequestParam @ApiParam("交易资产编号") String assetCode,
                                        @RequestParam @ApiParam("交易时间") String datetime,
                                        @RequestParam @ApiParam("交易价格") Double price,
                                        @RequestParam @ApiParam("目标通道") String targetChannel) {
        return transactionService.crossChainTransaction(sender, receiver, assetCode, datetime, price, targetChannel);
    }

    @ApiOperation(value = "获取待处理跨链交易",notes = "获取交易目标为登录组织的所有需要处理的跨链交易")
    @RequestMapping(value = "/getCrossChainTransaction", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getCrossChainTransaction() {
        return transactionService.getCrossChainTransaction();
    }

    @ApiOperation(value = "处理跨链交易",notes = "按照跨链交易的id以确认码的方式处理交易")
    @RequestMapping(value = "/confirmCrossChainTransaction", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String confirmCrossChainTransaction(@RequestParam @ApiParam("交易id") String transactionId,
                                               @RequestParam @ApiParam("确认码") String checkCode) {
        return transactionService.confirmCrossChainTransaction(transactionId, checkCode);
    }
}
