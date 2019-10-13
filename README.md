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

