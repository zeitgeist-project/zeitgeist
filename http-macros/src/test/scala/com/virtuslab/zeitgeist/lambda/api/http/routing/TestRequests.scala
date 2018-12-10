package com.virtuslab.zeitgeist.lambda.api.http.routing

object TestRequests {
  val sampleRequestPost =
    """
      |{
      |    "resource": "/quaich-http-demo/users/{username}/foo/{bar}",
      |    "path": "/quaich-http-demo/users/brendan/foo/123",
      |    "httpMethod": "POST",
      |    "headers": {
      |        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
      |        "Accept-Encoding": "gzip, deflate, sdch, br",
      |        "Accept-Language": "en-US,en;q=0.8",
      |        "CloudFront-Forwarded-Proto": "https",
      |        "CloudFront-Viewer-Country": "US",
      |        "DNT": "1",
      |        "Host": "f7hyd8m7yl.execute-api.us-east-1.amazonaws.com",
      |        "Upgrade-Insecure-Requests": "1",
      |        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
      |        "Via": "1.1 9bf53fbf949b06bf7635b36bc0201861.cloudfront.net (CloudFront)",
      |        "X-Amz-Cf-Id": "ooOkkDuDZjyNIifl8ASTciCp2gkUCmUM5n-PxOytKwwyq1Wqd39dXA==",
      |        "X-Forwarded-For": "207.91.160.7, 54.240.149.12",
      |        "X-Forwarded-Port": "443",
      |        "X-Forwarded-Proto": "https"
      |    },
      |    "queryStringParameters": null,
      |    "pathParameters": {
      |        "bar": "123",
      |        "username": "brendan"
      |    },
      |    "stageVariables": null,
      |    "requestContext": {
      |        "accountId": "176770676006",
      |        "resourceId": "kas444",
      |        "stage": "prod",
      |        "requestId": "4d296250-8a60-11e6-bf73-358d1757f263",
      |        "identity": {
      |            "cognitoIdentityPoolId": null,
      |            "accountId": null,
      |            "cognitoIdentityId": null,
      |            "caller": null,
      |            "apiKey": null,
      |            "sourceIp": "207.91.160.7",
      |            "cognitoAuthenticationType": null,
      |            "cognitoAuthenticationProvider": null,
      |            "userArn": null,
      |            "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
      |            "user": null
      |        },
      |        "resourcePath": "/quaich-http-demo/users/{username}/foo/{bar}",
      |        "httpMethod": "POST",
      |        "apiId": "f7hyd8m7yl"
      |    },
      |    "body": {
      |       "foo": "I will not buy this record, it is scratched.",
      |       "bar": "my hovercraft is full of eels!"
      |    }
      |}
    """.stripMargin

  val sampleRequestPut  =
    """
      |{
      |    "resource": "/quaich-http-demo/users/{username}",
      |    "path": "/quaich-http-demo/users/brendan",
      |    "httpMethod": "PUT",
      |    "headers": {
      |        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
      |        "Accept-Encoding": "gzip, deflate, sdch, br",
      |        "Accept-Language": "en-US,en;q=0.8",
      |        "Cache-Control": "max-age=0",
      |        "CloudFront-Forwarded-Proto": "https",
      |        "CloudFront-Is-Desktop-Viewer": "true",
      |        "CloudFront-Is-Mobile-Viewer": "false",
      |        "CloudFront-Is-SmartTV-Viewer": "false",
      |        "CloudFront-Is-Tablet-Viewer": "false",
      |        "CloudFront-Viewer-Country": "US",
      |        "DNT": "1",
      |        "Host": "f7hyd8m7yl.execute-api.us-east-1.amazonaws.com",
      |        "Upgrade-Insecure-Requests": "1",
      |        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
      |        "Via": "1.1 efe8f585d51dfd5d8d354f74f0385a50.cloudfront.net (CloudFront)",
      |        "X-Amz-Cf-Id": "l8tkw9ISay4QU1FVsfhq48OQqxm4fDbTVtGCqxiL92atksvwZHXYrQ==",
      |        "X-Forwarded-For": "207.91.160.7, 216.137.42.125",
      |        "X-Forwarded-Port": "443",
      |        "X-Forwarded-Proto": "https"
      |    },
      |    "queryStringParameters": null,
      |    "pathParameters": {
      |        "username": "brendan"
      |    },
      |    "stageVariables": null,
      |    "requestContext": {
      |        "accountId": "176770676006",
      |        "resourceId": "w11v9j",
      |        "stage": "prod",
      |        "requestId": "d2eb971d-8a5f-11e6-b30c-19c3d9849330",
      |        "identity": {
      |            "cognitoIdentityPoolId": null,
      |            "accountId": null,
      |            "cognitoIdentityId": null,
      |            "caller": null,
      |            "apiKey": null,
      |            "sourceIp": "207.91.160.7",
      |            "cognitoAuthenticationType": null,
      |            "cognitoAuthenticationProvider": null,
      |            "userArn": null,
      |            "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
      |            "user": null
      |        },
      |        "resourcePath": "/quaich-http-demo/users/{username}",
      |        "httpMethod": "PUT",
      |        "apiId": "f7hyd8m7yl"
      |    },
      |    "body": {
      |       "foo": "I will not buy this record, it is scratched.",
      |       "bar": "my hovercraft is full of eels!"
      |    }
      |}
    """.stripMargin

  val sampleRequestJSONNoArgs =
    """
      |{
      |    "resource": "/quaich-http-demo",
      |    "path": "/quaich-http-demo",
      |    "httpMethod": "POST",
      |    "headers": {
      |        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
      |        "Accept-Encoding": "gzip, deflate, sdch, br",
      |        "Accept-Language": "en-US,en;q=0.8",
      |        "CloudFront-Forwarded-Proto": "https",
      |        "CloudFront-Viewer-Country": "US",
      |        "DNT": "1",
      |        "Host": "f7hyd8m7yl.execute-api.us-east-1.amazonaws.com",
      |        "Upgrade-Insecure-Requests": "1",
      |        "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
      |        "Via": "1.1 696ddb6cca7fadc053f7e4e8b21c9273.cloudfront.net (CloudFront)",
      |        "X-Amz-Cf-Id": "hHVXceyv01PJlreM9cTnjSJ-BUt7ze_3UN3zSwqNs0n99p7kJYLuCg==",
      |        "X-Forwarded-For": "207.91.160.7, 54.240.149.46",
      |        "X-Forwarded-Port": "443",
      |        "X-Forwarded-Proto": "https"
      |    },
      |    "queryStringParameters": null,
      |    "pathParameters": null,
      |    "stageVariables": null,
      |    "requestContext": {
      |        "accountId": "176770676006",
      |        "resourceId": "y168r3",
      |        "stage": "prod",
      |        "requestId": "7a1df3f3-8a57-11e6-b6f7-4dc1e2a29025",
      |        "identity": {
      |            "cognitoIdentityPoolId": null,
      |            "accountId": null,
      |            "cognitoIdentityId": null,
      |            "caller": null,
      |            "apiKey": null,
      |            "sourceIp": "207.91.160.7",
      |            "cognitoAuthenticationType": null,
      |            "cognitoAuthenticationProvider": null,
      |            "userArn": null,
      |            "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36",
      |            "user": null
      |        },
      |        "resourcePath": "/quaich-http-demo",
      |        "httpMethod": "POST",
      |        "apiId": "f7hyd8m7yl"
      |    },
      |    "body": {
      |       "foo": "I will not buy this record, it is scratched.",
      |       "bar": "my hovercraft is full of eels!"
      |    }
      |}
    """.stripMargin

  val sampleSlackGet =
    """
      |{
      |    "resource": "/{path+}",
      |    "path": "/version",
      |    "httpMethod": "POST",
      |    "headers": {
      |        "Accept": "application/json,*/*",
      |        "Accept-Encoding": "gzip,deflate",
      |        "CloudFront-Forwarded-Proto": "https",
      |        "CloudFront-Is-Desktop-Viewer": "true",
      |        "CloudFront-Is-Mobile-Viewer": "false",
      |        "CloudFront-Is-SmartTV-Viewer": "false",
      |        "CloudFront-Is-Tablet-Viewer": "false",
      |        "CloudFront-Viewer-Country": "US",
      |        "Content-Type": "application/x-www-form-urlencoded",
      |        "Host": "2bm9nx14sg.execute-api.eu-west-1.amazonaws.com",
      |        "User-Agent": "Slackbot 1.0 (+https://api.slack.com/robots)",
      |        "Via": "1.1 dddbce278f81f85c0d8ad70ec0b24e44.cloudfront.net (CloudFront)",
      |        "X-Amz-Cf-Id": "pUW4ddRQ5tyoKDa04lzckB-mYBLcKY9S8qp2NslkCwMV7fnDqzUBUA==",
      |        "X-Amzn-Trace-Id": "Root=1-593acbc1-65bbd984146d97a1551cb312",
      |        "X-Forwarded-For": "52.91.184.228, 54.239.145.87",
      |        "X-Forwarded-Port": "443",
      |        "X-Forwarded-Proto": "https"
      |    },
      |    "queryStringParameters": null,
      |    "pathParameters": {
      |        "path": "version"
      |    },
      |    "stageVariables": null,
      |    "requestContext": {
      |        "path": "/dev/version",
      |        "accountId": "910294104187",
      |        "resourceId": "g8zesp",
      |        "stage": "dev",
      |        "requestId": "1f72b72c-4d30-11e7-a6c1-13201e0798c9",
      |        "identity": {
      |            "cognitoIdentityPoolId": null,
      |            "accountId": null,
      |            "cognitoIdentityId": null,
      |            "caller": null,
      |            "apiKey": "",
      |            "sourceIp": "52.91.184.228",
      |            "accessKey": null,
      |            "cognitoAuthenticationType": null,
      |            "cognitoAuthenticationProvider": null,
      |            "userArn": null,
      |            "userAgent": "Slackbot 1.0 (+https://api.slack.com/robots)",
      |            "user": null
      |        },
      |        "resourcePath": "/{path+}",
      |        "httpMethod": "POST",
      |        "apiId": "2bm9nx14sg"
      |    },
      |    "body": "token=xxxxxx&team_id=T42NTAGHM&team_domain=virtuslab&channel_id=C48LP9MPT&channel_name=ss-hackaton&user_id=U424SEL84&user_name=pdolega&command=%2Fquaich-version&text=&response_url=https%3A%2F%2Fhooks.slack.com%2Fcommands%2FT42NTAGHM%2F196325465175%2FmrWnUZHt5oObdG0LbwZYkOqL",
      |    "isBase64Encoded": false
      |}
    """.stripMargin
}
