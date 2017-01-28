#Testing API integration framework

##Requirements
To be able to operate with framework your system should have next tools:
* java 1.8 and above http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
* gradle https://gradle.org/gradle-download/

##FAQ
###How to run test cases?
command line: gradle ddtest

Couple system's variables should be established in a system prior:
* REST_APP_TAGS (smoke, users)
* REST_APP_API_URL (http://some-endpoint.com)
* REST_APP_API_PORT (80)

###Where test cases have to be located?
All cases have to be located in framework resource folder test\resources\TestSuite\

Sub-folders are welcome.

###Is there SetUp and TearDown functionality?
There are two levels of SetUp/TearDown functionality: global and local

####Global SetUp/TearDown
Cases will be run once before and after ALL cases.

SetUp cases should be located under folder test\resources\TestSuite\_SetUp

TearDown cases should be in folder test\resources\TestSuite\_TearDown

All variables are created in that case are Global.

####Local SetUp/TearDown
Sometimes we need to run one/few cases before our test cases. Then we have options SetUp/TearDown in our .json files.
Simple define this sections in your test .json file as
```"SetUp":[]``` and ```"TearDown":[]```

Inside those sections tests are expected. See question "What is a template for test case?"


###What is a file format for cases?
json file with name *.json

```{
  "Microservice": "",
  "Tags":[""],
  "Variables": {
    "variable1":""
 },
  "Tests": [
  ],
  "SetUp": [
  ],
  "TearDown": [
  ]
}```

Where 

* Microservice is a name of service that is mapped with url and map. If name is not mapped with any name in file APIUrls.properties then [base] section will be used.
* Tags is a array of tags to distinguish test cases and test suites
* Variables contain a list of variables for this scope of tests
* Tests - is array of test cases

###What is a template for test case?
```{
  "Name": "",
  "Method": "",
  "URL": "",
  "Params": {
    "Authorization": ""
 },
  "Timeout": 1,
  "Loop": 1,
  "LoopTimeout": 1,
  "Body": {
    "projectConfigurations": []
  },
  "Expectations": [
    {
      "type": "",
      "xpath": "",
      "value": ""
 }
  ]
}```

Where

* Name is a name of case
* Method is on of GET, POST, DELETE, PUT
* URL is a full endpoint url or part like "/project/add"
* Params is array of params for request
* Timeout is an idle in seconds. It will wait that time before run case
* Loop is max number of repetitions for this request. It will be repeated until all expectations are done
* LoopTimeout is an idle in seconds between repetitions
* Body is a request body as json object
* Expectations is array of expected results

###What expectations are there?
Expectation of status

```{
  "type":"STATUS",
  "value":201
}```

Expectation of xpath

```{
  "type":"XPATH",
  "xpath": "$.totalHits",
  "value": "1"
}```

Expectation of arrays equality 

```{
  "type":"XEQUAL",
  "xpath":"$.[0].child[?(@.name=~/%filename/)].status",
  "value":["DONE"]
}```

Expectation the array contains

```{
  "type": "XCONTAINS",
  "xpath": "$.[*].id",
  "value": ["%groupId_1","%groupId_2","%groupId_3"]
}```

Expectation of NULL object

```{
  "type": "XNULL",
  "xpath": "$..[?(@.projectId==%projectId)].active"
}```

Expectation the array is bigger than

```{
  "type": "XSIZEGREATER",
  "xpath": "$.metadataResponseBean[*]",
  "value": 0
}```

Expectation the regex in body

```{
  "type": "REGEXEQUAL",
  "regex": "([a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12})",
  "value": "somevalue"
}```

###How to use variables in tests?
Set variables

There are two types of variables
* global
* local
Global variables can be used across all test cases. To set it
* user variables.json file in folder TestSuite
* set variable in scope of SetUp cases

Local variables are set inside of each test suite file. Look at Variables option. Other way to set variable is on fly.

Set variable groupId with dynamic value

```{
  "type": "XVARIABLE",
  "xpath": "$.groupId",
  "value": "groupId"
}```

Variable as regex in body

```{
  "type": "REGEXEQUAL",
  "regex": "([a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12})",
  "value": "regId"
}```

####Use variables

Variable can be used in URL, Params, Body, Expectations. In next case we can see variables: projectId, token, login

```{
  "Name": "check non-active project",
  "Method": "GET",
  "URL": "%projectId",
  "Tags":["smoke"],
  "Params": {
    "Authorization": "%token"
 },
  "Expectations": [
    {
      "type":"XPATH",
      "xpath":"$.projectInfo.emailId",
      "value":"%login"
 }
  ]
}```

###What macros are there for variables?
* {GUID} - will generate guid without "-"
* {DATE(yyyy-MM-dd'T'HH:mm:ss.SSS'Z')} - will generate date in given format