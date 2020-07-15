# nrich 

Nrich is a java library developed at CROZ whose purpose is to make development of applications on JVM a little easier.
It contains modules that were found useful on projects and as such were extracted to a common library.
It is based on Spring Framework it also provides Spring Boot starters for most of the modules to make 
configuration easier.

Most modules are composed of multiple submodules: 

- api - has the `api` suffix and contains classes that represent API of the module (service interfaces, parameters, return types...)
- implementation - contains actual api implementation
- spring boot starter - has the `spring-boot-starter` suffix and contains spring boot auto configuration of the specified module 

In Spring Boot environment only spring boot starter modules should be added as dependencies.

nrich is composed of following modules:

- nrich-core-api
  
  Contains common classes that are used throughout library
  
- nrich-encrypt
   
   Provides easier encryption of data, it can encrypt method results and decrypt method arguments.
   Method for encryption/decryption can be marked using annotations or as properties (i.e. in `application.yml` files).
   
- nrich-excel

  Provides easier generation of excel reports from provided data and templates. Default implementation uses `Apache POI` library but tries to 
  simplify usage.

- nrich-form-configuration

  Allows for resolving of server side constraints (`jakarta-validation-api`) on client side. 
  Its purpose is to have a central place for constraint definitions. On the server side client provides a mapping between client form and class containing constraints. After that when client asks request for form, all constraints with error messages defined for a class are returned to the client
  that is then responsible for processing and applying them to the form.

- nrich-jackson

  Sets commonly used defaults for standard Jackson properties (i.e `FAIL_ON_EMPTY_BEANS: false`) to avoid repeating
  it in every project. It also provides Jackson modules that serialize empty strings to null and that serialize class name for `@Entity` annotated
  classes or classes from defined package list.

- nrich-logging

  Provides a logging service that log's errors in a standard format and can also resolve verbosity and logging levels
  for each exception. It is used in `webmvc` module for logging exceptions but can easily be 
  replaced with custom implementation.

- nrich-notification

  Allows for resolving of client notifications from server side. Notifications can be resolved for controller actions (i.e. `Entity has successfully been saved`), 
  for exceptions (i.e. `Error occurred`) and for validation errors (i.e. `Validation errors found in request`). Validation notification also return a list of validation errors with properties that failed validation. 
  Notification messages are resolved by message keys or exception class names from `message.properties`.
  Users can also return responses with notification. `webmvc` module uses this module to send notification responses to client on exceptions with registered
  `@RestControllerAdvice`.
  
  
- nrich-registry

  Purpose of this module is transforming JPA entities in a format that client can interpret
  and create dynamic forms and grids for editing of entities without additional implementation
  on server side. On the server side methods are provided for searching, creating, updating and deleting
  entities through REST API. Configuration consists only from defining included entities and (optionaly) providing
  property label/header messages in `messsage.properties`.  


- nrich-search

  Allows for easier querying of entities. For its input it uses a class holding values that will be used
  in a query and a parameter (`SearchConfiguration`) that defines how those values will be added to query (i.e. what operators should be used, mapping of those properties on entities etc.) it also defines what result
  type will be returned, what associations should also be fetched etc. It also allows for using string as an input (instead of class holding the value), and a list of fields that should be searched from the entity (for quick search functionality).
  It was created as a means of simplifying creation of queries for various search forms on client side.
  `spring-data` module is used in implementation and module functionality is added through two repositories interfaces that users can add to their repository interface.
  
- nrich-security-csrf

   Intended as a replacement for Spring Security csrf functionality. It works with Spring Web MVC and WebFlux libraries. Clients should define initial token url and after that send generated
   token with each request in a header or as a parameter.

- nrich-spring-boot
   
   Adds additional functionality to Spring Boot. i.e. `ConditionalOnPropertyNotEmpty` annotation
   that checks if a property is not empty.
   
- nrich-spring-util
   
   Contains utility classes for Spring access. i.e. `ApplicationContextHolder` for resolving of `ApplicationContext`
   from static context. 

- nrich-validation
   
  Contains additional `jakarta-validation-api` constraints and validators (i.e `ValidFile`, `MaxSizeInBytess` etc).
  
- nrich-webmvc

  Provides additional functionality built on top of Spring Web MVC framework. Main purpose is handling exceptions through `NotificationErrorHandlingRestControllerAdvice` that 
  handles exceptions by logging them and sending notifications to client with exception descriptions.
  Also add additional classes handle binding (i.e. transforming empty string to null), locale resolving etc.
