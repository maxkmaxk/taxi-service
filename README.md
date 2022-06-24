# Taxi-service
***
Taxi-service is a web application training project, that provide CRUD operations with
taxi service entities,
such as drivers, cars, car manufacturers.

Project has 3-layer architecture
- data access layer
- application logic layer
- presentation layer
### Features
***
* Drivers
    
     <table style="border: none" align="center">
        <tr>
            <th align="center">feature</th>
            <th align="center">link</th>
        </tr>
        <tr>
            <td align="center">display all drivers</td>
            <td align="center">/drivers</td>
        </tr>
        <tr>
            <td align="center">add driver</td>
            <td align="center">/drivers/add</td>
        </tr>
        <tr>
            <td align="center">delete driver</td>
            <td align="center">/drivers/delete</td>
        </tr>
        <tr>
            <td align="center">display current driver's cars</td>
            <td align="center">/drivers/cars</td>
        </tr>
    </table>

* Manufacturers

     <table style="border: none" align="center">
        <tr>
            <th align="center">feature</th>
            <th align="center">link</th>
        </tr>
        <tr>
            <td align="center">display all manufacturers</td>
            <td align="center">/manufacturers</td>
        </tr>
        <tr>
            <td align="center">add manufacturer</td>
            <td align="center">/manufacturers/add</td>
        </tr>
        <tr>
            <td align="center">delete manufacturer</td>
            <td align="center">/manufacturers/delete</td>
        </tr>
    </table>

* Cars

     <table style="border: none" align="center">
        <tr>
            <th align="center">feature</th>
            <th align="center">link</th>
        </tr>
        <tr>
            <td align="center">display all cars</td>
            <td align="center">/cars</td>
        </tr>
        <tr>
            <td align="center">add car</td>
            <td align="center">/cars/add</td>
        </tr>
        <tr>
            <td align="center">delete car</td>
            <td align="center">/cars/delete</td>
        </tr>
        <tr>
            <td align="center">add driver to car</td>
            <td align="center">/cars/drivers/add</td>
        </tr>
    </table>


### Technologies
***
* JDK 11
* Apache Maven
* Apache Log4j
* MySQL
* JDBC
* Apache TomCat
* Javax Servlet
* JSP
* JSTL

### Startup
***
* install java servlet container (tested on TomCat 9.0.58)
* install MySQL DBMS (tested on MySQL 8.0.26) 
* create database schema using script 'resources/init_db.sql'
* configure connection to database in the ConnectionUtil class 
* rebuild project with Maven - run 'mvn clean package'
* [Deploy application to TomCat](https://www.baeldung.com/tomcat-deploy-war)

