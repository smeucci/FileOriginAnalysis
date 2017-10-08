# File Origin Analysis
Video Forensic based on File Format Analysis. Project for the master's thesis.

Usage
=====
```bash
usage: foa [-cA <xml file>] [-cB <xml file>] [-h | -init | -trn | -tst |
       -ute | -utr] [-i <xml/txt file or folder>]  [-lA <txt/json file>]
       [-lB <txt/json file>] [-o <folder>]     [-v]
 -cA,--configA <xml file>              xml config file for class A, only
                                       for --test
 -cB,--configB <xml file>              xml config file for class B, only
                                       for --test
 -h,--help                             print help message
 -i,--input <xml/txt file or folder>   xml file or txt file with list of
                                       xml paths for which compute the
                                       likelihood for class A and B, only
                                       for --test, dataset folder path for
                                       --update
 -init,--initialize                    initialize database
 -lA,--listA <txt/json file>           text/json file containing a list of
                                       xml file for class A, only for
                                       --train
 -lB,--listB <txt/json file>           text/json file containing a list of
                                       xml file for class B, only for
                                       --train
 -o,--output <folder>                  output folder for the training
                                       config files, only for --train
 -trn,--train                          train a binary classification
                                       problem
 -tst,--test                           predict the class of a xml file
 -ute,--update-testing                 update testing database
 -utr,--update-training                update trainingdatabase
 -v,--verbose                          whether or not display information,
                                       only for --test
```
