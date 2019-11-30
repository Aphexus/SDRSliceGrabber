# SDRSliceGrabber

## Background

Title VII of the Dodd-Frank Wall Street Reform Act gave the CFTC and the SEC a broad framework for regulating OTC derivatives markets. There are requirements for clearing and trade reporting amongst other things mentioned in the law. Section 727 specifically mandates that some of this trade data be reported to the public. 

As it stands today, certain participants are required to report their trades to Swap Data Repositories or SDRs. These SDRs then report the data to the public. It is commonly referred to as "Part 43" data after Part 43 of Chapter 1 of Title 17 in the Code of Federal Regulations which you can read [here](https://www.law.cornell.edu/cfr/text/17/part-43).

DTCC operates the Global Trade Repository or GTR which is the largest SDR in the world by market share. The code in this repo is primarily a Spring Batch program which will download public data from DTCC's SDR public reporting site [here](https://rtdata.dtcc.com/gtr/), insert it into a database, and process cancellation or correction messages. DTCC refers to this data as slice reports, hence the name. A more concise description of the public data reported by DTCC can be found [here](https://www.sec.gov/rules/other/2017/ddr/dtcc-data-repository-form-sdr-ex-gg-7-amend.pdf). There are directions below on how to deploy the code to AWS Batch for the initial historical data load and to AWS Lambda for nightly downloads of the latest data.

## More on this Repo

A DockerFile is supplied to build an image which can run on AWS Batch for the initial historical data load, plus stored procedures to process the CORRECT and CANCEL messages afterwards (to save you from spending more money on EC2 instances for AWS Batch). 

The main class in the codebase (```net.nicholaspurdy.sdrslicegrabber.App```) implements AWS Lambda's RequestHandler interface so the code can then be ran on AWS Lambda on a nightly basis. The CORRECT and CANCEL messages will be processed automatically in this case. No need to manually call a stored procedure. 

CloudFormation templates are a work in process.

## Architecture

The Spring Batch program will download files from DTCC's public reporting website and save the files themselves to S3 while inserting the individual records into a database so that CORRECT and CANCEL messages can be processed. Whether or not this post-processing occurs depends on the first command line argument, either LAMBDA or BATCH. 

The execution path of a single job is provided below (one job = date + asset class):

![SDRSliceGrabber Execution Path](http://nicholaspurdy.net/SDRSliceGrabber_execution_path.png)

The tables in Postgres (minus the ones for Spring Batch) are shown here:

![SDRSliceGrabber Tables](http://nicholaspurdy.net/SDRSliceGrabber_tables.png)

## Local Setup w/ Docker

You do not need an AWS account to run the code locally, but a couple of docker containers do need to be set up.

#### PostgreSQL
Postgres can be set up with one command:
```
docker run -p 5432:5432 --name=slicegrabber_postgres -e POSTGRES_PASSWORD=password -d postgres
```
Once that's done, assuming ```slicegrabber_postgres``` is the only docker container running on your machine, you should be able to connect to it using a JDBC URL of ```jdbc:postgresql://172.17.0.2:5432/postgres```. Username is postgres and password is password. From there, you need to create the necessary tables and stored procedures in the [schema](https://github.com/NicholasPurdy/SDRSliceGrabber/tree/master/schema) directory.

#### S3Mock

To simulate AWS S3, you should use [S3Mock](https://github.com/adobe/S3Mock), built by Adobe. You can get it up and running with one command:
```
docker run -p 9090:9090 -p 9191:9191 --name=slicegrabber_s3mock -e initialBuckets='mockbucket' -d adobe/s3mock
```

#### Useful Docker Commands
| Result | Command
|-------|-------
| Stop, start, or restart a container | ```docker stop/start/restart slicegrabber_postgres```
| Remove a container | ```docker rm slicegrabber_s3mock```
| Stop all containers | ```killall docker-containerd-shim```
| Remove all containers | ```docker-compose down```
| List images | ```docker images```
| List all containers | ```docker container ls -a```

## Building and Executing

To build the project, simply run ```mvn clean package```. Besides generating a jar in the standard target directory, maven will also call ```docker build``` to generate a docker image made specifically for AWS Batch. This will overwrite old docker images without deleting them automatically. To delete them, use ```docker rmi $(docker images --filter "dangling=true" -q --no-trunc)```.

To execute the jar, command line args are required in the following order:
```
(LAMBDA|BATCH) ((CREDITS|COMMODITIES|EQUITIES|FOREX|RATES) (START_DATE) (END_DATE))+
```

```START_DATE``` and ```END_DATE``` should be in ```yyyy_MM_dd``` format with the start date being less than or equal to the end date and the end date being less than the current date if your local date is behind UTC (end of day cumulative slice files are usually released a few minutes after midnight, UTC).

To execute the docker image, run the following:
```
docker run --rm sdrslicegrabber:latest ((CREDITS|COMMODITIES|EQUITIES|FOREX|RATES) (START_DATE) (END_DATE))+
```

With the docker image, the ```BATCH``` argument is always used. This is hardcoded in the DockerFile. 

**Note:** Public data only goes as far back as December 31, 2012 for Credits and Rates, and as far back as February 28, 2013 for Equities, Forex and Commodities.

#### Configurable Properties

| Property | Notes |
|----------|------------|
| ```slicegrabber.executor.threadPoolSize``` | Controls how many jobs can run at once. Default is 8 if executing the jar file directly.
| ```slicegrabber.itemwriter.chunkSize``` | Spring Batch will hold this number of records in memory before writing each chunk to the database. Default is 1000.
| ```slicegrabber.datasource.maxPoolSize``` | Specifies the size of the database connection pool. Default is 10.

For AWS Lambda, the more memory you allocate to your lambda function, the higher you can set the chunk size.

For jobs running with the ```BATCH``` argument, the optimum pool/chunk size will depend on your physical machine's resources or the instance size.

In both cases however, the connection pool size should always at least be equal to the ```threadPoolSize```.

#### Example Executions

| Command | Explanation
|---|---
| ```java -jar target/sdrslicegrabber-0.0.1-SNAPSHOT.jar LAMBDA RATES 2019_01_05 2019_01_09``` | This will download and process all cumulative slice files for RATES between January 1st through the 9th inclusively,all at once since the default thread pool size of 8 is used.
| ```java -Dslicegrabber.executor.threadPoolSize=10 -jar target/sdrslicegrabber-0.0.1-SNAPSHOT.jar BATCH EQUITIES 2019_03_01 2019_03_03 FOREX 2018_07_01 2018_07_01 CREDITS 2017_10_10 2017_11_05``` | This will download 3 days' worth of data for EQUITIES in March 2019, 1 days' worth of data for FOREX in July 2018, and 27 days' worth of data for CREDITS for October-November 2017. Since the ```BATCH``` argument is used, cancellation and correction records will not be processed. They will still be inserted, but the original dissemination IDs will not be marked as cancelled or corrected. The stored procedures PROCESS_EQUITIES, PROCESS_FOREX, and PROCESS_CREDITS will have to be called manually.
| ```docker run --rm -e chunkSize="2000" sdrslicegrabber:latest CREDITS 2019_02_12 2019_02_12``` | This run is using the docker image which only uses the ```BATCH``` argument. The 3 variables discussed earlier are all overridable using docker environment variables as seen here with ```chunkSize```. Just use the last word in the property as oppossed to the whole thing.

**Note:** PostgreSQL and S3Mock still need to be running in their own docker containers when using the ```sdrslicegrabber``` image to execute the program locally.


## TODO
* Provide CloudFormation templates/directions on deploying to AWS
* Handle historical data from Bloomberg SDR
* Handle missing underlying asset values for equities
* Provide option for pulling data from ICE Trade Vault + CME SDR
