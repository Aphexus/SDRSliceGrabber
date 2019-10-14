# GTRSliceGrabber

## Background

Title VII of the Dodd-Frank Wall Street Reform Act gave the CFTC and the SEC a broad framework for regulating OTC derivatives markets. There are requirements for clearing and trade reporting amongst other things mentioned in the law. Section 727 specifically mandates that some of this data be reported to the public. 

As it stands today, certain participants are required to report their trades to Swap Data Repositories or SDRs. These SDRs then report the data to the public. It is commonly referred to as "Part 43" data after Part 43 of Chapter 1 of Title 17 in the Code of Federal Regulations which you can read [here](https://www.law.cornell.edu/cfr/text/17/part-43).

DTCC operates the Global Trade Repository or GTR which is the largest SDR in the world by market share. The code in this repo is primarily a Spring Batch program which will download public data from DTCC's SDR public reporting site [here](https://rtdata.dtcc.com/gtr/), insert it into a database, and process cancellation or correction messages. DTCC refers to this data as slice reports, hence the name. A more concise description of the public data reported by DTCC can be found [here](https://www.sec.gov/rules/other/2017/ddr/dtcc-data-repository-form-sdr-ex-gg-7-amend.pdf). There are directions below on how to deploy the code to AWS Batch for the initial historical data load and to AWS Lambda for nightly downloads of the latest data.

## Local MySQL Setup w/ Docker
```
docker pull mysql/mysql-server:8.0
docker images
docker run -p 3306:3306 --name=slicegrabber_container -e MYSQL_ROOT_PASSWORD=password -d mysql/mysql-server:8.0
docker ps
docker exec -it slicegrabber_container mysql -uroot -p
update mysql.user set host = '%' where user = 'root';
select host, user from mysql.user;
create database slicegrabberdb;
(exit mysql)
docker restart slicegrabber_container;

docker stop/start/restart slicegrabber_container
```

This command will stop all docker containers.
```
killall docker-containerd-shim
```

This command will remove all docker containers.
```
docker-compose down
```

Be sure to create the necessary tables located in the schema directory.

## Configurable Properties

The following properties can be set either as environment variables or as JVM properties. If there is no default, it must be set.

| Property | Notes |
|----------|------------|
| spring.profiles.active | The active Spring profile. Default profile should only be used for local development.
| aws.accessKeyId | Self-explanatory
| aws.secretAccessKey | Self-explanatory.
| slicegrabber.jdbcUrl | Database jdbc url.
| slicegrabber.datasource.username | Database username.
| slicegrabber.datasource.password | Database password
| slicegrabber.itemwriter.chunkSize | Number of records stored in memory (per asset class) before being written to the database. Default is 1000. |
| slicegrabber.executors.threadPoolSize | Number of concurrent threads (per asset class). Should be 1 when executing jobs on AWS Lambda since lambda jobs will process cancellation and correction records. Default is 1. |
| slicegrabber.datasource.maxPoolSize | Specifies the size of the database connection pool. Default is 16. |

## Building and Executing

To build the project, simply run ```mvn clean package```.

The jar itself is executable, but requires certain command line args in the following order:

```(LAMBDA|BATCH) ((CREDITS|COMMODITIES|EQUITIES|FOREX|RATES) (START_DATE) (END_DATE))+```

```START_DATE``` and ```END_DATE``` should be in ```yyyy_MM_dd``` format with the start date being less than or equal to the end date and the end date being less than the current date if your local date is behind UTC (end of day cumulative slice files are usually released a few minutes after midnight, UTC). **Note:** Public data only goes as far back as October 23, 2016.

Using ```LAMBDA``` as the first argument will make it so that cancellation and correction records are accounted for at the end of each job. This post-processing will not work (due to locking and potentially missing original dimmenation IDs) when ```slicegrabber.executors.threadPoolSize``` is set to a number greater than 1. Therefore, it is recommended to use the ```BATCH``` argument for your initial historical data load, and then run the stored procedure afterwards for that particular asset class.

A few example arguments with their explanation are provided below:

| Command | Explanation
|---|---
| ```java -jar target/gtrslicegrabber-0.0.1-SNAPSHOT.jar LAMBDA RATES 2019_01_05 2019_01_09``` | This will download and process all cumulative slice files for RATES between January 1st through the 9th inclusively, one at a time.
| ```java -Dslicegrabber.executors.threadPoolSize=3 -jar target/gtrslicegrabber-0.0.1-SNAPSHOT.jar BATCH EQUITIES 2019_03_01 2019_03_03 FOREX 2018_07_01 2018_07_01 CREDITS 2017_10_10 2017_11_05``` | This will download 3 days' worth of data for EQUITIES in March 2019, 1 days' worth of data for FOREX in July 2018, and 27 days' worth of data for CREDITS for October-November 2017. Each asset class will have its own threadpool of size 3 to download and insert data into the database, but since the ```BATCH``` argument was used, cancellation and correction records were not processed (they were still inserted, but the original dissemination ID was not marked as cancelled/corrected).

## Deploying to AWS Batch for the Initial Historical Data Load

## Deploying to AWS Lambda for Nightly Downloads
