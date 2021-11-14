package au.net.api.loyalty
import com.sap.gateway.ip.core.customdev.util.Message
import groovy.json.*
/* ************************************************************************
    Program     : FormatInput.groovy
    Create Date : Mar-04-2020
    Author      : Stephen Xue
    Function    :
        1. for POST, Parse path in the URL into Header property;
        for example: if path is '/v1/royalty/register', the header property
        will be: ProcessType:register
        2. for GET, parse the query parameter pairs and set them into message
        payload
 *************************************************************************/
Message processData(Message message) {
    def result = [ErrorMsg: ""];
    // Check header property 'SourceSystem', if it is blank, raise error msg
    def sourceSystem = message.getHeaders().get("SourceSystem");
    if(sourceSystem == null){
        message.setHeader('ProcessType', 'error');
        result.ErrorMsg = "Please provide header property 'SourceSystem'";
        message.setHeader("CamelHttpResponseCode", 400);
        message.setBody(JsonOutput.toJson(result));
    }else {
        def operation = message.getHeaders().get("CamelHttpMethod");
        switch (operation){
            case "GET" :
                message = processGET(message);
                break;
            case "POST":
                message = processPOST(message);
                break;
        }
    }
    return message
}
Message processGET(Message message){
    def params = [:]
    def urlParameters = message.getHeaders().get("CamelHttpQuery")
    // Parse all URL parameters into message payload
    urlParameters.split("&").each {
        it ->
            String[] pair = it.split("=")
            params[pair[0]] = (pair as List)[1]
    }

    // Parse all header parameters into message payload
    message.getHeaders().each {
        it ->
            params << it
    }
    message.setBody(JsonOutput.toJson(params))
    message.setHeader('Content-Type', 'application/json');
    message.setHeader('ProcessType', 'query');
    return message
}

Message processPOST(Message message){
    def path = message.getHeaders().get("CamelHttpUrl");
    def processType = path.substring(path.lastIndexOf("/") + 1);
    // If the path is not valid, set error msg
    Set validParameterList = ["register", "update"];
    if (validParameterList.contains(processType)) {
        message.setHeader('ProcessType', processType);
    } else {
        message.setHeader('ProcessType', 'error');
        result.ErrorMsg = "The endpoint you are calling, ${path}, is invalid. Please check it.";
        message.setHeader("CamelHttpResponseCode", 400);
        message.setBody(JsonOutput.toJson(result));
    }
    return message
}