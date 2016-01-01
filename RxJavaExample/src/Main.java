import agl.*;
import agl.error.ServiceException;
import agl.other.PartnerContracts;
import agl.other.PartnerStatistics;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

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
                        System.out.println(contract.getServiceContracts());
                    }
                );
    }

    private static void showContentAndSenderAddOfSms(ResponseSMS responseSMS)
    {
        Observable.just(responseSMS)
                .map(t -> t.getOutBoundSMSMessageRequest())
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
                .map( remain -> remain.getPartnerContracts())
                .flatMap(partnerContracts -> Observable.from(partnerContracts.getContracts()))
                .filter(contract -> contract.getContractDescription().equals(description))
                .map(contract1 -> contract1.getContractDescription())
                .subscribe(s -> System.out.println(s));
    }

    public static void showServiceStatistic(StatisticSMS statisticSMS)
    {
        Observable.just(statisticSMS)
                .map(statisticSMS1 -> statisticSMS1.getPartnerStatistics())
                .flatMap(partnerStatistics -> Observable.from(partnerStatistics.getStatistics()))
                .map(statistics -> statistics.getService())
                .subscribe(s -> System.out.println(s));
                
    }
    public static void showListCountryStatistics(StatisticSMS statisticSMS)
    {
        Observable.just(statisticSMS)
                .map(statisticSMS1 -> statisticSMS1.getPartnerStatistics())
                .doOnNext(partnerStatistics -> System.out.println(partnerStatistics.getPartnerId()))
                .flatMap(partnerStatistics1 -> Observable.from(partnerStatistics1.getStatistics()))
                .flatMap( statistics -> Observable.from(statistics.getServiceStatistics()))
                .map(serviceStatistics -> serviceStatistics.getCountry())
                .subscribe(s -> System.out.println(s));
    }

    public static void showUsageCountryStatistics(StatisticSMS statisticSMS,String country)
    {
        Observable.just(statisticSMS)
                .map(statisticSMS1 -> statisticSMS1.getPartnerStatistics())
                .flatMap(partnerStatistics -> Observable.from(partnerStatistics.getStatistics()))
                .flatMap(statistics -> Observable.from(statistics.getServiceStatistics()))
                .filter(serviceStatistics -> serviceStatistics.getCountry().equals(country))
                .flatMap(serviceStatistics1 -> Observable.from(serviceStatistics1.getCountyStatistics()))
                .map(countryStatistics -> countryStatistics.getUsage())
                .subscribe(s -> System.out.println(s));
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
