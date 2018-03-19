## Question-1
1- Build application
```
mvn package
```

2- Jar package will be located under ./target folder
3- Run with sample data
```
java -jar target/invitation-0.0.1-SNAPSHOT.jar --inputFile=customers.txt 
```

## Question-2

I propose a solution for telecom operator to process all their customers' events in real-time and offer promotions to customers in certain circumstances ( i.e when they have 5 call-drops in 10 mins). Current system was not scalable and handling events of only 30k VIP customers. I redesigned the system and implement a distributed event processing system to handle 20M customers' event in real-time. It was fantastic because we provide competitive advantage to our customer and have direct impact on revenue. 