# Camel K Open Tracing Example

![Camel K CI](https://github.com/openshift-integration/camel-k-example-basic/workflows/Camel%20K%20CI/badge.svg)

This example demonstrates how to enable Open Tracing with Camel K. It provides a solution for describing and analyzing the cross-process transactions in the distributed deployment model.

In this specific example, You will create a sequence of api calls, and see the trace of the distributed call.

In this example, you will be using Jaeger,a distributed tracing system. It is used for monitoring and troubleshooting microservices-based distributed systems and it supports the OpenTracing standard.

## Before you begin

Make sure you check-out this repository from git and open it with [VSCode](https://code.visualstudio.com/).

Instructions are based on [VSCode Didact](https://github.com/redhat-developer/vscode-didact), so make sure it's installed
from the VSCode extensions marketplace.

From the VSCode UI, click on the `readme.didact.md` file and select "Didact: Start Didact tutorial from File". A new Didact tab will be opened in VS Code.

## Checking requirements

<a href='didact://?commandId=vscode.didact.validateAllRequirements' title='Validate all requirements!'><button>Validate all Requirements at Once!</button></a>

**VS Code Extension Pack for Apache Camel**

The VS Code Extension Pack for Apache Camel by Red Hat provides a collection of useful tools for Apache Camel K developers,
such as code completion and integrated lifecycle management.

You can install it from the VS Code Extensions marketplace.

[Check if the VS Code Extension Pack for Apache Camel by Red Hat is installed](didact://?commandId=vscode.didact.extensionRequirementCheck&text=extension-requirement-status$$redhat.apache-camel-extension-pack&completion=Camel%20extension%20pack%20is%20available%20on%20this%20system. "Checks the VS Code workspace to make sure the extension pack is installed"){.didact}

*Status: unknown*{#extension-requirement-status}

**OpenShift CLI ("oc")**

The OpenShift CLI tool ("oc") will be used to interact with the OpenShift cluster.

[Check if the OpenShift CLI ("oc") is installed](didact://?commandId=vscode.didact.cliCommandSuccessful&text=oc-requirements-status$$oc%20help "Tests to see if `oc help` returns a 0 return code"){.didact}


*Status: unknown*{#oc-requirements-status}


**Connection to an OpenShift cluster**

You need to connect to an OpenShift cluster in order to run the examples.

[Check if you're connected to an OpenShift cluster](didact://?commandId=vscode.didact.requirementCheck&text=cluster-requirements-status$$oc%20get%20project$$NAME&completion=OpenShift%20is%20connected. "Tests to see if `kamel version` returns a result"){.didact}

*Status: unknown*{#cluster-requirements-status}

**Apache Camel K CLI ("kamel")**

Apart from the support provided by the VS Code extension, you also need the Apache Camel K CLI ("kamel") in order to
access all Camel K features.

[Check if the Apache Camel K CLI ("kamel") is installed](didact://?commandId=vscode.didact.requirementCheck&text=kamel-requirements-status$$kamel%20version$$Camel%20K%20Client&completion=Apache%20Camel%20K%20CLI%20is%20available%20on%20this%20system. "Tests to see if `kamel version` returns a result"){.didact}

*Status: unknown*{#kamel-requirements-status}


## 1. Preparing a new OpenShift project


Go to your working project, open a terminal tab and type the following command:


```
oc project userX-lab-jaeger
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20project%20userX-lab-jaeger&completion=Use%20your%20namespace. "Opens a new terminal and sends the command above"){.didact})

You should ensure that the Camel K operator is installed. We'll use the `kamel` CLI to do it:

```
kamel install --skip-operator-setup --skip-cluster-setup --trait-profile OpenShift
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20install%20--skip-operator-setup%20--skip-cluster-setup%20--trait-profile%20OpenShift&completion=Camel%20K%20platform%20installation. "Opens a new terminal and sends the command above"){.didact})


You should have an IntegrationPlatform custom resource in your project. To verify it:

```
oc get integrationplatform
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20get%20integrationplatform&completion=Camel%20K%20integration%20platform%20verification. "Opens a new terminal and sends the command above"){.didact})

If everything is ok, you should see an IntegrationPlatform named `camel-k` with phase `Ready` (it can take some time for the
operator to being installed).


## 2. Configure and Setup Jaeger

Jaeger is a set of distributed components for collecting, storing, and displaying trace information. It comes in “all-in-one” image that runs the entire system. We’ll use that to keep the install simple on the OpenShift plaform.

To install **Jaeger**, make sure the Jaeger operater is installed:

```
oc apply -f opentracing/jaeger.yaml
```

([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$oc%20apply%20-f%20opentracing/jaeger.yaml "Opens a new terminal and sends the command above"){.didact})

Once installed, you will be able to access the console via following URL.

```
echo http://$(oc get route jaeger-all-in-one-inmemory -o jsonpath='{.spec.host}')
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$echo%20http://$(oc%20get%20route%20jaeger-all-in-one-inmemory%20-o%20jsonpath='{.spec.host}') "Opens a new terminal and sends the command above"){.didact})


Currently the only service avalible is the default `jaeger-query`

## 3. Enable open tracing and trace a REST API Call in Camel K Route

Tracing is an essential strategy for managing and monitoring  users’ experience. You will be creating three distributed services, `Order` is a rest service that will call both the `inventory` and `invoice`  which are also rest services.


Quarkus OpenTracing extension in Camel automatically creates a Camel OpenTracingTracer and binds it to the Camel registry. Simply configure the properties to enable open tracing.

See `quarkus.properties`([open](didact://?commandId=vscode.openFolder&projectFilePath=../camel-k-example-jaeger/quarkus.properties&completion=Opened%20the%20quarkus.properties%20file "Opens the quarkus.properties file"){.didact}) for details

```
kamel run InventoryService.java --name inventory -d mvn:org.apache.camel.quarkus:camel-quarkus-opentracing  -d camel-jackson --property-file quarkus.properties -t quarkus.enabled=true
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20InventoryService.java%20--name%20inventory%20-d%20mvn:org.apache.camel.quarkus:camel-quarkus-opentracing%20-d%20camel-jackson%20--property-file%20quarkus.properties%20-t%20quarkus.enabled=true "Opens a new terminal and sends the command above"){.didact})

Let's inject the Opentracing Tracer to the camel OrderService.java application. Let's start the inventory service.


See customizers/`OpentracingCustomizer.java`([open](didact://?commandId=vscode.openFolder&projectFilePath=../camel-k-example-jaeger/customizers/OpentracingCustomizer.java&completion=Opened%20the%20OpentracingCustomizer.java%20file "Opens the OpentracingCustomizer.java file"){.didact}) for more details.

```
kamel run --name order OrderService.java customizers/OpentracingCustomizer.java -d camel-opentracing -d mvn:io.jaegertracing:jaeger-client:1.2.0 -d camel-jackson -d camel-undertow -d camel-swagger-java --property-file application.properties
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20--name%20order%20OrderService.java%20customizers/OpentracingCustomizer.java%20-d%20camel-opentracing%20-d%20mvn:io.jaegertracing:jaeger-client:1.2.0%20-d%20camel-jackson%20-d%20camel-undertow%20-d%20camel-swagger-java%20--property-file%20application.properties "Opens a new terminal and sends the command above"){.didact})

The `Invoice` REST service is written in groovy, but no worries, we also got that covered!

See `InvoiceService.groovy`([open](didact://?commandId=vscode.openFolder&projectFilePath=../camel-k-example-jaeger/InvoiceService.groovy&completion=Opened%20the%20InvoiceService.groovy%20file "Opens the InvoiceService.groovy file"){.didact}).

```
kamel run InvoiceService.groovy --name invoice customizers/OpentracingCustomizer.java -d camel-opentracing -d mvn:io.jaegertracing:jaeger-client:1.2.0 -d camel-swagger-java -d camel-jackson -d camel-undertow --property-file application.properties
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20run%20InvoiceService.groovy%20--name%20invoice%20customizers/OpentracingCustomizer.java%20-d%20camel-opentracing%20-d%20mvn:io.jaegertracing:jaeger-client:1.2.0%20-d%20camel-swagger-java%20-d%20camel-jackson%20-d%20camel-undertow%20--property-file%20application.properties "Opens a new terminal and sends the command above"){.didact})


### 4. Calling the REST Services and monitor tracing.

Place an order to trigger the distributed transaction.

```
ORDER_URL=http://$(oc get route order -o jsonpath='{.spec.host}')
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$ORDER_URL=http://$(oc%20get%20route%20order%20-o%20jsonpath='{.spec.host}') "Opens a new terminal and sends the command above"){.didact})

```
curl --location --request POST $ORDER_URL/place \
--header 'Content-Type: application/json' \
--data-raw '{
"orderId" : 12345,      
"itemId" : 2345,
"orderItemName" : "Nintendo Switch",
"quantity" : 1,
"price" : 199,
"address" : "445 Test Street",
"zipCode" : 83748,
"datetime" : "2020-04-11",
"department" : "inventory"
}'
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$curl%20--location%20--request%20POST%20$ORDER_URL/place%20--header%20'Content-Type:%20application/json'%20--data-raw%20'{"orderId"%20:%2012345,"itemId"%20:%202345,"orderItemName"%20:%20"Nintendo%20Switch","quantity"%20:%201,"price"%20:%20199,"address"%20:%20"445%20Test%20Street","zipCode"%20:%2083748,"datetime"%20:%20"2020-04-11","department"%20:%20"inventory"}' "Opens a new terminal and sends the command above"){.didact})

Both inventory and invoice data will be returned upon success. Go back to the Jaeger Tracing Console, and you should be able to see four services available.  Find `order` and click on `Find Traces`. And you should be able to see the trace from the order call.

Click on the `order: /place` to view details of your call.

Stop the `invoice` service.

```
kamel delete invoice
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$kamel%20delete%20invoice "Opens a new terminal and sends the command above"){.didact})


Place an order to trigger the distributed transaction again.

```
curl --location --request POST $ORDER_URL/place \
--header 'Content-Type: application/json' \
--data-raw '{
"orderId" : 12345,      
"itemId" : 2345,
"orderItemName" : "Nintendo Switch",
"quantity" : 1,
"price" : 199,
"address" : "445 Test Street",
"zipCode" : 83748,
"datetime" : "2020-04-11",
"department" : "inventory"
}'
```
([^ execute](didact://?commandId=vscode.didact.sendNamedTerminalAString&text=camelTerm$$curl%20--location%20--request%20POST%20$ORDER_URL/place%20--header%20'Content-Type:%20application/json'%20--data-raw%20'{"orderId"%20:%2012345,"itemId"%20:%202345,"orderItemName"%20:%20"Nintendo%20Switch","quantity"%20:%201,"price"%20:%20199,"address"%20:%20"445%20Test%20Street","zipCode"%20:%2083748,"datetime"%20:%20"2020-04-11","department"%20:%20"inventory"}' "Opens a new terminal and sends the command above"){.didact})


Go back to the Jaeger Tracing Console. Find `order` and click on `Find Traces` again. And view your new request. Find the new trace result, an error should appear in the trace.

Thank you, you have completed the example.
