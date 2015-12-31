/**
 * Created by AMANI CHRISTIAN on 29/12/2015.
 */

import agl.*;
import agl.error.ServiceException;
import agl.other.PartnerContracts;
import agl.other.PartnerStatistics;
import agl.other.ServiceContracts;
import agl.other.ServiceStatistics;

import java.io.IOException;


public class Main
{
    public static void main(String[] args)
    {
        String id = "****" ; // your orange id
        String secretCode = "**********"; // your orange secret code
        GenerateService service = null;
        Token token;
        ResponseSMS responseSMS = null;
        RemainderSMS remainderSMS = null;
        StatisticSMS statisticSMS = null;
        HistoricPurchase historicPurchase = null;
        try
        {
            token = obtainToken(id,secretCode,service);
            responseSMS = sendSms(token,service);

            //
            remainderSMS = service.remainderSMS(token);
            //
            statisticSMS = service.statisticSMS(token);
            //
            historicPurchase = service.obtainHistoric(token);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        catch (ServiceException e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        finally
        {
            if(responseSMS != null)
            {
                String resourceUrl = responseSMS.getResourceURL();
                ResponseSMS.SMSResponse smsResponse = responseSMS.getOutBoundSMSMessageRequest();
                String senderAddress = smsResponse.getSenderAddress();
                String content = smsResponse.getOutboundSMSTextMessage();

                System.out.println("the sender address is "+senderAddress + ".\n"+"The content is "+content+".\n"+
                        "The resource Url is "+resourceUrl);
            }
            if(remainderSMS != null)
            {
                showDataRemainder(remainderSMS);
            }
            if(statisticSMS != null)
            {
                PartnerStatistics partnerStatistics = statisticSMS.getPartnerStatistics();
                String partnerId = partnerStatistics.getPartnerId();
                PartnerStatistics.Statistics[] statistics = partnerStatistics.getStatistics();
                String serviceStat;
                for(PartnerStatistics.Statistics statistic: statistics)
                {
                    serviceStat = statistic.getService();
                    System.out.println("The partner id is "+partnerId+".\n"
                                    +"the service is "+serviceStat);
                    ServiceStatistics[] serviceStatistics = statistic.getServiceStatistics();
                    for(ServiceStatistics serviceStatistic : serviceStatistics)
                    {
                        String country = serviceStatistic.getCountry();
                        ServiceStatistics.CountryStatistics[] countryStatistics = serviceStatistic.getCountyStatistics();
                        String applicationId;
                        String usage;
                        for(ServiceStatistics.CountryStatistics countryStatistic: countryStatistics)
                        {
                            applicationId = countryStatistic.getApplicationId();
                            usage =    countryStatistic.getUsage();
                            System.out.println("The country is "+country+".\n"
                                    +"The application id is "+applicationId+".\n"
                                    +"The usage of application is "+usage);
                        }
                    }
                }
            }
        }
    }

    private static void showDataRemainder(RemainderSMS remainderSMS)
    {
        PartnerContracts partnerContracts = remainderSMS.getPartnerContracts();
        String partnerId = partnerContracts.getPartnerId();
        PartnerContracts.Contract[] contracts = partnerContracts.getContracts();
        //
        String partnerService;
        String description;
        String country;
        String expiration;
        String contractService;
        String serviceDescription;
        String contractId;
        String availableUnit;

        //
        for(PartnerContracts.Contract contract: contracts)
        {
            partnerService = contract.getService();
            description = contract.getContractDescription();
            System.out.println("The partner id is "+partnerId+"\n"
                    +"The partner service is "+partnerService+".\n"
                    +"The description is "+description);
            ServiceContracts[] serviceContracts = contract.getServiceContracts();
            //
            for (ServiceContracts serviceContract:serviceContracts)
            {
                country = serviceContract.getService();
                expiration = serviceContract.getExpires();
                contractService = serviceContract.getService();
                serviceDescription = serviceContract.getScDescription();
                contractId = serviceContract.getContractId();
                availableUnit  = serviceContract.getAvailableUnits();
                System.out.println("The country is "+country+".\n"
                        + "The service expire an "+expiration+".\n"
                        +"The contract service is "+contractService+".\n"
                        +"The service description is "+serviceDescription+".\n"
                        +"The contract id is "+contractId+".\n"
                        +"The available unit is "+availableUnit);
            }
        }
    }
    // montrer la difficulté d'itérer des données assez conséquente.

    //
    public static Token obtainToken(String id, String secretCode,GenerateService service) throws IOException, ServiceException
    {
        service = new GenerateService(id,secretCode);
        return service.getToken();
    }

    //
    public static ResponseSMS sendSms(Token token, GenerateService service) throws IOException, ServiceException
    {
        SMS sms = new SMS("+XXXxxxxxxxx","+XXXxxxxxxxx","hello word");
        SMSHeader smsHeader = new SMSHeader();
        return  service.sendSMS(token,sms,smsHeader);
    }


}
