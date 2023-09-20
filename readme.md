# spring security starter

This code is a starter for REST API secured with JWT.

## development environment
1. in `application.yml`, make sure spring.datasource.url is `jdbc:postgresql://localhost:5432/postgres`

2. run postgresql database with Docker `docker run -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres`

3. run `mvn spring-boot:run` to start the app

4. (optional) enable hot reloading in IntelliJ by checking these two options
- Build project automatically
- Allow auto-make to start even if developed application is currently running


## production enviroment

1. in `application.yml`, make sure spring.datasource.url is `jdbc:postgresql://postgres:5432/postgres`
2. run `docker-compose up --build`


## REST api workflow
1. requests comes in
2. JwtAuthFilter is applied (OncePerRequestFilter) and validates JWT
3. if token is missing in the request, 403 status is returned
4. if token is present
   1. extract user email from the token
   2. call UserDetailsService, which fetches user details from dockerized database
   3. if the user does not exist, return 403
5. Using the extracted, validate the JWT
   1. call JWT service
   2. if token is invalid, return 403
6. if the token is valid, update SecurityContextHolder to indicate that the user is now authenticated for the rest of the filter chain. 
7. Once SecurityContextHolder is updated, the request is automatically dispatched. It is then sent to the DispatcherServlet and from there it's forwarded to the controller. 
8. The controller and services execute all necessary operations, such as calling the service, interacting with the database, and sending the response (JWT, JSON, etc.).

