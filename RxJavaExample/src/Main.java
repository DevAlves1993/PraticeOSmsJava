import agl.*;
import agl.error.ServiceException;
import agl.other.PartnerContracts;
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
            }
        }
    }

    private static void showDataContract(RemainderSMS remainderSMS)
    {
        String idPartner;
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

    public static ResponseSMS sendSMS(GenerateService service,Token token, SMSHeader smsHeader) throws IOException, ServiceException
    {
        String address = "XXXxxxxxxxx";
        String senderAddress = "XXXxxxxxxxx";
        String content = "my content";
        SMS sms = new SMS(address,senderAddress,content);
        return service.sendSMS(token,sms,smsHeader);
    }
}
