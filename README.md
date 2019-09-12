# GTRSliceGrabber

## Background

Title VII of Dodd-Frank gave the CFTC and the SEC a broad framework for regulating OTC derivatives markets. There are requirements for clearing, trade reporting, and other things mentioned in the law. Section 727 specifically mandates that some of this data be reported to the public. 

As it stands today, certain participants are required to report their trades to Swap Data Repositories or SDRs. These SDRs then report the data to the public. It is commonly referred to as "Part 43" data after Part 43 of Chapter 1 of Title 17 in the Code of Federal Regulations which you can read [here](https://www.law.cornell.edu/cfr/text/17/part-43).

DTCC operates the Global Trade Repository or GTR which is the largest SDR in the world, although there are in fact others. The code in this repo is a Spring Batch program which will download public data from DTCC's SDR public reporting site [here](https://rtdata.dtcc.com/gtr/), insert it into a database, and process cancellation or correction messages. There are directions below on how to deploy the code to AWS Batch for the initial historical data load and to AWS Lambda for nightly downloads of the latest data.
