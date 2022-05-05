# Lab 3 - G04


##
1)First of all, in order to run the application and also Unit Tests and Integration Tests, create and run the container with the postgres image using
   docker run --name pg-docker -e POSTGRES_PASSWORD=postgres -d -p 54320:5432 -v C:\docker\postgres:/var/lib/postgresql/data  postgres.
   I’m setting the port on my host to 54320, and 54320 will map back to 5432 in the container.
##
2)Username and Password for connecting to postgres service are both "postgres" (as written in the application-credential.properties file). We choose to keep in a separate .properties file 
all credentials, those used for postgres as well as those used for the test mail account )
##
3)In src/main/kotlin/it.polito.wa2-lab3group4 you will find a series of folders : 
   - annotations/ contains classes used for customizing Password validation. Basically we have created
     a new annotation (@ValidPassword) that is then used on the password attribute in UserDTO class for 
     checking all the constraints requested for password and validating it
   - controllers/ contains 2 classes : one used for serving Post requests on the user/register and user/validate endpoints,
    and one used for managing cases of BadRequest or NotFound returned to the client.
   - dtos/ contains the requested DTOs classes, and on each attribute of this class the validation for the incoming requests 
   is applied
   - entities/ contains the Entity classes (EntityBase is just a base class extended by User class).
     Note that an Activation record expires after 24 hours since its creation if it wouldn't have been validated in the meanwhile
   - exceptions/ contains exceptions classes
   - interceptors/ contains all files required for implementing rate limiter
   - repositories/ contains the repositories interfaces
   - services/ contains  services interfaces, while instead inside services/impl/ you will find
     their implementation. Inside UserServiceImpl you will find also the function required for pruning,
     (pruningExpiredRegistrationData()) which execution is scheduled every 24 hours

##
4)Before running the application, run the container with the postgres image
##
5)In order to test the application, you will find inside src/test/kotlin/it.polito.wa2-lab3group4 
   two main directories : 
   - unit_tests/ contains a folder with all utils files used for running tests, while the 3 remaining classes
     contain the final Unit Tests file that you can run separately (respectively for Dtos,and the 2 services).
     Before running any Unit Test make sure you have your container with postgres running.
     Note that for UserServiceUnitTest, rather than use Mocking Libraries, it was chosen (as discussed and agreed during the Lab with the professor)
     to populate our DB (and to clear it after every test execution) using @BeforeAll and @AfterAll
     annotations (you will find them in SpringTestBase class).
   - integration_tests/ contains 5 files : we needed to split the single integration test file into 5 ones because
     the rate limiter constraint (of maximum 10 requests per second) quite often lead to fail some tests (if executed
     in block,due to the high number of test requests received in a second). So it is better to run them separately.
     Before running any Integration Test make sure you have your container created.