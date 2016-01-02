import agl.*;
import agl.error.ServiceException;
import agl.other.PartnerContracts;
import agl.other.PartnerStatistics;
import agl.other.PurchaseOrders;
import agl.other.ServiceStatistics;
import rx.Observable;

import java.io.IOException;

/**
 * Created by AMANI CHRISTIAN on 29/12/2015.
 */


public class Main
{
    public static void main(String... args)
    {
        String id = "****";
        String secretCode = "****";
        GenerateService service = new GenerateService(id,secretCode);
        Token token;
        ResponseSMS responseSMS = null;
        RemainderSMS remainderSMS = null;
        try
        {
            token = service.getToken();
            SMSHeader smsHeader = new SMSHeader();
            responseSMS = sendSMS(service,token,smsHeader);
            remainderSMS =service.remainderSMS(token);
        }
        catch (IOException | ServiceException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(responseSMS != null)
            {
                showContentAndSenderAddOfSms(responseSMS);

            }
            if(remainderSMS != null)
            {
                showDataContract(remainderSMS);
                getSpecificContractDescr(remainderSMS,"contract description");
            }
        }
    }

    private static void showDataContract(RemainderSMS remainderSMS)
    {
            Observable.just(remainderSMS)
                .map( remain -> remain.getPartnerContracts())
                .doOnNext(partnerContracts1 ->System.out.println( partnerContracts1.getPartnerId()))
                .flatMap( partnerContracts -> Observable.from(partnerContracts.getContracts()))
                .subscribe
                (
                    contract ->
                    {
                        System.out.println(contract.getContractDescription());
                        System.out.println(contract.getService());
                    }
                );
    }

    private static void showDataServiceContract(RemainderSMS remainderSMS)
    {
        Observable.just(remainderSMS)
                .map(RemainderSMS::getPartnerContracts)
                .doOnNext(partnerContracts1 ->System.out.println( partnerContracts1.getPartnerId()))
                .flatMap( partnerContracts -> Observable.from(partnerContracts.getContracts()))
                .flatMap(contract ->  Observable.from(contract.getServiceContracts()))
                .subscribe(
                        s ->
                        {
                            System.out.println(s.getService());
                            System.out.println(s.getAvailableUnits());
                            System.out.println(s.getContractId());
                            System.out.println(s.getExpires());
                            System.out.println(s.getCountry());
                            System.out.println(s.getScDescription());
                        });
    }

    private static void showContentAndSenderAddOfSms(ResponseSMS responseSMS)
    {
        Observable.just(responseSMS)
                .map(ResponseSMS::getOutBoundSMSMessageRequest)
                .doOnCompleted( () -> System.out.println("Task is a completed"))
                .doOnError( (throwable) -> System.out.println("Error is occurred"))
                .subscribe
                (
                    smsResponse ->
                    {
                        System.out.println(smsResponse.getOutboundSMSTextMessage());
                        System.out.println(smsResponse.getSenderAddress());
                    }
                );
    }


    public static void getSpecificContractDescr(RemainderSMS remainderSMS,String description)
    {
        Observable.just(remainderSMS)
                .map(RemainderSMS::getPartnerContracts)
                .flatMap(partnerContracts -> Observable.from(partnerContracts.getContracts()))
                .filter(contract -> contract.getContractDescription().equals(description))
                .map(PartnerContracts.Contract::getContractDescription)
                .subscribe(System.out::println);
    }

    public static void showServiceStatistic(StatisticSMS statisticSMS)
    {
        Observable.just(statisticSMS)
                .map(StatisticSMS::getPartnerStatistics)
                .flatMap(partnerStatistics -> Observable.from(partnerStatistics.getStatistics()))
                .map(PartnerStatistics.Statistics::getService)
                .subscribe(System.out::println);
                
    }
    public static void showListCountryStatistics(StatisticSMS statisticSMS)
    {
        Observable.just(statisticSMS)
                .map(StatisticSMS::getPartnerStatistics)
                .doOnNext(partnerStatistics -> System.out.println(partnerStatistics.getPartnerId()))
                .flatMap(partnerStatistics1 -> Observable.from(partnerStatistics1.getStatistics()))
                .flatMap( statistics -> Observable.from(statistics.getServiceStatistics()))
                .map(ServiceStatistics::getCountry)
                .subscribe(System.out::println);
    }

    public static void showUsageCountryStatistics(StatisticSMS statisticSMS,String country)
    {
        Observable.just(statisticSMS)
                .map(StatisticSMS::getPartnerStatistics)
                .flatMap(partnerStatistics -> Observable.from(partnerStatistics.getStatistics()))
                .flatMap(statistics -> Observable.from(statistics.getServiceStatistics()))
                .filter(serviceStatistics -> serviceStatistics.getCountry().equals(country))
                .flatMap(serviceStatistics1 -> Observable.from(serviceStatistics1.getCountyStatistics()))
                .map(ServiceStatistics.CountryStatistics::getUsage)
                .subscribe(System.out::println);
    }

    public static void showDataPurchaseHistoric(HistoricPurchase historicPurchase)
    {
        Observable.from(historicPurchase.getPurchaseOrders())
                .subscribe
                (
                    purchaseOrders ->
                    {
                        System.out.println(purchaseOrders.getBundleDescription());
                        System.out.println(purchaseOrders.getBundleId());
                        System.out.println(purchaseOrders.getPurchaseOrderId());
                        purchaseOrders.getOrderExecutionInformation();
                    }
                );
    }

    public static void showDataOrderExecutionInfor(HistoricPurchase historicPurchase)
    {
        Observable.from(historicPurchase.getPurchaseOrders())
                .map(PurchaseOrders::getOrderExecutionInformation)
                .subscribe
                (
                        orderExecutionInformation ->
                        {
                            System.out.println(orderExecutionInformation.getContractId());
                            System.out.println(orderExecutionInformation.getService());
                            System.out.println(orderExecutionInformation.getCountry());
                            System.out.println(orderExecutionInformation.getAmount());
                            System.out.println(orderExecutionInformation.getCurrency());
                            System.out.println(orderExecutionInformation.getDate());
                        }
                );
    }

    public static ResponseSMS sendSMS(GenerateService service,Token token, SMSHeader smsHeader) throws IOException, ServiceException
    {
        String address = "XXXxxxxxxxx";
        String senderAddress = "XXXxxxxxxxx";
        String content = "my content";
        SMS sms = new SMS(address,senderAddress,content);
        return service.sendSMS(token,sms,smsHeader);
    }
}
