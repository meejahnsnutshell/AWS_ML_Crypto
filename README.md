# CryptoPredict [![Crates.io](https://img.shields.io/crates/l/rustc-serialize.svg)]()

CryptoPredict is a Spring-based RESTful API written in Java that gets & stores historical cryptocurrency pricing. It then
uses that data to make price predictions.  
CryptoPredict utilizes a number of third-party tools to accomplish this, including [CrytoCompare API](https://www.cryptocompare.com/api/#),  
[AWS SDK for Java](https://aws.amazon.com/sdk-for-java/), [AWS Redshift](hhttps://aws.amazon.com/redshift/) & 
[AWS Machine Learning](https://aws.amazon.com/machine-learning/) [Real Time Predictions](https://docs.aws.amazon.com/machine-learning/latest/dg/requesting-real-time-predictions.html).

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

* Use an existing model created via the AWS console (where createmodel=false & modelid & modelname are provided) or 
create one programmatically (createmodel=true, a unique name/id may be provided or the program auto-generates them). 
(Note: At this time the programmatic creation of a model is not fully developed. New models must use an existing datasource.)

* Make a prediction HTTP call (in your browser url or curl command in the cronjob)
For example, to use an existing model: 
localhost:8080/prediction/realtime?modelid=1234&modelname=model1234

## Contributors
* [Jialor Cheung](https://github.com/PopoPenguin)
* [Meghan Boyce](https://github.com/meejahnsnutshell)

## Acknowledgments

* [CryptoCompare API](https://www.cryptocompare.com/api/#)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
* [Machine Learning Samples](https://github.com/awslabs/machine-learning-samples/blob/master/targeted-marketing-java/src/main/java/com/amazonaws/samples/machinelearning/BuildModel.java) by awslabs

## License
[MIT](https://choosealicense.com/licenses/mit/)