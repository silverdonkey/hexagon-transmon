# Playground Project for Implementing a Hexagonal Architecture style with Spring Boot

## Description
The intent of Hexagonal Architecture is to encapsulate an application in first place. 
The application may consist of many bounded contexts or none at all. Hexagonal Architecture is all about managing a boundary between an application and the outside world. The boundary is made up of certain input ports provided by the application and certain output ports expected by the application. 

Hexagonal Architecture does not help us to manage finer-grained boundaries within our application. Inside our “hexagon”, we can do whatever we want. However, if the codebase gets too big for our working memory (in our brain), we should fall back to Domain-Driven Design or other concepts to create boundaries within our codebase.

## Topics covered
* Organizing Code - create a package structure that expresses Hexagonal Architecture: entities, use cases, ports and adapters
* Implementing main Architecture Elements - Use Case, Web Adapter, Persistence Adapter
* Testing architecture elements - Implementing Unit, Integration and System tests
* Enforcing Architecture Boundaries (and thus managing dependencies)
  * Keeping package structure in mind and use package-private visibility when possible 
  * Within a single build module - Using ArchUnit
  * Dividing the Architecture into different Build Artifacts - e.g. Maven Modules
* Managing Multiple Bounded Contexts (DDD)
  * Using Domain Events to exchange information between Bounded Contexts in a loosely coupled fashion.
  * Using an Application Service (Service Aggregator Pattern) that orchestrates Domain Services of different Bounded Contexts. Here the Application Service implements the Input Port(s) and acts as a Transaction Boundary.
* Starting Small - A Component-Based Approach to Software Architecture