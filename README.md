# DynamicWorkflowAdjustmentWithSecurityConstraints

### Overview
This Research project aims to create a management system program that takes in many real life variables such as binding constraints, separation constraints, capability, user load, and authorization constraints. It also tests the resilience of this program
This program will take in these variables and determine whether a feasible solution of filling ever job with users is possible. 

### Constraint Definitions
 - Binding Constraint - Which jobs has to be done by the same user
     ex: two jobs that may have to be done together inventory manager and cashier
 - Separation Constraint - Which jobs cannot be done by the same user
     ex: two jobs that cannot be done together, pharmicist and shipper (for personal discretion)
 - Authorization - Is a User authorized to do this job
     ex: needs a masters degree
 - Capability - how well can a user do a specific job
     ex: user X is a good register and can handle 100 people an hour. his rating will be 100
 - User Load - Can the user do this many jobs
     ex: user X is doing Jobs cashier, inventory manager, and store clean up. Can he perform all these jobs without overloading
 
### Resilience Testing
 - If there is a feasible solution, it is possible that people will quit, new people are hired, or certain constraints are changed. This part of the program will adjust the system and job-to-user matchup with minimal change given the change
