# AWS_ML_Crypto

AWS_ML_Crypto is a Spring-based RESTful API written in Java that gets & stores historical cryptocurrency pricing. It then
uses that data to make price predictions.  
AWS_ML_Crypto utilizes a number of third-party tools to accomplish this, including [CrytoCompare API](https://www.cryptocompare.com/api/#), [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/), [AWS Redshift](hhttps://aws.amazon.com/redshift/) & [AWS Machine Learning](https://aws.amazon.com/machine-learning/) [Real Time Predictions](https://docs.aws.amazon.com/machine-learning/latest/dg/requesting-real-time-predictions.html).

Using cron & the provided shell scripts, the user may continuously store new data, predict future values, and evaluate predictions.

Please note: This API is in active development.
 
## Getting Started

### Requirements
The following must be installed on your local machine to use this program as is.
* [Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](https://maven.apache.org/download.cgi)
* [SQL Workbench/J](http://www.sql-workbench.net/downloads.html)

Clone the project to your desired location

```
$ git clone https://github.com/meejahnsnutshell/AWS_ML_Crypto
$ cd AWS_ML_Crypto
$ mvn clean install
```
### AWS Setup

You will need an [AWS](https://maven.apache.org/download.cgi) account. Please note that some services may incur fees. 
Avoid unnecessary charges by disabling services after use.

* Set up your AWS [Credentials](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html).

* Launch a Redshift Cluster. In the application.properties file of the application, update the datasource url, username, 
and password.

* Follow [these](https://docs.aws.amazon.com/redshift/latest/mgmt/connecting-using-workbench.html) instructions to connect 
Workbench/J to your Redshift cluster. [Build your tables.](https://github.com/meejahnsnutshell/AWS_ML_Crypto/blob/master/build_tables.sql)

## Loading Historical Data

* [Cryptocompare API](https://www.cryptocompare.com/api/#-api-data-) is the source for historical data of current cryptocurrency 
pricing. All API calls for aggregating and loading data are within the HistoController.  Calls that can be made include "data",  
"backload", "backloadYear", and "coin". "coin" should be run first to populate data tables for coin list before "backloadYear"
is used to backload the data for cryptocurrency wanted.

* Parameters for API call include type (type of historical call being made; default = histohour), fsym (from Symbol; default = BTC), 
tysm (to Symbol; default = USD), e (exchange; default = CCCAGG), extraParams (name of application), 
sign (If set to true, the server will sign the requests; default = false), tryConversion (If set to false, it will try to 
get values without using any conversion at all; default = true), aggregate (default = 1), limit (default = 50), toTs (unix Timestamp). 

**Required Parameters:**
Type, fsym, tsym, and e are the minimum parameters that should be included for all API calls.
* Ex of a URI call 
```
.../histo/data?type=histohour&fsym=BTC&tsym=USD&limit=10&aggregate=1&e=CCCAGG
```

* Data API calls mirror API calls from Cryptocompare.  Data received will be checked against database for new data entries 
between timestamps.  Any new data will be inserted into the database.

* Backload API calls will check database for most current timestamp for requested cryptocurrency and will aggregate new 
data up to current time stamp and  insert into database.

* BackloadYear API call will aggregate all data since the requested year change based on current timestamp 
and cryptocurrency selected.  Parameters are the same as other API calls with the addition of year (default value = -1). 
This parameter is the change to the current year date for data requested. Any new data that does not exist in the database 
will be entered.
**Ex. URI for backloading data for previous year:**
```$xslt
/histo/backloadYear?type=histohour&fsym=BTC&tsym=USD&e=CCCAGG&limit=500&year=-1
```

* Coin API calls will get all current cryptocurrency listed on Cryptocompare and will insert any new currency into the 
database.

## Making Predictions

* Create a datasource in the AWS console (data is exported from Redshift to S3) for your machine learning model. Models 
can be created programmatically or via the console - both require an existing datasource.  

* To programatically create a model, set createmodel=true. Optional: modelId=xxxx&modelName=xxxx. Id and name are 
generated if not provided. Ex URIs:
```
.../predict/realtime?createmodel=true&modelId=1234&modelName=MyModel
```
or
```
.../predict/realtime?createmodel=true
```
Model parameters may be configured in the code:

![ModelParamsScreenShot](http://path/to/image/on/github)

* To use an existing model, provide id and name.
```
.../predict/realtime?createmodel=false&modelid=1234&modelname=MyModel
```

## Contributors
* [Jialor Cheung](https://github.com/PopoPenguin)
* [Meghan Boyce](https://github.com/meejahnsnutshell)

## Acknowledgments

* [CryptoCompare API](https://www.cryptocompare.com/api/#)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/)
* [Machine Learning Samples](https://github.com/awslabs/machine-learning-samples/blob/master/targeted-marketing-java/src/main/java/com/amazonaws/samples/machinelearning/BuildModel.java) by awslabs