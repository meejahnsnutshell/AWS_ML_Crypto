# AWS ML Crypto [![Crates.io](https://img.shields.io/crates/l/rustc-serialize.svg)]()

AWS ML Crypto is a Spring-based RESTful API written in Java that utilizes the [CrytoCompare](https://www.cryptocompare.com/api/#) 
API's [HistoHour](https://www.cryptocompare.com/api/#-api-data-histohour-) call to load historical cryptocurrency pricing 
data to an AWS [Redshift](hhttps://aws.amazon.com/redshift/) database.

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

You will need an [AWS](https://maven.apache.org/download.cgi) account to utilize Redshift, EC2 & Machine Learning 
services. Please note that some services may incur fees. Avoid unnecessary charges by disabling services after use.

* Set up your AWS [Credentials](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html).

* Launch a Redshift Cluster. In the application.properties file of the application, update the datasource url, username, 
and password.

* Create IAM roles

* Follow [these](https://docs.aws.amazon.com/redshift/latest/mgmt/connecting-using-workbench.html) instructions to connect 
Workbench/J to your Redshift cluster.

## Loading Historical Data


Instructions go here

## Making Predictions

* Create a machine learning model via AWS console or 



## Contributors
* [Jialor Cheung](https://github.com/PopoPenguin)
* [Meghan Boyce](https://github.com/meejahnsnutshell)

## Acknowledgments

* [CryptoCompare API](https://www.cryptocompare.com/api/#)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
* [Machine Learning Samples](https://github.com/awslabs/machine-learning-samples/blob/master/targeted-marketing-java/src/main/java/com/amazonaws/samples/machinelearning/BuildModel.java) by awslabs

## License
? Don't know about this
[MIT](https://choosealicense.com/licenses/mit/)