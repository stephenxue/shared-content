package au.net.api.loyalty

import com.sap.gateway.ip.core.customdev.util.Message;
import com.sap.it.api.ITApiFactory;
import com.sap.it.api.nrc.NumberRangeConfigurationService
import groovy.json.*;
/* ************************************************************************
    Program     : GetMemberIDFromNumberRange.groovy
    Create Date : Mar-04-2020
    Author      : Stephen Xue
    Function    :
        If the ProcessType is 'register', get a new member ID from number
        range: LoyaltyMemberId
 *************************************************************************/

Message processData(Message message) {
    final NR_NAME = "LoyaltyMemberId";
    // Get Header property 'ProcessType'
    def processType = message.getHeaders().get("ProcessType");
    if(processType == "register") {
        def body = message.getBody() as String;  //def body = message.getBody(java.lang.String) as String;
        def jsonSlurper = new JsonSlurper();
        def jsonBody = jsonSlurper.parseText(body);
        jsonBody.id = getNextNumber(NR_NAME);
        def result = JsonOutput.toJson(jsonBody);
        message.setBody(result);
    }
    return message;
}

def String getNextNumber(String NR_NAME) {
    def NRCS = ITApiFactory.getApi(NumberRangeConfigurationService.class, null);
    def nextValue = NRCS.getNextValuefromNumberRange(NR_NAME,null);

    return nextValue;
}