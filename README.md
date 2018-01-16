# CryptoPredict

CryptoPredict is a Spring-based RESTful API written in Java that gets & stores historical cryptocurrency pricing. It then
uses that data to make price predictions.  
CryptoPredict utilizes a number of third-party tools to accomplish this, including [CrytoCompare API](https://www.cryptocompare.com/api/#), [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/), [AWS Redshift](hhttps://aws.amazon.com/redshift/) & [AWS Machine Learning](https://aws.amazon.com/machine-learning/) [Real Time Predictions](https://docs.aws.amazon.com/machine-learning/latest/dg/requesting-real-time-predictions.html).

Using an hourly cronjob, this API continuously stores new data, predicts future values, and evaluates its predictions.

Please note: This API is in active development.
 
## Getting Started

### Requirements
The following must be installed on your local machine to use this program as is.
* [Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](https://maven.apache.org/download.cgi)
* [SQL Workbench/J](http://www.sql-workbench.net/downloads.html)

Clone the project to your desired location

```
$ git clone https://github.com/meejahnsnutshell/AWSMLCrypto
$ cd AWSMLCrypto
$ mvn clean install
```
### AWS Setup

You will need an [AWS](https://maven.apache.org/download.cgi) account. Please note that some services may incur fees. 
Avoid unnecessary charges by disabling services after use.

* Set up your AWS [Credentials](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html).

* Launch a Redshift Cluster. In the application.properties file of the application, update the datasource url, username, 
and password.

* Follow [these](https://docs.aws.amazon.com/redshift/latest/mgmt/connecting-using-workbench.html) instructions to connect 
Workbench/J to your Redshift cluster.

## Loading Historical Data


Instructions go here

## Making Predictions

* Using the AWS console, ensure you have a datasource (which is data that AWS exports from your Redshift db to an S3 
bucket) available for your machine learning model. Models can be created programatically or via the console - both use 
an existing datasource.  

* To programatically create a model, set createmodel=true. Optional: modelId=xxxx&modelName=xxxx. Id and name are 
generated if not provided. Ex URIs:
```
.../predict/realtime?createmodel=true&modelId=1234&modelName=MyModel
```
or
```
.../predict/realtime?createmodel=true
```
At present, model parameters may be configured in the code:
[insert screenshot]

* To use an existing model, provide id and name.
```
.../predict/realtime?createmodel=false&modelid=1234&modelname=MyModel
```

* Make a prediction HTTP call. For example, using an existing model: 
```
.../predict/realtime?modelid=1234&modelname=model1234
```

## Contributors
* [Jialor Cheung](https://github.com/PopoPenguin)
* [Meghan Boyce](https://github.com/meejahnsnutshell)

## Acknowledgments

* [CryptoCompare API](https://www.cryptocompare.com/api/#)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
* [Machine Learning Samples](https://github.com/awslabs/machine-learning-samples/blob/master/targeted-marketing-java/src/main/java/com/amazonaws/samples/machinelearning/BuildModel.java) by awslabs

## License
[MIT](https://choosealicense.com/licenses/mit/)